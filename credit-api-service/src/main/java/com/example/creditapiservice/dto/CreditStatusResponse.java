package com.example.creditapiservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditStatusResponse {

    private Long id;
    private String status;
    private Double amount;
    private Integer term;
}