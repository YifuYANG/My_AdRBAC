package app.xacml.risk_engine;

import app.constant.OperationType;
import app.constant.ResourceSensitivity;
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
    private double riskScore = 0.0;
    public double evaluateRiskReturnRiskScore(Long userId, Long recordId,
                                              Long currentOfficeId){
        riskScore = 10.0;
        evaluateRiskBasedOnIfInsideSensitiveTimePeriod(userId);
        //evaluateRiskBasedOnResourceSensitivity(recordId);
        evaluateRiskBasedOnUserHistory(userId);
        evaluateRiskBasedOnOfficeSite(userId,currentOfficeId);
        return riskScore;
    }

    private void evaluateRiskBasedOnIfInsideSensitiveTimePeriod(Long userId) {
        LocalTime currentTime = LocalTime.now();
        if (currentTime.isAfter(pip.getStartTimeByUserId(userId)) && currentTime.isBefore(pip.getEndTimeByUserId(userId))){
            riskScore *= 1.0;
        } else {
            riskScore *= 0.8;
        }
    }

    private void evaluateRiskBasedOnOfficeSite(Long userId, Long currentOfficeId){
        if(!pip.getOfficeNameByUserId(userId).equals(pip.getOfficeNameById(currentOfficeId))){
            riskScore*=0.8;
        }
    }

    private void evaluateRiskBasedOnResourceSensitivity(Long recordId){
        ResourceSensitivity resourceSensitivity = pip.getResourceSensitivityByRecordId(recordId);
        switch (resourceSensitivity) {
            case Internal -> riskScore *= 1.0;
            case Confidential -> riskScore *= 0.8;
            case Restricted -> riskScore *= 0.6;
            default -> {
            }
        }
    }

    private void evaluateRiskBasedOnUserHistory(Long userId){
        List<RiskHistory> riskHistoryList = pip.getRiskHistoryByUserId(userId);
        if (riskHistoryList!=null){
            if(riskHistoryList.isEmpty()){
                riskScore*=1.0;
            } else if(riskHistoryList.size()<=3){
                riskScore*=0.7;
            } else {
                riskScore*=0.0;
            }
        }
    }
}
