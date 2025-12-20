package presentation.controller;

import domain.service.MessagingService;
import domain.messaging.Message;
import domain.messaging.MessageThread;
import domain.patient.Alert;

import java.util.List;

public class MessagingController {

    private final MessagingService service = new MessagingService();

    public MessageThread createThread(Long patientId, Long caregiverId, Long doctorId) {
        return service.getOrCreatePatientRoom(patientId);
    }

    public Message send(Long threadId, Long senderId, String msg) {
        return service.sendMessage(threadId, senderId, msg);
    }

    public List<Message> getMessages(Long threadId) {
        return service.getMessages(threadId);
    }

    public List<MessageThread> getThreads(Long userId) {
        return service.getThreads(userId);
    }

    // [확인] UI에서 호출하는 메서드
    public List<Alert> getAlerts(Long userId) {
        return service.getAlerts(userId);
    }
}