package app.controller;


import app.constant.UserLevel;
import app.dao.LoginDao;
import app.exception.CustomErrorException;
import app.model.User;
import app.repository.UserRepository;
import app.bean.TokenPool;
import app.vo.LoginForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@Slf4j
public class LoginController {

    private final LoginDao loginDao;

    @Autowired
    public LoginController(LoginDao loginDao) {
        this.loginDao = loginDao;
    }

    @PostMapping("/login")
    @ResponseBody
    public Map<String, Object> login(@RequestBody LoginForm loginForm) throws CustomErrorException{
        try {
            Map<String, Object> map = new HashMap<>(3);
            User user = loginDao.findUserByEmail(loginForm.getUserEmail());
            if(user == null){
                map.put("status", "fail");
                map.put("msg", "Account does not exist.");
            } else if(loginForm.getPassword().equals("")){
                map.put("status", "fail");
                map.put("msg", "Password filed should not be empty.");
            } else if (!emailValidator(loginForm.getUserEmail())){
                map.put("status", "fail");
                map.put("msg", "Wrong email type.");
            } else {
                if(!user.getPassword().equals(loginForm.getPassword())){
                    map.put("status", "fail");
                    map.put("msg", "Wrong password.");
                } else {
                    String token = loginDao.generateToken();
                    loginDao.loginUser(user.getUserId(),token);
                    log.info("Token issued to " + user.getLast_name()+" "+user.getFirst_name());
                    map.put("status", "success");
                    map.put("token", token);
                    map.put("role", loginDao.getUserRoleByToken(token).name());
                }
            }
            return map;
        } catch (Exception e){
            throw new CustomErrorException("Error!!!");
        }
    }

    private Boolean emailValidator(String email){
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
