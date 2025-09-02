# Attendance-System-API

本專案為使用 **Spring Boot**、**Spring Security** 和 **Spring Data JPA** 等框架架構的請假系統後端 API。此專案主要提供使用者身份驗證、授權功能以及用戶資料管理的 RESTful API 服務。此外還整合了 Swagger 提供互動式 API 文件。

**前端專案、資料庫設計、雲基礎設施 請參見**:

+ [Attendance-System-frontend](https://github.com/zzronggg/Attendance-System-frontend/tree/main)

+ [Attendance-System-db](https://github.com/JunTingLin/Attendance-System-db)

+ [Attendance-System-Infra](https://github.com/JunTingLin/Attendance-System-Infra)

---

## 🚀 專案環境與前置需求

### Java 版本：
- JDK 21

### MySQL 資料庫配置：
本專案使用 MySQL 作為主資料庫，並已經另外建立一個包含資料庫設定檔的 repo（內有 `docker-compose.yml`）：

📌 [JunTingLin/Attendance-System-db](https://github.com/JunTingLin/Attendance-System-db)

請先至上述 repo 根據說明啟動資料庫環境後，再於本專案設定的資料庫連線設定：

---

## 🛠️ 開發環境 (Dev)

### 方法一：IDE 執行 (後端開發者)
> 📌 須先自行裝對應的JDK版本
1. 將 `dev.env` 檔案匯入 IntelliJ IDEA 的 Run/Debug Configuration → Environment variables

>   📌 設定方式可參考 [Discussion #9](https://github.com/JunTingLin/Attendance-System-API/discussions/9) 的錄製影片

2. `dev.env`：
```
# dev
SPRING_PROFILES_ACTIVE=dev

DB_HOST=127.0.0.1
DB_PORT=3307
DB_NAME=Attendance_System
DB_USER=user
DB_PASS=user123

JWT_SECRET=TRfOM+M50a6zz78EzdabF3+nQfBvI7xjZE4Xx3ERFUy40/jQYG2IfKz93hiPKmSyfaaOoUbhBEB1pz7yuYby7A==

UPLOAD_DIR=C:/Users/junting/Desktop/Attendance-System-upload

SERVER_PORT=8080

TELEGRAM_BOT_TOKEN=temp_placeholder_token_1234567890
```

3. 啟動應用：
直接啟動主程式入口[AttendanceSystemApiApplication](src/main/java/com/tsmc/cloudnative/attendancesystemapi/AttendanceSystemApiApplication.java)

### 方法二：Docker 執行 (推薦)
1. 從 Docker Hub 拉取映像：
```
docker pull juntinglin/attendance-app:latest
```

2. 使用 `dev.env` 參數檔啟動容器：
```
docker run \
  --rm \
  -e SPRING_PROFILES_ACTIVE="dev" \
  -e SERVER_PORT="8080" \
  -e SPRING_CLOUD_GCP_SQL_ENABLED=false \
  -e DB_HOST="host.docker.internal" \
  -e DB_PORT="3307" \
  -e DB_NAME="Attendance_System" \
  -e DB_USER="root" \
  -e DB_PASS="root123" \
  -e JWT_SECRET="TRfOM+M50a6zz78EzdabF3+nQfBvI7xjZE4Xx3ERFUy40/jQYG2IfKz93hiPKmSyfaaOoUbhBEB1pz7yuYby7A==" \
  -e TELEGRAM_BOT_TOKEN="YOUR_TELEGRAM_BOT_TOKEN" \
  -e UPLOAD_DIR="/app/upload" \
  -v "C:/Users/junting/Desktop/Attendance-System-upload:/app/upload" \
  -p 8080:8080 \
  juntinglin/attendance-app:latest
```
💡 補充：
1. 以下 `\` 為 bash 的續行符號，Windows 使用者請使用 Git Bash 或 WSL 執行，或改寫成單行命令。
2. `--rm` 表示容器在結束後自動移除，可視需求選擇是否保留。
3. 請將 `YOUR_TELEGRAM_BOT_TOKEN` 替換為實際的 `TELEGRAM_BOT_TOKEN` 值。
4. 檔案上傳路徑`UPLOAD_DIR`，請搭配 -v 掛載對應目錄
   +  容器內部可設定為 `/app/upload`
   + 本機目錄依使用者作業系統而異
+ Windows 範例：
```
-e UPLOAD_DIR="/app/upload" \
-v "C:/Users/你的名稱/Desktop/Attendance-System-upload:/app/upload" \
```
+ macOS/Linux 範例：
```
-e UPLOAD_DIR="/app/upload" \
-v "$HOME/Attendance-System-upload:/app/upload" \
```
## 🌐 正式環境 (GCP Cloud Storage + Cloud Run + Cloud SQL)
環境變數：
```
# Prod
SPRING_PROFILES_ACTIVE=prod

CLOUD_SQL_INSTANCE=tsmc-attendance-system:asia-east1:attendace-system-mysql
DB_NAME=Attendance_System
DB_USER=YOUR_DB_USER
DB_PASS=YOUR_DB_PASS

JWT_SECRET=YOUR_JWT_SECRET

GCS_BUCKET_NAME=attendance-system-files

SERVER_PORT=8080

TELEGRAM_BOT_TOKEN=YOUR_TELEGRAM_BOT_TOKEN

```
正式環境的具體部署細節將於未來說明。

---
##  測試
本專案採用多層次測試，依需求選擇適合的測試範圍：

### 1. 單元測試 (Unit Tests)
+ 目的：測試單一類別或方法的邏輯，完全不啟動 Spring Context；所有外部依賴以 Mockito Stub。
+ 涵蓋範圍：因為 Controller 的主要任務是接收請求並轉發至 Service 層，由 Service 層負責主要業務邏輯，因此目前主要針對 service 層進行測試， 
+ 範例：
  +  `EmployeeServiceTest.java`
  + `LeaveApplicationServiceTest.java`
  + `LeaveApplicationControllerTest.java`

### 2. Repository Slice 測試 (@DataJpaTest)
+ 目的：啟動 Spring Data JPA slice，使用 H2 in-memory database 測試 Repository CRUD 與 自訂Query 方法。
+ 配置：
```
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
```
+ 範例:
`EmployeeRepositoryTest.java`

### 整合測試 (Integration / Smoke Test)
+ 目的：啟動完整 Spring Boot AppContext，包含 Security、Controller、Service、Repository，確保應用能正常啟動與整體流程通暢。
+ 配置：
```
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
```
+ 範例:
  `AttendanceSystemApiApplicationTests.java`

 
#### 指令
```
mvn test -Dskip.integration
```

測試環境設定檔：[application-test.properties](src/test/resources/application-test.properties)

## 查看測試覆蓋率（Code Coverage）
+ 使用 JaCoCo 工具產生測試覆蓋率報告。

+ 執行下列 Maven 指令即可重新產生報告：
```
mvn clean test jacoco:report
```
+ 報告會產生在以下路徑，打開 HTML 查看：
```
target/site/jacoco/index.html
```
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

## 📖 開發規範建議
 [開發注意事項與規範](docs/DEVELOPMENT_GUIDELINES.md)
