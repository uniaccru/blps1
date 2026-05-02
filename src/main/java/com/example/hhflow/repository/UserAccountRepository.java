package com.example.hhflow.repository;

import com.example.hhflow.model.UserAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    @Query("SELECT DISTINCT u FROM UserAccount u LEFT JOIN FETCH u.employer LEFT JOIN FETCH u.applicant WHERE u.phone = :phone")
    Optional<UserAccount> findByPhone(@Param("phone") String phone);

    @Query("SELECT DISTINCT u FROM UserAccount u LEFT JOIN FETCH u.employer LEFT JOIN FETCH u.applicant WHERE u.employer.id = :employerId")
    Optional<UserAccount> findByEmployer_Id(@Param("employerId") Long employerId);

    boolean existsByPhone(String phone);
}
