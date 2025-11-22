
## 🚀 راه‌اندازی سرویس

برای اجرای این سرویس مراحل زیر را دنبال کنید:

### 1️⃣ ایجاد پایگاه داده MySQL

در ابتدا باید یک دیتابیس MySQL ایجاد کنید.
به‌عنوان مثال در محیط MySQL دستور زیر را اجرا کنید:

```sql
CREATE DATABASE serverdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

---

### 2️⃣ تنظیم فایل `.env`

در ریشه‌ی پروژه، یک فایل با نام `.env` ایجاد کرده و مقادیر زیر را در آن قرار دهید.
مطمئن شوید که اطلاعات دیتابیس خود را مطابق با سیستم‌تان تغییر می‌دهید.

```env
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8181

SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/serverdb?useSSL=false&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=1234

APP_SERVER_HOST=http://localhost:8181
APP_CLIENT_HOST=http://localhost:3000
APP_CLIENT_LOGIN_REDIRECT_URL=/login
```

📝 **توضیحات:**

* `SPRING_PROFILES_ACTIVE` : پروفایل فعال برنامه (در حالت توسعه `dev` است).
* `SERVER_PORT` : پورتی که سرور Spring Boot روی آن اجرا می‌شود.
* `SPRING_DATASOURCE_URL` : آدرس اتصال JDBC به پایگاه داده MySQL.
* `SPRING_DATASOURCE_USERNAME` و `SPRING_DATASOURCE_PASSWORD` : نام کاربری و رمز عبور دیتابیس.
* `APP_SERVER_HOST` : آدرس سرور Backend.
* `APP_CLIENT_HOST` : آدرس فرانت‌اند (معمولاً Next.js یا React).
* `APP_CLIENT_LOGIN_REDIRECT_URL` : مسیر ریدایرکت پس از ورود کاربر.

---
** اگر دیتابیس جایی دیگری دیپلوی باشه از پروفایل prod با متقاییر های خودتون استفاده کنید در صورتی که دیتابیستون mysql هست . در غیر این صورت از روی prod یک پروفایل دیگری بسازید و اطلاعات مربوط به دیتابیستون رو وارد کنید و دیپندنسی مخصوص دیتابیس درایورتون**
---


### 4️⃣ دسترسی به برنامه

* **Backend:** [http://localhost:8181](http://localhost:8181)
* **Frontend:** [http://localhost:3000](http://localhost:3000)

---
"# sodor24-server" 
"# sodor24-server" 
