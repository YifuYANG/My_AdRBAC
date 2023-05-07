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

    public String getLocation(){
        return "office";
    }

    public UserLevel getUserRole(long userId){
        return userRepository.findByUserId(userId).getUserLevel();
    }

    public String getAction(){
        return "read";
    }

    public String getResource(){
        return "medical_record";
    }
}
