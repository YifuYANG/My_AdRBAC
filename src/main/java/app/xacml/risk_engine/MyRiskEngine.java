package app.xacml.risk_engine;

import app.constant.OperationType;
import app.constant.UserLevel;
import app.model.MedicalRecord;
import app.model.Office;
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
    private double riskScore = 1.0;
    public double evaluateRiskReturnRiskScore(Long userId, Long recordId, OperationType operationType,Long currentOfficeId){
        evaluateRiskBasedOnResourceSensitivity(recordId);
        evaluateRiskBasedOnUserHistory(userId);
        evaluateRiskBasedOnOperationType(operationType);
        evaluateRiskBasedOnOfficeSite(userId,currentOfficeId);
        evaluateRiskBasedOnIfInsideSensitiveTimePeriod();
        return riskScore;
    }

    private void evaluateRiskBasedOnIfInsideSensitiveTimePeriod() {
        LocalTime currentTime = LocalTime.now();
        if (currentTime.isAfter(LocalTime.of(6, 0)) && currentTime.isBefore(LocalTime.of(9, 0))) {
            riskScore *= 0.9;
        } else if (currentTime.isAfter(LocalTime.of(18, 0)) && currentTime.isBefore(LocalTime.of(21, 0))) {
            riskScore *= 0.7;
        } else if (currentTime.isAfter(LocalTime.of(21, 0)) && currentTime.isBefore(LocalTime.of(23, 59))) {
            riskScore *= 0.5;
        }
    }

    private void evaluateRiskBasedOnOfficeSite(Long userId, Long currentOfficeId){
        Office userOffice = pip.getOfficeByUserId(userId);
        Office currentOffice = pip.getOfficeById(currentOfficeId);
        if(!userOffice.getOfficeName().equals(currentOffice.getOfficeName())){
            riskScore*=0.7;
        }
    }

    private void evaluateRiskBasedOnResourceSensitivity(Long recordId){
        MedicalRecord resource = pip.getResourceByRecordId(recordId);
        switch (resource.getResourceSensitivity()) {
            case Internal -> riskScore *= 0.9;
            case Confidential -> riskScore *= 0.7;
            case Restricted -> riskScore *= 0.5;
            default -> {
            }
        }
    }

    private void evaluateRiskBasedOnUserHistory(Long userId){
        List<RiskHistory> riskHistoryList = pip.getRiskHistoryByUserId(userId);
        if (riskHistoryList!=null){
            if(riskHistoryList.size()<=3){
                riskScore*=0.7;
            } else {
                riskScore*=0.5;
            }
        }
    }

    private void evaluateRiskBasedOnOperationType(OperationType operationType){
        switch (operationType) {
            case Read -> riskScore *= 0.9;
            case Write -> riskScore *= 0.7;
            case Delete -> riskScore *= 0.5;
            default -> {
            }
        }
    }
}
