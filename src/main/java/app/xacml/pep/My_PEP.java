package app.xacml.pep;

import app.constant.UserLevel;
import app.exception.CustomErrorException;
import app.repository.UserRepository;
import app.token_pool.TokenPool;
import app.xacml.pdp.My_PDP;
import app.xacml.pip.My_PIP;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
public class My_PEP {
    private final My_PIP pip;
    private final My_PDP pdp;
    private final TokenPool tokenPool;
    @Autowired
    public My_PEP(My_PIP pip, My_PDP pdp, TokenPool tokenPool) {
        this.pip = pip;
        this.pdp = pdp;
        this.tokenPool = tokenPool;
    }
    @Pointcut("@annotation(app.xacml.pep.PEP_Interceptor)")
    public void pointcut() {
    }
    @Around("pointcut()")
    public Object restrictUserAccess(ProceedingJoinPoint joinPoint) throws Throwable{
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        UserLevel requiredLevel = method.getAnnotation(PEP_Interceptor.class).requiredLevel();
        try {
            String token = (String) joinPoint.getArgs()[0];
            token = token.replaceAll("\"", "");
            if(token.length() == 0) {
                log.warn("Absent token detected");
                throw new CustomErrorException("Access denied, please login first.");
            }
            if(!tokenPool.containsToken(token)) {
                log.warn("Invalid token detected -> " + token);
                throw new CustomErrorException("Access denied, invalid token >> " + token);
            }
            Long userId = tokenPool.getUserIdByToken(token);
            //Then check if the token is expired
            if(!tokenPool.validateTokenExpiry(token, LocalDateTime.now())) {
                log.warn("Expired token detected -> " + token);
                throw new CustomErrorException("Access denied, your token has been expired, please re-login.");
            }
            if(requiredLevel == UserLevel.any) {
                if(pdp.XACML_response(pip.getUserRole(userId).toString(), pip.getLocation(), pip.getAction(), pip.getResource())){
                    log.info("Token approved to execute " + method.getName());
                    return joinPoint.proceed();
                }
            } else if(pip.getUserRole(userId) != requiredLevel) {
                log.warn("Insufficient authorisation detected -> " + token);
                throw new CustomErrorException("Access denied, you have no privileges to access this content.");
            }
            if(!pdp.XACML_response(pip.getUserRole(userId).toString(), pip.getLocation(), pip.getAction(), pip.getResource())){
                log.warn("Insufficient authorisation detected -> " + token);
                throw new CustomErrorException("Access denied, you can not access this content at this time.");
            }
            log.info("Token approved to execute " + method.getName());
            return joinPoint.proceed();
        } catch (Exception e){
            log.warn(e.getMessage());
            throw new CustomErrorException("Access denied.");
        }
    }
}
