package com.app.server.dto.response;

import com.github.mfathi91.time.PersianDate;
import lombok.*;

import java.time.LocalTime;
import java.util.Date;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {
    private String message;
    private String access_token;
    private String refresh_token;


    private PersianDate timestamp;

    public String getTimestamp() {
        if (timestamp == null) return null;

        String year = String.valueOf(timestamp.getYear());
        String month = String.format("%02d", timestamp.getMonth().getValue());
        String day = String.format("%02d", timestamp.getDayOfMonth());

        LocalTime now = LocalTime.now();
        String hour = String.format("%02d", now.getHour());
        String minute = String.format("%02d", now.getMinute());
        String second = String.format("%02d", now.getSecond());

        return year + "-" + month + "-" + day + "," + hour + ":" + minute + ":" + second;
    }
}
