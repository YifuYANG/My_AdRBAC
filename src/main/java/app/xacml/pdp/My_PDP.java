package app.xacml.pdp;

import app.constant.RiskLevel;
import app.exception.CustomErrorException;
import app.xacml.pip.My_PIP;
import app.xacml.risk_engine.MyRiskEngine;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.DecisionType;
import org.ow2.authzforce.core.pdp.api.*;
import org.ow2.authzforce.core.pdp.api.value.AttributeBag;
import org.ow2.authzforce.core.pdp.api.value.Bags;
import org.ow2.authzforce.core.pdp.api.value.StandardDatatypes;
import org.ow2.authzforce.core.pdp.api.value.StringValue;
import org.ow2.authzforce.core.pdp.impl.BasePdpEngine;
import org.ow2.authzforce.core.pdp.impl.PdpEngineConfiguration;
import org.ow2.authzforce.xacml.identifiers.XacmlAttributeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

import static org.ow2.authzforce.xacml.identifiers.XacmlAttributeCategory.*;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class My_PDP{

    private final My_PIP pip;
    private final MyRiskEngine myRiskEngine;
    @Autowired
    public My_PDP(My_PIP pip, MyRiskEngine myRiskEngine) throws IOException {
        this.pip = pip;
        this.myRiskEngine = myRiskEngine;
    }
    private final PdpEngineConfiguration pdpEngineConf = PdpEngineConfiguration.getInstance("src/main/resources/PDP.xml");;

    public boolean XACML_response(Long userId, Long officeId, String action, String resource, Long recordId) throws CustomErrorException {
        try (BasePdpEngine pdp = new BasePdpEngine(pdpEngineConf)){

            DecisionRequestBuilder<?> requestBuilder = pdp.newRequestBuilder(-1, -1);
            AttributeFqn subjectIdAttributeId = AttributeFqns.newInstance(XACML_1_0_ACCESS_SUBJECT.value(), Optional.empty(), XacmlAttributeId.XACML_1_0_SUBJECT_ID.value());
            AttributeBag<?> subjectIdAttributeValues = Bags.singletonAttributeBag(StandardDatatypes.STRING, new StringValue(pip.getUserRole(userId).toString()));
            requestBuilder.putNamedAttributeIfAbsent(subjectIdAttributeId, subjectIdAttributeValues);

            AttributeFqn environmentIdAttributeId = AttributeFqns.newInstance(XACML_3_0_ENVIRONMENT.value(), Optional.empty(), "urn:oasis:names:tc:xacml:1.0:environment:environment-id");
            AttributeBag<?> environmentIdAttributeValues = Bags.singletonAttributeBag(StandardDatatypes.STRING, new StringValue(pip.getOfficeTypeById(officeId).toString()));
            requestBuilder.putNamedAttributeIfAbsent(environmentIdAttributeId, environmentIdAttributeValues);

            AttributeFqn actionIdAttributeId = AttributeFqns.newInstance(XACML_3_0_ACTION.value(), Optional.empty(), XacmlAttributeId.XACML_1_0_ACTION_ID.value());
            AttributeBag<?> actionIdAttributeValues = Bags.singletonAttributeBag(StandardDatatypes.STRING, new StringValue(action));
            requestBuilder.putNamedAttributeIfAbsent(actionIdAttributeId, actionIdAttributeValues);

            AttributeFqn resourceIdAttributeId = AttributeFqns.newInstance(XACML_3_0_RESOURCE.value(), Optional.empty(), "urn:oasis:names:tc:xacml:1.0:resource:resource-id");
            AttributeBag<?> resourceIdAttributeValues = Bags.singletonAttributeBag(StandardDatatypes.STRING, new StringValue(resource));
            requestBuilder.putNamedAttributeIfAbsent(resourceIdAttributeId, resourceIdAttributeValues);

            AttributeFqn resourceSensitivityIdAttributeId = AttributeFqns.newInstance(XACML_3_0_RESOURCE.value(), Optional.empty(), "resourceSensitivity:resourceSensitivity-id");
            AttributeBag<?> resourceSensitivityIdAttributeValues = Bags.singletonAttributeBag(StandardDatatypes.STRING, new StringValue(pip.getResourceSensitivityByRecordId(recordId).toString()));
            requestBuilder.putNamedAttributeIfAbsent(resourceSensitivityIdAttributeId, resourceSensitivityIdAttributeValues);

            DecisionRequest request = requestBuilder.build(false);
            return pdp.evaluate(request).getDecision() == DecisionType.PERMIT;
        } catch (Exception ignore){
            throw new CustomErrorException("Bad Request");
        }
    }

    public RiskLevel decisionMakingEngine(Long userId, Long recordId,
                                          String action, String resource,
                                          Long currentOfficeId) throws CustomErrorException {
        if (!XACML_response(userId, currentOfficeId, action, resource, recordId)) {
            return RiskLevel.Extreme;
        }

        double riskScore = myRiskEngine.evaluateRiskReturnRiskScore(userId, recordId, currentOfficeId);

        return (riskScore >= 9.0) ? RiskLevel.Low :
               (riskScore >= 7.0) ? RiskLevel.Medium :
               (riskScore >= 5.0) ? RiskLevel.High :
               RiskLevel.Extreme;
    }
}
