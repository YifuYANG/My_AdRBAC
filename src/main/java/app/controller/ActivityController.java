package app.controller;

import app.constant.OperationType;
import app.constant.ResourceType;
import app.constant.UserLevel;
import app.xacml.pep.PEP_Interceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class ActivityController {

    @GetMapping(value = "/readMedicalRecord")
    @PEP_Interceptor(requiredLevel = UserLevel.any, operationType = OperationType.read, resourceType = ResourceType.medical_record)
    public String readMedicalRecord(@RequestHeader("token") String token){
        return "Read success";
    }

    @GetMapping(value = "/writeMedicalRecord")
    @PEP_Interceptor(requiredLevel = UserLevel.any, operationType = OperationType.write, resourceType = ResourceType.medical_record)
    public String writeMedicalRecord(@RequestHeader("token") String token){
        return "Write success";
    }

    @GetMapping(value = "/deleteMedicalRecord")
    @PEP_Interceptor(requiredLevel = UserLevel.any, operationType = OperationType.delete, resourceType = ResourceType.medical_record)
    public String deleteMedicalRecord(@RequestHeader("token") String token){
        return "Delete success";
    }
}
