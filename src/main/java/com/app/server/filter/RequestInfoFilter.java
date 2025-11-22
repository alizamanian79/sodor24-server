package com.app.server.filter;

import com.app.server.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mfathi91.time.PersianDate;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class RequestInfoFilter implements Filter {

    private final RestTemplate restTemplate = new RestTemplate();
    private final Map<String, String> ipCountryCache = new HashMap<>();
    private final Set<String> boycottCountries = new HashSet<>();

    public RequestInfoFilter(@Value("${boycott.countries}") String countries) {
        Arrays.stream(countries.split(","))
                .map(String::trim)
                .forEach(boycottCountries::add);
    }



    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        isoFormat.setTimeZone(TimeZone.getTimeZone("Asia/Tehran"));


        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String ip = getClientIp(req);

        String country = ipCountryCache.get(ip);
        if (country == null) {
            country = getCountryFromIp(ip);
            ipCountryCache.put(ip, country);
        }

//        System.out.println("🌐 Request from IP: " + ip + " | Country: " + country);

        if (boycottCountries.contains(country)) {

            ErrorResponse errorResponse = ErrorResponse.builder()
                    .message("Your country is in our boycott list")
                    .details("We hope both countries will improve relations")
                    .status(HttpStatus.FORBIDDEN.value())
                    .timestamp(PersianDate.now())
                    .build();

            ObjectMapper mapper = new ObjectMapper();
            mapper.setDateFormat(isoFormat);
            String json = mapper.writeValueAsString(errorResponse);

            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            res.setContentType("application/json");
            res.getWriter().write(json);
            return;
        }

        chain.doFilter(request, response);
    }


    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private String getCountryFromIp(String ip) {
        try {

            String url1 = "https://ipwho.is/" + ip;
            String response1 = restTemplate.getForObject(url1, String.class);
            JSONObject json1 = new JSONObject(response1);
            String country = json1.optString("country");
            if (country != null && !country.isEmpty()) return country;


            String url2 = "https://ipinfo.io/" + ip + "/json";
            String response2 = restTemplate.getForObject(url2, String.class);
            JSONObject json2 = new JSONObject(response2);
            country = json2.optString("country");
            return country != null && !country.isEmpty() ? country : "Unknown";

        } catch (Exception e) {
            return "Unknown";
        }
    }
}
