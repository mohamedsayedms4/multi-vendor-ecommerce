# 🛍️ E-Commerce Backend System

> A comprehensive, multi-vendor e-commerce platform built with **Spring Boot**, featuring role-based authentication, product management, order processing, and real-time notifications.

---

## 🚀 Features

### 🔐 Authentication & Authorization
- **JWT-based authentication** with secure token generation and validation
- **Role-based access control** (Admin, Seller, Customer)
- **Multiple login methods**: Email, Phone Number
- **Security measures**: Failed login attempt tracking with account lockout after 4 attempts
- **Password encryption** using BCrypt

### 👥 User Management
- User registration and profile management
- Address management system
- Profile image upload support
- Password change functionality
- Admin user management capabilities

### 🏪 Seller Management
- User-to-seller conversion process
- Business profile management (logo, banner, contact info)
- Seller verification system
- Account status management (Active, Banned, Pending, Closed)
- Multi-vendor support

### 📦 Product Management
- **Hierarchical category system** with parent-child relationships
- Product CRUD operations with image support
- Product verification system (admin-approved products only)
- Advanced product filtering and sorting
- Product review and rating system

### 🛒 Cart & Order System
- **Guest cart functionality** with cookie-based temporary IDs
- Cart merging when users log in
- **Multi-seller order splitting** (orders are grouped by seller)
- Order status tracking (Pending, Confirmed, Shipped, Delivered, Cancelled)
- Order history for users and sellers

### 💳 Payment Integration
- **Stripe payment gateway** integration
- Payment intent and charge management
- Multi-vendor payment routing support
- Payment status tracking

### 📊 Admin Dashboard
- Comprehensive user and seller management
- Product verification system
- Category management
- Real-time notifications system
- Sales and order monitoring

### 🔔 Real-time Features
- **WebSocket-based notifications**
- Real-time admin alerts for new sellers, products, and orders
- Email notifications for important events
- Live order status updates

---

## 🛠️ Technology Stack

| Component | Technology |
|-----------|------------|
| **Backend** | Spring Boot 3.x, Spring Security, Spring Data JPA |
| **Database** | MySQL/PostgreSQL (JPA with Hibernate) |
| **Authentication** | JWT with Spring Security |
| **File Storage** | Cloudinary integration for image management |
| **Real-time Communication** | WebSocket with STOMP protocol |
| **Email Service** | SMTP integration for notifications |
| **Payment Processing** | Stripe API integration |
| **Validation** | Bean Validation with custom constraints |
| **Internationalization** | Spring MessageSource with locale support |

---

## 📁 Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── org/example/ecommerce/
│   │       ├── application/          # Application services
│   │       ├── domain/               # Domain models and business logic
│   │       ├── infrastructure/       # Controllers, repositories, configuration
│   │       └── EcommerceApplication.java
│   └── resources/
│       ├── application.properties    # Application configuration
│       ├── messages.properties       # Internationalization messages
│       └── firebase-service-account.json # Firebase configuration (if used)
```

### 🎯 Key Packages

| Package | Purpose |
|---------|---------|
| `domain.model` | Entity classes (User, Product, Order, Category, etc.) |
| `application.service` | Business logic and service implementations |
| `infrastructure.controller` | REST API endpoints |
| `infrastructure.repository` | Data access layer |
| `infrastructure.config` | Security and application configuration |
| `infrastructure.dto` | Data Transfer Objects for API requests/responses |

---

## 🚀 API Endpoints

### 🔐 Authentication (`/api/v1/auth`)
- **`POST /sign-up`** - User registration with optional profile image
- **`POST /login-email`** - Login with email and password
- **`POST /login-phone`** - Login with phone number and password

### 👤 Users (`/api/v1/users`)
- **`POST /jwt`** - Get user profile by JWT token
- **`PATCH /update`** - Update user information
- **`PUT /update/image`** - Update profile image
- **`DELETE /del/image-profile`** - Delete profile image
- **`PUT /changePwd`** - Change password
- **`DELETE /del`** - Delete user account

### 📦 Products (`/api/v1/products`)
- **`POST /`** - Create new product *(Seller/Admin only)*
- **`GET /{id}`** - Get product by ID
- **`GET /`** - Get paginated products
- **`GET /category/{categoryId}`** - Get products by category
- **`GET /seller/{sellerId}`** - Get products by seller
- **`PUT /status`** - Update product verification status *(Admin only)*
- **`PATCH /`** - Update product information
- **`DELETE /{id}`** - Delete product

### 📂 Categories (`/api/v1/Categories`)
- **`POST /`** - Create category *(Admin only)*
- **`PATCH /`** - Update category *(Admin only)*
- **`DELETE /`** - Delete category *(Admin only)*
- **`GET /`** - Get all categories
- **`GET /{id}`** - Get category by ID
- **`GET /admin`** - Admin category management

### 🏪 Sellers (`/api/v1/sellers`)
- **`POST /`** - Become a seller (register business)
- **`POST /profile`** - Get seller profile
- **`PATCH /update`** - Update seller profile

### 👨‍💼 Admin (`/api/v1/admin`)
- **`GET /email/{email}`** - Get user by email
- **`GET /phone/{phone}`** - Get user by phone
- **`GET /id/{id}`** - Get user by ID
- **`GET /users`** - Get paginated users
- **`GET /sellers`** - Get paginated sellers
- **`PUT /verified`** - Verify seller email
- **`DELETE /del`** - Delete seller
- **`PUT /status`** - Update seller account status

### 🛒 Cart (`/api/v1/cart`)
- **`PUT /`** - Add item to cart
- **`GET /`** - Get cart contents

### 📋 Orders (`/api/v1/orders`)
- **`POST /create`** - Create order from cart

---

## 🔧 Configuration

### 🌍 Environment Variables

```properties
# JWT Configuration
JWT_SECRET=your-jwt-secret-key

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce
spring.datasource.username=username
spring.datasource.password=password

# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password

# Cloudinary Configuration
cloudinary.cloud-name=your-cloud-name
cloudinary.api-key=your-api-key
cloudinary.api-secret=your-api-secret

# Stripe Configuration
stripe.secret-key=your-stripe-secret-key
stripe.publishable-key=your-stripe-publishable-key
```

### 🔒 Security Configuration

- ✅ CSRF protection configured with cookie-based tokens
- ✅ CORS configured for cross-origin requests
- ✅ Role-based authorization with `@PreAuthorize` annotations
- ✅ JWT token validation filter
- ✅ Custom authentication provider

---

## 🎯 Key Features Implementation

### 🔑 JWT Authentication
- Tokens contain user ID, email, and authorities
- Automatic token generation upon successful login
- Token validation on each request
- Custom filters for token processing

### 🖼️ Image Management
- **Cloudinary integration** for image storage
- Support for multiple product images
- Profile image upload for users
- Business logo and banner for sellers

### 🔔 Real-time Notifications
- **WebSocket implementation** for live updates
- Email notifications for admin events
- Database persistence for notification history
- User-specific notification channels

### 💰 Payment Processing
- **Stripe PaymentIntent** creation
- Support for connected accounts (multi-vendor payments)
- Payment status tracking
- Webhook handling for payment events

---

## 📦 Database Schema

The system uses a relational database with entities including:

| Entity | Description |
|--------|-------------|
| **Users** | Customer/seller/admin roles |
| **Products** | Category relationships |
| **Orders** | OrderItems |
| **Categories** | Hierarchical structure |
| **Carts** | CartItems |
| **Addresses** | User addresses |
| **Notifications** | Real-time alerts |
| **Payments** | Payment information and orders |

---

## 🚀 Deployment

### 📋 Prerequisites
- ☑️ Java 17 or higher
- ☑️ MySQL/PostgreSQL database
- ☑️ Cloudinary account for image storage
- ☑️ Stripe account for payments
- ☑️ SMTP server for email notifications

### 🏗️ Build and Run

```bash
# Build the project
./mvnw clean package

# Run the application
java -jar target/ecommerce-0.0.1-SNAPSHOT.jar

# Or run with Maven
./mvnw spring-boot:run
```

### 🐳 Docker Deployment

```dockerfile
FROM openjdk:17-jdk-alpine
COPY target/ecommerce-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

---

## 🤝 Contributing

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add some amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

---

## 📄 License

This project is licensed under the **MIT License** - see the `LICENSE.md` file for details.

---

## 🆘 Support

For support, please **open an issue** in the GitHub repository or contact the development team.

---

## 🗺️ Future Enhancements

### 📱 Mobile & Frontend
- Mobile app with React Native
- Advanced analytics dashboard
- Social media integration

### 🛒 E-commerce Features
- Recommendation engine
- Wishlist functionality
- Coupon and discount system
- Inventory management
- Shipping integration

### 🌐 Platform Features
- Multi-language support
- Advanced search with filters
- Affiliate program support

---
