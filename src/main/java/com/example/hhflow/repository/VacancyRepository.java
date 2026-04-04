package com.example.hhflow.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.hhflow.model.Vacancy;

import java.util.Optional;

public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
	@EntityGraph(attributePaths = "employer")
	Page<Vacancy> findAll(Pageable pageable);

	@EntityGraph(attributePaths = "employer")
	Optional<Vacancy> findById(Long id);
}
