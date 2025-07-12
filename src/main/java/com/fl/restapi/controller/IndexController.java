package com.fl.restapi.controller;

import com.fl.restapi.reqeust.CallLambdaReq;
import com.fl.restapi.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class IndexController {

    private final S3Service s3Service;

    @Value("${aws.gateway.endpoint-url}")
    private String endpointUrl;

    @GetMapping("/")
    public String index() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HH:mm:ss_SSS");
        return "Hello World nguyen!!!!!!!!!!!! Now: " + LocalDateTime.now().format(formatter);
    }

    @SneakyThrows
    @PostMapping("/call-lambda")
    public String callLambda(
            @RequestBody CallLambdaReq req
    ) {

        Map<String, Object> payload = Map.of(
                "orderId", req.orderId(),
                "amount", req.amount(),
                "item", req.item()
        );
        s3Service.invokeLambdaFunctionAsync(payload);

        return "callLambda req: + " + req;
    }

    @SneakyThrows
    @PostMapping("/call-gateway-lambda")
    public String callGatewayLambda(
            @RequestBody CallLambdaReq req
    ) {

        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("orderId", req.orderId());
            requestBody.put("amount", req.amount());
            requestBody.put("item", req.item());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.postForEntity(endpointUrl, requestEntity, String.class);
        } catch (Exception e) {
            return "error";
        }
        return "success";
    }

    @PostMapping("/put-s3")
    public String putS3() {
        String uploadedFile = s3Service.uploadFile();
        return "fileName: " + uploadedFile;
    }

    @GetMapping("/list-s3")
    public List<String>  listS3() {
        return Collections.singletonList(s3Service.listFiles().toString());
    }
}
