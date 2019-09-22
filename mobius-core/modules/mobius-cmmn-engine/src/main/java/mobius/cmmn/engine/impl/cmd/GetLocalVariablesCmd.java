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
package mobius.cmmn.engine.impl.cmd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.api.scope.ScopeTypes;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.variable.service.impl.persistence.entity.VariableInstanceEntity;

/**
 * @author Tijs Rademakers
 */
public class GetLocalVariablesCmd implements Command<Map<String, Object>> {
    
    protected String planItemInstanceId;
    
    public GetLocalVariablesCmd(String planItemInstanceId) {
        this.planItemInstanceId = planItemInstanceId;
    }
    
    @Override
    public Map<String, Object> execute(CommandContext commandContext) {
        if (planItemInstanceId == null) {
            throw new FlowableIllegalArgumentException("planItemInstanceId is null");
        }
        List<VariableInstanceEntity> variableInstanceEntities = CommandContextUtil.getVariableService(commandContext)
                .findVariableInstanceBySubScopeIdAndScopeType(planItemInstanceId, ScopeTypes.CMMN);
        Map<String, Object> variables = new HashMap<>(variableInstanceEntities.size());
        for (VariableInstanceEntity variableInstanceEntity : variableInstanceEntities) {
            variables.put(variableInstanceEntity.getName(), variableInstanceEntity.getValue());
        }
        return variables;
    }

}