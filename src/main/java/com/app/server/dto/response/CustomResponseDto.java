package com.app.server.dto.response;

import com.github.mfathi91.time.PersianDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Date;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CustomResponseDto {
    private String message;
    private String details;
    private PersianDate timestamp;
    private int status;

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
