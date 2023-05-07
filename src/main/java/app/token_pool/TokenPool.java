package app.token_pool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Slf4j
public class TokenPool {

    //pool object maps <Long userId -> String token>
    private Map<Long, String> pool = new HashMap<>();

    public String generateToken() {
        LocalDateTime date = LocalDateTime.now().plusMinutes(15); //Token will be expired after 15 minutes
        String uuid = UUID.randomUUID().toString();
        String content = uuid + ";" + date;
        String token = Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
        return token;
    }

    public Long getUserIdByToken(String token) {
        for (Map.Entry<Long, String> entry : pool.entrySet()) {
            if (entry.getValue().equals(token))
                return entry.getKey();
        }
        return -1L;
    }

    public String getTokenByUserId(Long id) {
        return pool.get(id);
    }

    public void login(Long userId, String token) {
        pool.put(userId, token);
    }

    public void logout(String token) {
        for (Map.Entry<Long, String> entry : pool.entrySet())
            if (entry.getValue().equals(token)) pool.remove(entry.getKey());
    }

    public boolean containsUserId(Long userId) {
        return pool.containsKey(userId);
    }

    public boolean containsToken(String token) {
        return pool.containsValue(token);
    }

    public boolean validateTokenExpiry(String token, LocalDateTime dest) {
        String decodedToken = new String(Base64.getDecoder().decode(token.getBytes(StandardCharsets.UTF_8)));
        LocalDateTime tokenDate = LocalDateTime.parse(decodedToken.split(";")[1]);
        if(!tokenDate.isAfter(dest)) {
            removeToken(token);
            log.info("Cleaned expired token >> " + token);
            return false;
        }
        return true;
    }

    public String toString() {
        return pool.toString();
    }

    private void removeToken(String token) {
        Long key = getUserIdByToken(token);
        pool.remove(key);
    }

}
