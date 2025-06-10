package com.fl.restapi.controller;

import com.fl.restapi.reqeust.CallLambdaReq;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class IndexController {


    @GetMapping("/")
    public String index() {
        return "Hello World";
    }

    @PostMapping("/call-lambda")
    public String callLambda(
            @RequestBody CallLambdaReq req
    ) {
        return "callLambda req: + " + req;
    }

    @PostMapping("/put-s3")
    public String putS3() {
        return "putS3";
    }
}
