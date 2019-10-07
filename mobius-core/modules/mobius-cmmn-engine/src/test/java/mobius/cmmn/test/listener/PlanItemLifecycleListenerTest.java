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
package mobius.cmmn.test.listener;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import mobius.cmmn.api.delegate.DelegatePlanItemInstance;
import mobius.cmmn.api.listener.PlanItemInstanceLifecycleListener;
import mobius.cmmn.api.runtime.CaseInstance;
import mobius.cmmn.api.runtime.UserEventListenerInstance;
import mobius.cmmn.engine.CmmnEngineConfiguration;
import mobius.cmmn.engine.test.CmmnDeployment;
import mobius.cmmn.test.impl.CustomCmmnConfigurationFlowableTestCase;
import mobius.common.engine.impl.history.HistoryLevel;
import org.junit.Test;

/**
 *
 */
public class PlanItemLifecycleListenerTest extends CustomCmmnConfigurationFlowableTestCase {

    @Override
    protected String getEngineName() {
        return this.getClass().getName();
    }

    @Override
    protected void configureConfiguration(CmmnEngineConfiguration cmmnEngineConfiguration) {
        Map<Object, Object> beans = new HashMap<>();
        cmmnEngineConfiguration.setBeans(beans);

        beans.put("delegateListener", new TestDelegateTaskListener());
    }

    @Test
    @CmmnDeployment
    public void testListeners() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testPlanItemLifecycleListeners").start();

        assertVariable(caseInstance, "classDelegateVariable", "Hello World");
        assertVariable(caseInstance, "variableFromDelegateExpression", "Hello World from delegate expression");
        assertVariable(caseInstance, "expressionVar", "planItemIsActive");

        assertVariable(caseInstance, "stageActive",true);
        assertVariable(caseInstance, "milestoneReached", true);
    }

    @Test
    @CmmnDeployment
    public void testEventListenerPlanItemLifecycleListener() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testEventListenerPlanItemLifecycleListener").start();

        assertThat((Boolean) cmmnRuntimeService.getVariable(caseInstance.getId(), "available")).isTrue();
        assertThat((Boolean) cmmnRuntimeService.getVariable(caseInstance.getId(), "completed")).isNull();
        assertThat((Boolean) cmmnRuntimeService.getVariable(caseInstance.getId(), "terminated")).isNull();

        UserEventListenerInstance userEventListenerInstance = cmmnRuntimeService.createUserEventListenerInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
        cmmnRuntimeService.completeUserEventListenerInstance(userEventListenerInstance.getId());

        assertThat((Boolean) cmmnRuntimeService.getVariable(caseInstance.getId(), "available")).isTrue();
        assertThat((Boolean) cmmnRuntimeService.getVariable(caseInstance.getId(), "completed")).isTrue();
        assertThat((Boolean) cmmnRuntimeService.getVariable(caseInstance.getId(), "terminated")).isNull();

        // Same, but terminate the case
        caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testEventListenerPlanItemLifecycleListener").start();
        cmmnRuntimeService.terminateCaseInstance(caseInstance.getId());

        if (cmmnEngineConfiguration.getHistoryLevel().isAtLeast(HistoryLevel.AUDIT)) {
            assertThat((Boolean) cmmnHistoryService.createHistoricVariableInstanceQuery().caseInstanceId(caseInstance.getId()).variableName("available").singleResult().getValue()).isTrue();
            assertThat(cmmnHistoryService.createHistoricVariableInstanceQuery().caseInstanceId(caseInstance.getId()).variableName("completed").singleResult()).isNull();
            assertThat((Boolean) cmmnHistoryService.createHistoricVariableInstanceQuery().caseInstanceId(caseInstance.getId()).variableName("terminate").singleResult().getValue()).isTrue();
        }
    }

    private void assertVariable(CaseInstance caseInstance, String varName, boolean value) {
        Boolean variable = (Boolean) cmmnRuntimeService.getVariable(caseInstance.getId(), varName);
        assertThat(variable).isEqualTo(value);
    }

    private void assertVariable(CaseInstance caseInstance, String varName, String value) {
        String variable = (String) cmmnRuntimeService.getVariable(caseInstance.getId(), varName);
        assertThat(variable).isEqualTo(value);
    }

    static class TestDelegateTaskListener implements PlanItemInstanceLifecycleListener {

        @Override
        public String getSourceState() {
            return null;
        }

        @Override
        public String getTargetState() {
            return null;
        }

        @Override
        public void stateChanged(DelegatePlanItemInstance planItemInstance, String oldState, String newState) {
            planItemInstance.setVariable("variableFromDelegateExpression", "Hello World from delegate expression");
        }

    }

}
