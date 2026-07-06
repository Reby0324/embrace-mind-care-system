# 擁抱身心醫療預約系統

這是一個 Java Swing + MySQL 的 Maven 範例專案，主題為「醫院精神科門診、心理諮商 / 心理治療、臨床心理衡鑑」預約系統。

## 開發環境

- JDK 11
- MySQL 8.0
- Eclipse Java SE
- WindowBuilder 可開啟 Swing UI
- Maven Project
- MVC + DAO Pattern

## 專案資料夾

```text
src/main/java
├─ controller
├─ dao
│  └─ impl
├─ exception
├─ model
├─ service
│  └─ impl
└─ util
```

## 功能

- 依日期預約
- 依服務類別預約
- 依專業人員預約
- 查詢 / 取消預約
- 當日看診進度
- 預約說明
- 門診時間表 UI
- 諮商費用說明
- 預約成功小視窗

## 主軸

1. 醫院精神科門診
2. 心理諮商 / 心理治療
3. 臨床心理衡鑑

## 服務類別

- 高齡心智醫學中心
- 社區精神醫療服務
- 成癮醫學發展中心
- 藥癮醫療示範中心
- 腦刺激治療中心
- 兒童青少年精神醫學中心
- 司法精神醫學中心
- 心身醫學中心
- 心理諮商
- 心理治療
- 臨床心理衡鑑

## 諮商費用說明

| 服務項目 | 時間（分鐘） | 費用（NTD） |
|---|---:|---:|
| 個別諮商（成人/青少年） | 50 | 2200-3000 |
| 兒童個別諮商 | 50 | 2200-3000 |
| 伴侶諮商 | 80 | 3500-5200 |

## 執行方式

1. 使用 MySQL Workbench 執行：

```text
database/schema.sql
```

2. 修改資料庫連線：

```text
src/main/resources/db.properties
```

預設：

```properties
db.user=root
db.password=1234
```

3. Eclipse 匯入 Maven Project：

```text
File → Import → Maven → Existing Maven Projects
```

4. 執行主程式：

```text
controller.MainUI
```

## 測試資料

可使用日期：

```text
2026-07-06
```

範例當天資料：

```text
上午
成人精神科　陳志文　剩餘 5 名
兒童青少年精神科　王雅婷　剩餘 3 名

下午
臨床心理衡鑑　李佳蓉　剩餘 2 名
心理諮商　張怡君　剩餘 1 名
```

## UI 色彩

- 主色：#ebb2a3
- 背景：#fcf7f6
