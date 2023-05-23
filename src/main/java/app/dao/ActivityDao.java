package app.dao;

import app.bean.TokenPool;
import app.model.MedicalRecord;
import app.model.Office;
import app.model.User;
import app.repository.MedicalRecordRepository;
import app.repository.OfficeRepository;
import app.repository.UserRepository;
import app.vo.MedicalRecordForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivityDao {
    private final TokenPool tokenPool;
    private final UserRepository userRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final OfficeRepository officeRepository;

    @Autowired
    public ActivityDao(TokenPool tokenPool, UserRepository userRepository, MedicalRecordRepository medicalRecordRepository, OfficeRepository officeRepository) {
        this.tokenPool = tokenPool;
        this.userRepository = userRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.officeRepository = officeRepository;
    }

    public User getUserByUserId(Long userId){
        return userRepository.findByUserId(userId);
    }

    public Long getUserIdByToken(String token){
        return tokenPool.getUserIdByToken(token);
    }

    public MedicalRecord getMedicalRecordById(Long id){
        return medicalRecordRepository.findRecordById(id);
    }

    public Office getOfficeById(Long officeId){
        return officeRepository.findByOfficeId(officeId);
    }
    public void updateRecord(MedicalRecordForm medicalRecordForm){
        MedicalRecord updateRecord=medicalRecordRepository.findRecordById(medicalRecordForm.getRecordId());
        updateRecord.setDescription(medicalRecordForm.getDescription());
        medicalRecordRepository.save(updateRecord);
    }

    public void deleteByRecordId(Long recordId){
        medicalRecordRepository.deleteById(recordId);
    }
}
