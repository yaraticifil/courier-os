package main

import (
	"database/sql"
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"os"
	"time"

	"github.com/gorilla/mux"
	_ "github.com/lib/pq"
)

// Global database connection
var db *sql.DB

// DispatchRequest represents the incoming order location
type DispatchRequest struct {
	OrderId string  `json:"orderId"`
	Lat     float64 `json:"lat"`
	Lng     float64 `json:"lng"`
}

// DispatchResponse represents the finalized dispatch decision matching contract
type DispatchResponse struct {
	OrderId   string   `json:"orderId"`
	CourierId string   `json:"courierId"`
	Eta       int      `json:"eta"`
	Reason    []string `json:"reason"`
	Mode      string   `json:"mode"`
}

// HealthResponse represents service health status
type HealthResponse struct {
	Status    string    `json:"status"`
	Timestamp time.Time `json:"timestamp"`
}

func main() {
	var err error
	
	// Establish connection to PostgreSQL container (dynamic host support)
	dbHost := os.Getenv("DB_HOST")
	if dbHost == "" {
		dbHost = "localhost"
	}
	connStr := fmt.Sprintf("postgres://courier:courier123@%s:5432/courierdb?sslmode=disable", dbHost)
	db, err = sql.Open("postgres", connStr)
	if err != nil {
		log.Fatalf("Database connection initialization failed: %v", err)
	}
	defer db.Close()

	// Wait and retry connection to ensure DB container is ready
	for i := 0; i < 5; i++ {
		err = db.Ping()
		if err == nil {
			break
		}
		log.Printf("Waiting for database connection (retry %d)...", i+1)
		time.Sleep(2 * time.Second)
	}
	if err != nil {
		log.Fatalf("Could not ping database: %v", err)
	}
	log.Println("Successfully connected to PostgreSQL/PostGIS database.")

	// 2. Set up Gorilla Mux Router
	r := mux.NewRouter()

	// Logger Middleware
	r.Use(func(next http.Handler) http.Handler {
		return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
			log.Printf("%s %s %s", r.Method, r.RequestURI, r.RemoteAddr)
			next.ServeHTTP(w, r)
		})
	})

	// Register Endpoints
	r.HandleFunc("/health", HealthHandler).Methods("GET")
	r.HandleFunc("/dispatch", DispatchHandler).Methods("POST")

	// 3. Start Server
	port := ":8080"
	log.Printf("Starting CREATIVE ELEPHANT // DISPATCH SERVICE on port %s", port)
	if err := http.ListenAndServe(port, r); err != nil {
		log.Fatalf("Server startup failed: %v", err)
	}
}

// HealthHandler handles health check requests
func HealthHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	json.NewEncoder(w).Encode(HealthResponse{
		Status:    "UP",
		Timestamp: time.Now(),
	})
}

// DispatchHandler handles adaptive courier dispatch matching via PostGIS
func DispatchHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")

	// Parse JSON Request
	var req DispatchRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		w.WriteHeader(http.StatusBadRequest)
		json.NewEncoder(w).Encode(map[string]string{"error": "Invalid JSON request payload"})
		return
	}

	if req.OrderId == "" || req.Lat == 0 || req.Lng == 0 {
		w.WriteHeader(http.StatusBadRequest)
		json.NewEncoder(w).Encode(map[string]string{"error": "Missing required fields: orderId, lat, or lng"})
		return
	}

	log.Printf("Received dispatch request for Order: %s at Coordinates: [%f, %f]", req.OrderId, req.Lat, req.Lng)

	// Close closest available courier (is_busy = FALSE) using PostGIS spatial geography distance
	var courierId string
	var courierName string
	var distanceMeters float64

	// Note: ST_MakePoint takes (longitude, latitude) -> (Lng, Lat)
	query := `
		SELECT id, name, ST_Distance(geom, ST_SetSRID(ST_MakePoint($1, $2), 4326)::geography) as distance
		FROM couriers
		WHERE is_busy = FALSE
		ORDER BY distance
		LIMIT 1;
	`
	err := db.QueryRow(query, req.Lng, req.Lat).Scan(&courierId, &courierName, &distanceMeters)
	if err != nil {
		if err == sql.ErrNoRows {
			w.WriteHeader(http.StatusNotFound)
			json.NewEncoder(w).Encode(map[string]string{"error": "No available couriers found in the region"})
			return
		}
		log.Printf("Spatial query execution error: %v", err)
		w.WriteHeader(http.StatusInternalServerError)
		json.NewEncoder(w).Encode(map[string]string{"error": "Internal spatial processing error"})
		return
	}

	// Calculate ETA: distance in meters / 300 + 3 minutes
	etaMinutes := int(distanceMeters/300) + 3

	// Perform State Transaction: Mark matched courier as busy
	updateQuery := "UPDATE couriers SET is_busy = TRUE WHERE id = $1"
	_, err = db.Exec(updateQuery, courierId)
	if err != nil {
		log.Printf("Courier state update error: %v", err)
		w.WriteHeader(http.StatusInternalServerError)
		json.NewEncoder(w).Encode(map[string]string{"error": "Failed to lock courier node state"})
		return
	}

	log.Printf("Matched Courier %s (%s) to Order %s. Distance: %.2f meters. Calculated ETA: %d minutes.", 
		courierName, courierId, req.OrderId, distanceMeters, etaMinutes)

	// Assemble Decision contract response
	reasons := []string{"en düşük ETA", "PostGIS konum optimizasyonu"}
	if distanceMeters > 3000 {
		reasons = append(reasons, "bölgesel kısıt esnetme")
	}

	resp := DispatchResponse{
		OrderId:   req.OrderId,
		CourierId: courierId,
		Eta:       etaMinutes,
		Reason:    reasons,
		Mode:      "live",
	}

	w.WriteHeader(http.StatusOK)
	json.NewEncoder(w).Encode(resp)
}
