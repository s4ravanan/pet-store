# Pet Store Microservices

A Java/Spring Boot monorepo inspired by the [Azure AKS Store Demo](https://github.com/Azure-Samples/aks-store-demo). It demonstrates a storefront, product catalog, order API, asynchronous order processing, health probes, container images, and Kubernetes deployment.

## Services

| Service | Port | Responsibility |
|---|---:|---|
| `store-front` | 8080 | Customer UI and API gateway-like calls |
| `product-service` | 8081 | Product catalog |
| `order-service` | 8082 | Accepts orders and publishes them to RabbitMQ |
| `makeline-service` | 8083 | Consumes orders and tracks fulfillment |
| RabbitMQ | 5672 / 15672 | Order event broker / management UI |

## Run locally

```bash
docker compose up --build
```

Open http://localhost:8080. The APIs are available at `/api/products`, `/api/orders`, and `/api/makeline/orders`.

## Deploy to Kubernetes

```bash
kubectl create namespace pets
kubectl apply -k deploy/kubernetes/overlays/dev
kubectl -n pets port-forward service/store-front 8080:80
```

The manifests use local image names (`pet-store/<service>:dev`); build and load them into your cluster before applying, or update the image names in the overlay.

## Quality checks

Formatting and checks are centralized in the root Maven build:

```bash
mvn spotless:apply  # format Java sources
mvn verify          # format check, Checkstyle, Java version enforcement, tests, JaCoCo report
```

Spotless uses Google Java Format in AOSP style. Checkstyle runs for main and test sources, and JaCoCo writes reports under each service's `target/site/jacoco` directory.
