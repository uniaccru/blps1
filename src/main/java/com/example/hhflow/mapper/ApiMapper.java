package com.example.hhflow.mapper;

import com.example.hhflow.domain.JobApplication;
import com.example.hhflow.domain.Resume;
import com.example.hhflow.domain.Vacancy;
import com.example.hhflow.dto.response.ApplicationDto;
import com.example.hhflow.dto.response.ResumeDto;
import com.example.hhflow.dto.response.VacancyDto;
import org.springframework.stereotype.Component;

@Component
public class ApiMapper {

    public VacancyDto toDto(Vacancy vacancy) {
        return new VacancyDto(
                vacancy.getId(),
                vacancy.getTitle(),
                vacancy.getStatus(),
                vacancy.isRequiresTest(),
                vacancy.getEmployerEmail()
        );
    }

    public ResumeDto toDto(Resume resume) {
        return new ResumeDto(
                resume.getId(),
                resume.getCandidateId(),
                resume.getFullName(),
                resume.getSummary(),
                resume.getCreatedAt()
        );
    }

    public ApplicationDto toDto(JobApplication application) {
        return new ApplicationDto(
                application.getId(),
                application.getVacancy().getId(),
                application.getResume().getId(),
                application.getCandidateId(),
                application.getStatus(),
                application.getCreatedAt(),
                application.getEmployerNotifiedAt()
        );
    }
}
