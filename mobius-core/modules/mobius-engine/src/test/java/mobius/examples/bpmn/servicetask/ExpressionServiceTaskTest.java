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
package mobius.examples.bpmn.servicetask;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobius.common.engine.impl.history.HistoryLevel;
import mobius.engine.history.HistoricActivityInstance;
import mobius.engine.impl.test.HistoryTestHelper;
import mobius.engine.impl.test.PluggableFlowableTestCase;
import mobius.engine.runtime.ProcessInstance;
import mobius.engine.test.Deployment;
import mobius.task.api.Task;
import org.junit.jupiter.api.Test;

/**
 * @author Christian Stettler
 */
public class ExpressionServiceTaskTest extends PluggableFlowableTestCase {

    @Test
    @Deployment
    public void testSetServiceResultToProcessVariables() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("bean", new ValueBean("ok"));
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("setServiceResultToProcessVariables", variables);
        assertEquals("ok", runtimeService.getVariable(pi.getId(), "result"));
    }

    @Test
    @Deployment
    public void testSetServiceResultToProcessVariablesWithSkipExpression() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("bean", new ValueBean("ok"));
        variables.put("_ACTIVITI_SKIP_EXPRESSION_ENABLED", true);
        variables.put("skip", false);
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("setServiceResultToProcessVariablesWithSkipExpression", variables);
        assertEquals("ok", runtimeService.getVariable(pi.getId(), "result"));

        Map<String, Object> variables2 = new HashMap<>();
        variables2.put("bean", new ValueBean("ok"));
        variables2.put("_ACTIVITI_SKIP_EXPRESSION_ENABLED", true);
        variables2.put("skip", true);
        ProcessInstance pi2 = runtimeService.startProcessInstanceByKey("setServiceResultToProcessVariablesWithSkipExpression", variables2);
        assertNull(runtimeService.getVariable(pi2.getId(), "result"));
        
        if (HistoryTestHelper.isHistoryLevelAtLeast(HistoryLevel.AUDIT, processEngineConfiguration)) {
            HistoricActivityInstance skipActivityInstance = historyService.createHistoricActivityInstanceQuery().processInstanceId(pi2.getId())
                    .activityId("valueExpressionServiceWithResultVariableNameSet")
                    .singleResult();
            assertActivityInstancesAreSame(skipActivityInstance, runtimeService.createActivityInstanceQuery().activityInstanceId(skipActivityInstance .getId()).singleResult());

            assertNotNull(skipActivityInstance);
        }

        Map<String, Object> variables3 = new HashMap<>();
        variables3.put("bean", new ValueBean("okBean"));
        variables3.put("_ACTIVITI_SKIP_EXPRESSION_ENABLED", false);
        ProcessInstance pi3 = runtimeService.startProcessInstanceByKey("setServiceResultToProcessVariablesWithSkipExpression", variables3);
        assertEquals("okBean", runtimeService.getVariable(pi3.getId(), "result"));
    }

    @Test
    @Deployment
    public void testBackwardsCompatibleExpression() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("var", "---");
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("BackwardsCompatibleExpressionProcess", variables);
        assertEquals("...---...", runtimeService.getVariable(pi.getId(), "result"));
    }

    @Test
    @Deployment
    public void testSetServiceResultWithParallelMultiInstance() {
        Map<String, Object> variables = new HashMap<>();
        List<ValueBean> beans = Arrays.asList(new ValueBean("OK"), new ValueBean("NOT_OK"));
        variables.put("beans", beans);

        ProcessInstance pi = runtimeService.startProcessInstanceByKey("setServiceResultToWithParallelMultiInstance", variables);

        assertEquals("NOT_OK", runtimeService.getVariable(pi.getId(), "subProcessVar"));

        List<Task> tasks = taskService.createTaskQuery()
            .processInstanceId(pi.getProcessInstanceId())
            .list();

        assertThat(tasks.size()).isEqualTo(1);
        assertThat(tasks.get(0).getTaskDefinitionKey()).isEqualTo("processWaitState");
    }

    @Test
    @Deployment
    public void testSetServiceLocalScopedResultWithParallelMultiInstance() {
        Map<String, Object> variables = new HashMap<>();
        List<ValueBean> beans = Arrays.asList(new ValueBean("OK"), new ValueBean("NOT_OK"));
        variables.put("beans", beans);

        ProcessInstance pi = runtimeService.startProcessInstanceByKey("setServiceLocalScopedResultWithParallelMultiInstance", variables);

        assertNull("subProcessVar should not be on process instance scope", runtimeService.getVariable(pi.getId(), "subProcessVar"));

        List<Task> tasks = taskService.createTaskQuery()
            .processInstanceId(pi.getProcessInstanceId())
            .list();

        assertThat(tasks.size()).isEqualTo(1);
        assertThat(tasks.get(0).getTaskDefinitionKey()).isEqualTo("subProcessWaitState");
    }

    @Test
    @Deployment
    public void testVarargs() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("bean", new TestVarargsBean());
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("testVarargsInExpression", variables);
        assertEquals("ABC", runtimeService.getVariable(pi.getId(), "result"));
    }
}
