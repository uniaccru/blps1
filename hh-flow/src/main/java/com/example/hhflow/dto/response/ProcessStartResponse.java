package com.example.hhflow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProcessStartResponse {

    private final String processInstanceId;
}