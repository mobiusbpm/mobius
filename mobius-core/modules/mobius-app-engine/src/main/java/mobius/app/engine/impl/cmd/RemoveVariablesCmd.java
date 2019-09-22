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
package mobius.app.engine.impl.cmd;

import java.util.Collection;
import java.util.List;

import mobius.app.engine.impl.util.CommandContextUtil;
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.api.scope.ScopeTypes;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.variable.service.VariableService;
import mobius.variable.service.impl.persistence.entity.VariableInstanceEntity;

/**
 * @author Tijs Rademakers
 */
public class RemoveVariablesCmd implements Command<Void> {
    
    protected String appDefinitionId;
    protected Collection<String> variableNames;
    
    public RemoveVariablesCmd(String appDefinitionId, Collection<String> variableNames) {
        this.appDefinitionId = appDefinitionId;
        this.variableNames = variableNames;
    }
    
    @Override
    public Void execute(CommandContext commandContext) {
        if (appDefinitionId == null) {
            throw new FlowableIllegalArgumentException("appDefinitionId is null");
        }
        
        if (variableNames == null) {
            throw new FlowableIllegalArgumentException("variableNames is null");
        }
     
        VariableService variableService = CommandContextUtil.getVariableService(commandContext);
        List<VariableInstanceEntity> variableInstances = variableService.findVariableInstanceByScopeIdAndScopeType(appDefinitionId, ScopeTypes.APP);
        if (variableInstances != null && variableInstances.size() > 0) {
            for (VariableInstanceEntity variableInstanceEntity : variableInstances) {
                if (variableNames.contains(variableInstanceEntity.getName())) {
                    variableService.deleteVariableInstance(variableInstanceEntity);
                }
            }
        }
        
        return null;
    }

}
