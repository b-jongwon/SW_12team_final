
package infra;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {

    private static final ConcurrentHashMap<String, AtomicLong> map = new ConcurrentHashMap<>();

    public static synchronized long nextId(String key) {
        AtomicLong seq = map.computeIfAbsent(key, k -> new AtomicLong(loadLastId(k)));
        long next = seq.incrementAndGet();
        saveLastId(key, next);
        return next;
    }

    private static long loadLastId(String key) {
        File file = new File("data/ids_" + key + ".txt");
        if (!file.exists()) return 0L;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            return line == null ? 0L : Long.parseLong(line);
        } catch (Exception e) {
            return 0L;
        }
    }

    private static void saveLastId(String key, long value) {
        File file = new File("data/ids_" + key + ".txt");
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(Long.toString(value));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
