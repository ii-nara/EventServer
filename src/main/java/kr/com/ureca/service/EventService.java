package kr.com.ureca.service;

import jakarta.transaction.Transactional;
import kr.com.ureca.dto.EventSaveRequestDto;
import kr.com.ureca.entity.EventEntity;
import kr.com.ureca.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EventService {

  private final EventRepository eventRepository;

  @Transactional
  @RabbitListener(queues = "eventQueue")
  public void addEventApplicantMQ(@Payload EventSaveRequestDto eventSaveRequestDto) {
    System.out.println("eventSaveRequestDto >>> " + eventSaveRequestDto.getName() + " - " + eventSaveRequestDto.getPhone());
    System.out.println(addEventApplicant(eventSaveRequestDto));
  }

  public String addEventApplicant(EventSaveRequestDto eventSaveRequestDto) {
    if (eventRepository.findByPhoneNumber(eventSaveRequestDto.getPhone()).isPresent()) {
      return "이미 응모한 이력이 있습니다.";
    } else {
      eventRepository.save(
          EventEntity.builder()
              .name(eventSaveRequestDto.getName())
              .phoneNumber(eventSaveRequestDto.getPhone())
              .build());
      return "응모 완료됐습니다!\n결과는 다음날 13시에 확인할 수 있습니다.";
    }
  }

}
