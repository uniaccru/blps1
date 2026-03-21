package com.example.hhflow.dto;

import com.example.hhflow.domain.VacancyStatus;
import javax.validation.constraints.NotNull;

public class VacancyStatusUpdateRequest {

	@NotNull
	private VacancyStatus status;

	public VacancyStatus getStatus() {
		return status;
	}

	public void setStatus(VacancyStatus status) {
		this.status = status;
	}
}
