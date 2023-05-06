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

import java.io.IOException;
import java.util.Optional;

import static org.ow2.authzforce.xacml.identifiers.XacmlAttributeCategory.*;

public class My_PDP {
    public void hello() throws IOException {
        final PdpEngineConfiguration pdpEngineConf = PdpEngineConfiguration.getInstance("src/main/resources/PDP.xml");
        final BasePdpEngine pdp = new BasePdpEngine(pdpEngineConf);
        final DecisionRequestBuilder<?> requestBuilder = pdp.newRequestBuilder(-1, -1);


        final AttributeFqn subjectIdAttributeId = AttributeFqns.newInstance(XACML_1_0_ACCESS_SUBJECT.value(), Optional.empty(), XacmlAttributeId.XACML_1_0_SUBJECT_ID.value());
        final AttributeBag<?> subjectIdAttributeValues = Bags.singletonAttributeBag(StandardDatatypes.STRING, new StringValue("doctor"));
        requestBuilder.putNamedAttributeIfAbsent(subjectIdAttributeId, subjectIdAttributeValues);

        final AttributeFqn resourceIdAttributeId = AttributeFqns.newInstance(XACML_3_0_ENVIRONMENT.value(), Optional.empty(), "urn:oasis:names:tc:xacml:1.0:environment:environment-id");
        final AttributeBag<?> resourceIdAttributeValues = Bags.singletonAttributeBag(StandardDatatypes.STRING, new StringValue("office"));
        requestBuilder.putNamedAttributeIfAbsent(resourceIdAttributeId, resourceIdAttributeValues);

        final AttributeFqn actionIdAttributeId = AttributeFqns.newInstance(XACML_3_0_ACTION.value(), Optional.empty(), XacmlAttributeId.XACML_1_0_ACTION_ID.value());
        final AttributeBag<?> actionIdAttributeValues = Bags.singletonAttributeBag(StandardDatatypes.STRING, new StringValue("read"));
        requestBuilder.putNamedAttributeIfAbsent(actionIdAttributeId, actionIdAttributeValues);

        final DecisionRequest request = requestBuilder.build(false);
        // Evaluate the request
        final DecisionResult result = pdp.evaluate(request);
        if(result.getDecision() == DecisionType.PERMIT) {
            System.out.println("=================");
        } else {
            System.out.println("XXXXXXXXXXXXXXXXXX");
        }

    }
}
