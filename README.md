# 🏥 MediConnect

> A full-stack web application that bridges the gap between doctors and patients — enabling seamless appointment booking, prescription management, and patient history tracking.

🔗 **Live Demo:** [medi-connect-complete.vercel.app](https://medi-connect-complete.vercel.app)
📦 **Backend API:** [mediconnectcomplete-production.up.railway.app](https://mediconnectcomplete-production.up.railway.app/swagger-ui.html)

---

## 🚀 Features

### 👨‍⚕️ Doctor
- Register and manage profile (specialization, qualification, experience)
- Set and manage availability slots
- View and manage patient appointments
- Generate prescriptions for patients
- Track complete patient history

### 🧑‍💼 Patient
- Register and manage personal health profile
- Browse and book appointments with doctors
- View prescriptions issued by doctors
- Track appointment history and consultations

### 🔐 Authentication & Security
- JWT-based authentication
- Role-based access control (Doctor / Patient)
- Secure password encryption with BCrypt
- Stateless session management

### 🔔 Notifications
- Real-time unread notification count
- Mark notifications as read
- In-app notification management

---

## 🛠️ Tech Stack

### Frontend
| Technology | Purpose |
|-----------|---------|
| Angular 17 | SPA Framework |
| TypeScript | Language |
| CSS | Styling |

### Backend
| Technology | Purpose |
|-----------|---------|
| Java Spring Boot | REST API |
| Spring Security | Authentication & Authorization |
| JWT | Token-based Auth |
| Spring Data JPA | ORM |
| Hibernate | Database Mapping |
| MySQL | Database |
| Swagger / OpenAPI | API Documentation |

### Deployment
| Service | Purpose |
|---------|---------|
| Railway | Backend + MySQL hosting |
| Vercel | Frontend hosting |
| GitHub | Version control |

---

## 📁 Project Structure

```
MediConnect_Complete/
├── MediConnect-Backend/        # Spring Boot REST API
│   ├── src/main/java/
│   │   └── com/example/MediConnect_Backend/
│   │       ├── config/         # Security & CORS config
│   │       ├── controller/     # REST controllers
│   │       ├── model/          # JPA entities
│   │       ├── repository/     # Data repositories
│   │       ├── service/        # Business logic
│   │       └── security/       # JWT filter & utils
│   └── src/main/resources/
│       └── application.properties
│
└── MediConnect-Frontend/       # Angular SPA
    └── src/
        ├── app/
        │   ├── components/     # UI components
        │   ├── services/       # API services
        │   └── models/         # TypeScript models
        └── environments/       # Environment configs
```

---

## ⚙️ Local Setup

### Prerequisites
- Java 21+
- Node.js 18+
- MySQL 8+
- Angular CLI

### Backend

```bash
# Clone the repo
git clone https://github.com/mirfaizanali/MediConnect_Complete.git
cd MediConnect_Complete/MediConnect-Backend

# Set environment variables (or update application.properties)
DB_URL=jdbc:mysql://localhost:3306/mediconnect
DB_USERNAME=root
DB_PASSWORD=yourpassword

# Run the application
./mvnw spring-boot:run
```

API will be available at `http://localhost:8080`
Swagger UI at `http://localhost:8080/swagger-ui.html`

### Frontend

```bash
cd MediConnect_Complete/MediConnect-Frontend

# Install dependencies
npm install

# Run development server
ng serve
```

App will be available at `http://localhost:4200`

---

## 🌐 API Documentation

Full API documentation available via Swagger UI:
👉 [mediconnectcomplete-production.up.railway.app/swagger-ui.html](https://mediconnectcomplete-production.up.railway.app/swagger-ui.html)

Key endpoints:
```
POST   /api/auth/login
POST   /api/auth/register-doctor
POST   /api/auth/register-patient
GET    /api/doctors/all
GET    /api/doctors/top-rated
POST   /api/appointments/book
GET    /api/patient-appointments
GET    /api/notifications
PATCH  /api/notifications/{id}/read
```

---

## 🚢 Deployment

- **Backend** deployed on [Railway](https://railway.app) with MySQL database
- **Frontend** deployed on [Vercel](https://vercel.com)
- Auto-deploys on every push to `main` branch

---

## 👨‍💻 Author

**Mir Faizan Ali**
- GitHub: [@mirfaizanali](https://github.com/mirfaizanali)

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
