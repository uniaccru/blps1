package com.example.hhflow.dto.response;

import com.example.hhflow.model.VacancyStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VacancyDto {

        private final Long id;
        private final String title;
        private final VacancyStatus status;
        private final boolean requiresTest;
        private final String employerEmail;
}
