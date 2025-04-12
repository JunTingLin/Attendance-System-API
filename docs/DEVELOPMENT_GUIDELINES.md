## 🚩 開發注意事項與規範

### 🔧 Controller 與異常處理
- **Controller 盡量單純化**，只負責處理路由與最基本的轉交動作，不應包含過多邏輯處理。
- 不需在 Controller 中進行 `try-catch` 處理異常，可以透過自訂 Exception 並在 Service 層拋出，交由全域異常處理器集中處理。
- Security 驗證相關的錯誤（如未帶 JWT Token 或驗證失敗），皆已在 `SecurityFilterChain` 中透過過濾器處理完成，Controller 無需再次檢查這類情況。

### 📑 DTO 的使用原則
- DTO 的轉換邏輯應放置於 **Service** 層進行處理，而非 Controller 中。
- 透過註解 `@Valid` 搭配 DTO 中的 `Jakarta Bean Validation (@NotNull, @Size...)`，即可快速實現表單基本欄位驗證。

### 💡 共用回傳格式（ApiResponse）
- 為 API 設計了一個統一回應結構的泛型工具類：`ApiResponse<T>`，可用於包裝 API 回傳結果，包含提示訊息與實際資料。
- 在撰寫 Controller 時可統一使用此格式。

```
{
  "code": 401,
  "message": "訊息文字",
  "data": {...返回資料物件...}
}

```

### 📌 依賴管理與程式碼撰寫風格
- 實體物件的建立盡量交由 **Spring Container** 管理，一般情形下**避免自行實例化 (`new` 物件)**。
- 優先使用 Lombok 的注解 (如：`@RequiredArgsConstructor`, `@Data`, `@Getter`, `@Setter` 等)，減少過於冗餘的樣板程式碼。
- 需要紀錄日誌或進行 Debug 時，可使用 Lombok 提供的 `@Slf4j` 註解，直接使用 `log.info()` 或 `log.error()` 等函式。
```
@Slf4j
@Service
public class ExampleService {

    public void exampleMethod() {
        log.info("這是一個使用 @Slf4j 印出的 log 訊息");
    }

}
```

