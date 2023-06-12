package app.xacml.risk_engine;

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
        double risk = 1.0;
        List<RiskHistory> riskHistoryList = pip.getRiskHistoryByUserId(userId);
        if (riskHistoryList!=null){
            if(riskHistoryList.isEmpty()){
                risk = 0;
            } else if(riskHistoryList.size()<=3){
                risk = 0.6;
            }
        }
        System.out.println("User History: "+risk);
        return risk;
    }

    private double getOfficeSiteRisk(Long userId, Long currentOfficeId){
        double risk = 0.0;
        if(!pip.getOfficeNameByUserId(userId).equals(pip.getOfficeNameById(currentOfficeId))){
            risk = 0.8;
        }
        System.out.println("Office Site: "+risk);
        return risk;
    }

    private double getSensitiveTimePeriodRisk(Long userId){
        double risk = 0.8;
        LocalTime currentTime = LocalTime.now();
        if (currentTime.isAfter(pip.getStartTimeByUserId(userId)) && currentTime.isBefore(pip.getEndTimeByUserId(userId))){
            risk = 0.0;
        }
        System.out.println("Time: "+ risk);
        return risk;
    }
}
