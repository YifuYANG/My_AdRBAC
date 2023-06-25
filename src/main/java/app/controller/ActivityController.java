package app.controller;

import app.constant.OperationType;
import app.constant.ResourceType;
import app.constant.UserLevel;
import app.dao.ActivityDao;
import app.exception.CustomErrorException;
import app.model.MedicalRecord;
import app.model.User;
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

    private final ActivityDao activityDao;
    @Autowired
    public ActivityController(ActivityDao activityDao) {

        this.activityDao = activityDao;
    }

    @GetMapping(value = "/readMedicalRecord/{id}")
    @PEP_Interceptor(requiredLevel = UserLevel.Any, operationType = OperationType.Read, resourceType = ResourceType.MedicalRecord)
    @ResponseBody
    public ResponseEntity<Map<String,MedicalRecord>> readMedicalRecord(@RequestHeader("token") String token,
                                                                @RequestHeader("officeId") Long officeId,
                                                                @PathVariable("id") Long id) throws CustomErrorException {
        try {
            User user = activityDao.getUserByUserId(activityDao.getUserIdByToken(token));
            log.info("User => "+user.getLast_name() +" "+user.getFirst_name() +" [" + user.getUserLevel() +"]"
                    + " Performed Read Operation to [" + activityDao.getMedicalRecordById(id).getPatient_last_name()
                    +"]'s Medical Record at => ["+ activityDao.getOfficeById(officeId).get(0).getOfficeType()+"].");
            Map<String,MedicalRecord> map=new HashMap<>();
            map.put("medical record",activityDao.getMedicalRecordById(id));
            return new ResponseEntity<>(map,HttpStatus.ACCEPTED);
        } catch (Exception ignored){
            throw new CustomErrorException("something went wrong");
        }
    }

    @PostMapping(value = "/writeMedicalRecord")
    @PEP_Interceptor(requiredLevel = UserLevel.Any, operationType = OperationType.Write, resourceType = ResourceType.MedicalRecord)
    @ResponseBody
    public ResponseEntity<Map<String,String>> writeMedicalRecord(@RequestHeader("token") String token,
                                                                 @RequestHeader("officeId") Long officeId,
                                                                 @RequestBody MedicalRecordForm medicalRecordForm) throws CustomErrorException {
        try {
            activityDao.updateRecord(medicalRecordForm);
            User user = activityDao.getUserByUserId(activityDao.getUserIdByToken(token));
            log.info("User => "+user.getLast_name() +" "+user.getFirst_name() +" [" + user.getUserLevel() +"]"
                    + " Performed Write Operation to ["+activityDao.getMedicalRecordById(medicalRecordForm.getRecordId()).getPatient_last_name()
                    +"]'s Medical Record at => ["+ activityDao.getOfficeById(officeId).get(0).getOfficeType()+"].");
            Map<String,String> map=new HashMap<>();
            map.put("msg","Write Operation Performed to Medical Record");
            return new ResponseEntity<>(map,HttpStatus.ACCEPTED);
        } catch (Exception ignored){
            throw new CustomErrorException("something went wrong");
        }
    }

    @GetMapping(value = "/deleteMedicalRecord/{id}")
    @PEP_Interceptor(requiredLevel = UserLevel.Any, operationType = OperationType.Delete, resourceType = ResourceType.MedicalRecord)
    @ResponseBody
    public ResponseEntity<Map<String,String>> deleteMedicalRecord(@RequestHeader("token") String token,
                                                                  @RequestHeader("officeId") Long officeId,
                                                                  @PathVariable("id") Long id) throws CustomErrorException {
        try {
            activityDao.deleteByRecordId(id);
            User user = activityDao.getUserByUserId(activityDao.getUserIdByToken(token));
            log.info("User => "+user.getLast_name() +" "+user.getFirst_name() +" [" + user.getUserLevel() +"]"
                    + " Performed Delete Operation to Medical Record at => ["
                    + activityDao.getOfficeById(officeId).get(0).getOfficeType()+"].");
            Map<String,String> map=new HashMap<>();
            map.put("msg","Delete Operation Performed to Medical Record");
            return new ResponseEntity<>(map,HttpStatus.ACCEPTED);
        } catch (Exception ignored){
            throw new CustomErrorException("something went wrong");
        }
    }
}