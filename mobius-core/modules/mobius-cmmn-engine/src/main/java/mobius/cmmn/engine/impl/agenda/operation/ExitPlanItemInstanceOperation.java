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
 * @author Joram Barrez
 */
public class ExitPlanItemInstanceOperation extends AbstractMovePlanItemInstanceToTerminalStateOperation {

    protected String exitCriterionId;
    
    public ExitPlanItemInstanceOperation(CommandContext commandContext, PlanItemInstanceEntity planItemInstanceEntity, String exitCriterionId) {
        super(commandContext, planItemInstanceEntity);
        this.exitCriterionId = exitCriterionId;
    }
    
    @Override
    protected String getNewState() {
        return PlanItemInstanceState.TERMINATED;
    }
    
    @Override
    protected String getLifeCycleTransition() {
        return PlanItemTransition.EXIT;
    }
    
    @Override
    protected void internalExecute() {
        if (isStage(planItemInstanceEntity)) {
            exitChildPlanItemInstances(exitCriterionId);
        }

        planItemInstanceEntity.setExitCriterionId(exitCriterionId);
        planItemInstanceEntity.setEndedTime(getCurrentTime(commandContext));
        planItemInstanceEntity.setExitTime(planItemInstanceEntity.getEndedTime());
        CommandContextUtil.getCmmnHistoryManager(commandContext).recordPlanItemInstanceExit(planItemInstanceEntity);
    }

    @Override
    protected boolean isEvaluateRepetitionRule() {
        return false;
    }
    
}
