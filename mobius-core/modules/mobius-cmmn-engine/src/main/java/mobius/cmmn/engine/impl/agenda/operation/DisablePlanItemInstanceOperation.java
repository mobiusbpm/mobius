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
package mobius.cmmn.engine.impl.agenda.operation;

import mobius.cmmn.api.runtime.PlanItemInstanceState;
import mobius.cmmn.engine.impl.persistence.entity.PlanItemInstanceEntity;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.cmmn.model.PlanItemTransition;
import mobius.common.engine.impl.interceptor.CommandContext;

/**
 *
 */
public class DisablePlanItemInstanceOperation extends AbstractChangePlanItemInstanceStateOperation {
    
    public DisablePlanItemInstanceOperation(CommandContext commandContext, PlanItemInstanceEntity planItemInstanceEntity) {
        super(commandContext, planItemInstanceEntity);
    }
    
    @Override
    protected String getLifeCycleTransition() {
        return PlanItemTransition.DISABLE;
    }
    
    @Override
    protected String getNewState() {
        return PlanItemInstanceState.DISABLED;
    }
    
    @Override
    protected void internalExecute() {
        planItemInstanceEntity.setLastDisabledTime(getCurrentTime(commandContext));
        CommandContextUtil.getCmmnHistoryManager(commandContext).recordPlanItemInstanceDisabled(planItemInstanceEntity);
    }
    
}
