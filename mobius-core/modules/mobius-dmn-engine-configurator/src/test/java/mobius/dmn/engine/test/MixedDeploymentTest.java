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
package mobius.dmn.engine.test;

import mobius.common.engine.api.FlowableException;
import mobius.common.engine.api.FlowableObjectNotFoundException;
import mobius.common.engine.impl.DefaultTenantProvider;
import mobius.common.engine.impl.interceptor.EngineConfigurationConstants;
import mobius.dmn.api.DmnDecisionTable;
import mobius.dmn.api.DmnHistoricDecisionExecution;
import mobius.dmn.api.DmnRepositoryService;
import mobius.dmn.engine.DmnEngineConfiguration;
import mobius.dmn.engine.DmnEngines;
import mobius.engine.repository.ProcessDefinition;
import mobius.engine.runtime.ProcessInstance;
import mobius.engine.test.Deployment;
import mobius.variable.api.history.HistoricVariableInstance;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Yvo Swillens
 */
public class MixedDeploymentTest extends AbstractFlowableDmnEngineConfiguratorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    @Deployment(resources = { "mobius/dmn/engine/test/deployment/oneDecisionTaskProcess.bpmn20.xml",
			"mobius/dmn/engine/test/deployment/simple.dmn" })
    public void deploySingleProcessAndDecisionTable() {
        try {
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .latestVersion()
                    .processDefinitionKey("oneDecisionTaskProcess")
                    .singleResult();
    
            assertNotNull(processDefinition);
            assertEquals("oneDecisionTaskProcess", processDefinition.getKey());
    
            DmnRepositoryService dmnRepositoryService = DmnEngines.getDefaultDmnEngine().getDmnRepositoryService();
            DmnDecisionTable decisionTable = dmnRepositoryService.createDecisionTableQuery()
                    .latestVersion()
                    .decisionTableKey("decision1")
                    .singleResult();
            assertNotNull(decisionTable);
            assertEquals("decision1", decisionTable.getKey());
    
            List<DmnDecisionTable> decisionTableList = repositoryService.getDecisionTablesForProcessDefinition(processDefinition.getId());
            assertEquals(1l, decisionTableList.size());
            assertEquals("decision1", decisionTableList.get(0).getKey());
        } finally {
            deleteAllDmnDeployments();
        }
    }
    
    @Test
    @Deployment(resources = { "mobius/dmn/engine/test/deployment/oneDecisionTaskProcess.bpmn20.xml",
			"mobius/dmn/engine/test/deployment/simple.dmn" })
    public void testDecisionTaskExecution() {
        try {
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneDecisionTaskProcess", Collections.singletonMap("inputVariable1", (Object) 1));
            List<HistoricVariableInstance> variables = historyService.createHistoricVariableInstanceQuery()
                            .processInstanceId(processInstance.getId()).orderByVariableName().asc().list();
            
            assertEquals("inputVariable1", variables.get(0).getVariableName());
            assertEquals(1, variables.get(0).getValue());
            assertEquals("outputVariable1", variables.get(1).getVariableName());
            assertEquals("result1", variables.get(1).getValue());
        } finally {
            deleteAllDmnDeployments();
        }
    }
    
    @Test
    @Deployment(resources = { "mobius/dmn/engine/test/deployment/oneDecisionTaskProcess.bpmn20.xml",
			"mobius/dmn/engine/test/deployment/simple.dmn" })
    public void testFailedDecisionTask() {
        try {
            runtimeService.startProcessInstanceByKey("oneDecisionTaskProcess");
            fail("Expected DMN failure due to missing variable");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Unknown property used in expression: #{inputVariable1"));
        } finally {
            deleteAllDmnDeployments();
        }
    }
    
    @Test
    @Deployment(resources = { "mobius/dmn/engine/test/deployment/oneDecisionTaskNoHitsErrorProcess.bpmn20.xml",
			"mobius/dmn/engine/test/deployment/simple.dmn" })
    public void testNoHitsDecisionTask() {
        try {
            runtimeService.startProcessInstanceByKey("oneDecisionTaskProcess", Collections.singletonMap("inputVariable1", (Object) 2));
            fail("Expected Exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("did not hit any rules for the provided input"));
        } finally {
            deleteAllDmnDeployments();
        }
    }

    @Test
    @Deployment(resources = { "mobius/dmn/engine/test/deployment/oneDecisionTaskNoHitsErrorProcess.bpmn20.xml" })
    public void testDecisionNotFound() {
        try {
            runtimeService.startProcessInstanceByKey("oneDecisionTaskProcess", Collections.singletonMap("inputVariable1", (Object) 2));
            fail("Expected Exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Decision table for key [decision1] was not found"));
        } finally {
            deleteAllDmnDeployments();
        }
    }

    @Test
    @Deployment(resources = {
			"mobius/dmn/engine/test/deployment/oneDecisionTaskProcessFallBackToDefaultTenant.bpmn20.xml" },
        tenantId = "flowable"
    )
    public void testDecisionTaskExecutionInAnotherDeploymentAndTenant() {
        deployDecisionAndAssertProcessExecuted();
    }
    
    @Test
    @Deployment(resources = {
			"mobius/dmn/engine/test/deployment/oneDecisionTaskProcessFallBackToDefaultTenant.bpmn20.xml" },
        tenantId = "someTenant"
    )
    public void testDecisionTaskExecutionWithGlobalTenantFallback() {
        deployDecisionWithGlobalTenantFallback();
    }

    @Test
    @Deployment(resources = { "mobius/dmn/engine/test/deployment/oneDecisionTaskProcess.bpmn20.xml" }
    )
    public void testDecisionTaskExecutionInAnotherDeploymentAndTenantDefaultBehavior() {
        this.expectedException.expect(FlowableObjectNotFoundException.class);
        this.expectedException.expectMessage("Process definition with key 'oneDecisionTaskProcess' and tenantId 'flowable' was not found");

        deployDecisionAndAssertProcessExecuted();
    }

    @Test
    @Deployment(resources = {
			"mobius/dmn/engine/test/deployment/oneDecisionTaskProcessFallBackToDefaultTenantFalse.bpmn20.xml" },
        tenantId = "flowable"
    )
    public void testDecisionTaskExecutionInAnotherDeploymentAndTenantFalse() {
        this.expectedException.expect(FlowableException.class);
        this.expectedException.expectMessage("Decision table for key [decision1] and tenantId [flowable] was not found");

        deployDecisionAndAssertProcessExecuted();
    }

    @Test
    @Deployment(resources = {
			"mobius/dmn/engine/test/deployment/oneDecisionTaskProcessFallBackToDefaultTenantFalse.bpmn20.xml" },
        tenantId = "flowable"
    )
    public void testDecisionTaskExecutionInAnotherDeploymentAndTenantFallbackFalseWithoutDeployment() {
        this.expectedException.expect(FlowableException.class);
        this.expectedException.expectMessage("Decision table for key [decision1] and tenantId [flowable] was not found");

        deleteAllDmnDeployments();
        mobius.engine.repository.Deployment deployment = repositoryService.createDeployment().
            addClasspathResource("mobius/dmn/engine/test/deployment/simple.dmn").
            tenantId("anotherTenant").
            deploy();
        try {
            assertDmnProcessExecuted();
        } finally {
            this.repositoryService.deleteDeployment(deployment.getId(), true);
            deleteAllDmnDeployments();
        }
    }

    @Test
    @Deployment(resources = {
			"mobius/dmn/engine/test/deployment/oneDecisionTaskProcessFallBackToDefaultTenant.bpmn20.xml" },
        tenantId = "flowable"
    )
    public void testDecisionTaskExecutionInAnotherDeploymentAndTenantFallbackTrueWithoutDeployment() {
        this.expectedException.expect(FlowableException.class);
        this.expectedException.expectMessage("No decision found for key: decision1. There was also no fall back decision table found without tenant.");

        mobius.engine.repository.Deployment deployment = repositoryService.createDeployment().
            addClasspathResource("mobius/dmn/engine/test/deployment/simple.dmn").
            tenantId("anotherTenant").
            deploy();
        try {
            assertDmnProcessExecuted();
        } finally {
            this.repositoryService.deleteDeployment(deployment.getId(), true);
            deleteAllDmnDeployments();
        }
    }


    protected void deployDecisionAndAssertProcessExecuted() {
        mobius.engine.repository.Deployment deployment = repositoryService.createDeployment().
            addClasspathResource("mobius/dmn/engine/test/deployment/simple.dmn").
            tenantId("").
            deploy();
        try {
            assertDmnProcessExecuted();
        } finally {
            this.repositoryService.deleteDeployment(deployment.getId(), true);
            deleteAllDmnDeployments();
        }
    }
    
    protected void deployDecisionWithGlobalTenantFallback() {
        DmnEngineConfiguration dmnEngineConfiguration = (DmnEngineConfiguration) processEngineConfiguration.getEngineConfigurations().get(
                        EngineConfigurationConstants.KEY_DMN_ENGINE_CONFIG);
        
        DefaultTenantProvider originalDefaultTenantProvider = dmnEngineConfiguration.getDefaultTenantProvider();
        dmnEngineConfiguration.setFallbackToDefaultTenant(true);
        dmnEngineConfiguration.setDefaultTenantValue("defaultFlowable");
        
        mobius.engine.repository.Deployment deployment = repositoryService.createDeployment().
            addClasspathResource("mobius/dmn/engine/test/deployment/simple.dmn").
            tenantId("defaultFlowable").
            deploy();
        
        try {
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKeyAndTenantId(
                "oneDecisionTaskProcess", Collections.singletonMap("inputVariable1", (Object) 1), "someTenant");
            List<HistoricVariableInstance> variables = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstance.getId()).orderByVariableName().asc().list();

            assertEquals("inputVariable1", variables.get(0).getVariableName());
            assertEquals(1, variables.get(0).getValue());
            assertEquals("outputVariable1", variables.get(1).getVariableName());
            assertEquals("result1", variables.get(1).getValue());
            
            DmnHistoricDecisionExecution decisionExecution = dmnEngineConfiguration.getDmnHistoryService()
                            .createHistoricDecisionExecutionQuery()
                            .instanceId(processInstance.getId())
                            .singleResult();
            
            assertNotNull(decisionExecution);
            assertEquals("someTenant", decisionExecution.getTenantId());
            
        } finally {
            dmnEngineConfiguration.setFallbackToDefaultTenant(false);
            dmnEngineConfiguration.setDefaultTenantProvider(originalDefaultTenantProvider);
            this.repositoryService.deleteDeployment(deployment.getId(), true);
            deleteAllDmnDeployments();
        }
    }

    protected void assertDmnProcessExecuted() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKeyAndTenantId(
            "oneDecisionTaskProcess",
            Collections.singletonMap("inputVariable1", (Object) 1),
            "flowable");
        List<HistoricVariableInstance> variables = historyService.createHistoricVariableInstanceQuery()
            .processInstanceId(processInstance.getId()).orderByVariableName().asc().list();

        assertEquals("inputVariable1", variables.get(0).getVariableName());
        assertEquals(1, variables.get(0).getValue());
        assertEquals("outputVariable1", variables.get(1).getVariableName());
        assertEquals("result1", variables.get(1).getValue());
        
        DmnEngineConfiguration dmnEngineConfiguration = (DmnEngineConfiguration) processEngineConfiguration.getEngineConfigurations().get(
                        EngineConfigurationConstants.KEY_DMN_ENGINE_CONFIG);
        DmnHistoricDecisionExecution decisionExecution = dmnEngineConfiguration.getDmnHistoryService()
                        .createHistoricDecisionExecutionQuery()
                        .instanceId(processInstance.getId())
                        .singleResult();
        
        assertNotNull(decisionExecution);
        assertEquals("flowable", decisionExecution.getTenantId());
    }



    protected void deleteAllDmnDeployments() {
        DmnEngineConfiguration dmnEngineConfiguration = (DmnEngineConfiguration) flowableRule.getProcessEngine().getProcessEngineConfiguration().getEngineConfigurations()
            .get(EngineConfigurationConstants.KEY_DMN_ENGINE_CONFIG);
        dmnEngineConfiguration.getDmnRepositoryService().createDeploymentQuery().list().stream()
        .forEach(
            deployment -> dmnEngineConfiguration.getDmnRepositoryService().deleteDeployment(deployment.getId())
        );
    }

}