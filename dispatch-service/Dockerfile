# Stage 1: Build binary
FROM golang:1.21-alpine AS builder

WORKDIR /app

# Copy dependency configs and download modules
COPY go.mod ./
# Note: we will download packages using go mod tidy in the build stage
RUN go env -w GOPROXY=https://goproxy.cn,direct || true
RUN go mod download

# Copy source code and build statically linked executable
COPY . .
RUN CGO_ENABLED=0 GOOS=linux go build -ldflags="-w -s" -o dispatch-service .

# Stage 2: Minimal runner
FROM alpine:3.18

WORKDIR /app

COPY --from=builder /app/dispatch-service .

EXPOSE 8080

CMD ["./dispatch-service"]
