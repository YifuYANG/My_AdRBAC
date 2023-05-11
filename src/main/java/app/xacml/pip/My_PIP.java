package app.xacml.pip;

import app.constant.OfficeType;
import app.constant.ResourceSensitivity;
import app.constant.UserLevel;
import app.model.RiskHistory;
import app.repository.MedicalRecordRepository;
import app.repository.RiskHistoryRepository;
import app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class My_PIP {
    private final UserRepository userRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    private final RiskHistoryRepository riskHistoryRepository;

    @Autowired
    public My_PIP(UserRepository userRepository, MedicalRecordRepository medicalRecordRepository,RiskHistoryRepository riskHistoryRepository) {
        this.userRepository = userRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.riskHistoryRepository = riskHistoryRepository;
    }

    public String getUserName(long userId){
        return userRepository.findByUserId(userId).getLast_name()+ " " +userRepository.findByUserId(userId).getFirst_name();
    }

    public UserLevel getUserRole(long userId){
        return userRepository.findByUserId(userId).getUserLevel();
    }

    public ResourceSensitivity getResourceSensitivity(Long recordID){
        return medicalRecordRepository.findRecordById(recordID).getResourceSensitivity();
    }

    public List<RiskHistory> getRiskHistory(Long userId){
        return riskHistoryRepository.findByUserId(userId);
    }

    public OfficeType getOfficeType(String location){
        for (OfficeType officeType : OfficeType.values()) {
            if (location.contains(officeType.toString())) {
                return officeType;
            }
        }
        return null;
    }
}
