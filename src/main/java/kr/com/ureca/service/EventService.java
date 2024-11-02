package kr.com.ureca.service;

import jakarta.transaction.Transactional;
import kr.com.ureca.dto.EventSaveRequestDto;
import kr.com.ureca.entity.Enum.EventStatus;
import kr.com.ureca.entity.EventEntity;
import kr.com.ureca.exception.AlreadyExistException;
import kr.com.ureca.repository.EventRepository;
import kr.com.ureca.util.SmsCertificationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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
  public void addEventApplicantMQ(@Payload EventSaveRequestDto eventSaveRequestDto) throws Exception {
    addEventApplicant(eventSaveRequestDto);
  }

  public void addEventApplicant(EventSaveRequestDto eventSaveRequestDto) throws Exception {
    if (eventRepository.findByPhoneNumber(eventSaveRequestDto.getPhone()).isPresent()) {
      throw new AlreadyExistException("이미 응모한 기록이 있습니다.");
    } else {
      EventEntity eventEntity = eventRepository.save(
          EventEntity.builder()
              .name(eventSaveRequestDto.getName())
              .phoneNumber(eventSaveRequestDto.getPhone())
                  .status(EventStatus.APPLYING)
              .build());
      if (eventRepository.countByStatus(EventStatus.WINNING) < 100L) {
        eventEntity.updateStatus(EventStatus.WINNING);
      } else eventEntity.updateStatus(EventStatus.LOSE);
    }
  }

  @Scheduled(cron = "0 0 13 5 11 *")
  public void sendMsgToWinner() {
    List<EventEntity> events = eventRepository.findByStatus(EventStatus.WINNING);
    for (EventEntity eventEntity : events) {
      String phoneNumber = String.join("", eventEntity.getPhoneNumber().split("-"));
      String name = eventEntity.getName();
      String content = name + "님! 당첨을 진심으로 축하드립니다!\n\n경품은 MBTKids 마이페이지를 참고해주세요";
      smsCertificationUtil.sendSMS(phoneNumber, content);
    }
  }

}
