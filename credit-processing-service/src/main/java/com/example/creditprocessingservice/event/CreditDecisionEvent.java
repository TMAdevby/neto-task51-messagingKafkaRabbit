package com.example.creditprocessingservice.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditDecisionEvent implements Serializable {

    private Long applicationId;
    private boolean approved;
    private String reason;
}