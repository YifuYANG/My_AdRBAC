package app.xacml.pdp;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.DecisionType;
import org.ow2.authzforce.core.pdp.api.*;
import org.ow2.authzforce.core.pdp.api.value.AttributeBag;
import org.ow2.authzforce.core.pdp.api.value.Bags;
import org.ow2.authzforce.core.pdp.api.value.StandardDatatypes;
import org.ow2.authzforce.core.pdp.api.value.StringValue;
import org.ow2.authzforce.core.pdp.impl.BasePdpEngine;
import org.ow2.authzforce.core.pdp.impl.PdpEngineConfiguration;
import org.ow2.authzforce.xacml.identifiers.XacmlAttributeId;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

import static org.ow2.authzforce.xacml.identifiers.XacmlAttributeCategory.*;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class My_PDP{
    public My_PDP() throws IOException {
    }
    private final PdpEngineConfiguration pdpEngineConf = PdpEngineConfiguration.getInstance("src/main/resources/PDP.xml");;
    private final BasePdpEngine pdp = new BasePdpEngine(pdpEngineConf);
    private final DecisionRequestBuilder<?> requestBuilder = pdp.newRequestBuilder(3, 3);

    private AttributeFqn subjectIdAttributeId;

    public boolean XACML_response(String role, String location, String action, String resource){
        subjectIdAttributeId = AttributeFqns.newInstance(XACML_1_0_ACCESS_SUBJECT.value(), Optional.empty(), XacmlAttributeId.XACML_1_0_SUBJECT_ID.value());
        AttributeBag<?> subjectIdAttributeValues = Bags.singletonAttributeBag(StandardDatatypes.STRING, new StringValue(role));
        requestBuilder.putNamedAttributeIfAbsent(subjectIdAttributeId, subjectIdAttributeValues);

        AttributeFqn environmentIdAttributeId = AttributeFqns.newInstance(XACML_3_0_ENVIRONMENT.value(), Optional.empty(), "urn:oasis:names:tc:xacml:1.0:environment:environment-id");
        AttributeBag<?> environmentIdAttributeValues = Bags.singletonAttributeBag(StandardDatatypes.STRING, new StringValue(location));
        requestBuilder.putNamedAttributeIfAbsent(environmentIdAttributeId, environmentIdAttributeValues);

        AttributeFqn actionIdAttributeId = AttributeFqns.newInstance(XACML_3_0_ACTION.value(), Optional.empty(), XacmlAttributeId.XACML_1_0_ACTION_ID.value());
        AttributeBag<?> actionIdAttributeValues = Bags.singletonAttributeBag(StandardDatatypes.STRING, new StringValue(action));
        requestBuilder.putNamedAttributeIfAbsent(actionIdAttributeId, actionIdAttributeValues);

        AttributeFqn resourceIdAttributeId = AttributeFqns.newInstance(XACML_3_0_RESOURCE.value(), Optional.empty(), "urn:oasis:names:tc:xacml:1.0:resource:resource-id");
        AttributeBag<?> resourceIdAttributeValues = Bags.singletonAttributeBag(StandardDatatypes.STRING, new StringValue(resource));
        requestBuilder.putNamedAttributeIfAbsent(resourceIdAttributeId, resourceIdAttributeValues);

        DecisionRequest request = requestBuilder.build(false);

        return pdp.evaluate(request).getDecision() == DecisionType.PERMIT;
    }

//    public void hello() {
//
//
//        //final DecisionRequestBuilder<?> requestBuilder = pdp.newRequestBuilder(-1, -1);
//
//        subjectIdAttributeId = AttributeFqns.newInstance(XACML_1_0_ACCESS_SUBJECT.value(), Optional.empty(), XacmlAttributeId.XACML_1_0_SUBJECT_ID.value());
//        AttributeBag<?> subjectIdAttributeValues = Bags.singletonAttributeBag(StandardDatatypes.STRING, new StringValue("doctor"));
//        requestBuilder.putNamedAttributeIfAbsent(subjectIdAttributeId, subjectIdAttributeValues);
//
//        AttributeFqn environmentIdAttributeId = AttributeFqns.newInstance(XACML_3_0_ENVIRONMENT.value(), Optional.empty(), "urn:oasis:names:tc:xacml:1.0:environment:environment-id");
//        AttributeBag<?> environmentIdAttributeValues = Bags.singletonAttributeBag(StandardDatatypes.STRING, new StringValue("office"));
//        requestBuilder.putNamedAttributeIfAbsent(environmentIdAttributeId, environmentIdAttributeValues);
//
//        AttributeFqn actionIdAttributeId = AttributeFqns.newInstance(XACML_3_0_ACTION.value(), Optional.empty(), XacmlAttributeId.XACML_1_0_ACTION_ID.value());
//        AttributeBag<?> actionIdAttributeValues = Bags.singletonAttributeBag(StandardDatatypes.STRING, new StringValue("read"));
//        requestBuilder.putNamedAttributeIfAbsent(actionIdAttributeId, actionIdAttributeValues);
//
//        AttributeFqn resourceIdAttributeId = AttributeFqns.newInstance(XACML_3_0_RESOURCE.value(), Optional.empty(), "urn:oasis:names:tc:xacml:1.0:resource:resource-id");
//        AttributeBag<?> resourceIdAttributeValues = Bags.singletonAttributeBag(StandardDatatypes.STRING, new StringValue("medical_record"));
//        requestBuilder.putNamedAttributeIfAbsent(resourceIdAttributeId, resourceIdAttributeValues);
//
//        final DecisionRequest request = requestBuilder.build(false);
//        // Evaluate the request
//        final DecisionResult result = pdp.evaluate(request);
//
//        System.out.println("==================================================");
//        if(result.getDecision() == DecisionType.PERMIT) {
//            System.out.println("Permit!!!!");
//        } else {
//            System.out.println("Deny!!!!!");
//        }
//        System.out.println("==================================================");
//    }
}
