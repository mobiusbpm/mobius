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
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.api.scope.ScopeTypes;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.variable.api.types.VariableType;
import mobius.variable.api.types.VariableTypes;
import mobius.variable.service.VariableService;
import mobius.variable.service.impl.persistence.entity.VariableInstanceEntity;

/**
 *
 */
public class SetVariableCmd implements Command<Void> {
    
    protected String appDefinitionId;
    protected String variableName;
    protected Object variableValue;
    
    public SetVariableCmd(String appDefinitionId, String variableName, Object variableValue) {
        this.appDefinitionId = appDefinitionId;
        this.variableName = variableName;
        this.variableValue = variableValue;
    }
    
    @Override
    public Void execute(CommandContext commandContext) {
        if (appDefinitionId == null) {
            throw new FlowableIllegalArgumentException("appDefinitionId is null");
        }
        if (variableName == null) {
            throw new FlowableIllegalArgumentException("variable name is null");
        }
        
        VariableTypes variableTypes = CommandContextUtil.getAppEngineConfiguration().getVariableTypes();
        VariableType type = variableTypes.findVariableType(variableValue);
     
        VariableService variableService = CommandContextUtil.getVariableService(commandContext);
        VariableInstanceEntity variableInstance = variableService.createVariableInstance(variableName, type);
        variableInstance.setScopeId(appDefinitionId);
        variableInstance.setScopeType(ScopeTypes.APP);
        variableInstance.setValue(variableValue);
        variableService.updateVariableInstance(variableInstance);
        
        return null;
    }

}
