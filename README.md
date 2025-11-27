# Order Inventory Microservices Assignment

In this package there are 2 repositories present:
---

## Inventory Service

This service contains all the product details along with batches.  
Run this service first. It inserts few records after successfully launched.

Run the application from this class: `InventoryServiceApplication`

It exposes business logic using two URIs:

---

### 1. Get Inventory Details
**Endpoint:** `GET /inventory/{productId}`  
**Example URI:** `http://localhost:8081/inventory/1`

**Responses:**

- **200 OK**
<pre>
{
  "productId": 1,
  "sku": "SKU-APPLE",
  "productName": "Apple",
  "batches": [
    { "batchCode": "A1", "quantity": 50, "expiryDate": "2025-12-01" },
    { "batchCode": "A2", "quantity": 30, "expiryDate": "2025-12-15" }
  ]
}</pre>

- **404 NOT FOUND**
<pre>
{
  "code": "NOT_FOUND",
  "message": "Product not found: 99",
  "timestamp": "2025-11-22T15:10:00Z",
  "path": "/inventory/99"
}
</pre>

### 2. POST Inventory Details

**Endpoint:** `POST /inventory/update`  
**Example URI:** `http://localhost:8081/inventory/update`

**Request:**
<pre>
{ "sku": "SKU-APPLE", "quantity": 60 }
</pre>
**Responses:**

- **204 No Content**

- **400 Validation error**
<pre>
{
  "code": "VALIDATION_ERROR",
  "message": "Insufficient stock for sku: SKU-APPLE",
  "timestamp": "2025-11-22T15:11:00Z",
  "path": "/inventory/update"
}

</pre>

- **400 Validation error**
<pre>
{
  "code": "VALIDATION_ERROR",
  "message": "Quantity must be positive",
  "timestamp": "2025-11-22T15:11:30Z",
  "path": "/inventory/update"
}
</pre>

- **404 Not Found**
<pre>
{
  "code": "NOT_FOUND",
  "message": "Product not found: SKU-UNKNOWN",
  "timestamp": "2025-11-22T15:12:00Z",
  "path": "/inventory/update"
}
</pre>

---

## Order Service

This service post the order details towards the Inventory service.
It takes all the required order, based on the details existed in DB the details will modify through Inventory service.

After launched the Inventory service successfully launch the Order service.


Run the service from the `InventoryServiceApplication.class`

It exposes business logic using below URI:

---

### POST Order Details

**Endpoint:** `POST /order`  
**Example URI:** `http://localhost:8082/order`

**Request:**
<pre>
{
  "orderNumber": "ORD-1001",
  "items": [
    { "sku": "SKU-APPLE", "quantity": 20 },
    { "sku": "SKU-BANANA", "quantity": 50 }
  ]
}

</pre>
**Responses:**

- **201 Created**
<pre>
{
  "orderId": 1,
  "orderNumber": "ORD-1001",
  "status": "CREATED"
}
</pre>

- **Validation error when duplicate Order Number**
<pre>
{
  "code": "VALIDATION_ERROR",
  "message": "Order already exists: ORD-1001",
  "timestamp": "2025-11-22T15:13:00Z",
  "path": "/order"
}
</pre>

- **Validation error propagated from inventory (e.g., insufficient stock)**
<pre>
{
  "code": "VALIDATION_ERROR",
  "message": "Inventory update failed for SKU-BANANA: {\"code\":\"VALIDATION_ERROR\",\"message\":\"Insufficient stock for sku: SKU-BANANA\",\"timestamp\":\"2025-11-22T15:12:10Z\",\"path\":\"/inventory/update\"}",
  "timestamp": "2025-11-22T15:12:12Z",
  "path": "/order"
}
</pre>

---
### Run and verify locally
- Start Inventory Service on port 8081. Confirm H2 console at http://localhost:8081/h2-console. Verify seeded products exist.
- Call GET /inventory/{productId} for products and check sorted batches by expiry.
- Start Order Service on port 8082. Set inventory.base-url in application.yml as http://localhost:8081.
- Place an order via POST /order with items referencing SKUs from the seed file.
- Re-check Inventory to confirm quantities deducted across batches FIFO.

---
### Attached the postman collection in the same repo with name `Koerber.postman_collection`.
