
package data.repository;

import domain.user.User;
import infra.BaseJsonRepository;
import infra.IdGenerator;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Optional;

public class UserRepository extends BaseJsonRepository<User> {

    public UserRepository() {
        super("data/users.json", new TypeToken<List<User>>(){});
    }

    public UserRepository(String filePath, TypeToken<List<User>> typeToken) {
        super(filePath, typeToken);
    }

    public User saveNewUser(User user) {
        if (user.getId() == null) {
            user.setId(IdGenerator.nextId("user"));
        }
        save(user);
        return user;
    }

    public Optional<User> findByLoginId(String loginId) {
        return findAll().stream()
                .filter(u -> u.getLoginId().equals(loginId))
                .findFirst();
    }
}
