package com.veosaf.controller;

import com.veosaf.service.CustomBackend;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/backend")
@AllArgsConstructor
public class BackendController {

    private final CustomBackend customBackend;

    @GetMapping("timeout")
    public String unhealthyBackendWithTimeout() {
        return customBackend.unhealthyBackendOnTimeout().join();
    }

    @GetMapping("failure")
    public String unhealthyOnUnexpectedError() {
        return customBackend.unhealthyOnUnexpectedError().join();
    }

    @GetMapping("success")
    public String success() {
        return customBackend.healthyBackend().join();
    }

    @GetMapping("business-error")
    public String successOnBusinessError() {
        return customBackend.healthyBackendWithCustomBusinessException().join();
    }

}
