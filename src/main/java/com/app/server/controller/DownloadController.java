package com.app.server.controller;

import com.app.server.util.signature_service_producer.dto.request.DownloadFileRequestDto;
import com.app.server.util.signature_service_producer.dto.response.DownloadFileResponseDto;
import com.app.server.util.signature_service_producer.producer.DownloadFileProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/download")
@RequiredArgsConstructor
@Slf4j
public class DownloadController {

    private final DownloadFileProducer downloadFileProducer;



    @GetMapping("/signature/{id}")
    public ResponseEntity<ByteArrayResource> downloadSignatureKey(@PathVariable String id) throws Exception {

        DownloadFileRequestDto req = DownloadFileRequestDto.builder()
                .fileType("p12")
                .fileName(id)
                .build();

        DownloadFileResponseDto res = downloadFileProducer.download(req);

        ByteArrayResource resource = new ByteArrayResource(res.getContent());

        return ResponseEntity.ok()
                .contentLength(res.getContent().length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(res.getFileName())
                                .build()
                                .toString())
                .body(resource);
    }

    @GetMapping("/contract/signed/{slug}")
    public ResponseEntity<ByteArrayResource> downloadSignedContract(@PathVariable String slug) throws Exception {

        DownloadFileRequestDto req = DownloadFileRequestDto.builder()
                .fileType("signed-contract")
                .fileName(slug+".pdf")
                .build();


        DownloadFileResponseDto res = downloadFileProducer.download(req);

        ByteArrayResource resource = new ByteArrayResource(res.getContent());

        return ResponseEntity.ok()
                .contentLength(res.getContent().length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(res.getFileName())
                                .build()
                                .toString())
                .body(resource);
    }

    @GetMapping("/contract/unsigned/{slug}")
    public ResponseEntity<ByteArrayResource> downloadUnSignedContract(@PathVariable String slug) throws Exception {

        DownloadFileRequestDto req = DownloadFileRequestDto.builder()
                .fileType("unsigned-contract")
                .fileName(slug+".pdf")
                .build();

        DownloadFileResponseDto res = downloadFileProducer.download(req);

        ByteArrayResource resource = new ByteArrayResource(res.getContent());

        return ResponseEntity.ok()
                .contentLength(res.getContent().length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(res.getFileName())
                                .build()
                                .toString())
                .body(resource);
    }


}
