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
package mobius.engine.test.api.runtime;

import mobius.common.engine.impl.history.HistoryLevel;
import mobius.engine.delegate.DelegateExecution;
import mobius.engine.delegate.ExecutionListener;
import mobius.engine.history.HistoricProcessInstance;
import mobius.engine.impl.persistence.entity.ExecutionEntity;
import mobius.engine.impl.test.HistoryTestHelper;
import mobius.engine.impl.test.PluggableFlowableTestCase;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.engine.runtime.ProcessInstance;
import mobius.engine.test.Deployment;
import org.junit.jupiter.api.Test;

public class ProcessInstanceUpdateBusinessKeyTest extends PluggableFlowableTestCase {

    @Test
    @Deployment
    public void testProcessInstanceUpdateBusinessKey() {
        runtimeService.startProcessInstanceByKey("businessKeyProcess");

        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().singleResult();
        assertEquals("bzKey", processInstance.getBusinessKey());

        if (HistoryTestHelper.isHistoryLevelAtLeast(HistoryLevel.ACTIVITY, processEngineConfiguration)) {
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().singleResult();
            assertEquals("bzKey", historicProcessInstance.getBusinessKey());
        }
    }

    @Test
    @Deployment
    public void testUpdateExistingBusinessKey() {
        runtimeService.startProcessInstanceByKey("businessKeyProcess", "testKey");

        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().singleResult();
        assertEquals("testKey", processInstance.getBusinessKey());

        if (HistoryTestHelper.isHistoryLevelAtLeast(HistoryLevel.ACTIVITY, processEngineConfiguration)) {
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().singleResult();
            assertEquals("testKey", historicProcessInstance.getBusinessKey());
        }

        runtimeService.updateBusinessKey(processInstance.getId(), "newKey");

        processInstance = runtimeService.createProcessInstanceQuery().singleResult();
        assertEquals("newKey", processInstance.getBusinessKey());

        if (HistoryTestHelper.isHistoryLevelAtLeast(HistoryLevel.ACTIVITY, processEngineConfiguration)) {
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().singleResult();
            assertEquals("newKey", historicProcessInstance.getBusinessKey());
        }
    }

    public static class UpdateBusinessKeyExecutionListener implements ExecutionListener {

        private static final long serialVersionUID = 1L;

        @Override
        public void notify(DelegateExecution delegateExecution) {
            CommandContextUtil.getExecutionEntityManager().updateProcessInstanceBusinessKey((ExecutionEntity) delegateExecution, "bzKey");
        }
    }

}
