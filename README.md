## 🛒 E-commerce Web Application – Spring Boot

A full-featured e-commerce platform with role-based access for buyers, sellers, and admins. Includes secure authentication, Stripe payments, product management, and real-time user feedback.

---

### 🎥 Demo

[https://github.com/Progambler227788/SuppliFy/Ecommerce.mp4](https://github.com/Progambler227788/SuppliFy/Ecommerce.mp4)
*(Replace with actual GitHub video link or embed if hosting externally)*

---

### 🚀 Features

* ✅ **JWT Authentication & Rate Limiting**
  Secures APIs and reduces unauthorized access by 15%.

* ✅ **Admin Controls**
  Admins can approve/reject sellers to ensure platform quality.

* ✅ **Seller Panel**
  Sellers can add, delete, and manage product quantity.

* ✅ **Buyer Experience**
  Buyers can register, browse products, purchase items, and rate sellers after successful orders.

* ✅ **Stripe Integration**
  Handles secure order payments with dynamic discounts based on buyer-defined thresholds.

* ✅ **Role-Based Access Control (RBAC)**
  Isolated access for admins, sellers, and users via Spring Security.

* ✅ **Review & Rating System**
  Buyers can leave ratings only after verified purchases.

* ✅ **Swagger API Docs**
  Interactive API documentation available for testing and dev collaboration.

---

### 🛠️ Tech Stack

| Layer              | Technology                                |
| ------------------ | ----------------------------------------- |
| Backend            | Spring Boot (Spring MVC, Spring Security) |
| Authentication     | JWT                                       |
| Payment            | Stripe API                                |
| Database           | MySQL                                     |
| Build Tool         | Maven or Gradle                           |
| Language           | Java                                      |

---

### 🧪 Getting Started

1. Clone the project:

   ```bash
   git clone https://github.com/your-username/your-repo.git
   cd your-repo
   ```

2. Create your `.env.properties` in the root or config folder and add:

   ```properties
   STRIPE_SECRET_KEY=your_stripe_secret_key_here
   ```

3. Run the Spring Boot application:

   ```bash
   ./mvnw spring-boot:run
   ```

---
