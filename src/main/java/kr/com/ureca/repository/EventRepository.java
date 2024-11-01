package kr.com.ureca.repository;

import kr.com.ureca.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventRepository extends JpaRepository<EventEntity, Long> {
  Optional<EventEntity> findByPhoneNumber(String phoneNumber);
}
