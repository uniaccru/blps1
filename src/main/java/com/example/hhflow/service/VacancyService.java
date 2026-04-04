package com.example.hhflow.service;

import com.example.hhflow.dto.request.CreateVacancyRequest;
import com.example.hhflow.model.Employer;
import com.example.hhflow.exception.NotFoundException;
import com.example.hhflow.model.Vacancy;
import com.example.hhflow.model.VacancyStatus;
import com.example.hhflow.repository.EmployerRepository;
import com.example.hhflow.repository.VacancyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VacancyService {

    private final VacancyRepository vacancyRepository;
    private final EmployerRepository employerRepository;

    public Page<Vacancy> findAll(Pageable pageable) {
        return vacancyRepository.findAll(pageable);
    }

    public Vacancy getById(Long vacancyId) {
        return vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new NotFoundException("Vacancy not found: " + vacancyId));
    }

    @Transactional
    public Vacancy create(CreateVacancyRequest request) {
        Employer employer = employerRepository.findById(request.getEmployerId())
            .orElseThrow(() -> new NotFoundException("Employer not found: " + request.getEmployerId()));

        Vacancy vacancy = new Vacancy();
        vacancy.setTitle(request.getTitle());
        vacancy.setRequiresTest(request.getRequiresTest());
        vacancy.setEmployer(employer);
        vacancy.setStatus(VacancyStatus.ACTIVE);
        return vacancyRepository.save(vacancy);
    }

    @Transactional
    public Vacancy updateStatus(Long vacancyId, VacancyStatus status) {
        Vacancy vacancy = getById(vacancyId);
        vacancy.setStatus(status);
        return vacancyRepository.save(vacancy);
    }
}
