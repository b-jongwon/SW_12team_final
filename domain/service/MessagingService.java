
package domain.service;

import data.repository.MessagingRepository;
import domain.messaging.Message;
import domain.messaging.MessageThread;
import domain.patient.Alert;

import java.util.List;

public class MessagingService {

    private final MessagingRepository repo = new MessagingRepository();

    public MessageThread getOrCreatePatientRoom(Long patientId) {
        // 1. 해당 환자의 방이 이미 존재하는지 DB에서 조회
        return repo.findThreadByPatientId(patientId)
                .orElseGet(() -> {
                    // 2. 없으면 새로 생성 (참여자는 환자 본인 ID만 기록, 의사/보호자는 동적으로 참여)
                    MessageThread newThread = new MessageThread();
                    newThread.create(patientId);
                    return repo.createThread(newThread);
                });
    }

    public Message sendMessage(Long threadId, Long senderId, String content) {
        Message m = new Message();
        m.setThreadId(threadId);
        m.send(senderId, content);
        repo.saveMessage(m);

        // generate alert for receiver(s)
        List<Long> receivers = List.of(); // extend later
        // simple example: alert doctor
        Alert a = new Alert();
        a.create(senderId, "새 메시지가 도착했습니다.");
        repo.saveAlert(a);

        return m;
    }

    public List<Message> getMessages(Long threadId) {
        return repo.getMessagesByThread(threadId);
    }

    public List<MessageThread> getThreads(Long userId) {
        return repo.getThreadsForUser(userId);
    }

    public List<Alert> getAlerts(Long userId) {
        return repo.getAlerts(userId);
    }
}
