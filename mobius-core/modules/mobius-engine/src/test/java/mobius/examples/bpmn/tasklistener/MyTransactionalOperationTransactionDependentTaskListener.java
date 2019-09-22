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
package mobius.examples.bpmn.tasklistener;

import java.util.List;
import java.util.Map;

import mobius.bpmn.model.Task;
import mobius.common.engine.impl.history.HistoryLevel;
import mobius.engine.HistoryService;
import mobius.engine.history.HistoricProcessInstance;
import mobius.engine.impl.cfg.ProcessEngineConfigurationImpl;
import mobius.engine.impl.util.CommandContextUtil;

/**
 * @author Yvo Swillens
 */
public class MyTransactionalOperationTransactionDependentTaskListener extends CurrentTaskTransactionDependentTaskListener {

    @Override
    public void notify(String processInstanceId, String executionId, Task task, Map<String, Object> executionVariables, Map<String, Object> customPropertiesMap) {
        super.notify(processInstanceId, executionId, task, executionVariables, customPropertiesMap);

        ProcessEngineConfigurationImpl processEngineConfiguration = CommandContextUtil.getProcessEngineConfiguration();
        if (processEngineConfiguration.getHistoryLevel().isAtLeast(HistoryLevel.ACTIVITY)) {
            HistoryService historyService = processEngineConfiguration.getHistoryService();

            // delete first historic instance
            List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery().list();
            historyService.deleteHistoricProcessInstance(historicProcessInstances.get(0).getId());
        }
    }
}
