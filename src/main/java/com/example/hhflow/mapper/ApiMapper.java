package com.example.hhflow.mapper;

import com.example.hhflow.dto.response.ApplicationDto;
import com.example.hhflow.dto.response.EmployerDto;
import com.example.hhflow.dto.response.ResumeDto;
import com.example.hhflow.dto.response.VacancyDto;
import com.example.hhflow.model.Employer;
import com.example.hhflow.model.JobApplication;
import com.example.hhflow.model.Resume;
import com.example.hhflow.model.Vacancy;

import org.springframework.stereotype.Component;

@Component
public class ApiMapper {

    public EmployerDto toDto(Employer employer) {
        return new EmployerDto(
                employer.getId(),
                employer.getEmail()
        );
    }

    public VacancyDto toDto(Vacancy vacancy) {
        return new VacancyDto(
                vacancy.getId(),
                vacancy.getTitle(),
                vacancy.getStatus(),
                vacancy.isRequiresTest(),
                vacancy.getEmployer().getEmail()
        );
    }

    public ResumeDto toDto(Resume resume) {
        return new ResumeDto(
                resume.getId(),
            resume.getApplicant().getId(),
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
            application.getApplicant().getId(),
                application.getStatus(),
                application.getCreatedAt(),
                application.getEmployerNotifiedAt()
        );
    }
}
