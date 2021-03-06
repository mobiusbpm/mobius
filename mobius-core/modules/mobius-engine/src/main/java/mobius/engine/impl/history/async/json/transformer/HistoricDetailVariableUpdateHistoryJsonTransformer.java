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

import static mobius.job.service.impl.history.async.util.AsyncHistoryJsonUtil.getBooleanFromJson;
import static mobius.job.service.impl.history.async.util.AsyncHistoryJsonUtil.getDateFromJson;
import static mobius.job.service.impl.history.async.util.AsyncHistoryJsonUtil.getDoubleFromJson;
import static mobius.job.service.impl.history.async.util.AsyncHistoryJsonUtil.getIntegerFromJson;
import static mobius.job.service.impl.history.async.util.AsyncHistoryJsonUtil.getLongFromJson;
import static mobius.job.service.impl.history.async.util.AsyncHistoryJsonUtil.getStringFromJson;

import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.history.HistoricActivityInstance;
import mobius.engine.impl.history.async.HistoryJsonConstants;
import mobius.engine.impl.persistence.entity.HistoricDetailEntityManager;
import mobius.engine.impl.persistence.entity.HistoricDetailVariableInstanceUpdateEntity;
import mobius.engine.impl.persistence.entity.data.HistoricDetailDataManager;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.job.service.impl.persistence.entity.HistoryJobEntity;
import mobius.variable.api.types.VariableType;
import mobius.variable.api.types.VariableTypes;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class HistoricDetailVariableUpdateHistoryJsonTransformer extends AbstractHistoryJsonTransformer {

    @Override
    public List<String> getTypes() {
        return Collections.singletonList(HistoryJsonConstants.TYPE_HISTORIC_DETAIL_VARIABLE_UPDATE);
    }

    @Override
    public boolean isApplicable(ObjectNode historicalData, CommandContext commandContext) {
        String activityId = getStringFromJson(historicalData, HistoryJsonConstants.ACTIVITY_ID);
        
        // Variables for a mi root execution (like nrOfInstances, nrOfCompletedInstance, etc.) are stored without a reference to the historical activity.
        Boolean isMiRootExecution = getBooleanFromJson(historicalData, HistoryJsonConstants.IS_MULTI_INSTANCE_ROOT_EXECUTION, false);
        
        if (!isMiRootExecution && StringUtils.isNotEmpty(activityId)) {
            HistoricActivityInstance activityInstance = findHistoricActivityInstance(commandContext, 
                    getStringFromJson(historicalData, HistoryJsonConstants.SOURCE_EXECUTION_ID), activityId);
            if (activityInstance == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void transformJson(HistoryJobEntity job, ObjectNode historicalData, CommandContext commandContext) {
        HistoricDetailDataManager historicDetailDataManager = CommandContextUtil.getProcessEngineConfiguration(commandContext).getHistoricDetailDataManager();
        HistoricDetailVariableInstanceUpdateEntity historicDetailEntity = historicDetailDataManager.createHistoricDetailVariableInstanceUpdate();
        historicDetailEntity.setProcessInstanceId(getStringFromJson(historicalData, HistoryJsonConstants.PROCESS_INSTANCE_ID));
        historicDetailEntity.setExecutionId(getStringFromJson(historicalData, HistoryJsonConstants.EXECUTION_ID));
        historicDetailEntity.setTaskId(getStringFromJson(historicalData, HistoryJsonConstants.TASK_ID));
        historicDetailEntity.setRevision(getIntegerFromJson(historicalData, HistoryJsonConstants.REVISION));
        historicDetailEntity.setName(getStringFromJson(historicalData, HistoryJsonConstants.NAME));
        
        Boolean isMiRootExecution = getBooleanFromJson(historicalData, HistoryJsonConstants.IS_MULTI_INSTANCE_ROOT_EXECUTION, false);
        if (!isMiRootExecution) {
            String runtimeActivityInstanceId = getStringFromJson(historicalData, HistoryJsonConstants.RUNTIME_ACTIVITY_INSTANCE_ID);
            if (StringUtils.isNotEmpty(runtimeActivityInstanceId)) {
                historicDetailEntity.setActivityInstanceId(runtimeActivityInstanceId);
            } else {
                // there can be still jobs in the queue without runtimeActivityInstanceId
                String activityId = getStringFromJson(historicalData, HistoryJsonConstants.ACTIVITY_ID);
                if (StringUtils.isNotEmpty(activityId)) {
                    HistoricActivityInstance activityInstance = findHistoricActivityInstance(commandContext,
                        getStringFromJson(historicalData, HistoryJsonConstants.SOURCE_EXECUTION_ID), activityId);

                    if (activityInstance != null) {
                        historicDetailEntity.setActivityInstanceId(activityInstance.getId());
                    }
                }
            }
        }
        
        VariableTypes variableTypes = CommandContextUtil.getProcessEngineConfiguration().getVariableTypes();
        VariableType variableType = variableTypes.getVariableType(getStringFromJson(historicalData, HistoryJsonConstants.VARIABLE_TYPE));
        
        historicDetailEntity.setVariableType(variableType);

        historicDetailEntity.setTextValue(getStringFromJson(historicalData, HistoryJsonConstants.VARIABLE_TEXT_VALUE));
        historicDetailEntity.setTextValue2(getStringFromJson(historicalData, HistoryJsonConstants.VARIABLE_TEXT_VALUE2));
        historicDetailEntity.setDoubleValue(getDoubleFromJson(historicalData, HistoryJsonConstants.VARIABLE_DOUBLE_VALUE));
        historicDetailEntity.setLongValue(getLongFromJson(historicalData, HistoryJsonConstants.VARIABLE_LONG_VALUE));
        
        String variableBytes = getStringFromJson(historicalData, HistoryJsonConstants.VARIABLE_BYTES_VALUE);
        if (StringUtils.isNotEmpty(variableBytes)) {
            historicDetailEntity.setBytes(Base64.getDecoder().decode(variableBytes));
        }
        
        Date time = getDateFromJson(historicalData, HistoryJsonConstants.CREATE_TIME);
        historicDetailEntity.setTime(time);
        
        HistoricDetailEntityManager historicDetailEntityManager = CommandContextUtil.getProcessEngineConfiguration(commandContext).getHistoricDetailEntityManager();
        historicDetailEntityManager.insert(historicDetailEntity);
    }

}
