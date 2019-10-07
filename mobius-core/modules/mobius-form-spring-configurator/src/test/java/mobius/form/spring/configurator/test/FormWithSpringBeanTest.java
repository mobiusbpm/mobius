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
package mobius.form.spring.configurator.test;

import mobius.common.engine.impl.interceptor.EngineConfigurationConstants;
import mobius.engine.runtime.ProcessInstance;
import mobius.engine.test.Deployment;
import mobius.form.api.FormDeployment;
import mobius.form.api.FormInfo;
import mobius.form.api.FormRepositoryService;
import mobius.form.engine.FormEngineConfiguration;
import mobius.form.model.ExpressionFormField;
import mobius.form.model.SimpleFormModel;
import mobius.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@ContextConfiguration("classpath:flowable-context.xml")
public class FormWithSpringBeanTest extends SpringFormFlowableTestCase {

    @Test
    @Deployment(resources = { "mobius/form/spring/configurator/test/oneTaskWithFormKeyProcess.bpmn20.xml",
        "mobius/form/spring/configurator/test/simple.form" })
    public void testFormOnUserTask() {
        
        FormEngineConfiguration formEngineConfiguration = (FormEngineConfiguration) processEngineConfiguration.getEngineConfigurations()
                        .get(EngineConfigurationConstants.KEY_FORM_ENGINE_CONFIG);
        
        FormDeployment formDeployment = formEngineConfiguration.getFormRepositoryService().createDeploymentQuery().singleResult();
        assertNotNull(formDeployment);
        
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("var1", "test");
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTaskWithFormProcess", variables);
            Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
            assertNotNull(task);
            
            FormInfo formInfo = taskService.getTaskFormModel(task.getId());
            SimpleFormModel formModel = (SimpleFormModel) formInfo.getFormModel();
            ExpressionFormField expressionFormField = (ExpressionFormField) formModel.getFields().get(1);
            assertEquals("#{testFormBean.getExpressionText(var1)}", expressionFormField.getExpression());
            assertEquals("hello test", expressionFormField.getValue());
            
            taskService.complete(task.getId());
    
            assertProcessEnded(processInstance.getId());
            
        } finally {
            formEngineConfiguration.getFormRepositoryService().deleteDeployment(formDeployment.getId());
        }
    }
    
    @Test
    @Deployment(resources = { "mobius/form/spring/configurator/test/oneTaskWithFormKeyProcess.bpmn20.xml",
        "mobius/form/spring/configurator/test/simple.form" })
    public void testFormOnUserTaskWithoutVariables() {
        
        FormEngineConfiguration formEngineConfiguration = (FormEngineConfiguration) processEngineConfiguration.getEngineConfigurations()
                        .get(EngineConfigurationConstants.KEY_FORM_ENGINE_CONFIG);
        
        FormDeployment formDeployment = formEngineConfiguration.getFormRepositoryService().createDeploymentQuery().singleResult();
        assertNotNull(formDeployment);
        
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("var1", "test");
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTaskWithFormProcess", variables);
            Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
            assertNotNull(task);
            
            FormInfo formInfo = taskService.getTaskFormModel(task.getId(), true);
            SimpleFormModel formModel = (SimpleFormModel) formInfo.getFormModel();
            ExpressionFormField expressionFormField = (ExpressionFormField) formModel.getFields().get(1);
            assertEquals("#{testFormBean.getExpressionText(var1)}", expressionFormField.getExpression());
            assertNull(expressionFormField.getValue());
            
        } finally {
            formEngineConfiguration.getFormRepositoryService().deleteDeployment(formDeployment.getId());
        }
    }
    
    @Test
    @Deployment(resources = { "mobius/form/spring/configurator/test/oneTaskWithFormKeyProcess.bpmn20.xml"})
    public void testFormOnUserTaskWithoutVariablesSeparateDeployments() {
        
        FormEngineConfiguration formEngineConfiguration = (FormEngineConfiguration) processEngineConfiguration.getEngineConfigurations()
                        .get(EngineConfigurationConstants.KEY_FORM_ENGINE_CONFIG);
        
        FormRepositoryService formRepositoryService = formEngineConfiguration.getFormRepositoryService();
        FormDeployment formDeployment = formRepositoryService.createDeployment().addClasspathResource("mobius/form/spring/configurator/test/simple.form").deploy();
        
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("var1", "test");
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTaskWithFormProcess", variables);
            Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
            assertNotNull(task);
            
            FormInfo formInfo = taskService.getTaskFormModel(task.getId(), true);
            SimpleFormModel formModel = (SimpleFormModel) formInfo.getFormModel();
            ExpressionFormField expressionFormField = (ExpressionFormField) formModel.getFields().get(1);
            assertEquals("#{testFormBean.getExpressionText(var1)}", expressionFormField.getExpression());
            assertNull(expressionFormField.getValue());
            
        } finally {
            formEngineConfiguration.getFormRepositoryService().deleteDeployment(formDeployment.getId());
        }
    }

}
