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

import java.util.Map;

import mobius.cmmn.engine.impl.persistence.entity.PlanItemInstanceEntity;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.api.FlowableObjectNotFoundException;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;

/**
 *
 */
public class SetLocalVariablesCmd implements Command<Void> {
    
    protected String planItemInstanceId;
    protected Map<String, Object> variables;
    
    public SetLocalVariablesCmd(String planItemInstanceId, Map<String, Object> variables) {
        this.planItemInstanceId = planItemInstanceId;
        this.variables = variables;
    }
    
    @Override
    public Void execute(CommandContext commandContext) {
        if (planItemInstanceId == null) {
            throw new FlowableIllegalArgumentException("planItemInstanceId is null");
        }
        if (variables == null) {
            throw new FlowableIllegalArgumentException("variables is null");
        }
        if (variables.isEmpty()) {
            throw new FlowableIllegalArgumentException("variables is empty");
        }
     
        PlanItemInstanceEntity planItemInstanceEntity = CommandContextUtil.getPlanItemInstanceEntityManager(commandContext).findById(planItemInstanceId);
        if (planItemInstanceEntity == null) {
            throw new FlowableObjectNotFoundException("No plan item instance found for id " + planItemInstanceId, PlanItemInstanceEntity.class);
        }
        planItemInstanceEntity.setVariablesLocal(variables);
        
        CommandContextUtil.getAgenda(commandContext).planEvaluateCriteriaOperation(planItemInstanceEntity.getCaseInstanceId());
        
        return null;
    }

}
