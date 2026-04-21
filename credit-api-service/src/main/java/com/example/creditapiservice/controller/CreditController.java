package com.example.creditapiservice.controller;

import com.example.creditapiservice.dto.CreditRequest;
import com.example.creditapiservice.dto.CreditStatusResponse;
import com.example.creditapiservice.service.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/credit")
@RequiredArgsConstructor
public class CreditController {

    private final CreditService creditService;

    @PostMapping("/applications")
    public ResponseEntity<Long> createApplication(@RequestBody CreditRequest request) {

        Long applicationId = creditService.createApplication(request);

        return ResponseEntity
                .created(URI.create("/api/credit/applications/" + applicationId))
                .body(applicationId);
    }


    @GetMapping("/applications/{id}")
    public ResponseEntity<CreditStatusResponse> getStatus(@PathVariable Long id) {

        CreditStatusResponse status = creditService.getStatus(id);

        if (status != null) {

            return ResponseEntity.ok(status);
        } else {

            return ResponseEntity.notFound().build();
        }
    }
}
