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
import mobius.variable.service.impl.persistence.entity.HistoricVariableInstanceEntity;

import com.fasterxml.jackson.databind.node.ObjectNode;

import static mobius.job.service.impl.history.async.util.AsyncHistoryJsonUtil.getDateFromJson;
import static mobius.job.service.impl.history.async.util.AsyncHistoryJsonUtil.getDoubleFromJson;
import static mobius.job.service.impl.history.async.util.AsyncHistoryJsonUtil.getLongFromJson;
import static mobius.job.service.impl.history.async.util.AsyncHistoryJsonUtil.getStringFromJson;

/**
 *
 */
public class VariableUpdatedHistoryJsonTransformer extends AbstractHistoryJsonTransformer {

    @Override
    public List<String> getTypes() {
        return Collections.singletonList(CmmnAsyncHistoryConstants.TYPE_VARIABLE_UPDATED);
    }

    @Override
    public boolean isApplicable(ObjectNode historicalData, CommandContext commandContext) {
        return getHistoricVariableInstanceEntity(historicalData, commandContext) != null;
    }
    
    protected HistoricVariableInstanceEntity getHistoricVariableInstanceEntity(ObjectNode historicalData, CommandContext commandContext) {
        return CommandContextUtil.getHistoricVariableService(commandContext)
            .getHistoricVariableInstance(getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_ID));
    }

    @Override
    public void transformJson(HistoryJobEntity job, ObjectNode historicalData, CommandContext commandContext) {
        HistoricVariableInstanceEntity historicVariable = getHistoricVariableInstanceEntity(historicalData, commandContext);
        
        Date time = getDateFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_LAST_UPDATE_TIME);
        if (historicVariable.getLastUpdatedTime().after(time)) {
            // If the historic variable already has a later time, we don't need to change its details
            // to something that is already superseded by later data.
            return;
        }
        
        VariableTypes variableTypes = CommandContextUtil.getCmmnEngineConfiguration(commandContext).getVariableTypes();
        VariableType variableType = variableTypes.getVariableType(getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_VARIABLE_TYPE));
        historicVariable.setVariableType(variableType);

        historicVariable.setTextValue(getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_VARIABLE_TEXT_VALUE));
        historicVariable.setTextValue2(getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_VARIABLE_TEXT_VALUE2));
        historicVariable.setDoubleValue(getDoubleFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_VARIABLE_DOUBLE_VALUE));
        historicVariable.setLongValue(getLongFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_VARIABLE_LONG_VALUE));
        
        String variableBytes = getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_VARIABLE_BYTES_VALUE);
        if (StringUtils.isNotEmpty(variableBytes)) {
            historicVariable.setBytes(Base64.getDecoder().decode(variableBytes));
        }
        
        historicVariable.setLastUpdatedTime(time);
    }

}
