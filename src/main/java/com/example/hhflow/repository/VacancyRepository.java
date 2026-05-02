package com.example.hhflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hhflow.model.Vacancy;

public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
}
