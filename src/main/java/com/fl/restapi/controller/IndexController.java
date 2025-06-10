package com.fl.restapi.controller;

import com.fl.restapi.reqeust.CallLambdaReq;
import com.fl.restapi.service.S3Service;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class IndexController {

    private final S3Service s3Service;

    @GetMapping("/")
    public String index() {
        return "Hello World";
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
