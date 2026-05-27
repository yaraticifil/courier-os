#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
CREATIVE ELEPHANT // COURIER OS
LIVE MARKET ADAPTIVE DISPATCH ENGINE & SANDBOX SIMULATOR

"Bu bir oyun değil. Bu bir iş aracı."
"""

import time
import json
import random
import math
from datetime import datetime

# ANSI Color Codes for Premium Vantablack Terminal UI
C_DARK = "\033[90m"
C_WHITE = "\033[97m"
C_GREEN = "\033[92m"
C_PURPLE = "\033[95m"
C_YELLOW = "\033[93m"
C_RED = "\033[91m"
C_RESET = "\033[0m"
C_BOLD = "\033[1m"

class Courier:
    def __init__(self, courier_id, name, lat, lng, zone):
        self.courier_id = courier_id
        self.name = name
        self.lat = lat
        self.lng = lng
        self.zone = zone
        self.is_busy = False
        self.idle_ticks = 0
        self.delivery_count = 0
        self.total_earnings = 0.0

    def to_dict(self):
        return {
            "id": self.courier_id,
            "name": self.name,
            "zone": self.zone,
            "busy": self.is_busy,
            "deliveries": self.delivery_count,
            "earnings": round(self.total_earnings, 2)
        }

class Order:
    def __init__(self, order_id, pickup_zone, dropoff_zone, pickup_lat, pickup_lng, dropoff_lat, dropoff_lng):
        self.order_id = order_id
        self.pickup_zone = pickup_zone
        self.dropoff_zone = dropoff_zone
        self.pickup_lat = pickup_lat
        self.pickup_lng = pickup_lng
        self.dropoff_lat = dropoff_lat
        self.dropoff_lng = dropoff_lng
        self.assigned_courier = None
        self.created_at = time.time()
        self.assigned_at = None
        self.delivered_at = None
        self.eta = 0
        self.base_payout = 50.0
        self.multiplier = 1.0
        self.total_payout = 50.0

    def calculate_distance(self, c_lat, c_lng):
        # Calculate flat Euclidean distance as proxy for telemetry routing
        return math.sqrt((self.pickup_lat - c_lat)**2 + (self.pickup_lng - c_lng)**2)

class AdaptiveDispatchEngine:
    def __init__(self):
        # Initial policy rules - Rule first
        self.policy = {
            "max_matching_distance": 0.15,      # Max spatial search boundary
            "base_eta_factor": 120,             # Base scaling factor for ETA
            "payout_per_km": 15.0,              # base pricing coefficient
            "balancing_coefficient": 1.0        # System balancing sensitivity
        }
        # Telemetry logs
        self.telemetry = {
            "ticks": 0,
            "total_deliveries": 0,
            "total_delays": 0.0,
            "zone_congestion": {"Kadıköy": 0.0, "Beşiktaş": 0.0, "Üsküdar": 0.0},
            "zone_supply": {"Kadıköy": 0, "Beşiktaş": 0, "Üsküdar": 0},
            "zone_demand": {"Kadıköy": 0, "Beşiktaş": 0, "Üsküdar": 0},
            "payout_multiplier": {"Kadıköy": 1.0, "Beşiktaş": 1.0, "Üsküdar": 1.0}
        }
        self.decisions_log = []

    def log_decision(self, order_id, courier_id, eta, reasons, mode="simulation"):
        decision = {
            "orderId": order_id,
            "courierId": courier_id,
            "eta": eta,
            "reason": reasons,
            "mode": mode,
            "timestamp": datetime.now().isoformat()
        }
        self.decisions_log.append(decision)
        return decision

    def update_telemetry(self, couriers, pending_orders):
        self.telemetry["ticks"] += 1
        
        # Reset zone tallies
        for z in self.telemetry["zone_supply"]:
            self.telemetry["zone_supply"][z] = 0
            self.telemetry["zone_demand"][z] = 0

        # Count available courier nodes
        for c in couriers:
            if not c.is_busy:
                self.telemetry["zone_supply"][c.zone] += 1
                c.idle_ticks += 1

        # Count pending order demand
        for o in pending_orders:
            self.telemetry["zone_demand"][o.pickup_zone] += 1

        # Adaptive Telemetry analysis - Detect patterns and adjust multipliers/rules
        self.adapt_policies()

    def adapt_policies(self):
        # Check imbalances in zones (supply/demand mismatch)
        for zone in self.telemetry["zone_supply"]:
            supply = self.telemetry["zone_supply"][zone]
            demand = self.telemetry["zone_demand"][zone]
            
            # Pattern Detection: Imbalance check
            if demand > supply * 2 and demand > 0:
                # Deficit detected! Adaptive logic updates constraints in real time
                self.telemetry["zone_congestion"][zone] = min(self.telemetry["zone_congestion"][zone] + 0.15, 1.0)
                self.telemetry["payout_multiplier"][zone] = round(1.0 + (demand - supply) * 0.12, 2)
            else:
                # Balance recovering
                self.telemetry["zone_congestion"][zone] = max(self.telemetry["zone_congestion"][zone] - 0.08, 0.0)
                self.telemetry["payout_multiplier"][zone] = max(self.telemetry["payout_multiplier"][zone] - 0.05, 1.0)

    def dispatch(self, order, couriers, mode="simulation"):
        """
        Adaptif Eşleştirme Karar Algoritması:
        Deterministik kısıt kontrolü (Policy Check) -> En uygun adayın seçimi.
        """
        best_courier = None
        min_distance = float('inf')
        reasons = ["en yakın + en uygun kurye"]

        # 1. Dispatch filter constraint check
        pickup_multiplier = self.telemetry["payout_multiplier"][order.pickup_zone]
        if pickup_multiplier > 1.2:
            reasons.append(f"bölgede kurye eksikliği (Surge: +%{int((pickup_multiplier-1)*100)})")
            # Adaptive policy: Expand search boundary to pull couriers from other zones
            max_dist = self.policy["max_matching_distance"] * 1.8
        else:
            max_dist = self.policy["max_matching_distance"]

        # 2. Iterate candidates
        for c in couriers:
            if c.is_busy:
                continue
            
            # Check spatial distance
            dist = order.calculate_distance(c.lat, c.lng)
            if dist < max_dist:
                # Policy: Prioritize idle couriers in the same zone if equal distance
                score = dist
                if c.zone == order.pickup_zone:
                    score *= 0.8  # 20% spatial score advantage for local zone match
                
                if score < min_distance:
                    min_distance = score
                    best_courier = c

        if best_courier:
            # Match approved!
            best_courier.is_busy = True
            order.assigned_courier = best_courier
            order.assigned_at = time.time()
            
            # Calculate dynamic pricing
            order.multiplier = pickup_multiplier
            order.total_payout = round(order.base_payout * order.multiplier + (min_distance * 100 * self.policy["payout_per_km"]), 2)
            
            # Calculate ETA (distance * factor + traffic/congestion penalty)
            congestion_penalty = self.telemetry["zone_congestion"][order.pickup_zone] * 8 # Max 8 minutes penalty
            order.eta = int(min_distance * self.policy["base_eta_factor"] + 4 + congestion_penalty)

            if pickup_multiplier > 1.3:
                reasons.append("yük dengeleme optimizasyonu")

            decision = self.log_decision(order.order_id, best_courier.courier_id, order.eta, reasons, mode)
            return decision
        
        return None

def render_terminal_ui(engine, couriers, active_orders, pending_orders):
    # Clear screen (terminal friendly print)
    print("\n" * 2)
    print(f"{C_BOLD}{C_WHITE}┌──────────────────────────────────────────────────────────────┐{C_RESET}")
    print(f"{C_BOLD}{C_WHITE}│               CREATIVE ELEPHANT // COURIER OS               │{C_RESET}")
    print(f"{C_BOLD}{C_WHITE}│      \"Kuryelerin en kısa sürede en çok kazandığı motor\"     │{C_RESET}")
    print(f"{C_BOLD}{C_WHITE}└──────────────────────────────────────────────────────────────┘{C_RESET}")
    
    # State Metrics
    print(f"{C_BOLD}{C_WHITE} [ OPERASYONEL METRİKLER ]{C_RESET}")
    print(f"  • Simülasyon Adımı (Tick)  : {C_WHITE}{engine.telemetry['ticks']}{C_RESET}")
    print(f"  • Toplam Dağıtılan Sipariş : {C_GREEN}{engine.telemetry['total_deliveries']}{C_RESET}")
    print(f"  • Bekleyen Sipariş Yükü    : {C_YELLOW}{len(pending_orders)}{C_RESET}")
    print(f"  • Taşıma Aşamasındaki Sip. : {C_PURPLE}{len(active_orders)}{C_RESET}")
    print(f"  • Aktif Kurye Sayısı       : {C_WHITE}{len(couriers)}{C_RESET}")
    
    # Zone stats
    print(f"\n{C_BOLD}{C_WHITE} [ BÖLGESEL TALEP & YOĞUNLUK ANALİZİ ]{C_RESET}")
    print(f"  {C_DARK}Bölge      Kurye(Arz)  Sipariş(Talep)  Surge      Yoğunluk{C_RESET}")
    for zone in engine.telemetry["zone_supply"]:
        supply = engine.telemetry["zone_supply"][zone]
        demand = engine.telemetry["zone_demand"][zone]
        mult = engine.telemetry["payout_multiplier"][zone]
        cong = engine.telemetry["zone_congestion"][zone]
        
        cong_bar = "█" * int(cong * 10) + "░" * (10 - int(cong * 10))
        cong_color = C_GREEN if cong < 0.3 else (C_YELLOW if cong < 0.6 else C_RED)
        
        print(f"  %-10s %-11d %-14d {C_GREEN}x%-9.2f{C_RESET} {cong_color}[%s]{C_RESET}" % 
              (zone, supply, demand, mult, cong_bar))

    # Real time courier telemetry
    print(f"\n{C_BOLD}{C_WHITE} [ AKTİF KURYE TELEMETRİ ALANI ]{C_RESET}")
    print(f"  {C_DARK}ID    Kurye Düğümü  Bölge     Durum       Teslimat  Toplam Kazanç{C_RESET}")
    for c in couriers[:5]: # Show first 5 couriers
        status = f"{C_RED}TAŞIYOR{C_RESET}" if c.is_busy else f"{C_GREEN}SERBEST{C_RESET}"
        print("  %-5s %-13s %-9s %-20s %-9d {C_GREEN}%-8.2f TL{C_RESET}" %
              (c.courier_id, c.name, c.zone, status, c.delivery_count, c.total_earnings))
    
    # Show last dispatch decision JSON output contract
    if engine.decisions_log:
        last_decision = engine.decisions_log[-1]
        print(f"\n{C_BOLD}{C_WHITE} [ SON ADAPTİF KARAR SÖZLEŞMESİ (API CONTRACT) ]{C_RESET}")
        print(f"{C_DARK}" + json.dumps(last_decision, indent=2, ensure_ascii=False) + f"{C_RESET}")

def main():
    engine = AdaptiveDispatchEngine()
    
    # Initialize mock couriers (Supply nodes)
    couriers = [
        Courier("C10", "Mert Kaya", 40.991, 29.027, "Kadıköy"),
        Courier("C11", "Ahmet Yurt", 40.992, 29.029, "Kadıköy"),
        Courier("C12", "Selin Kılıç", 41.042, 29.008, "Beşiktaş"),
        Courier("C13", "Caner Demir", 41.041, 29.006, "Beşiktaş"),
        Courier("C14", "Burak Tunç", 40.988, 29.015, "Üsküdar"),
        Courier("C15", "Hale Gümüş", 40.989, 29.016, "Üsküdar")
    ]
    
    active_orders = []
    pending_orders = []
    
    order_counter = 1
    
    # Spatial coordinates mapping for zones
    zone_coordinates = {
        "Kadıköy": (40.991, 29.027),
        "Beşiktaş": (41.042, 29.008),
        "Üsküdar": (40.988, 29.015)
    }

    try:
        # Run simulation loops
        while True:
            # 1. Update system state telemetry
            engine.update_telemetry(couriers, pending_orders)
            
            # 2. Randomly generate incoming orders
            if random.random() < 0.7:
                p_zone = random.choice(list(zone_coordinates.keys()))
                # Increase Kadıköy demand chance to trigger adaptive pricing/surges
                if random.random() < 0.6:
                    p_zone = "Kadıköy"
                
                d_zone = random.choice(list(zone_coordinates.keys()))
                
                p_lat, p_lng = zone_coordinates[p_zone]
                # Add tiny telemetry offsets
                p_lat += random.uniform(-0.02, 0.02)
                p_lng += random.uniform(-0.02, 0.02)
                
                d_lat, d_lng = zone_coordinates[d_zone]
                d_lat += random.uniform(-0.02, 0.02)
                d_lng += random.uniform(-0.02, 0.02)
                
                new_order = Order(
                    order_id=f"ORD_{order_counter:03d}",
                    pickup_zone=p_zone,
                    dropoff_zone=d_zone,
                    pickup_lat=p_lat,
                    pickup_lng=p_lng,
                    dropoff_lat=d_lat,
                    dropoff_lng=d_lng
                )
                pending_orders.append(new_order)
                order_counter += 1

            # 3. Process dispatch matches (Adaptive engine loop)
            dispatched_list = []
            for o in pending_orders:
                decision = engine.dispatch(o, couriers, mode="simulation")
                if decision:
                    active_orders.append(o)
                    dispatched_list.append(o)
            
            # Remove matched orders from pending queue
            for o in dispatched_list:
                pending_orders.remove(o)

            # 4. Simulate shipping progress (ETA updates)
            delivered_list = []
            for o in active_orders:
                # Deduct ETA by time step
                o.eta -= 1
                if o.eta <= 0:
                    # Delivery complete!
                    o.delivered_at = time.time()
                    c = o.assigned_courier
                    c.is_busy = False
                    c.delivery_count += 1
                    c.total_earnings += o.total_payout
                    
                    engine.telemetry["total_deliveries"] += 1
                    engine.telemetry["total_delays"] += (o.delivered_at - o.assigned_at)
                    delivered_list.append(o)
                    
            # Remove delivered from active transit list
            for o in delivered_list:
                active_orders.remove(o)

            # 5. Render live simulation UI to terminal
            render_terminal_ui(engine, couriers, active_orders, pending_orders)
            
            # Save telemetry snapshot to local JSON log for telemetry audits
            with open("/home/sanoi/.gemini/antigravity-cli/scratch/android-profile-prototype/adaptive-dispatch/telemetry_audit.json", "w") as f:
                json.dump({
                    "timestamp": datetime.now().isoformat(),
                    "telemetry": engine.telemetry,
                    "active_couriers": [c.to_dict() for c in couriers],
                    "decisions": engine.decisions_log[-10:] # Last 10 decisions
                }, f, indent=2, ensure_ascii=False)

            # Sleep to match tick frequency (1.5 seconds)
            time.sleep(1.5)

    except KeyboardInterrupt:
        print(f"\n{C_BOLD}{C_GREEN}• Simülasyon sonlandırıldı. Telemetri verileri 'telemetry_audit.json' dosyasına kaydedildi.{C_RESET}")

if __name__ == "__main__":
    main()
