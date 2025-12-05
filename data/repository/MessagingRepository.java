
package data.repository;

import infra.BaseJsonRepository;
import infra.IdGenerator;

import domain.messaging.Message;
import domain.messaging.MessageThread;
import domain.patient.Alert;

import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MessagingRepository {

    private final BaseJsonRepository<MessageThread> threadRepo =
        new BaseJsonRepository<>("data/message_threads.json",
            new TypeToken<List<MessageThread>>() {}) {};

    private final BaseJsonRepository<Message> messageRepo =
        new BaseJsonRepository<>("data/messages.json",
            new TypeToken<List<Message>>() {}) {};

    private final BaseJsonRepository<Alert> alertRepo =
        new BaseJsonRepository<>("data/alerts.json",
            new TypeToken<List<Alert>>() {}) {};

    // THREAD
    public MessageThread createThread(MessageThread t) {
        t.setId(IdGenerator.nextId("thread"));
        threadRepo.save(t);
        return t;
    }

    public Optional<MessageThread> findThreadByPatientId(Long patientId) {
        return threadRepo.findAll().stream()
                .filter(t -> t.getPatientId().equals(patientId))
                .findFirst();
    }
    public List<MessageThread> getThreadsForUser(Long userId) {
        return threadRepo.findAll().stream()
            .filter(t -> 
                t.getPatientId().equals(userId) || 
                (t.getCaregiverId() != null && t.getCaregiverId().equals(userId)) ||
                (t.getDoctorId() != null && t.getDoctorId().equals(userId))
            ).collect(Collectors.toList());
    }

    // MESSAGE
    public Message saveMessage(Message m) {
        m.setId(IdGenerator.nextId("msg"));
        messageRepo.save(m);
        return m;
    }

    public List<Message> getMessagesByThread(Long threadId) {
        return messageRepo.findAll().stream()
            .filter(m -> m.getThreadId().equals(threadId))
            .collect(Collectors.toList());
    }

    // ALERT
    public Alert saveAlert(Alert a) {
        a.setId(IdGenerator.nextId("alert"));
        alertRepo.save(a);
        return a;
    }

    public List<Alert> getAlerts(Long userId) {
        return alertRepo.findAll().stream()
            .filter(a -> a.getUserId().equals(userId))
            .collect(Collectors.toList());
    }
}
