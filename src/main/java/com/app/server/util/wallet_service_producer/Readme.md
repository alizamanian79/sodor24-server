# 💳 Wallet Service | سرویس کیف پول

<div dir="rtl">

سرویسی بر پایه **Spring Boot** برای مدیریت کیف پول دیجیتال — شامل ایجاد کیف پول، واریز، برداشت و پردازش پرداخت از طریق **RabbitMQ** به صورت ناهمزمان و رویداد-محور.

</div>

> A **Spring Boot** microservice for managing digital wallets — supporting creation, deposits, withdrawals, and payment processing via **RabbitMQ** for asynchronous, event-driven communication.

---

## 📋 Table of Contents | فهرست مطالب

- [Requirements | پیش‌نیازها](#-requirements--پیش-نیازها)
- [Dependencies | وابستگی‌ها](#-dependencies--وابستگیها)
- [Running RabbitMQ | راه‌اندازی RabbitMQ](#-running-rabbitmq--راهاندازی-rabbitmq)
- [Configuration | پیکربندی](#-configuration--پیکربندی)
- [Project Structure | ساختار پروژه](#-project-structure--ساختار-پروژه)
- [Features | امکانات](#-features--امکانات)
- [Integration Notes | نکات یکپارچه‌سازی](#-integration-notes--نکات-یکپارچهسازی)
- [Quick Start Checklist | چک‌لیست شروع سریع](#-quick-start-checklist--چکلیست-شروع-سریع)

---

## ✅ Requirements | پیش‌نیازها

| ابزار / Tool | نسخه / Version |
|---|---|
| Java | 17+ |
| Spring Boot | 3.x |
| RabbitMQ | 3.x |
| MySQL | 8.x |
| Maven | 3.x |

---

## 📦 Dependencies | وابستگی‌ها

<div dir="rtl">

وابستگی‌های زیر را به فایل `pom.xml` خود اضافه کنید:

</div>

Add the following to your `pom.xml`:

```xml
<!-- RabbitMQ / AMQP -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>

<!-- MySQL Driver -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

---

## 🐰 Running RabbitMQ | راه‌اندازی RabbitMQ

### Option 1: Docker (Recommended | توصیه‌شده)

<div dir="rtl">

سریع‌ترین روش راه‌اندازی RabbitMQ استفاده از Docker است:

</div>

```bash
docker run -d --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:3-management
```

| | |
|---|---|
| 🌐 Dashboard | http://localhost:15672 |
| 👤 Username / نام کاربری | `guest` |
| 🔑 Password / رمز عبور | `guest` |

### Option 2: Local Installation | نصب محلی

<div dir="rtl">

از [rabbitmq.com/download.html](https://www.rabbitmq.com/download.html) دانلود و نصب کنید، سپس پلاگین مدیریت را فعال کنید:

</div>

Download from [rabbitmq.com/download.html](https://www.rabbitmq.com/download.html), then enable the management plugin:

```bash
rabbitmq-plugins enable rabbitmq_management
```

---

## ⚙️ Configuration | پیکربندی

<div dir="rtl">

تنظیمات زیر را به فایل `application.yml` خود اضافه کنید:

</div>

Add the following to your `application.yml`:

```yaml
application:
  wallet-service:
    currency: IRT

    payments:
      zarinpal:
        env: dev
        name: zarinpal
        merchant_id: d8234a3a-dbcf-4f30-ab52-b195b0f0f1a3

    rabbitmq:
      exchange: wallet.exchange
      routing:
        list-wallet:              wallet.list.routing-key
        create-wallet:            wallet.create.routing-key
        get-wallet:               wallet.get.routing-key
        active-wallet:            wallet.active.routing-key
        delete-wallet:            wallet.delete.routing-key
        update-sub-wallet:        wallet.update.sub.routing-key
        payment-request-wallet:   wallet.request.payment.routing-key
        payment-verifier-wallet:  wallet.verifier.payment.routing-key
```

---

## 📁 Project Structure | ساختار پروژه

<div dir="rtl">

پکیج یکپارچه‌سازی سرویس کیف پول را داخل اپلیکیشن اصلی خود قرار دهید:

</div>

Place the wallet service integration package inside your main application:

```
server-service/
└── src/main/java/com/app/server_service/
    ├── wallet-service-producer/     ← کلاس‌های producer کیف پول را اینجا کپی کنید
    │   └── WalletRMQProducer.java   ← نقطه ورودی اصلی برای ارسال رویدادهای کیف پول
    ├── config/                      ← تنظیمات exchange و queue در RabbitMQ
    └── ...
```

> **🇮🇷** تمام کلاس‌های پکیج `wallet-service-producer` را در سرویس خود کپی کنید و از `WalletRMQProducer` برای ارسال رویدادهای کیف پول استفاده کنید.
>
> **🇬🇧** Copy all classes from the `wallet-service-producer` package into your service. Use `WalletRMQProducer` to publish wallet events from your business logic.

---

## 💰 Features | امکانات

### Wallet Management | مدیریت کیف پول

| عملیات / Operation | توضیح / Description |
|---|---|
| ➕ Create Wallet / ایجاد کیف پول | Register a new wallet / ثبت کیف پول جدید برای کاربر |
| 📋 List Wallets / لیست کیف پول‌ها | Retrieve all wallets / دریافت تمام کیف پول‌ها |
| 🔍 Get Wallet / دریافت کیف پول | Fetch a wallet by ID / واکشی کیف پول با شناسه |
| ✅ Activate Wallet / فعال‌سازی | Enable a wallet for transactions / فعال کردن کیف پول برای تراکنش |
| 🗑️ Delete Wallet / حذف کیف پول | Remove a wallet / حذف کیف پول |
| ✏️ Update Sub-Wallet / ویرایش زیرکیف‌پول | Modify sub-wallet details / ویرایش اطلاعات زیرکیف‌پول |

### Payment Processing | پردازش پرداخت

| عملیات / Operation | توضیح / Description |
|---|---|
| 💸 Request Payment / درخواست پرداخت | Initiate internal or Zarinpal payment / شروع پرداخت داخلی یا زرین‌پال |
| ✔️ Verify Payment / تأیید پرداخت | Confirm and settle a Zarinpal payment / تأیید و تسویه پرداخت زرین‌پال |
| 🔔 Payment Callback / بازگشت پرداخت | Notify user after payment completes / اطلاع‌رسانی به کاربر پس از پرداخت |

---

## 🔗 Integration Notes | نکات یکپارچه‌سازی

<div dir="rtl">

- **فعال‌سازی اجباری:** کیف پول باید قبل از واریز، برداشت یا درخواست پرداخت **فعال** شده باشد.
- **درگاه‌های پرداخت:** در حال حاضر از `internal` و `Zarinpal` پشتیبانی می‌شود.
- **راه‌اندازی کلاس‌ها:** تمام کلاس‌های `wallet-service-producer` را قبل از استفاده در لایه سرویس خود تزریق کنید.
- **RabbitMQ باید در حال اجرا باشد** قبل از اجرای اپلیکیشن — در غیر این صورت تعریف صف‌ها با خطا مواجه می‌شود.
- **یکسانی کلیدهای مسیریابی:** routing key‌ها در producer و consumer باید دقیقاً یکسان باشند.
- **مقیاس‌پذیری:** برای هر عملیات کیف پول یک صف اختصاصی استفاده کنید.

</div>

- **Activation required:** A wallet must be **activated** before deposits, withdrawals, or payment requests.
- **Payment providers:** Currently supports `internal` and `Zarinpal` payment gateways.
- **Class setup:** Inject all classes from `wallet-service-producer` into your service layer before use.
- **RabbitMQ must be running** before the application starts — otherwise queue declarations will fail.
- **Routing key consistency:** Ensure routing keys match exactly between producer and consumer.
- **Scalability tip:** Use one dedicated queue per wallet operation to simplify error handling.

---

## 🧪 Quick Start Checklist | چک‌لیست شروع سریع

- [ ] RabbitMQ is running on port `5672` | RabbitMQ روی پورت `5672` در حال اجراست
- [ ] `application.yml` configured with correct routing keys | `application.yml` با کلیدهای مسیریابی صحیح پیکربندی شده
- [ ] MySQL is running and schema is migrated | MySQL در حال اجراست و schema مهاجرت کرده
- [ ] `wallet-service-producer` classes are copied into your service | کلاس‌های `wallet-service-producer` کپی شده‌اند
- [ ] `WalletRMQProducer` is injected where needed | `WalletRMQProducer` در جای لازم تزریق شده
- [ ] Wallet is activated before making payment requests | کیف پول قبل از درخواست پرداخت فعال شده است
