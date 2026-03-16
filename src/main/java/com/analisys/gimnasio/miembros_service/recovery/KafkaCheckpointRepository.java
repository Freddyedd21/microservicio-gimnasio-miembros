package com.analisys.gimnasio.miembros_service.recovery;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface KafkaCheckpointRepository extends JpaRepository<KafkaCheckpoint, Long> {
    Optional<KafkaCheckpoint> findByTopicAndPartition(String topic, int partition);
}
