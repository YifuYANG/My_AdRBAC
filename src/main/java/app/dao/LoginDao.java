package app.dao;

import app.bean.TokenPool;
import app.constant.UserLevel;
import app.model.User;
import app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginDao {
    private final UserRepository userRepository;
    private final TokenPool tokenPool;

    @Autowired
    public LoginDao(UserRepository userRepository, TokenPool tokenPool) {
        this.userRepository = userRepository;
        this.tokenPool = tokenPool;
    }

    public UserLevel getUserRoleByToken(String token) {
        Long userId = tokenPool.getUserIdByToken(token);
        return userRepository.findByUserId(userId).getUserLevel();
    }

    public User findUserByEmail(String email){
        return userRepository.findByUserEmail(email);
    }

    public String generateToken(){
        return tokenPool.generateToken();
    }

    public void loginUser(Long id, String token){
        tokenPool.login(id, token);
    }
}
