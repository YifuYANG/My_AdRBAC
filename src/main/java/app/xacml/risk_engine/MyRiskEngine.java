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
    private double riskScore = 1.0;
    public double evaluateRiskReturnRiskScore(UserLevel userLevel, Long userId, Long recordId, OperationType operationType){
        if(userLevel==UserLevel.Admin){
            evaluateRiskBasedOnIfInsideSensitiveTimePeriod();
            evaluateRiskBasedOnResourceSensitivity(recordId);
            evaluateRiskBasedOnUserHistory(userId);
            evaluateRiskBasedOnOperationType(operationType);
        }
        return riskScore;
    }

    private void evaluateRiskBasedOnIfInsideSensitiveTimePeriod() {
        LocalTime currentTime = LocalTime.now();
        LocalTime startTime_1 = LocalTime.of(6, 0);
        LocalTime endTim_1 = LocalTime.of(9, 0);

        LocalTime startTime_2 = LocalTime.of(18, 0);
        LocalTime endTime_2 = LocalTime.of(21, 0);

        LocalTime startTime_3 = LocalTime.of(21, 0);
        LocalTime endTime_3 = LocalTime.of(23, 0);
    }

    private void evaluateRiskBasedOnResourceSensitivity(Long recordId){
        ResourceSensitivity resourceSensitivity = pip.getResourceSensitivity(recordId);
    }

    private void evaluateRiskBasedOnUserHistory(Long userId){
        List<RiskHistory> riskHistoryList = pip.getRiskHistory(userId);
    }

    private void evaluateRiskBasedOnOperationType(OperationType operationType){
        if(operationType == OperationType.Write){

        }

        if(operationType == OperationType.Read){

        }

        if(operationType == OperationType.Delete){

        }
    }
}
