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
package mobius.engine.impl.history.async.json.transformer;

import static mobius.job.service.impl.history.async.util.AsyncHistoryJsonUtil.getStringFromJson;

import java.util.Collections;
import java.util.List;

import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.history.HistoricActivityInstance;
import mobius.engine.impl.HistoricActivityInstanceQueryImpl;
import mobius.engine.impl.history.async.HistoryJsonConstants;
import mobius.engine.impl.persistence.entity.HistoricActivityInstanceEntity;
import mobius.engine.impl.persistence.entity.HistoricActivityInstanceEntityManager;
import mobius.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import mobius.engine.impl.persistence.entity.HistoricProcessInstanceEntityManager;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.job.service.impl.persistence.entity.HistoryJobEntity;
import mobius.task.api.history.HistoricTaskInstance;
import mobius.task.service.HistoricTaskService;
import mobius.task.service.impl.HistoricTaskInstanceQueryImpl;
import mobius.task.service.impl.persistence.entity.HistoricTaskInstanceEntity;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class UpdateProcessDefinitionCascadeHistoryJsonTransformer extends AbstractNeedsProcessInstanceHistoryJsonTransformer {

    @Override
    public List<String> getTypes() {
        return Collections.singletonList(HistoryJsonConstants.TYPE_UPDATE_PROCESS_DEFINITION_CASCADE);
    }
    
    @Override
    public void transformJson(HistoryJobEntity job, ObjectNode historicalData, CommandContext commandContext) {
        String processDefinitionId = getStringFromJson(historicalData, HistoryJsonConstants.PROCESS_DEFINITION_ID);
        String processInstanceId = getStringFromJson(historicalData, HistoryJsonConstants.PROCESS_INSTANCE_ID);
        
        HistoricProcessInstanceEntityManager historicProcessInstanceEntityManager = CommandContextUtil.getHistoricProcessInstanceEntityManager(commandContext);
        HistoricProcessInstanceEntity historicProcessInstance = (HistoricProcessInstanceEntity) historicProcessInstanceEntityManager.findById(processInstanceId);
        historicProcessInstance.setProcessDefinitionId(processDefinitionId);
        historicProcessInstanceEntityManager.update(historicProcessInstance);

        HistoricTaskService historicTaskService = CommandContextUtil.getHistoricTaskService();
        HistoricTaskInstanceQueryImpl taskQuery = new HistoricTaskInstanceQueryImpl();
        taskQuery.processInstanceId(processInstanceId);
        List<HistoricTaskInstance> historicTasks = historicTaskService.findHistoricTaskInstancesByQueryCriteria(taskQuery);
        if (historicTasks != null) {
            for (HistoricTaskInstance historicTaskInstance : historicTasks) {
                HistoricTaskInstanceEntity taskEntity = (HistoricTaskInstanceEntity) historicTaskInstance;
                taskEntity.setProcessDefinitionId(processDefinitionId);
                historicTaskService.updateHistoricTask(taskEntity, true);
            }
        }
        
        HistoricActivityInstanceEntityManager historicActivityInstanceEntityManager = CommandContextUtil.getHistoricActivityInstanceEntityManager(commandContext);
        HistoricActivityInstanceQueryImpl activityQuery = new HistoricActivityInstanceQueryImpl();
        activityQuery.processInstanceId(processInstanceId);
        List<HistoricActivityInstance> historicActivities = historicActivityInstanceEntityManager.findHistoricActivityInstancesByQueryCriteria(activityQuery);
        if (historicActivities != null) {
            for (HistoricActivityInstance historicActivityInstance : historicActivities) {
                HistoricActivityInstanceEntity activityEntity = (HistoricActivityInstanceEntity) historicActivityInstance;
                activityEntity.setProcessDefinitionId(processDefinitionId);
                historicActivityInstanceEntityManager.update(activityEntity);
            }
        }
    }

}
