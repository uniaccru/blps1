package com.example.hhflow.service;

import com.example.hhflow.dto.request.CreateVacancyRequest;
import com.example.hhflow.exception.NotFoundException;
import com.example.hhflow.model.Vacancy;
import com.example.hhflow.model.VacancyStatus;
import com.example.hhflow.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VacancyService {

    private final VacancyRepository vacancyRepository;

    @Transactional(readOnly = true)
    public List<Vacancy> findAll() {
        return vacancyRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Vacancy getById(Long vacancyId) {
        return vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new NotFoundException("Vacancy not found: " + vacancyId));
    }

    @Transactional
    public Vacancy create(CreateVacancyRequest request) {
        Vacancy vacancy = new Vacancy();
        vacancy.setTitle(request.getTitle());
        vacancy.setRequiresTest(request.getRequiresTest());
        vacancy.setEmployerEmail(request.getEmployerEmail());
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
