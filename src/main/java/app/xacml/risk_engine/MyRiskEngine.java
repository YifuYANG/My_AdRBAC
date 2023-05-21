package app.xacml.risk_engine;

import app.constant.OperationType;
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
    private double riskScore = 0.0;
    public double evaluateRiskReturnRiskScore(Long userId, Long recordId, OperationType operationType,Long currentOfficeId){
        riskScore = 10.0;
        evaluateRiskBasedOnIfInsideSensitiveTimePeriod(operationType,userId);
        //evaluateRiskBasedOnResourceSensitivity(recordId);
        evaluateRiskBasedOnUserHistory(userId);
        evaluateRiskBasedOnOfficeSite(userId,currentOfficeId);
        return riskScore;
    }

    private void evaluateRiskBasedOnIfInsideSensitiveTimePeriod(OperationType operationType,Long userId) {
        LocalTime currentTime = LocalTime.now();
        if (currentTime.isAfter(pip.getTimeTableByUserId(userId).getStartTime()) && currentTime.isBefore(pip.getTimeTableByUserId(userId).getEndTime())){
            evaluateRiskBasedOnOperationType(operationType, 1.0,1.0,1.0);
        } else {
            evaluateRiskBasedOnOperationType(operationType, 0.8,0.7,0.6);
        }
    }

    private void evaluateRiskBasedOnOfficeSite(Long userId, Long currentOfficeId){
        Office userOffice = pip.getOfficeByUserId(userId);
        Office currentOffice = pip.getOfficeById(currentOfficeId);
        if(!userOffice.getOfficeName().equals(currentOffice.getOfficeName())){
            riskScore*=0.8;
        }
    }

    private void evaluateRiskBasedOnResourceSensitivity(Long recordId){
        MedicalRecord resource = pip.getResourceByRecordId(recordId);
        switch (resource.getResourceSensitivity()) {
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

    private void evaluateRiskBasedOnOperationType(OperationType operationType, double read_parameter, double write_parameter, double delete_parameter){
        switch (operationType) {
            case Read -> riskScore *= read_parameter;
            case Write -> riskScore *= write_parameter;
            case Delete -> riskScore *= delete_parameter;
            default -> {
            }
        }
    }
}
