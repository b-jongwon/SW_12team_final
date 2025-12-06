package data.repository;

import com.google.gson.reflect.TypeToken;
import domain.user.User;
import infra.BaseJsonRepository;
import infra.IdGenerator;

import java.util.List;
import java.util.Optional;

/**
 * User 정보를 JSON 파일에 저장/조회하는 리포지토리.
 * - data/users.json 사용
 */
public class UserRepository extends BaseJsonRepository<User> {

    private static final String FILE_PATH = "data/users.json";

    public UserRepository() {
        super(FILE_PATH, new TypeToken<List<User>>() {});
    }

    /** 신규 사용자 저장 (ID 자동 발급) */
    public User saveNewUser(User user) {
        if (user.getId() == null) {
            user.setId(IdGenerator.nextId("user"));
        }
        List<User> all = findAll();
        all.add(user);
        saveAll(all);
        return user;
    }

    /** 로그인 ID로 사용자 조회 */
    public Optional<User> findByLoginId(String loginId) {
        return findAll().stream()
                .filter(u -> loginId.equals(u.getLoginId()))
                .findFirst();
    }

    /** PK(ID)로 사용자 조회 */
    public Optional<User> findById(Long id) {
        return findAll().stream()
                .filter(u -> id.equals(u.getId()))
                .findFirst();
    }
}
