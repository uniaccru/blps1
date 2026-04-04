package com.example.hhflow.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hhflow.model.Vacancy;

import java.util.List;
import java.util.Optional;

public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
	@EntityGraph(attributePaths = "employer")
	List<Vacancy> findAll();

	@EntityGraph(attributePaths = "employer")
	Optional<Vacancy> findById(Long id);
}
