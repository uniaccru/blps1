package com.example.hhflow.dto.request;

import javax.validation.constraints.NotNull;

import com.example.hhflow.model.VacancyStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VacancyStatusUpdateRequest {

	@NotNull(message = "must not be null")
	private VacancyStatus status;
}
