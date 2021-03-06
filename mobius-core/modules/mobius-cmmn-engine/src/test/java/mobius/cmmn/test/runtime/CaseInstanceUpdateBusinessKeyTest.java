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
package mobius.cmmn.test.runtime;

import mobius.cmmn.api.delegate.DelegatePlanItemInstance;
import mobius.cmmn.api.history.HistoricCaseInstance;
import mobius.cmmn.api.listener.PlanItemInstanceLifecycleListener;
import mobius.cmmn.api.runtime.CaseInstance;
import mobius.cmmn.engine.impl.persistence.entity.CaseInstanceEntity;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.cmmn.engine.test.CmmnDeployment;
import mobius.cmmn.engine.test.FlowableCmmnTestCase;
import mobius.common.engine.impl.history.HistoryLevel;
import mobius.task.api.Task;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CaseInstanceUpdateBusinessKeyTest extends FlowableCmmnTestCase {

    private String deplId;

    @Before
    public void createCase() {
        mobius.cmmn.api.repository.CmmnDeployment deployment = cmmnRepositoryService.createDeployment().
            addClasspathResource("mobius/cmmn/test/runtime/CaseInstanceUpdateBusinessKeyTest.testUpdateExistingCaseBusinessKey.cmmn").
            deploy();

        deplId = deployment.getId();
    }

    @After
    public void deleteCase() {
        cmmnRepositoryService.deleteDeployment(deplId, true);
    }

    @Test
    @CmmnDeployment
    public void testCaseInstanceUpdateBusinessKey() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("businessKeyCase").start();
        assertEquals("bzKey", caseInstance.getBusinessKey());

        Task task = cmmnTaskService.createTaskQuery().singleResult();
        cmmnTaskService.complete(task.getId());

        if (cmmnEngineConfiguration.getHistoryLevel().isAtLeast(HistoryLevel.ACTIVITY)) {
            HistoricCaseInstance historicCaseInstance = cmmnHistoryService.createHistoricCaseInstanceQuery().singleResult();
            assertEquals("bzKey", historicCaseInstance.getBusinessKey());
        }
    }

    @Test
    @CmmnDeployment
    public void testUpdateExistingCaseBusinessKey() {
        cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("businessKeyCase").businessKey("bzKey").start();

        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceQuery().singleResult();
        assertEquals("bzKey", caseInstance.getBusinessKey());

        if (cmmnEngineConfiguration.getHistoryLevel().isAtLeast(HistoryLevel.ACTIVITY)) {
            HistoricCaseInstance historicCaseInstance = cmmnHistoryService.createHistoricCaseInstanceQuery().singleResult();
            assertEquals("bzKey", historicCaseInstance.getBusinessKey());
        }

        cmmnRuntimeService.updateBusinessKey(caseInstance.getId(), "newKey");

        caseInstance = cmmnRuntimeService.createCaseInstanceQuery().singleResult();
        assertEquals("newKey", caseInstance.getBusinessKey());

        Task task = cmmnTaskService.createTaskQuery().singleResult();
        cmmnTaskService.complete(task.getId());
        if (cmmnEngineConfiguration.getHistoryLevel().isAtLeast(HistoryLevel.ACTIVITY)) {
            HistoricCaseInstance historicCaseInstance2 = cmmnHistoryService.createHistoricCaseInstanceQuery().singleResult();
            assertEquals("newKey", historicCaseInstance2.getBusinessKey());
        }
    }

    public static class UpdateBusinessKeyPlanItemJavaDelegate implements PlanItemInstanceLifecycleListener {

        private static final long serialVersionUID = 1L;

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
            CaseInstanceEntity caseInstanceEntity = CommandContextUtil.getCaseInstanceEntityManager().findById(planItemInstance.getCaseInstanceId());
            CommandContextUtil.getCaseInstanceEntityManager().updateCaseInstanceBusinessKey(caseInstanceEntity, "bzKey");
        }
    }

}
