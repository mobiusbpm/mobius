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

package mobius.engine.test.el;

import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import mobius.common.engine.api.delegate.Expression;
import mobius.common.engine.impl.identity.Authentication;
import mobius.engine.impl.persistence.entity.ExecutionEntity;
import mobius.engine.impl.test.PluggableFlowableTestCase;
import mobius.engine.runtime.ProcessInstance;
import mobius.engine.test.Deployment;
import mobius.variable.service.impl.el.NoExecutionVariableScope;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;

/**
 *
 */
public class ExpressionManagerTest extends PluggableFlowableTestCase {

    @Test
    public void testExpressionEvaluationWithoutProcessContext() {
        Expression expression = this.processEngineConfiguration.getExpressionManager().createExpression("#{1 == 1}");
        Object value = expression.getValue(new NoExecutionVariableScope());
        assertThat(value, Is.<Object>is(true));
    }

    @Test
    @Deployment(resources = "mobius/engine/test/api/runtime/oneTaskProcess.bpmn20.xml")
    public void testIntJsonVariableSerialization() {
        Map<String, Object> vars = new HashMap<>();
        vars.put("mapVariable", processEngineConfiguration.getObjectMapper().createObjectNode().put("minIntVar", Integer.MIN_VALUE));
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTaskProcess", vars);

        Expression expression = this.processEngineConfiguration.getExpressionManager().createExpression("#{mapVariable.minIntVar}");
        Object value = managementService.executeCommand(commandContext ->
            expression.getValue((ExecutionEntity) runtimeService.createProcessInstanceQuery().processInstanceId(processInstance.getId()).includeProcessVariables().singleResult()));

        assertThat(value, Is.is(Integer.MIN_VALUE));
    }

    @Test
    @Deployment(resources = "mobius/engine/test/api/runtime/oneTaskProcess.bpmn20.xml")
    public void testShortJsonVariableSerialization() {
        Map<String, Object> vars = new HashMap<>();
        vars.put("mapVariable", processEngineConfiguration.getObjectMapper().createObjectNode().put("minShortVar", Short.MIN_VALUE));
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTaskProcess", vars);

        Expression expression = this.processEngineConfiguration.getExpressionManager().createExpression("#{mapVariable.minShortVar}");
        Object value = managementService.executeCommand(commandContext ->
            expression.getValue((ExecutionEntity) runtimeService.createProcessInstanceQuery().processInstanceId(processInstance.getId()).includeProcessVariables().singleResult()));

        assertThat(value, Is.is((int) Short.MIN_VALUE));
    }

    @Test
    @Deployment(resources = "mobius/engine/test/api/runtime/oneTaskProcess.bpmn20.xml")
    public void testFloatJsonVariableSerialization() {
        Map<String, Object> vars = new HashMap<>();
        vars.put("mapVariable", processEngineConfiguration.getObjectMapper().createObjectNode().put("minFloatVar", new Float(-1.5)));
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTaskProcess", vars);

        Expression expression = this.processEngineConfiguration.getExpressionManager().createExpression("#{mapVariable.minFloatVar}");
        Object value = managementService.executeCommand(commandContext ->
            expression.getValue((ExecutionEntity) runtimeService.createProcessInstanceQuery().processInstanceId(processInstance.getId()).includeProcessVariables().singleResult()));

        assertThat(value, Is.is(-1.5d));
    }

    @Test
    @Deployment
    public void testMethodExpressions() {
        // Process contains 2 service tasks. one containing a method with no
        // params, the other
        // contains a method with 2 params. When the process completes without
        // exception,
        // test passed.
        Map<String, Object> vars = new HashMap<>();
        vars.put("aString", "abcdefgh");
        runtimeService.startProcessInstanceByKey("methodExpressionProcess", vars);

        assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey("methodExpressionProcess").count());
    }

    @Test
    @Deployment
    public void testExecutionAvailable() {
        Map<String, Object> vars = new HashMap<>();

        vars.put("myVar", new ExecutionTestVariable());
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("testExecutionAvailableProcess", vars);

        // Check of the testMethod has been called with the current execution
        String value = (String) runtimeService.getVariable(processInstance.getId(), "testVar");
        assertNotNull(value);
        assertEquals("myValue", value);
    }

    @Test
    @Deployment
    public void testAuthenticatedUserIdAvailable() {
        try {
            // Setup authentication
            Authentication.setAuthenticatedUserId("frederik");
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("testAuthenticatedUserIdAvailableProcess");

            // Check if the variable that has been set in service-task is the
            // authenticated user
            String value = (String) runtimeService.getVariable(processInstance.getId(), "theUser");
            assertNotNull(value);
            assertEquals("frederik", value);
        } finally {
            // Cleanup
            Authentication.setAuthenticatedUserId(null);
        }
    }
}
