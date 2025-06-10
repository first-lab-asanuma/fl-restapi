package com.fl.restapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class S3Service {

    @Autowired
    private S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Autowired
    private LambdaClient lambdaClient;

    @Value("${aws.lambda.function-name}")
    private String functionName;

    private ObjectMapper objectMapper = new ObjectMapper();


    public String invokeLambdaFunction(Map<String, Object> payload) throws Exception {
        String jsonPayload = objectMapper.writeValueAsString(payload);

        InvokeRequest invokeRequest = InvokeRequest.builder()
                .functionName(functionName)
                .payload(SdkBytes.fromUtf8String(jsonPayload))
                .build();

        InvokeResponse response = lambdaClient.invoke(invokeRequest);

        return response.payload().asUtf8String();
    }

    public void invokeLambdaFunctionAsync(Map<String, Object> payload) throws Exception {
        String jsonPayload = objectMapper.writeValueAsString(payload);

        InvokeRequest invokeRequest = InvokeRequest.builder()
                .functionName(functionName)
                .payload(SdkBytes.fromUtf8String(jsonPayload))
                .invocationType("Event") // 非同期設定
                .build();

        lambdaClient.invoke(invokeRequest);
        System.out.println("*-*-*-*-*-Lambda Executed-*-*-*-*");
    }


    public String uploadFile() {
        var now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS");

        String fileName = "restapi_" + now.format(formatter) + ".txt";

        String fileContent = "fl-restapiアプリから作成されたファイルです。.\n" +
                now.format(DateTimeFormatter.ofPattern("MM月 dd日 HH時 mm分 ss秒")) + "\n";

        PutObjectRequest putObjectRequest =
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .contentType("text/plain; charset=utf-8")
                        .build();

        s3Client.putObject(putObjectRequest,
                RequestBody.fromString(fileContent));

        return fileName;
    }

    public List<String> listFiles() {
        ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        ListObjectsV2Response response = s3Client.listObjectsV2(listObjectsRequest);

        return response.contents().stream()
                .map(S3Object::key)
                .collect(Collectors.toList());
    }

}