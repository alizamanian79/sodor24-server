package com.app.server.util.rabbitMQ.dto.request;


import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignatureRequestDto {

    private String username;          // نام کاربری یا نام کامل
    private String country;           // کد کشور (C)
    private String reason;            // دلیل امضا
    private String location;          // محل (L)
    private String organization;      // سازمان (O)
    private String department;        // واحد سازمانی (OU)
    private String state;             // استان یا ایالت (ST)
    private String email;             // ایمیل امضا کننده (E)
    private String title;             // عنوان شغلی یا نقش
    private String userId;            // شناسه یکتا کاربر (UID)
    private int signatureExpired;     // تاریخ انقضای امضا
    private String city;               // شهر
    private String signaturePassword; // زمز امضا

}
