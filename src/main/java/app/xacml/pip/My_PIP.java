package app.xacml.pip;

import app.constant.UserLevel;
import app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class My_PIP {
    private final UserRepository userRepository;

    @Autowired
    public My_PIP(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getUserName(long userId){
        return userRepository.findByUserId(userId).getLast_name()+ " " +userRepository.findByUserId(userId).getFirst_name();
    }

    public UserLevel getUserRole(long userId){
        return userRepository.findByUserId(userId).getUserLevel();
    }

}
