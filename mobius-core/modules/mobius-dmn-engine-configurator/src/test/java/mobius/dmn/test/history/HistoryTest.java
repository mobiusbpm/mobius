/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobius.dmn.test.history;

import mobius.cmmn.api.CmmnRepositoryService;
import mobius.cmmn.api.CmmnRuntimeService;
import mobius.cmmn.api.runtime.CaseInstance;
import mobius.cmmn.api.runtime.PlanItemInstance;
import mobius.cmmn.engine.CmmnEngineConfiguration;
import mobius.common.engine.impl.interceptor.EngineConfigurationConstants;
import mobius.dmn.api.DmnHistoricDecisionExecution;
import mobius.dmn.api.DmnHistoryService;
import mobius.dmn.engine.DmnEngineConfiguration;
import mobius.dmn.engine.DmnEngines;
import mobius.dmn.engine.test.AbstractFlowableDmnEngineConfiguratorTest;
import mobius.engine.runtime.ProcessInstance;
import mobius.engine.test.Deployment;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Tijs Rademakers
 */
public class HistoryTest extends AbstractFlowableDmnEngineConfiguratorTest {

    @Test
    @Deployment(resources = { "mobius/dmn/engine/test/deployment/callActivityProcess.bpmn20.xml",
			"mobius/dmn/engine/test/deployment/oneDecisionTaskProcess.bpmn20.xml",
			"mobius/dmn/engine/test/deployment/simple.dmn" })
    public void deployNestedProcessAndDecisionTable() {
        try {
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("callActivityProcess", Collections.singletonMap("inputVariable1", 10));
    
            DmnHistoryService dmnHistoryService = DmnEngines.getDefaultDmnEngine().getDmnHistoryService();
            DmnHistoricDecisionExecution decisionExecution = dmnHistoryService.createHistoricDecisionExecutionQuery().processInstanceIdWithChildren(processInstance.getId()).singleResult();
            assertEquals("decision1", decisionExecution.getDecisionKey());
            String subProcessInstanceId = decisionExecution.getInstanceId();
            assertNotEquals(subProcessInstanceId, processInstance.getId());
            
            decisionExecution = dmnHistoryService.createHistoricDecisionExecutionQuery().instanceId(processInstance.getId()).singleResult();
            assertNull(decisionExecution);
            
            decisionExecution = dmnHistoryService.createHistoricDecisionExecutionQuery().instanceId(subProcessInstanceId).singleResult();
            assertEquals("decision1", decisionExecution.getDecisionKey());
            
        } finally {
            deleteAllDmnDeployments();
        }
    }
    
    @Test
    @Deployment(resources = { "mobius/dmn/engine/test/deployment/callActivityProcess.bpmn20.xml",
			"mobius/dmn/engine/test/deployment/oneDecisionTaskProcess.bpmn20.xml",
			"mobius/dmn/engine/test/deployment/simple.dmn" })
    public void deployNestedCaseAndDecisionTable() {
        try {
            CmmnEngineConfiguration cmmnEngineConfiguration = (CmmnEngineConfiguration) processEngineConfiguration.getEngineConfigurations().get(EngineConfigurationConstants.KEY_CMMN_ENGINE_CONFIG);
            
            CmmnRepositoryService cmmnRepositoryService = cmmnEngineConfiguration.getCmmnRepositoryService();
            cmmnRepositoryService.createDeployment().addClasspathResource(
					"mobius/dmn/engine/test/deployment/decisionAndProcessTask.cmmn").deploy();
            
            CmmnRuntimeService cmmnRuntimeService = cmmnEngineConfiguration.getCmmnRuntimeService();
            CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").variable("inputVariable1", 10).start();
            
            DmnHistoryService dmnHistoryService = DmnEngines.getDefaultDmnEngine().getDmnHistoryService();
            List<DmnHistoricDecisionExecution> decisionExecutions = dmnHistoryService.createHistoricDecisionExecutionQuery().caseInstanceIdWithChildren(caseInstance.getId()).list();
            assertEquals(1, decisionExecutions.size());
            
            PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery()
                            .caseInstanceId(caseInstance.getId())
                            .planItemDefinitionId("task")
                            .singleResult();
            cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
            
            decisionExecutions = dmnHistoryService.createHistoricDecisionExecutionQuery().caseInstanceIdWithChildren(caseInstance.getId()).list();
            assertEquals(2, decisionExecutions.size());
            
            String caseInstanceId = null;
            String processInstanceId = null;
            for (DmnHistoricDecisionExecution dmnExecution : decisionExecutions) {
                assertEquals("decision1", dmnExecution.getDecisionKey());
                if (caseInstance.getId().equals(dmnExecution.getInstanceId())) {
                    caseInstanceId = dmnExecution.getInstanceId();
                } else {
                    processInstanceId = dmnExecution.getInstanceId();
                }
            }
            
            assertNotNull(caseInstanceId);
            assertNotNull(processInstanceId);
            
            DmnHistoricDecisionExecution decisionExecution = dmnHistoryService.createHistoricDecisionExecutionQuery().instanceId(processInstanceId).singleResult();
            assertEquals("decision1", decisionExecution.getDecisionKey());
            
            decisionExecution = dmnHistoryService.createHistoricDecisionExecutionQuery().processInstanceIdWithChildren(processInstanceId).singleResult();
            assertEquals("decision1", decisionExecution.getDecisionKey());
            
        } finally {
            deleteAllDmnDeployments();
        }
    }
    
    protected void deleteAllDmnDeployments() {
        DmnEngineConfiguration dmnEngineConfiguration = (DmnEngineConfiguration) flowableRule.getProcessEngine().getProcessEngineConfiguration().getEngineConfigurations()
            .get(EngineConfigurationConstants.KEY_DMN_ENGINE_CONFIG);
        dmnEngineConfiguration.getDmnRepositoryService().createDeploymentQuery().list().stream()
            .forEach(
                deployment -> dmnEngineConfiguration.getDmnRepositoryService().deleteDeployment(deployment.getId())
            );
        
        CmmnEngineConfiguration cmmnEngineConfiguration = (CmmnEngineConfiguration) processEngineConfiguration.getEngineConfigurations().get(EngineConfigurationConstants.KEY_CMMN_ENGINE_CONFIG);
        cmmnEngineConfiguration.getCmmnRepositoryService().createDeploymentQuery().list().stream()
            .forEach(
                deployment -> cmmnEngineConfiguration.getCmmnRepositoryService().deleteDeployment(deployment.getId(), true)
            );
    }
}
