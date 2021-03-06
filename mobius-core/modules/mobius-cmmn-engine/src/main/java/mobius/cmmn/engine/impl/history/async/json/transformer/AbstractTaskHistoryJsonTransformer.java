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
package mobius.cmmn.engine.impl.history.async.json.transformer;

import mobius.cmmn.engine.impl.history.async.CmmnAsyncHistoryConstants;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.task.service.impl.persistence.entity.HistoricTaskInstanceEntity;

import com.fasterxml.jackson.databind.node.ObjectNode;

import static mobius.job.service.impl.history.async.util.AsyncHistoryJsonUtil.getDateFromJson;
import static mobius.job.service.impl.history.async.util.AsyncHistoryJsonUtil.getIntegerFromJson;
import static mobius.job.service.impl.history.async.util.AsyncHistoryJsonUtil.getStringFromJson;

/**
 *
 */
public abstract class AbstractTaskHistoryJsonTransformer extends AbstractHistoryJsonTransformer {
    
    protected HistoricTaskInstanceEntity getHistoricTaskEntity(ObjectNode historicalData, CommandContext commandContext) {
        return CommandContextUtil.getHistoricTaskService(commandContext)
                .getHistoricTask(getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_ID));
    }

    protected void copyCommonHistoricTaskInstanceFields(ObjectNode historicalData,HistoricTaskInstanceEntity historicTaskInstanceEntity) {
        historicTaskInstanceEntity.setId(getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_ID));
        historicTaskInstanceEntity.setTaskDefinitionId(getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_TASK_DEFINITION_ID));
        historicTaskInstanceEntity.setTaskDefinitionKey(getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_TASK_DEFINITION_KEY));
        historicTaskInstanceEntity.setScopeId(getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_SCOPE_ID));
        historicTaskInstanceEntity.setSubScopeId(getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_SUB_SCOPE_ID));
        historicTaskInstanceEntity.setScopeType(getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_SCOPE_TYPE));
        historicTaskInstanceEntity.setScopeDefinitionId(getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_SCOPE_DEFINITION_ID));
        historicTaskInstanceEntity.setName(getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_NAME));
        historicTaskInstanceEntity.setParentTaskId(getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_PARENT_TASK_ID));
        historicTaskInstanceEntity.setDescription(getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_DESCRIPTION));
        historicTaskInstanceEntity.setOwner(getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_OWNER));
        historicTaskInstanceEntity.setAssignee(getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_ASSIGNEE));
        if (historicalData.has(CmmnAsyncHistoryConstants.FIELD_CREATE_TIME)) {
            historicTaskInstanceEntity.setCreateTime(getDateFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_CREATE_TIME));
        } else {
            // For backwards compatibility. New async data uses the FIELD_CREATE_TIME. This should be removed eventually
            historicTaskInstanceEntity.setCreateTime(getDateFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_START_TIME));
        }
        historicTaskInstanceEntity.setFormKey(getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_FORM_KEY));
        historicTaskInstanceEntity.setPriority(getIntegerFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_PRIORITY));
        historicTaskInstanceEntity.setDueDate(getDateFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_DUE_DATE));
        historicTaskInstanceEntity.setCategory(getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_CATEGORY));
        historicTaskInstanceEntity.setTenantId(getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_TENANT_ID));
        historicTaskInstanceEntity.setLastUpdateTime(getDateFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_LAST_UPDATE_TIME));
    }

}
