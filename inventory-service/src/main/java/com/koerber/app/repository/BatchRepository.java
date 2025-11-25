package com.koerber.app.repository;

import com.koerber.app.model.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {
    List<Batch> findByProductIdOrderByExpiryDateAsc(Long productId);
}
