package com.example.creditapiservice.repository;

import com.example.creditapiservice.model.CreditApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreditRepository extends JpaRepository<CreditApplication, Long> {
    
    Optional<CreditApplication> findById(Long id);
}
