package app.xacml.pep;

import app.constant.OperationType;
import app.constant.ResourceType;
import app.constant.UserLevel;
import app.exception.CustomErrorException;
import app.bean.TokenPool;
import app.xacml.pdp.My_PDP;
import app.xacml.pip.My_PIP;
import app.xacml.risk_engine.MyRiskEngine;
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
    private final MyRiskEngine myRiskEngine;
    @Autowired
    public My_PEP(My_PIP pip, My_PDP pdp, TokenPool tokenPool, MyRiskEngine myRiskEngine) {
        this.pip = pip;
        this.pdp = pdp;
        this.tokenPool = tokenPool;
        this.myRiskEngine = myRiskEngine;
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
            String officeType = "";
            officeType = pip.getOfficeById((Long) joinPoint.getArgs()[1]).getOfficeType().toString();
//            Long recordId= (Long) joinPoint.getArgs()[2];
            if(officeType.equals("")){
                log.warn("Absent location detected");
                throw new CustomErrorException("Access denied, location unknown");
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
            if (requiredLevel != UserLevel.Any && pip.getUserRole(userId) != requiredLevel) {
                log.warn("Insufficient authorisation detected -> " + token);
                throw new CustomErrorException("Access denied, you have no privileges to access this content.");
            }
            // Check if the user is authorized to access the resource for the given conditions
            if(!pdp.XACML_response(pip.getUserRole(userId).toString(), officeType, operationType.toString(), resourceType.toString())){
                log.warn("Insufficient authorisation detected: User [" + pip.getUserRole(userId)+"] "+pip.getUserName(userId) + " at "+officeType);
                throw new CustomErrorException("Access denied, may try it again later.");
            }
            System.out.println("Score: " + myRiskEngine.evaluateRiskReturnRiskScore(userId,(long)1,operationType,(Long) joinPoint.getArgs()[1]));
            log.info("Token approved to execute " + method.getName());
            return joinPoint.proceed();
        } catch (Exception e){
            log.warn(e.getMessage());
            throw new CustomErrorException("Access denied.");
        }
    }
}
