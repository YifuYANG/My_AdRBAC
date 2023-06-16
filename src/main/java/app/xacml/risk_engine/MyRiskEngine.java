package app.xacml.risk_engine;


import app.constant.OperationType;
import app.constant.ResourceSensitivity;
import app.constant.UserLevel;
import app.model.RiskHistory;
import app.xacml.pip.My_PIP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;


@Component
public class MyRiskEngine {

    private final My_PIP pip;
    @Autowired
    public MyRiskEngine(My_PIP pip) {
        this.pip = pip;
    }

    public double evaluateRiskReturnRiskScore(Long userId, Long recordId,
                                              Long currentOfficeId,OperationType operationType){
        final double officeSiteWeightage = 0.3;
        final double sensitiveTimePeriodWeightage = 0.3;

        final double operationWeightage = 0.1;
        final double userHistoryWeightage = 0.1;
        final double accessWeightage = 0.4;
        final double contextualWeightage = 0.4;

        System.out.println("access risk: "+getAccessRisk(userId,recordId)* accessWeightage);
        System.out.println("operation risk: "+getOperationRisk(operationType)* operationWeightage);
        System.out.println("contextual risk: "+getContextualRisk(userId,currentOfficeId)* contextualWeightage);
        System.out.println("user history risk: "+getUserHistoryRisk(userId)* userHistoryWeightage);

        System.out.print("New Risk is: ");
        System.out.println(
                (getAccessRisk(userId,recordId) * accessWeightage)+
                (getOperationRisk(operationType) * operationWeightage)+
                (getContextualRisk(userId,currentOfficeId) * contextualWeightage)+
                (getUserHistoryRisk(userId) * userHistoryWeightage));

        double Risk = (getUserHistoryRisk(userId) * userHistoryWeightage) +
                (getSensitiveTimePeriodRisk(userId) * sensitiveTimePeriodWeightage) +
                (getOfficeSiteRisk(userId,currentOfficeId) * officeSiteWeightage);

        return Risk;
    }

    private double getUserHistoryRisk(Long userId){
        double risk = 0.7;
        List<RiskHistory> riskHistoryList = pip.getRiskHistoryByUserId(userId);
        if (riskHistoryList!=null){
            if(riskHistoryList.isEmpty()){
                risk = 0;
            } else if(riskHistoryList.size()<=3){
                risk = 0.2;
            }
        }
        return risk;
    }

    private double getOfficeSiteRisk(Long userId, Long currentOfficeId){
        double risk = 0.0;
        if(!pip.getOfficeNameByUserID(userId).equals(pip.getOfficeNameById(currentOfficeId))){
            risk = 0.8;
        }
        return risk;
    }

    private double getSensitiveTimePeriodRisk(Long userId){
        double risk = 0.8;
        LocalTime currentTime = LocalTime.now();
        if(pip.getStartTimeByUserID(userId)!=null && pip.getEndTimeByUserID(userId)!=null){
            if (currentTime.isAfter(pip.getStartTimeByUserId(userId)) && currentTime.isBefore(pip.getEndTimeByUserId(userId))){
                risk = 0.0;
            }
        }
        return risk;
    }

    private double getAccessRisk(Long userId, Long recordId){
        UserLevel userRole = pip.getUserRole(userId);
        ResourceSensitivity resourceSensitivity = pip.getResourceSensitivityByRecordId(recordId);
        if (userRole == UserLevel.Nurse) {
            if (resourceSensitivity == ResourceSensitivity.Confidential) {
                return 0.6;
            }
            if (resourceSensitivity == ResourceSensitivity.Restricted) {
                return 0.8;
            }
        }
        if (userRole == UserLevel.Doctor) {
            if (resourceSensitivity == ResourceSensitivity.Confidential) {
                return 0.4;
            }
            if (resourceSensitivity == ResourceSensitivity.Restricted) {
                return 0.6;
            }
        }
        if (userRole == UserLevel.SocialWorker) {
            if (resourceSensitivity == ResourceSensitivity.Confidential) {
                return 0.6;
            }
            if (resourceSensitivity == ResourceSensitivity.Restricted) {
                return 0.8;
            }
        }
        if (userRole == UserLevel.PoliceOfficer && resourceSensitivity == ResourceSensitivity.Restricted) {
            return 0.6;
        }
        return 0.0;
    }
    private double getContextualRisk(Long userId, Long currentOfficeId){
        boolean onDuty = isInSensitiveTimePeriod(userId);
        boolean sameOfficeSite = isInSameOfficeSite(userId, currentOfficeId);
        boolean sameOfficeType = isInSameOfficeType(userId, currentOfficeId);
        if (onDuty) {
            if (sameOfficeSite) {
                return 0.0;
            } else {
                return sameOfficeType ? 0.2 : 0.4;
            }
        } else {
            if (sameOfficeSite) {
                return 0.4;
            } else {
                return sameOfficeType ? 0.6 : 0.8;
            }
        }
    }

    private double getOperationRisk(OperationType operationType){
        return switch (operationType) {
            case Read -> 0.2;
            case Write -> 0.4;
            case Delete -> 0.7;
        };
    }
    private boolean isInSensitiveTimePeriod(Long userId){
        LocalTime currentTime = LocalTime.now();
        if(pip.getStartTimeByUserID(userId)!=null && pip.getEndTimeByUserID(userId)!=null){
            return currentTime.isAfter(pip.getStartTimeByUserId(userId)) && currentTime.isBefore(pip.getEndTimeByUserId(userId));
        }
        return false;
    }

    private boolean isInSameOfficeSite(Long userId, Long currentOfficeId){
        return pip.getOfficeNameByUserID(userId).equals(pip.getOfficeNameById(currentOfficeId));
    }

    private boolean isInSameOfficeType(Long userId, Long currentOfficeId){
        return pip.getOfficeTypeById(currentOfficeId) == pip.getOfficeTypeByUserID(userId);
    }
}
