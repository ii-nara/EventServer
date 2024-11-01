package kr.com.ureca.service;

import jakarta.transaction.Transactional;
import kr.com.ureca.dto.EventSaveRequestDto;
import kr.com.ureca.entity.Enum.EventStatus;
import kr.com.ureca.entity.EventEntity;
import kr.com.ureca.repository.EventRepository;
import kr.com.ureca.util.SmsCertificationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EventService {

  private final EventRepository eventRepository;

  private final SmsCertificationUtil smsCertificationUtil;

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

  @Scheduled(cron = "0 0 13 4 11 *")
  public void sendMsgToWinner() {
    List<EventEntity> events = eventRepository.findAll();
    for (EventEntity eventEntity : events) {
      if (eventEntity.getStatus().equals(EventStatus.WINNING)) {
        String name = eventEntity.getName();
        String phoneNumber = eventEntity.getPhoneNumber();
        String content = name + "님! 당첨을 진심으로 축하드립니다!\n\n경품은 MBTKids 마이페이지를 참고해주세요";
        smsCertificationUtil.sendSMS(phoneNumber, content);
      }
    }
  }

}
