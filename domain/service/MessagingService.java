package domain.service;

import data.repository.MessagingRepository;
import domain.messaging.Message;
import domain.messaging.MessageThread;
import domain.patient.Alert;

import java.util.List;
import java.util.stream.Collectors;

public class MessagingService {

    private final MessagingRepository repo = new MessagingRepository();

    public MessageThread getOrCreatePatientRoom(Long patientId) {
        return repo.findThreadByPatientId(patientId)
                .orElseGet(() -> {
                    MessageThread newThread = new MessageThread();
                    newThread.create(patientId);
                    return repo.createThread(newThread);
                });
    }

    public void joinRoom(Long patientId, Long doctorId, Long caregiverId) {
        MessageThread thread = getOrCreatePatientRoom(patientId);
        if (doctorId != null) thread.setDoctorId(doctorId);
        if (caregiverId != null) thread.addCaregiver(caregiverId);
        updateThread(thread);
    }

    private void updateThread(MessageThread updated) {
        List<MessageThread> all = repo.findAllThreads();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(updated.getId())) {
                all.set(i, updated);
                break;
            }
        }
        repo.saveAllThreads(all);
    }

    public Message sendMessage(Long threadId, Long senderId, String content) {
        Message m = new Message();
        m.setThreadId(threadId);
        m.send(senderId, content);
        repo.saveMessage(m);

        Alert a = new Alert();
        a.create(senderId, "새 메시지가 도착했습니다.");
        repo.saveAlert(a);
        return m;
    }

    public List<Message> getMessages(Long threadId) {
        return repo.getMessagesByThread(threadId);
    }

    public List<MessageThread> getThreads(Long userId) {
        return repo.findAllThreads().stream()
                .filter(t ->
                        t.getPatientId().equals(userId) ||
                                (t.getDoctorId() != null && t.getDoctorId().equals(userId)) ||
                                t.getCaregiverIds().contains(userId)
                ).collect(Collectors.toList());
    }

    // [확인] 알림 조회 연결
    public List<Alert> getAlerts(Long userId) {
        return repo.getAlerts(userId);
    }
}