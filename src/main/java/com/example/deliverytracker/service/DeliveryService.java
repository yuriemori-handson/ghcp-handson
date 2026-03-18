package com.example.deliverytracker.service;

import com.example.deliverytracker.model.Delivery;
import com.example.deliverytracker.model.DeliveryStatus;
import com.example.deliverytracker.repository.DeliveryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public DeliveryService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    public List<Delivery> findAll() {
        return deliveryRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public Delivery create(String recipientName, String recipientAddress) {
        Delivery delivery = new Delivery();
        delivery.setRecipientName(recipientName);
        delivery.setRecipientAddress(recipientAddress);
        delivery.setStatus(DeliveryStatus.PENDING);

        // 一旦保存してIDを確定させる
        delivery = deliveryRepository.save(delivery);

        // IDから送り状番号を生成（TRK-XXXX）
        String trackingNumber = String.format("TRK-%04d", delivery.getId());
        delivery.setTrackingNumber(trackingNumber);

        return deliveryRepository.save(delivery);
    }

    @Transactional
    public Delivery markAsDelivered(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("配送が見つかりません: id=" + id));
        delivery.setStatus(DeliveryStatus.DELIVERED);
        return deliveryRepository.save(delivery);
    }
}
