package com.example.hhflow.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateResumeRequest {

        @NotNull
        private Long candidateId;

        @NotBlank
        private String fullName;

        @NotBlank
        private String summary;
}
