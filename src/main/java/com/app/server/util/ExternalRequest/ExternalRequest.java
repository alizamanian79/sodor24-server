package com.app.server.util.ExternalRequest;

import com.app.server.util.ExternalRequest.dto.ExternalRequestDto;
import com.app.server.util.ExternalRequest.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalRequest {

    private final RestTemplate restTemplate;

    public Map<String, Object> sendRequest(ExternalRequestDto req) {

        String url = buildUrl(req);
        HttpMethod httpMethod = mapMethod(req.getMethod());

        HttpEntity<Object> entity = new HttpEntity<>(
                req.getBody(),
                buildHeaders(req.getToken())
        );

        try {

            ResponseEntity<Map<String, Object>> response =
                    restTemplate.exchange(
                            url,
                            httpMethod,
                            entity,
                            new ParameterizedTypeReference<>() {}
                    );

            return response.getBody();

        } catch (HttpClientErrorException | HttpServerErrorException ex) {

            String responseBody = ex.getResponseBodyAsString();

            log.error(
                    "External API error [{} {}]: {} - {}",
                    httpMethod,
                    url,
                    ex.getStatusCode(),
                    responseBody
            );

            throw new ExternalApiException(
                    responseBody,
                    ex.getStatusCode().value()
            );

        } catch (ResourceAccessException ex) {

            log.error(
                    "External API unreachable [{} {}]",
                    httpMethod,
                    url,
                    ex
            );

            throw new ExternalApiException(
                    "سرویس مقصد در دسترس نیست",
                    503
            );

        } catch (RestClientException ex) {

            log.error(
                    "Unexpected error calling external API [{} {}]",
                    httpMethod,
                    url,
                    ex
            );

            throw new ExternalApiException(
                    "خطای غیرمنتظره در ارتباط با سرویس مقصد",
                    500
            );
        }
    }

    private String buildUrl(ExternalRequestDto req) {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(req.getUrl());

        if (req.getParam() != null && !req.getParam().isBlank()) {
            builder.pathSegment(req.getParam());
        }

        return builder.toUriString();
    }

    private HttpMethod mapMethod(
            com.app.server.util.ExternalRequest.dto.Method method
    ) {

        if (method == null) {
            throw new IllegalArgumentException("Method must not be null");
        }

        return HttpMethod.valueOf(method.name());
    }

    private HttpHeaders buildHeaders(String token) {

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        if (token != null && !token.isBlank()) {

            String cleanToken = token.trim();

            if (cleanToken.regionMatches(
                    true,
                    0,
                    "Bearer ",
                    0,
                    7
            )) {
                cleanToken = cleanToken.substring(7).trim();
            }

            headers.setBearerAuth(cleanToken);
        }

        return headers;
    }
}