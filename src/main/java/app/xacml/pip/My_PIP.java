package app.xacml.pip;

import app.constant.OfficeType;
import app.constant.ResourceSensitivity;
import app.constant.UserLevel;
import app.model.MedicalRecord;
import app.model.Office;
import app.model.RiskHistory;
import app.model.TimeTable;
import app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;

@Component
public class My_PIP {
    private final UserRepository userRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final RiskHistoryRepository riskHistoryRepository;
    private final OfficeRepository officeRepository;
    private final TimeTableRepository timeTableRepository;

    @Autowired
    public My_PIP(UserRepository userRepository, MedicalRecordRepository medicalRecordRepository,
                  RiskHistoryRepository riskHistoryRepository, OfficeRepository officeRepository,
                  TimeTableRepository timeTableRepository) {
        this.userRepository = userRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.riskHistoryRepository = riskHistoryRepository;
        this.officeRepository = officeRepository;
        this.timeTableRepository = timeTableRepository;
    }

    public UserLevel getUserRole(long userId){
        return userRepository.findByUserId(userId).getUserLevel();
    }

    //
    public ResourceSensitivity getResourceSensitivityByRecordId(Long recordID){
        return medicalRecordRepository.findRecordById(recordID).getResourceSensitivity();
    }

    public List<RiskHistory> getRiskHistoryByUserId(Long userId){
        return riskHistoryRepository.findByUserId(userId);
    }

    public String getOfficeNameByUserId(Long userId){
        return officeRepository.findByUserId(userId).getOfficeName();
    }

    public String getOfficeNameById(Long officeId){
        return officeRepository.findByOfficeId(officeId).getOfficeName();
    }
    public LocalTime getStartTimeByUserId(Long userId){
        return timeTableRepository.findByUserId(userId).getStartTime();
    }

    public LocalTime getEndTimeByUserId(Long userId){
        return timeTableRepository.findByUserId(userId).getEndTime();
    }

    public OfficeType getOfficeTypeById(Long officeId){
        return officeRepository.findByOfficeId(officeId).getOfficeType();
    }
}
