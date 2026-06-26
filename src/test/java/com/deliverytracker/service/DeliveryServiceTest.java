package com.deliverytracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.deliverytracker.domain.Delivery;
import com.deliverytracker.domain.DeliveryStatus;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DeliveryServiceTest {

    @Autowired
    private DeliveryService deliveryService;

    @Test
    void create_shouldGenerateTrackingNumberFromMaxIdPlusOne() {
        Delivery first = deliveryService.create("山田 太郎", "東京都新宿区西新宿1-1-1");
        Delivery second = deliveryService.create("鈴木 花子", "東京都渋谷区道玄坂2-2-2");

        assertThat(first.getTrackingNumber()).isEqualTo("TRK-0003");
        assertThat(second.getTrackingNumber()).isEqualTo("TRK-0004");
    }

    @Test
    void markAsDelivered_shouldUpdateStatus() {
        Delivery created = deliveryService.create("佐藤 次郎", "東京都港区1-1-1");

        Delivery updated = deliveryService.markAsDelivered(created.getId());

        assertThat(updated.getStatus()).isEqualTo(DeliveryStatus.DELIVERED);
    }

    @Test
    void markAsDelivered_shouldThrowWhenMissingId() {
        assertThatThrownBy(() -> deliveryService.markAsDelivered(9999L))
            .isInstanceOf(EntityNotFoundException.class);
    }
}
