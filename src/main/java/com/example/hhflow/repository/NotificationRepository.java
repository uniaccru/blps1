package com.example.hhflow.repository;

import com.example.hhflow.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipient_IdOrderByCreatedAtDesc(Long recipientId);
}
