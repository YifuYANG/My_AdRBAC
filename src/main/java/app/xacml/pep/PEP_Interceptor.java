package app.xacml.pep;

import app.constant.OperationType;
import app.constant.ResourceType;
import app.constant.UserLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PEP_Interceptor {

    UserLevel requiredLevel() default UserLevel.Any;

    OperationType operationType() default OperationType.Read;

    ResourceType resourceType() default ResourceType.NormalData;

}
