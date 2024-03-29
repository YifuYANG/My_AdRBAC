package app.xacml.pep;

import app.constant.OperationType;
import app.constant.ResourceType;
import app.constant.UserLevel;
import app.dao.PEPDao;
import app.exception.CustomErrorException;
import app.vo.MedicalRecordForm;
import app.xacml.pdp.My_PDP;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class My_PEP {
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    private final PEPDao pepDao;
    private final My_PDP pdp;
    @Autowired
    public My_PEP(PEPDao pepDao, My_PDP pdp) {
        this.pepDao = pepDao;
        this.pdp = pdp;
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
            Long officeId= (Long) joinPoint.getArgs()[1];
            Object[] args = joinPoint.getArgs();
            Long recordId;
            if(args[2] instanceof Long){
                recordId= (Long) args[2];
            } else {
                MedicalRecordForm medicalRecordForm=(MedicalRecordForm) args[2];
                recordId = medicalRecordForm.getRecordId();
            }
            if(token.length() == 0) {
                log.warn("Absent token detected");
                throw new CustomErrorException("Access denied, please login first.");
            }
            if(officeId==null){
                log.warn("Absent location detected");
                throw new CustomErrorException("Access denied, location unknown");
            }
            // Check if the token exist
            if(!pepDao.containsToken(token)) {
                log.warn("Invalid token detected -> " + token);
                throw new CustomErrorException("Access denied, invalid token >> " + token);
            }
            Long userId = pepDao.getUserIdByToken(token);
            // Check if the token is expired
            if(!pepDao.validateTokenExpiry(token)) {
                log.warn("Expired token detected -> " + token);
                throw new CustomErrorException("Access denied, your token has been expired, please re-login.");
            }
            // Check if the user has the required authorization level -> Role Based Control
            if (requiredLevel != UserLevel.Any && pepDao.findUserByUserId(userId).getUserLevel() != requiredLevel) {
                log.warn("Insufficient authorisation detected -> " + token);
                throw new CustomErrorException("Access denied, you have no privileges to access this content.");
            }
            // Check pdp and receives decision -> ABAC risk aware
            if(!pdp.XACML_response(userId,officeId,operationType,resourceType.toString(),recordId)){
                log.warn("Insufficient authorisation detected: " +ANSI_RED+
                        pepDao.findUserByUserId(userId).getLast_name()+ " " +pepDao.findUserByUserId(userId).getFirst_name() + ANSI_RESET +
                        " at " +ANSI_YELLOW +pepDao.findOfficeByOfficeId(officeId).get(0).getOfficeName()+ANSI_RESET);
                throw new CustomErrorException("Access deny.");
            }
            log.info("Token approved to execute " + method.getName());
            return joinPoint.proceed();
        } catch (Exception e){
            log.warn(e.getMessage());
            throw new CustomErrorException("Access denied.");
        }
    }
}
