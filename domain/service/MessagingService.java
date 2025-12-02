
package domain.service;

import data.repository.MessagingRepository;
import domain.messaging.Message;
import domain.messaging.MessageThread;
import domain.messaging.Alert;

import java.util.List;

public class MessagingService {

    private final MessagingRepository repo = new MessagingRepository();

    public MessageThread createThread(Long patientId, Long caregiverId, Long doctorId) {
        MessageThread t = new MessageThread();
        t.create(patientId, caregiverId, doctorId);
        return repo.createThread(t);
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
