\# Enterprise Fraud Detection Portal



A banking-grade web application for transaction monitoring and fraud detection built with Spring Boot and PostgreSQL.



\## Features



\- \*\*User Authentication\*\* — Registration with admin approval workflow

\- \*\*Role Based Access\*\* — Admin, Customer, Fraud Analyst roles

\- \*\*Transaction Engine\*\* — Customers submit money transfers

\- \*\*Fraud Rule Engine\*\* — 6 automatic fraud detection rules

\- \*\*Risk Scoring\*\* — Weighted scoring system (0-100+)

\- \*\*Admin Dashboard\*\* — Live counts, review queue, approve/block transactions

\- \*\*Registration Validation\*\* — Email format, password strength, confirm password



\## Fraud Detection Rules



| Rule | Trigger | Points |

|------|---------|--------|

| Amount Band | Amount >= Rs.50,000 | +20 |

| Critical Amount | Amount >= Rs.2,00,000 | +40 |

| Velocity Check | 5+ transactions in 60 min | +25 |

| Duplicate Detection | Same account + same amount | +20 |

| Daily Limit | Today total > Rs.5,00,000 | +30 |

| Historical Anomaly | Amount > average x 5 | +20 |

| Blacklist Check | Blocked receiver account | +50 |



\## Risk Score Decision



| Score | Decision |

|-------|----------|

| 0-30 | APPROVED |

| 31-60 | FLAGGED |

| 61-80 | UNDER REVIEW |

| 81+ | BLOCKED |



\## Tech Stack



| Layer | Technology |

|-------|-----------|

| Backend | Java 17, Spring Boot 3.5 |

| Security | Spring Security, BCrypt |

| Database | PostgreSQL 17 |

| ORM | Spring Data JPA, Hibernate |

| Frontend | Thymeleaf, Bootstrap 5 |

| Build | Maven |



\## Project Structure
src/main/java/com/banking/fraud\_detection/

├── config/          # Spring Security configuration

├── controller/      # Web controllers

├── service/         # Business logic

├── repository/      # Database access

├── entity/          # Database entities

└── fraud/           # Fraud rule engine

\## Setup Instructions



\### Prerequisites

\- Java 17

\- PostgreSQL 17

\- Maven



\### Steps



1\. Clone the repository
git clone https://github.com/Manasagowd2002/enterprise-fraud-detection.git

2\. Create PostgreSQL database

```sql

CREATE DATABASE fraudportal;

```



3\. Copy application-example.properties to application.properties
cp src/main/resources/application-example.properties src/main/resources/application.properties


4. Update application.properties with your credentials



5\. Run the application
6. Open browser
http://localhost:8080
## Default Admin Account



After setup insert admin user in database:

```sql

INSERT INTO users (full\_name, email, password, phone, role, status, created\_at)

VALUES ('Admin User', 'admin@fraudportal.com',

'$2a$10$5Og2NFJ6mOqLyknRrqEaM.ukcLGdQaovhBJeMh1HFMkxMMwsGRu6u',

'9999999999', 'ADMIN', 'ACTIVE', NOW());

```

Password: admin123



\## Developer



\*\*Manas\*\* — MCA Final Year Project

