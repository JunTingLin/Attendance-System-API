# Attendance-System-API

本專案為使用 **Spring Boot**、**Spring Security** 和 **Spring Data JPA** 等框架架構的請假系統後端 API。此專案主要提供使用者身份驗證、授權功能以及用戶資料管理的 RESTful API 服務。此外還整合了 Swagger 提供互動式 API 文件。

---

## 🚀 專案環境與前置需求

### Java 版本：
- JDK 21

### MySQL 資料庫配置：
本專案使用 MySQL 作為主資料庫，並已經另外建立一個包含資料庫設定檔的 repo（內有 `docker-compose.yml`）：

📌 [JunTingLin/Attendance-System-db](https://github.com/JunTingLin/Attendance-System-db)

請先至上述 repo 根據說明啟動資料庫環境後，再於本專案設定的資料庫連線設定：

 📌 編輯檔案 [application.properties](src/main/resources/application.properties)：

```properties
spring.datasource.url=jdbc:mysql://127.0.0.1:3307/Attendance_System?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Taipei
spring.datasource.username=user
spring.datasource.password=user123
```

---

## 🛠️ 專案啟動步驟

1. **從原始碼編譯並啟動：**

```bash
mvn clean install
mvn spring-boot:run
```

2. **透過 IDE 執行**：

如使用 Intellij IDEA 或 Eclipse，直接啟動主程式入口[AttendanceSystemApiApplication](./src/main/java/com/tsmc/cloudnative/attendancesystemapi/AttendanceSystemApiApplication.java)

---

## 🔗 API 文件與測試（Swagger）

啟動專案後透過瀏覽器訪問 Swagger UI 即可進行 API 測試及查看：

🌐 [Swagger UI (/swagger-ui/index.html)](http://localhost:8080/swagger-ui/index.html)

API 文件 JSON 格式：

🌐 [API JSON 文件 (/v3/api-docs)](http://localhost:8080/v3/api-docs)

---

## 📚 主要依賴

| 依賴名稱                              | 說明                                                          |
|-----------------------------------|-------------------------------------------------------------|
| `spring-boot-starter-web`         | RESTful Web API 開發及 HTTP 請求處理支援                             |
| `spring-boot-starter-data-jpa`    | 資料庫存取，由 JPA 提供 ORM 實作                               |
| `spring-boot-starter-security`    | 提供用戶身份驗證及授權相關功能支援                                  |
| `spring-boot-starter-validation`  | API 參數驗證，如格式、限制等等                                      |
| `mysql-connector-j`               | MySQL 資料庫連接 driver                                         |
| `jjwt-api`, `jjwt-impl`, `jjwt-jackson` | JWT Token 生產與驗證，用於 API 認證                               |
| `lombok`                          | 減少 Java 程式碼中的樣板程式碼                                     |
| `springdoc-openapi-starter-webmvc-ui` | 自動生成 Swagger API 文件及互動式 UI                               |
| `h2`                              | 提供單元測試與整合測試的內建記憶體資料庫                                 |

---
## ⚡ 部分 API 快速導覽

這裡列出其中兩個 API，用來快速了解此專案有哪些主要功能:

### 🔑 登入並取得 Token

**Request**

```http
POST /api/auth/login
Content-Type: application/json

{
  "employeeCode": "yourEmployeeCode",
  "password": "yourPassword"
}
```

### 🙍🏻 取得登入後使用者資訊

**Request**

```http
GET /api/employee
Authorization: Bearer <your_JWT_token>
```

---


## 📌 其他說明

如需更多詳細功能或 API 請參考 Swagger UI 文件，或訪問[Google Doc-Controller draft](https://docs.google.com/document/d/1ykyxhqslri5owPWKm_RMJchlDV6fuQn0eHwYQ1Lz95c/edit?tab=t.0#heading=h.2n97651aeg0t)。

