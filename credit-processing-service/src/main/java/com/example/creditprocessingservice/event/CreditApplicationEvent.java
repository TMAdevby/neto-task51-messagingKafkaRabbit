package com.example.creditprocessingservice.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditApplicationEvent implements Serializable {

    private Long applicationId;
    private Double amount;
    private Integer term;
    private Double income;
    private Double currentDebt;
    private Integer creditRating;
}
