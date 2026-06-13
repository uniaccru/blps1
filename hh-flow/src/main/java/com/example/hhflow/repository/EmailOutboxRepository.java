package com.example.hhflow.repository;

import com.example.hhflow.model.EmailOutbox;
import com.example.hhflow.model.OutboxStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailOutboxRepository extends JpaRepository<EmailOutbox, Long> {

    List<EmailOutbox> findByStatusOrderByCreatedAtAsc(OutboxStatus status, Pageable pageable);
}
