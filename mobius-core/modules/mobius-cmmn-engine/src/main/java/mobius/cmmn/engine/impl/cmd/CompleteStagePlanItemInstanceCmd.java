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

import mobius.cmmn.api.runtime.PlanItemDefinitionType;
import mobius.cmmn.engine.impl.persistence.entity.PlanItemInstanceEntity;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.impl.interceptor.CommandContext;

/**
 *
 */
public class CompleteStagePlanItemInstanceCmd extends AbstractNeedsPlanItemInstanceCmd {

    protected boolean force;

    public CompleteStagePlanItemInstanceCmd(String planItemInstanceId) {
        super(planItemInstanceId);
    }

    public CompleteStagePlanItemInstanceCmd(String planItemInstanceId, boolean force) {
        super(planItemInstanceId);
        this.force = force;
    }

    public CompleteStagePlanItemInstanceCmd(String planItemInstanceId, Map<String, Object> variables,
            Map<String, Object> localVariables, Map<String, Object> transientVariables, boolean force) {
        super(planItemInstanceId, variables, localVariables, transientVariables);
        this.force = force;
    }

    @Override
    protected void internalExecute(CommandContext commandContext, PlanItemInstanceEntity planItemInstanceEntity) {
        if (!PlanItemDefinitionType.STAGE.equals(planItemInstanceEntity.getPlanItemDefinitionType())) {
            throw new FlowableIllegalArgumentException("Can only complete plan item instances of type stage. Type is " + planItemInstanceEntity.getPlanItemDefinitionType());
        }
        if (!force && !planItemInstanceEntity.isCompleteable()) { // if force is true, ignore the completeable flag
            throw new FlowableIllegalArgumentException("Can only complete a stage plan item instance that is marked as completeable (there might still be active plan item instance).");
        }
        CommandContextUtil.getAgenda(commandContext).planCompletePlanItemInstanceOperation(planItemInstanceEntity);
    }
    
}
