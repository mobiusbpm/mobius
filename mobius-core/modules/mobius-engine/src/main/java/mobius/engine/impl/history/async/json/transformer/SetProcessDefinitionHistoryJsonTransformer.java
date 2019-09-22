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
import mobius.engine.impl.history.async.HistoryJsonConstants;
import mobius.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.job.service.impl.persistence.entity.HistoryJobEntity;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class SetProcessDefinitionHistoryJsonTransformer extends AbstractNeedsProcessInstanceHistoryJsonTransformer {

    @Override
    public List<String> getTypes() {
        return Collections.singletonList(HistoryJsonConstants.TYPE_SET_PROCESS_DEFINITION);
    }
    
    @Override
    public void transformJson(HistoryJobEntity job, ObjectNode historicalData, CommandContext commandContext) {
        String processInstanceId = getStringFromJson(historicalData, HistoryJsonConstants.PROCESS_INSTANCE_ID);
        String processDefinitionId = getStringFromJson(historicalData, HistoryJsonConstants.PROCESS_DEFINITION_ID);
        HistoricProcessInstanceEntity historicProcessInstance = CommandContextUtil.getProcessEngineConfiguration(commandContext).getHistoricProcessInstanceEntityManager().findById(processInstanceId);
        historicProcessInstance.setProcessDefinitionId(processDefinitionId);
    }

}
