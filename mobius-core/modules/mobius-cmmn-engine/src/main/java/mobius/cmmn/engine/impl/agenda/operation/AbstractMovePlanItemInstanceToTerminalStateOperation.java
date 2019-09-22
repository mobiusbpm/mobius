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
import mobius.cmmn.engine.impl.listener.PlanItemLifeCycleListenerUtil;
import mobius.cmmn.engine.impl.persistence.entity.PlanItemInstanceEntity;
import mobius.cmmn.engine.impl.runtime.StateTransition;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.cmmn.model.EventListener;
import mobius.cmmn.model.PlanItem;
import mobius.cmmn.model.PlanItemTransition;
import mobius.common.engine.impl.interceptor.CommandContext;

/**
 * Operation that moves a given {@link mobius.cmmn.api.runtime.PlanItemInstance} to a terminal state (completed, terminated or failed).
 *
 * @author Joram Barrez
 */
public abstract class AbstractMovePlanItemInstanceToTerminalStateOperation extends AbstractChangePlanItemInstanceStateOperation {

    public AbstractMovePlanItemInstanceToTerminalStateOperation(CommandContext commandContext, PlanItemInstanceEntity planItemInstanceEntity) {
        super(commandContext, planItemInstanceEntity);
    }

    @Override
    public void run() {
        super.run();
        
        if (isRepeatingOnDelete()) {

            // Create new repeating instance
            PlanItemInstanceEntity newPlanItemInstanceEntity = copyAndInsertPlanItemInstance(commandContext, planItemInstanceEntity, true);

            if (planItemInstanceEntity.getPlanItem() != null && planItemInstanceEntity.getPlanItem().getPlanItemDefinition() instanceof EventListener) {
                CommandContextUtil.getAgenda(commandContext).planCreatePlanItemInstanceOperation(newPlanItemInstanceEntity);

            } else {

                String oldState = newPlanItemInstanceEntity.getState();
                String newState = PlanItemInstanceState.WAITING_FOR_REPETITION;
                newPlanItemInstanceEntity.setState(newState);
                PlanItemLifeCycleListenerUtil.callLifecycleListeners(commandContext, newPlanItemInstanceEntity, oldState, newState);

                // Plan item creation "for Repetition"
                CommandContextUtil.getAgenda(commandContext).planCreatePlanItemInstanceForRepetitionOperation(newPlanItemInstanceEntity);
                // Plan item doesn't have entry criteria (checked in the if condition) and immediately goes to ACTIVE
                CommandContextUtil.getAgenda(commandContext).planActivatePlanItemInstanceOperation(newPlanItemInstanceEntity, null);
            }
        }
        
        removeSentryRelatedData();
    }

    /**
     * Implementing classes should be aware that unlike extending from AbstractChangePlanItemInstanceStateOperation, this
     * method will be executed just before the deleting the entity
     */
    @Override
    protected abstract void internalExecute();

    protected boolean isRepeatingOnDelete() {
        
        // If there are not entry criteria and the repetition rule evaluates to true, 
        // a new instance needs to be created.
        
        PlanItem planItem = planItemInstanceEntity.getPlanItem();
        if (isEvaluateRepetitionRule() && isPlanItemRepeatableOnComplete(planItem)) {
            return evaluateRepetitionRule(planItemInstanceEntity);
        }
        return false;
    }

    protected void exitChildPlanItemInstances() {
        exitChildPlanItemInstances(null);
    }

    protected void exitChildPlanItemInstances(String exitCriterionId) {
        for (PlanItemInstanceEntity child : planItemInstanceEntity.getChildPlanItemInstances()) {
            if (StateTransition.isPossible(child, PlanItemTransition.EXIT)) {
                CommandContextUtil.getAgenda(commandContext).planExitPlanItemInstanceOperation(child, exitCriterionId);
            }
        }
    }
    
    protected abstract boolean isEvaluateRepetitionRule();
    
}
