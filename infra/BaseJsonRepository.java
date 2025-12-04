
package infra;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseJsonRepository<T> {

    private final String filePath;
    private final Type listType;

    protected BaseJsonRepository(String filePath, TypeToken<List<T>> typeToken) {
        this.filePath = filePath;
        this.listType = typeToken.getType();
    }

    protected List<T> loadAll() {
        List<T> list = JsonUtil.readJson(filePath, listType);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public void saveAll(List<T> list) {
        JsonUtil.writeJson(filePath, list);
    }

    public List<T> findAll() {
        return loadAll();
    }

    public void save(T item) {
        List<T> list = loadAll();
        list.add(item);
        saveAll(list);
    }
}
