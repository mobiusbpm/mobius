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

import mobius.app.engine.impl.util.CommandContextUtil;
import mobius.common.engine.api.FlowableException;
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.api.scope.ScopeTypes;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.variable.service.VariableService;
import mobius.variable.service.impl.persistence.entity.VariableInstanceEntity;

/**
 *
 */
public class RemoveVariableCmd implements Command<Void> {
    
    protected String appDefinitionId;
    protected String variableName;
    
    public RemoveVariableCmd(String appDefinitionId, String variableName) {
        this.appDefinitionId = appDefinitionId;
        this.variableName = variableName;
    }
    
    @Override
    public Void execute(CommandContext commandContext) {
        if (appDefinitionId == null) {
            throw new FlowableIllegalArgumentException("appDefinitionId is null");
        }
        
        if (variableName == null) {
            throw new FlowableIllegalArgumentException("variableName is null");
        }
     
        VariableService variableService = CommandContextUtil.getVariableService(commandContext);
        VariableInstanceEntity variableInstance = variableService.findVariableInstanceByScopeIdAndScopeTypeAndName(appDefinitionId, ScopeTypes.APP, variableName);
        if (variableInstance == null) {
            throw new FlowableException("Variable not found");
        }
        
        variableService.deleteVariableInstance(variableInstance);
        
        return null;
    }

}
