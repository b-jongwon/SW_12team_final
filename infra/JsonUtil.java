package infra;

import com.google.gson.*;
import domain.user.*; // User 패키지 전체 임포트

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class JsonUtil {


    // JSON을 읽어서 role을 확인한 뒤, Doctor/Patient 등 맞는 클래스로 매핑해줍니다.
    private static final JsonDeserializer<User> userDeserializer = (json, typeOfT, context) -> {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement roleElement = jsonObject.get("role");

        if (roleElement == null) {
            // 역할 정보가 없으면 기본 User로
            return new Gson().fromJson(json, User.class);
        }

        String role = roleElement.getAsString();
        try {
            switch (role) {
                case "DOCTOR":
                    return context.deserialize(json, Doctor.class);
                case "PATIENT":
                    return context.deserialize(json, Patient.class);
                case "CAREGIVER":
                    return context.deserialize(json, Caregiver.class);
                case "ADMIN":
                    return context.deserialize(json, Admin.class);
                default:
                    return context.deserialize(json, User.class);
            }
        } catch (Exception e) {
            return context.deserialize(json, User.class);
        }
    };

    private static final Gson gson = new GsonBuilder()
            // 1. 날짜(LocalDateTime) 처리 어댑터
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                    LocalDateTime.parse(json.getAsString()))

            // 2. User 상속 구조 처리 어댑터 등록
            .registerTypeAdapter(User.class, userDeserializer)
            .setPrettyPrinting() // (선택사항) 파일 저장 시 줄바꿈/들여쓰기 적용
            .create();

    // synchronized 추가: 동시에 여러 스레드가 읽으려 할 때 충돌 방지
    public static synchronized <T> T readJson(String path, Type typeOfT) {
        File file = new File(path);
        if (!file.exists()) return null;
        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, typeOfT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // [수정] synchronized 추가: 동시에 여러 스레드가 쓰려고 할 때 데이터 유실 방지
    public static synchronized void writeJson(String path, Object data) {
        File file = new File(path);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}