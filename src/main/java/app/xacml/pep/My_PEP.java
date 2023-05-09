package app.xacml.pep;

import app.constant.OperationType;
import app.constant.ResourceType;
import app.constant.UserLevel;
import app.exception.CustomErrorException;
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
        OperationType operationType = method.getAnnotation(PEP_Interceptor.class).operationType();
        ResourceType resourceType = method.getAnnotation(PEP_Interceptor.class).resourceType();
        try {
            String token = (String) joinPoint.getArgs()[0];
            token = token.replaceAll("\"", "");
            String location = "";
            if(joinPoint.getArgs().length==2){
                location = (String) joinPoint.getArgs()[1];
                if(location.equals("")){
                    log.warn("Absent location detected");
                    throw new CustomErrorException("Access denied, location unknown");
                }
            }
            if(token.length() == 0) {
                log.warn("Absent token detected");
                throw new CustomErrorException("Access denied, please login first.");
            }
            // Check if the token exist
            if(!tokenPool.containsToken(token)) {
                log.warn("Invalid token detected -> " + token);
                throw new CustomErrorException("Access denied, invalid token >> " + token);
            }
            Long userId = tokenPool.getUserIdByToken(token);
            // Check if the token is expired
            if(!tokenPool.validateTokenExpiry(token, LocalDateTime.now())) {
                log.warn("Expired token detected -> " + token);
                throw new CustomErrorException("Access denied, your token has been expired, please re-login.");
            }
            // Check if the user has the required authorization level
            if (requiredLevel != UserLevel.any && pip.getUserRole(userId) != requiredLevel) {
                log.warn("Insufficient authorisation detected -> " + token);
                throw new CustomErrorException("Access denied, you have no privileges to access this content.");
            }
            // Check if the user is authorized to access the resource for the given conditions
            if(!pdp.XACML_response(pip.getUserRole(userId).toString(), location, operationType.toString(), resourceType.toString())){
                log.warn("Insufficient authorisation detected: User [" + pip.getUserRole(userId)+"] "+pip.getUserName(userId) + " at "+location);
                throw new CustomErrorException("Access denied, may try it again later.");
            }
            log.info("Token approved to execute " + method.getName());
            return joinPoint.proceed();
        } catch (Exception e){
            log.warn(e.getMessage());
            throw new CustomErrorException("Access denied.");
        }
    }
}
