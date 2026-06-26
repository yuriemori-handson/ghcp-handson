package com.deliverytracker.service;

import com.deliverytracker.domain.Delivery;
import com.deliverytracker.domain.DeliveryStatus;
import com.deliverytracker.repository.DeliveryRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public DeliveryService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @Transactional(readOnly = true)
    public List<Delivery> findAll() {
        return deliveryRepository.findAllByOrderByCreatedAtDesc();
    }

    public Delivery create(String recipientName, String recipientAddress) {
        if (!StringUtils.hasText(recipientName) || !StringUtils.hasText(recipientAddress)) {
            throw new IllegalArgumentException("recipientName and recipientAddress must not be blank");
        }

        long nextId = deliveryRepository.findTopByOrderByIdDesc()
            .map(delivery -> delivery.getId() + 1)
            .orElse(1L);

        Delivery delivery = new Delivery();
        delivery.setTrackingNumber(String.format("TRK-%04d", nextId));
        delivery.setRecipientName(recipientName.trim());
        delivery.setRecipientAddress(recipientAddress.trim());
        delivery.setStatus(DeliveryStatus.PENDING);

        return deliveryRepository.save(delivery);
    }

    public Delivery markAsDelivered(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Delivery not found: id=" + id));
        delivery.setStatus(DeliveryStatus.DELIVERED);
        return delivery;
    }
}
