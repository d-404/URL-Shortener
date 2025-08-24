# 🚀 URL Shortener (Spring Boot, Java)

A scalable **URL shortener service** built with **Spring Boot**, featuring Redis caching, PostgreSQL persistence, rate limiting, and observability via Prometheus/Grafana.  

---

## ✨ Features

- Create short URLs with optional **custom alias**
- URL expiry support (`ttlSeconds`)
- Fast redirects (`p99 < 10ms` via Redis cache)
- Rate limiting with **Bucket4j**
- Metrics, logs, and health endpoints (**Prometheus + Grafana**)
- Clean DTO-based API contracts
- Structured JSON logs for observability

---

## 🏗️ High-Level Architecture

- **POST /api/v1/urls**
  - Validate → Generate ID → Base62 encode → Save in Postgres → Prime Redis → Return short URL  
- **GET /{code}**
  - Lookup in Redis → Fallback to Postgres if not found → Backfill cache → Redirect (302)  

---

## 📡 API Endpoints

### Create Short URL
```http
POST /api/v1/urls
Content-Type: application/json

{
  "url": "https://www.google.com",
  "customAlias": "opt",
  "ttlSeconds": 86400
}

Response
{
  "shortUrl": "http://localhost:8080/abc123",
  "code": "abc123",
  "expiresAt": "2025-08-25T12:00:00Z"
}
