package app.dao;


import app.bean.TokenPool;
import app.model.Office;
import app.model.User;
import app.repository.OfficeRepository;
import app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PEPDao {

    private final UserRepository userRepository;
    private final OfficeRepository officeRepository;
    private final TokenPool tokenPool;

    @Autowired
    public PEPDao(UserRepository userRepository, OfficeRepository officeRepository, TokenPool tokenPool) {
        this.userRepository = userRepository;
        this.officeRepository = officeRepository;
        this.tokenPool = tokenPool;
    }

    public User findUserByUserId(Long userId){
        return userRepository.findByUserId(userId);
    }

    public Office findOfficeByOfficeId(Long officeId){
        return officeRepository.findByOfficeId(officeId);
    }

    public Boolean containsToken(String token){
        return tokenPool.containsToken(token);
    }

    public Long getUserIdByToken(String token){
        return tokenPool.getUserIdByToken(token);
    }

    public Boolean validateTokenExpiry(String token){
        return tokenPool.validateTokenExpiry(token, LocalDateTime.now());
    }
}
