package app.xacml.risk_engine;


import app.constant.AccessLevel;
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
                                              Long currentOfficeId){
        final double officeSiteWeightage = 0.3;
        final double sensitiveTimePeriodWeightage = 0.3;
        final double userHistoryWeightage = 0.4;

        double Risk = (getUserHistoryRisk(userId) * userHistoryWeightage) +
                (getSensitiveTimePeriodRisk(userId) * sensitiveTimePeriodWeightage) +
                (getOfficeSiteRisk(userId,currentOfficeId) * officeSiteWeightage);

        System.out.println("Risk: "+Risk);
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
        System.out.println("User History: "+risk);
        return risk;
    }

    private double getOfficeSiteRisk(Long userId, Long currentOfficeId){
        double risk = 0.0;
        if(!pip.getOfficeNameByUserID(userId).equals(pip.getOfficeNameById(currentOfficeId))){
            risk = 0.8;
        }
        System.out.println("Office Site: "+risk);
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
        System.out.println("Time: "+ risk);
        return risk;
    }

    private double getAccessLevelRisk(Long userId, Long recordId){
        UserLevel userRole = pip.getUserRole(userId);
        ResourceSensitivity resourceSensitivity = pip.getResourceSensitivityByRecordId(recordId);
        if (userRole == UserLevel.Nurse) {
            if (resourceSensitivity == ResourceSensitivity.Confidential) {
                return 0.2;
            }
            if (resourceSensitivity == ResourceSensitivity.Restricted) {
                return 0.4;
            }
        }
        if (userRole == UserLevel.Doctor && resourceSensitivity == ResourceSensitivity.Restricted) {
            return 0.4;
        }
        if (userRole == UserLevel.SocialWorker) {
            if (resourceSensitivity == ResourceSensitivity.Confidential) {
                return 0.4;
            }
            if (resourceSensitivity == ResourceSensitivity.Restricted) {
                return 0.7;
            }
        }
        if (userRole == UserLevel.PoliceOfficer && resourceSensitivity == ResourceSensitivity.Restricted) {
            return 0.4;
        }
        return 0.0;
    }
    private double getContextualRisk(Long userId, Long currentOfficeId){
        if(getSensitiveTimePeriod(userId) && getOfficeSite(userId, currentOfficeId)){
            return 0.2;
        } else if(!getSensitiveTimePeriod(userId) && getOfficeSite(userId, currentOfficeId)){
            return 0.4;
        } else {
            return 0.7;
        }
    }

    private double getOperationRisk(OperationType operationType){
        return switch (operationType) {
            case Read -> 0.2;
            case Write -> 0.4;
            case Delete -> 0.7;
        };
    }
    private boolean getSensitiveTimePeriod(Long userId){
        LocalTime currentTime = LocalTime.now();
        if(pip.getStartTimeByUserID(userId)!=null && pip.getEndTimeByUserID(userId)!=null){
            return currentTime.isAfter(pip.getStartTimeByUserId(userId)) && currentTime.isBefore(pip.getEndTimeByUserId(userId));
        }
        return false;
    }

    private boolean getOfficeSite(Long userId, Long currentOfficeId){
        return pip.getOfficeNameByUserID(userId).equals(pip.getOfficeNameById(currentOfficeId));
    }
}
