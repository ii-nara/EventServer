package kr.com.ureca.entity;

import jakarta.persistence.*;
import kr.com.ureca.entity.Enum.EventStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Table(name = "event")
@Entity
public class EventEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long eventId;

  private String name;

  private String phoneNumber;

  @Enumerated(EnumType.STRING)
  private EventStatus status;

  @Builder
  public EventEntity(String name, String phoneNumber, EventStatus status) {
    this.name = name;
    this.phoneNumber = phoneNumber;
    this.status = status;
  }

  public void updateStatus(EventStatus status) {
    this.status = status;
  }
}
