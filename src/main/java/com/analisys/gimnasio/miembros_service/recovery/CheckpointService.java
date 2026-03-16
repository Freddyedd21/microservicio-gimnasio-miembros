package com.analisys.gimnasio.miembros_service.recovery;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;

@Service
public class CheckpointService {

    private final KafkaCheckpointRepository repository;

    public CheckpointService(KafkaCheckpointRepository repository) {
        this.repository = repository;
    }

    public void guardarOffset(String topic, int partition, long offset) {
        KafkaCheckpoint checkpoint = repository.findByTopicAndPartition(topic, partition)
            .orElseGet(KafkaCheckpoint::new);

        checkpoint.setTopic(topic);
        checkpoint.setPartition(partition);
        checkpoint.setOffset(offset);
        checkpoint.setUpdatedAt(LocalDateTime.now());
        repository.save(checkpoint);
    }

    public Map<TopicPartition, Long> cargarUltimosOffsets() {
        List<KafkaCheckpoint> checkpoints = repository.findAll();
        Map<TopicPartition, Long> offsets = new HashMap<>();
        for (KafkaCheckpoint checkpoint : checkpoints) {
            TopicPartition tp = new TopicPartition(checkpoint.getTopic(), checkpoint.getPartition());
            offsets.put(tp, checkpoint.getOffset());
        }
        return offsets;
    }
}
