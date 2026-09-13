# Pet Store Microservices

A Java 21 / Spring Boot 3 multi-module microservices application inspired by the [Azure AKS Store Demo](https://github.com/Azure-Samples/aks-store-demo). It demonstrates a customer storefront, product catalog, order placement API, asynchronous event-driven order processing via RabbitMQ, containerized multi-stage builds, and Kubernetes deployment.

---

## Services Architecture

| Service | Port | Description |
|---|:---:|---|
| **`store-front`** | `8080` | Customer Web UI (HTML/JS) and API gateway routing `/api/products` and `/api/orders` |
| **`product-service`** | `8081` | In-memory product catalog REST API |
| **`order-service`** | `8082` | Order management REST API; publishes `order.created` events to RabbitMQ |
| **`makeline-service`** | `8083` | Asynchronous consumer; listens on RabbitMQ `petstore.makeline` queue and tracks fulfillment |
| **`rabbitmq`** | `5672` / `15672` | RabbitMQ message broker with Management UI |

---

## Prerequisites

- **Java 21** & **Maven 3.9+** (for local development)
- **Podman 5.x+** with Podman Machine initialized and running:
  ```powershell
  podman machine start
  ```
- **Podman Compose** or Docker Compose CLI configured with the Podman socket.

---

## Run Locally with Podman

### 1. Start all services

Run the compose stack to build images and launch all 5 containers:

```bash
# Using podman compose
podman compose up --build -d

# Or using docker compose with Podman backend
docker compose up --build -d
```

### 2. Verify running containers

Check that all containers are up and healthy:

```bash
podman ps
```

Expected containers:
- `pet-store-rabbitmq-1` (healthy on ports `5672`, `15672`)
- `pet-store-product-service-1` (port `8081`)
- `pet-store-order-service-1` (port `8082`)
- `pet-store-makeline-service-1` (port `8083`)
- `pet-store-store-front-1` (port `8080`)

### 3. Access Web UIs & APIs

- **Store Front Web UI**: [http://localhost:8080](http://localhost:8080)
- **RabbitMQ Management Dashboard**: [http://localhost:15672](http://localhost:15672) (User: `guest`, Password: `guest`)
- **Product Catalog API**: `GET http://localhost:8080/api/products` or `GET http://localhost:8081/api/products`
- **Makeline Orders API**: `GET http://localhost:8083/api/makeline/orders`

### 4. Test Order Processing Flow

**Place an order via Store Front:**

```powershell
# PowerShell
Invoke-RestMethod -Uri "http://localhost:8080/api/orders" -Method Post -ContentType "application/json" -Body '{"customer":"Alice","items":["dog-bed","cat-tower"]}'
```

```bash
# cURL
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customer":"Alice","items":["dog-bed","cat-tower"]}'
```

**Verify Makeline consumption:**

```powershell
# PowerShell
Invoke-RestMethod -Uri "http://localhost:8083/api/makeline/orders" -Method Get | ConvertTo-Json -Depth 5
```

```bash
# cURL
curl http://localhost:8083/api/makeline/orders
```

### 5. Stop services

To stop and remove containers along with volumes:

```bash
podman compose down -v
```

---

## Building Images Manually with Podman

You can build multi-stage container images individually from the repository root:

```bash
podman build -t pet-store-product-service:latest -f services/product-service/Dockerfile .
podman build -t pet-store-order-service:latest -f services/order-service/Dockerfile .
podman build -t pet-store-makeline-service:latest -f services/makeline-service/Dockerfile .
podman build -t pet-store-store-front:latest -f services/store-front/Dockerfile .
```

---

## Deploy to Kubernetes

```bash
kubectl create namespace pets
kubectl apply -k deploy/kubernetes/overlays/dev
kubectl -n pets port-forward service/store-front 8080:80
```

> The manifests use local image names (`pet-store/<service>:dev`); build and load them into your cluster before applying, or update the image names in the overlay.

---

## Code Quality & Formatting

Formatting and checks are centralized in the root Maven build:

```bash
mvn spotless:apply  # Auto-format Java sources (Google Java Format AOSP style)
mvn verify          # Format check, Checkstyle, Java version enforcement, unit tests, JaCoCo reports
```

JaCoCo coverage reports are generated under each service's `target/site/jacoco` directory.
