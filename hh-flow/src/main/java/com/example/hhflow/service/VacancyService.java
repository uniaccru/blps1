package com.example.hhflow.service;

import com.example.hhflow.dto.request.CreateVacancyRequest;
import com.example.hhflow.exception.BusinessException;
import com.example.hhflow.exception.NotFoundException;
import com.example.hhflow.model.Role;
import com.example.hhflow.model.User;
import com.example.hhflow.model.Vacancy;
import com.example.hhflow.model.VacancyStatus;
import com.example.hhflow.repository.UserRepository;
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
    private final UserRepository userRepository;

    public Page<Vacancy> findAll(Pageable pageable) {
        return vacancyRepository.findAll(pageable);
    }

    public Vacancy getById(Long vacancyId) {
        return vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new NotFoundException("Vacancy not found: " + vacancyId));
    }

    @Transactional
    public Vacancy create(CreateVacancyRequest request, Long employerUserId) {
        User employer = userRepository.findById(employerUserId)
                .filter(u -> u.getRole() == Role.EMPLOYER)
                .orElseThrow(() -> new NotFoundException("Employer not found: " + employerUserId));

        Vacancy vacancy = new Vacancy();
        vacancy.setTitle(request.getTitle());
        vacancy.setRequiresTest(request.getRequiresTest());
        vacancy.setEmployer(employer);
        vacancy.setStatus(VacancyStatus.ACTIVE);
        return vacancyRepository.save(vacancy);
    }

    @Transactional
    public Vacancy updateStatus(Long vacancyId, VacancyStatus status, Long employerUserId) {
        Vacancy vacancy = getById(vacancyId);
        if (!vacancy.getEmployer().getId().equals(employerUserId)) {
            throw new BusinessException("Vacancy does not belong to this employer");
        }
        vacancy.setStatus(status);
        return vacancyRepository.save(vacancy);
    }
}
