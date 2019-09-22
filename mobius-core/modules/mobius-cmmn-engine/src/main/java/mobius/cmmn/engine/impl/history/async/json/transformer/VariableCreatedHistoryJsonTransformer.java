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
import mobius.cmmn.engine.impl.history.async.CmmnAsyncHistoryConstants;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.job.service.impl.persistence.entity.HistoryJobEntity;
import mobius.variable.api.types.VariableType;
import mobius.variable.api.types.VariableTypes;
import mobius.variable.service.HistoricVariableService;
import mobius.variable.service.impl.persistence.entity.HistoricVariableInstanceEntity;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Joram Barrez
 */
public class VariableCreatedHistoryJsonTransformer extends AbstractHistoryJsonTransformer {

    @Override
    public List<String> getTypes() {
        return Collections.singletonList(CmmnAsyncHistoryConstants.TYPE_VARIABLE_CREATED);
    }

    @Override
    public boolean isApplicable(ObjectNode historicalData, CommandContext commandContext) {
        return true;
    }

    @Override
    public void transformJson(HistoryJobEntity job, ObjectNode historicalData, CommandContext commandContext) {
        HistoricVariableService historicVariableService = CommandContextUtil.getHistoricVariableService();
        HistoricVariableInstanceEntity historicVariableInstanceEntity = historicVariableService.createHistoricVariableInstance();
        historicVariableInstanceEntity.setId(getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_ID));
        historicVariableInstanceEntity.setScopeId(getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_SCOPE_ID));
        historicVariableInstanceEntity.setSubScopeId(getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_SUB_SCOPE_ID));
        historicVariableInstanceEntity.setScopeType(getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_SCOPE_TYPE));
        historicVariableInstanceEntity.setTaskId(getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_TASK_ID));
        historicVariableInstanceEntity.setExecutionId(getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_EXECUTION_ID));
        historicVariableInstanceEntity.setProcessInstanceId(getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_PROCESS_INSTANCE_ID));
        historicVariableInstanceEntity.setRevision(getIntegerFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_REVISION));
        historicVariableInstanceEntity.setName(getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_NAME));
        
        VariableTypes variableTypes = CommandContextUtil.getCmmnEngineConfiguration().getVariableTypes();
        VariableType variableType = variableTypes.getVariableType(getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_VARIABLE_TYPE));
        
        historicVariableInstanceEntity.setVariableType(variableType);

        historicVariableInstanceEntity.setTextValue(getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_VARIABLE_TEXT_VALUE));
        historicVariableInstanceEntity.setTextValue2(getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_VARIABLE_TEXT_VALUE2));
        historicVariableInstanceEntity.setDoubleValue(getDoubleFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_VARIABLE_DOUBLE_VALUE));
        historicVariableInstanceEntity.setLongValue(getLongFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_VARIABLE_LONG_VALUE));
        
        String variableBytes = getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_VARIABLE_BYTES_VALUE);
        if (StringUtils.isNotEmpty(variableBytes)) {
            historicVariableInstanceEntity.setBytes(Base64.getDecoder().decode(variableBytes));
        }
        
        Date time = getDateFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_CREATE_TIME);
        historicVariableInstanceEntity.setCreateTime(time);
        historicVariableInstanceEntity.setLastUpdatedTime(time);

        historicVariableService.insertHistoricVariableInstance(historicVariableInstanceEntity);
    }

}