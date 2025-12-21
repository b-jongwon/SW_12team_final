package data.repository;

import com.google.gson.reflect.TypeToken;
import domain.user.User;
import infra.BaseJsonRepository;
import infra.IdGenerator;

import java.util.List;
import java.util.Optional;


public class UserRepository extends BaseJsonRepository<User> {

    private static final String FILE_PATH = "data/users.json";

    public UserRepository() {
        super(FILE_PATH, new TypeToken<List<User>>() {});
    }


    public User saveNewUser(User user) {
        if (user.getId() == null) {
            user.setId(IdGenerator.nextId("user"));
        }
        List<User> all = findAll();
        all.add(user);
        saveAll(all);
        return user;
    }


    public Optional<User> findByLoginId(String loginId) {
        return findAll().stream()
                .filter(u -> loginId.equals(u.getLoginId()))
                .findFirst();
    }

    public Optional<User> findById(Long id) {
        return findAll().stream()
                .filter(u -> id.equals(u.getId()))
                .findFirst();
    }
}
