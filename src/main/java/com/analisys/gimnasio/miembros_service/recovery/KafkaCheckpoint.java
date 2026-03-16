package com.analisys.gimnasio.miembros_service.recovery;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Data
@Entity
@Table(
    name = "kafka_checkpoints",
    uniqueConstraints = @UniqueConstraint(columnNames = {"topic", "partition_id"})
)
public class KafkaCheckpoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String topic;

    @Column(name = "partition_id", nullable = false)
    private int partition;

    @Column(name = "kafka_offset", nullable = false) 
    private long offset;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}