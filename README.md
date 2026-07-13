# 📄 DocuMind AI

An AI-powered document Q&A system built with Java Spring Boot and React. Users can upload PDF documents and ask questions in natural language — the system retrieves relevant content and generates accurate answers using Groq's LLaMA model.

**Live Demo:** [Coming Soon — Deployment in progress]  
**Tech Stack:** Java 21 • Spring Boot • React • PostgreSQL • Groq AI • Docker • JWT

---

## 🚀 Features

- **JWT Authentication** — Secure multi-user system with register/login
- **PDF Upload & Processing** — Extract and chunk text using Apache PDFBox
- **RAG Pipeline** — Keyword-based retrieval + Groq LLaMA for accurate answers
- **Multi-user Isolation** — Each user's documents are private and secure
- **React Frontend** — Clean chat interface for document Q&A
- **Dockerized** — PostgreSQL runs via Docker Compose

---

## 🏗️ Architecture

React Frontend (localhost:3000)
↓ REST API (Axios + JWT)
Spring Boot Backend (localhost:8080)
↓
┌────────────────────────────┐
│  JWT Filter (Auth)         │
│  Document Service (PDFBox) │
│  RAG Service (Groq AI)     │
└────────────────────────────┘
↓
PostgreSQL (Docker)

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot 3.5, Maven |
| Security | Spring Security, JWT (jjwt 0.12.3) |
| Database | PostgreSQL 16 (Docker) |
| AI | Groq API (LLaMA 3.3-70b) |
| PDF Processing | Apache PDFBox 3.0 |
| Frontend | React 19, Axios, React Router |
| DevOps | Docker, Docker Compose |

---

## ⚙️ How RAG Works

User uploads PDF
↓
PDFBox extracts text
↓
Text split into 500-word chunks
↓
Chunks saved to PostgreSQL
↓
User asks question
↓
Keyword search finds relevant chunks
↓
Chunks + Question → Groq LLaMA
↓
AI generates accurate answer

---

## 🚦 Getting Started

### Prerequisites
- Java 21
- Node.js 18+
- Docker Desktop
- Groq API Key (free at console.groq.com)

### Backend Setup

```bash
# 1. Clone the repo
git clone https://github.com/sikharsethi/documind-ai.git
cd documind-ai

# 2. Start PostgreSQL
docker-compose up -d

# 3. Configure environment
cp backend/src/main/resources/application.example.properties \
   backend/src/main/resources/application.properties
# Fill in your Groq API key

# 4. Run Spring Boot
cd backend
./mvnw spring-boot:run
```

### Frontend Setup

```bash
cd frontend
npm install
npm start
```

Open `http://localhost:3000` — register, upload a PDF, and start asking questions!

---

## 📡 API Endpoints

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| POST | `/api/auth/register` | Register new user | ❌ |
| POST | `/api/auth/login` | Login, get JWT token | ❌ |
| POST | `/api/documents/upload` | Upload PDF | ✅ |
| GET | `/api/documents` | Get user's documents | ✅ |
| POST | `/api/rag/ask` | Ask question about document | ✅ |

---

## 🔐 Security Design

- Passwords hashed with **BCrypt** — never stored as plain text
- **JWT tokens** expire after 24 hours
- All document endpoints protected — users can only access their own documents
- API keys stored in environment variables — never committed to Git

---

## 🤔 Technical Decisions & Learnings

**Why keyword search instead of vector embeddings?**  
I initially implemented pgvector for semantic search, but Groq's embedding model wasn't available on the free tier. I made a pragmatic decision to use keyword-based retrieval — it works well for direct queries and keeps the system simple. The architecture is designed so vector embeddings can be plugged in easily in the future.

**Why JWT over sessions?**  
JWT is stateless — the server doesn't need to store session data. This makes the system horizontally scalable — any server instance can verify any token without shared state.

**Why chunk size of 500 words?**  
Large enough to contain meaningful context, small enough to stay within LLM token limits. Each chunk can be independently retrieved and fed to the AI.

---

## 📁 Project Structure

documind-ai/
├── backend/                    # Spring Boot application
│   └── src/main/java/com/sikhar/documindbackend/
│       ├── controller/         # REST endpoints
│       ├── service/            # Business logic
│       ├── model/              # JPA entities
│       ├── repository/         # Database queries
│       ├── security/           # JWT + Spring Security
│       └── dto/                # Data transfer objects
├── frontend/                   # React application
│   └── src/
│       ├── pages/              # Login, Dashboard, Chat
│       └── api/                # Axios configuration
└── docker-compose.yml          # PostgreSQL setup

---

## 👨‍💻 Author

**Sikhar Sethi**  
[GitHub](https://github.com/sikharsethi)