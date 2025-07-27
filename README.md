# 🧠 Smart Reminder Service

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/)
[![AWS Lambda](https://img.shields.io/badge/AWS%20Lambda-Serverless-yellow?logo=aws-lambda)](https://aws.amazon.com/lambda/)
[![API Gateway](https://img.shields.io/badge/API%20Gateway-REST-orange?logo=amazon-aws)](https://aws.amazon.com/api-gateway/)
[![S3](https://img.shields.io/badge/S3-Storage-569A31?logo=amazon-aws&logoColor=white)](https://aws.amazon.com/s3/)
[![DynamoDB](https://img.shields.io/badge/DynamoDB-NoSQL-blue?logo=amazon-dynamodb)](https://aws.amazon.com/dynamodb/)
[![Amazon EventBridge](https://img.shields.io/badge/EventBridge-Scheduler-brown?logo=amazon-aws)](https://aws.amazon.com/eventbridge/)
[![Amazon SES](https://img.shields.io/badge/Amazon%20SES-Email-lightgrey?logo=amazon-aws)](https://aws.amazon.com/ses/)
[![AWS SAM](https://img.shields.io/badge/SAM-IaC-FF9900?logo=aws)](https://aws.amazon.com/serverless/sam/)

> It's easy to forget the things we want to do for ourselves, especially in the middle of a busy week.  
> This project offers a simple but powerful solution: users can schedule daily or weekly email reminders with motivational quotes to stay on track.

---

## 📚 Table of Contents

- [Tech Stack](#-tech-stack)
- [Features](#-features)
- [Architecture Diagram](#-architecture-diagram)
- [How It Works](#-how-it-works)
- [Live Demo](#-live-demo)
- [Deployment (via SAM)](#-deployment-via-sam)
- [Screenshots](#-screenshots)
- [Possible Improvements](#-possible-improvements)
- [Contact](#-contact)

---

## 📌 Tech Stack

- **Language:** Java 17
- **Build Tool:** Maven
- **Frontend:** HTML + Tailwind
- **Cloud Services:**
    - **AWS Lambda** – for serverless compute
    - **Amazon API Gateway** – REST API endpoints
    - **Amazon S3** – Static website
    - **Amazon DynamoDB** – user storage and reminder setup info
    - **Amazon SES** – sends reminder emails
    - **AWS SAM** – infrastructure as code (IaC)

---

## ✨ Features

- ✅ Set reminders by email, message, time, and day(s)
- 📧 Receive email reminders with quote of the day
- 🕗 Scheduled execution using EventBridge (cron jobs)
- 🗃️ User preferences stored securely in DynamoDB
- 🌐 Static frontend served via S3

---

## 📐 Architecture Diagram

![Architecture Diagram](assets/architecture.png) <!-- Will be added -->

---

## 📖 How It Works

1. User opens the S3-hosted website and enters:
    - Email address
    - Reminder message
    - Time (UTC)
    - Days of the week

2. The form sends a POST request to an API Gateway endpoint which triggers a Lambda function that saves data to DynamoDB.

3. A second Lambda is triggered daily by EventBridge at 8:00 AM UTC(you can specify the time based on your needs). It:
    - Scans DynamoDB
    - Matches users whose reminder time + day match current UTC time
    - Fetches a quote of the day
    - Sends a personalized email via SES

---

## 🧪 Live Demo

![Live Demo](assets/live-demo.gif)

---

## 🚀 Deployment (via SAM)

> ⚙️ **Pre-requisites**:  
> Make sure you have the **AWS CLI** and **AWS SAM CLI** installed and configured locally with appropriate credentials.

```bash
# 1. Build
sam build

# 2. Deploy
sam deploy --guided

```

---
## 📖 Screenshots

### ✅ Lambda - ReminderSetupFunction
![Lambda SignupFunction](assets/remindersetupfunction.PNG)

### 📦 Lambda - SendReminderFunction
![Lambda - VerifyFunction](assets/sendreminderfunction.PNG)

### 🔔 S3 - Static Website
![S3 - Static Website](assets/aws-s3.PNG)

---

## 🧭 Possible Improvements

- Cognito login + user dashboard
- View, edit, and delete existing reminders
- Quote style selector (funny, deep, business)
- Styled unsubscribe footer and branding

---

## 📬 Contact

Built by **Kamran Zeynalov**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-blue?logo=linkedin&style=flat-square)](https://www.linkedin.com/in/zeynalov-kamran/)