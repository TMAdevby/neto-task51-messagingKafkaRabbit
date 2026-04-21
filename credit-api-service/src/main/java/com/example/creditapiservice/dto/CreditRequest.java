package com.example.creditapiservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditRequest {

    private Double amount;
    private Integer term;
    private Double income;
    private Double currentDebt;
    private Integer creditRating;
}
