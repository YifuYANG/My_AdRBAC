package app.controller;

import app.constant.OperationType;
import app.constant.ResourceType;
import app.constant.UserLevel;
import app.model.MedicalRecord;
import app.model.User;
import app.repository.MedicalRecordRepository;
import app.repository.UserRepository;
import app.token_pool.TokenPool;
import app.vo.MedicalRecordForm;
import app.xacml.pep.PEP_Interceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
public class ActivityController {

    private final TokenPool tokenPool;
    private final UserRepository userRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    @Autowired
    public ActivityController(TokenPool tokenPool, UserRepository userRepository, MedicalRecordRepository medicalRecordRepository) {
        this.tokenPool = tokenPool;
        this.userRepository = userRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    @GetMapping(value = "/readMedicalRecord/{id}")
    @PEP_Interceptor(requiredLevel = UserLevel.any, operationType = OperationType.read, resourceType = ResourceType.medical_record)
    @ResponseBody
    public ResponseEntity<Map<String,String>> readMedicalRecord(@RequestHeader("token") String token,
                                                                @RequestHeader("location") String location,
                                                                @PathVariable("id") Long id){
        User user = userRepository.findByUserId(tokenPool.getUserIdByToken(token));
        log.info("User => "+user.getLast_name() +" "+user.getFirst_name() +" [" + user.getUserLevel() +"]"
                + " Performed Read Operation to [" +medicalRecordRepository.findRecordById(id).getPatient_last_name() +"]'s Medical Record at => ["+location+"].");
        Map<String,String> map=new HashMap<>();
        map.put("msg","Read Operation Performed to Medical Record");
        return new ResponseEntity<>(map,HttpStatus.ACCEPTED);
    }

    @PostMapping(value = "/writeMedicalRecord")
    @PEP_Interceptor(requiredLevel = UserLevel.any, operationType = OperationType.write, resourceType = ResourceType.medical_record)
    @ResponseBody
    public ResponseEntity<Map<String,String>> writeMedicalRecord(@RequestHeader("token") String token,
                                                                 @RequestHeader("location") String location,
                                                                 @RequestBody MedicalRecordForm medicalRecordForm){
        updateRecord(medicalRecordForm);
        User user = userRepository.findByUserId(tokenPool.getUserIdByToken(token));
        log.info("User => "+user.getLast_name() +" "+user.getFirst_name() +" [" + user.getUserLevel() +"]"
                + " Performed Write Operation to ["+medicalRecordRepository.findRecordById(medicalRecordForm.getRecordId()).getPatient_last_name() +"]'s Medical Record at => ["+location+"].");
        Map<String,String> map=new HashMap<>();
        map.put("msg","Write Operation Performed to Medical Record");
        return new ResponseEntity<>(map,HttpStatus.ACCEPTED);
    }

    @GetMapping(value = "/deleteMedicalRecord/{id}")
    @PEP_Interceptor(requiredLevel = UserLevel.any, operationType = OperationType.delete, resourceType = ResourceType.medical_record)
    @ResponseBody
    public ResponseEntity<Map<String,String>> deleteMedicalRecord(@RequestHeader("token") String token,
                                                                  @RequestHeader("location") String location,
                                                                  @PathVariable("id") Long id){
        medicalRecordRepository.deleteById(id);
        User user = userRepository.findByUserId(tokenPool.getUserIdByToken(token));
        log.info("User => "+user.getLast_name() +" "+user.getFirst_name() +" [" + user.getUserLevel() +"]"
                + " Performed Delete Operation to Medical Record at => ["+location+"].");
        Map<String,String> map=new HashMap<>();
        map.put("msg","Delete Operation Performed to Medical Record");
        return new ResponseEntity<>(map,HttpStatus.ACCEPTED);
    }

    private void updateRecord(MedicalRecordForm medicalRecordForm){
        MedicalRecord updateRecord=medicalRecordRepository.findRecordById(medicalRecordForm.getRecordId());
        updateRecord.setDescription(medicalRecordForm.getDescription());
        medicalRecordRepository.save(updateRecord);
    }
}