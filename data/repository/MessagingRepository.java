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

    // ----------------------------------------------------
    // MessageThread 관련
    // ----------------------------------------------------
    public List<MessageThread> findAllThreads() {
        return threadRepo.findAll();
    }

    public void saveAllThreads(List<MessageThread> threads) {
        threadRepo.saveAll(threads);
    }

    public MessageThread createThread(MessageThread t) {
        if (t.getId() == null) {
            t.setId(IdGenerator.nextId("thread"));
        }
        threadRepo.save(t);
        return t;
    }

    public Optional<MessageThread> findThreadByPatientId(Long patientId) {
        return threadRepo.findAll().stream()
                .filter(t -> t.getPatientId().equals(patientId))
                .findFirst();
    }

    // ----------------------------------------------------
    // Message 관련
    // ----------------------------------------------------
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

    // ----------------------------------------------------
    // Alert (알림) 관련 [핵심]
    // ----------------------------------------------------
    public Alert saveAlert(Alert a) {
        a.setId(IdGenerator.nextId("alert"));
        alertRepo.save(a); // 파일(alerts.json)에 즉시 저장됨
        return a;
    }

    // [통합] 사용자 ID로 알림 조회
    public List<Alert> getAlerts(Long userId) {
        return alertRepo.findAll().stream()
                .filter(a -> a.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
}