package com.deliverytracker.repository;

import com.deliverytracker.domain.Delivery;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    List<Delivery> findAllByOrderByCreatedAtDesc();

    Optional<Delivery> findTopByOrderByIdDesc();
}
