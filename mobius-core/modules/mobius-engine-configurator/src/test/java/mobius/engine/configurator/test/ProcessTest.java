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
package mobius.engine.configurator.test;

import mobius.app.api.repository.AppDeployment;
import mobius.app.engine.test.FlowableAppTestCase;
import mobius.common.engine.api.FlowableObjectNotFoundException;
import mobius.common.engine.impl.interceptor.EngineConfigurationConstants;
import mobius.engine.ProcessEngineConfiguration;
import mobius.engine.RuntimeService;
import mobius.engine.TaskService;
import mobius.engine.runtime.ProcessInstance;
import mobius.form.api.FormDefinition;
import mobius.form.engine.FormEngineConfiguration;
import mobius.identitylink.api.IdentityLinkType;
import mobius.task.api.Task;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 *
 */
public class ProcessTest extends FlowableAppTestCase {
    
    @Test
    public void testCompleteTask() throws Exception {
        ProcessEngineConfiguration processEngineConfiguration = (ProcessEngineConfiguration) appEngineConfiguration.getEngineConfigurations()
                        .get(EngineConfigurationConstants.KEY_PROCESS_ENGINE_CONFIG);
        RuntimeService runtimeService = processEngineConfiguration.getRuntimeService();
        TaskService taskService = processEngineConfiguration.getTaskService();
        
        AppDeployment deployment = appRepositoryService.createDeployment()
            .addClasspathResource("mobius/engine/configurator/test/oneTaskProcess.bpmn20.xml").deploy();
        
        try {
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTask");
            Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
            assertNotNull(task);
            
            runtimeService.addUserIdentityLink(processInstance.getId(), "anotherUser", IdentityLinkType.STARTER);
            taskService.addUserIdentityLink(task.getId(), "testUser", IdentityLinkType.PARTICIPANT);
            
            assertEquals(2, runtimeService.getIdentityLinksForProcessInstance(processInstance.getId()).size());
            assertEquals(1, taskService.getIdentityLinksForTask(task.getId()).size());
            
            taskService.complete(task.getId());
            
            try {
                assertEquals(0, runtimeService.getIdentityLinksForProcessInstance(processInstance.getId()).size());
                fail("object not found expected");
            } catch (FlowableObjectNotFoundException e) {
                // expected
            }
            
            try {
                assertEquals(0, taskService.getIdentityLinksForTask(task.getId()).size());
                fail("object not found expected");
            } catch (FlowableObjectNotFoundException e) {
                // expected
            }
            
            assertEquals(0, runtimeService.createProcessInstanceQuery().processInstanceId(processInstance.getId()).count());
            
            
        } finally {
            appRepositoryService.deleteDeployment(deployment.getId(), true);
        }
    }
    
    @Test
    public void testCompleteTaskWithForm() throws Exception {
        ProcessEngineConfiguration processEngineConfiguration = (ProcessEngineConfiguration) appEngineConfiguration.getEngineConfigurations()
                        .get(EngineConfigurationConstants.KEY_PROCESS_ENGINE_CONFIG);
        RuntimeService runtimeService = processEngineConfiguration.getRuntimeService();
        TaskService taskService = processEngineConfiguration.getTaskService();
        
        AppDeployment deployment = appRepositoryService.createDeployment()
            .addClasspathResource("mobius/engine/configurator/test/oneTaskWithFormProcess.bpmn20.xml")
            .addClasspathResource("mobius/engine/configurator/test/simple.form").deploy();
        
        try {
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTask");
            Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
            assertNotNull(task);
            
            runtimeService.addUserIdentityLink(processInstance.getId(), "anotherUser", IdentityLinkType.STARTER);
            taskService.addUserIdentityLink(task.getId(), "testUser", IdentityLinkType.PARTICIPANT);
            
            assertEquals(2, runtimeService.getIdentityLinksForProcessInstance(processInstance.getId()).size());
            assertEquals(1, taskService.getIdentityLinksForTask(task.getId()).size());
            
            FormEngineConfiguration formEngineConfiguration = (FormEngineConfiguration) appEngineConfiguration.getEngineConfigurations()
                            .get(EngineConfigurationConstants.KEY_FORM_ENGINE_CONFIG);
            FormDefinition formDefinition = formEngineConfiguration.getFormRepositoryService().createFormDefinitionQuery().formDefinitionKey("form1").singleResult();
            assertNotNull(formDefinition);
            
            Map<String, Object> variables = new HashMap<>();
            variables.put("input1", "test");
            taskService.completeTaskWithForm(task.getId(), formDefinition.getId(), null, variables);
            
            try {
                assertEquals(0, runtimeService.getIdentityLinksForProcessInstance(processInstance.getId()).size());
                fail("object not found expected");
            } catch (FlowableObjectNotFoundException e) {
                // expected
            }
            
            try {
                assertEquals(0, taskService.getIdentityLinksForTask(task.getId()).size());
                fail("object not found expected");
            } catch (FlowableObjectNotFoundException e) {
                // expected
            }
            
            assertEquals(0, runtimeService.createProcessInstanceQuery().processInstanceId(processInstance.getId()).count());
            
            
        } finally {
            appRepositoryService.deleteDeployment(deployment.getId(), true);
        }
    }

    @Test
    public void testCompleteTaskWithAnotherForm() {
        ProcessEngineConfiguration processEngineConfiguration = (ProcessEngineConfiguration) appEngineConfiguration.getEngineConfigurations()
                        .get(EngineConfigurationConstants.KEY_PROCESS_ENGINE_CONFIG);
        RuntimeService runtimeService = processEngineConfiguration.getRuntimeService();
        TaskService taskService = processEngineConfiguration.getTaskService();

        AppDeployment deployment = appRepositoryService.createDeployment()
            .addClasspathResource("mobius/engine/configurator/test/oneTaskWithFormProcess.bpmn20.xml")
            .addClasspathResource("mobius/engine/configurator/test/another.form")
            .addClasspathResource("mobius/engine/configurator/test/simple.form").deploy();

        try {
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTask");
            Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
            assertNotNull(task);

            runtimeService.addUserIdentityLink(processInstance.getId(), "anotherUser", IdentityLinkType.STARTER);
            taskService.addUserIdentityLink(task.getId(), "testUser", IdentityLinkType.PARTICIPANT);

            assertEquals(2, runtimeService.getIdentityLinksForProcessInstance(processInstance.getId()).size());
            assertEquals(1, taskService.getIdentityLinksForTask(task.getId()).size());

            FormEngineConfiguration formEngineConfiguration = (FormEngineConfiguration) appEngineConfiguration.getEngineConfigurations()
                            .get(EngineConfigurationConstants.KEY_FORM_ENGINE_CONFIG);
            FormDefinition formDefinition = formEngineConfiguration.getFormRepositoryService().createFormDefinitionQuery().formDefinitionKey("anotherForm").singleResult();
            assertNotNull(formDefinition);

            Map<String, Object> variables = new HashMap<>();
            variables.put("anotherInput", "test");
            taskService.completeTaskWithForm(task.getId(), formDefinition.getId(), null, variables);

            assertEquals(0, runtimeService.createProcessInstanceQuery().processInstanceId(processInstance.getId()).count());


        } finally {
            appRepositoryService.deleteDeployment(deployment.getId(), true);
        }
    }
}
