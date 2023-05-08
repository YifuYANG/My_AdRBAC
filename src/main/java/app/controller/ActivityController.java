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
@RequestMapping("/activity")
@Slf4j
public class ActivityController {

    @GetMapping
    @PEP_Interceptor(requiredLevel = UserLevel.any, operationType = OperationType.read, resourceType = ResourceType.medical_record)
    public String readMedicalRecord(@RequestHeader("token") String token){
        return "Read success";
    }
}
