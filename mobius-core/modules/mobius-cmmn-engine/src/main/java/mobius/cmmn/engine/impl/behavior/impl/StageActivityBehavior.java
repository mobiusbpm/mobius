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
package mobius.cmmn.engine.impl.behavior.impl;

import java.util.List;

import mobius.cmmn.api.delegate.DelegatePlanItemInstance;
import mobius.cmmn.engine.impl.behavior.CoreCmmnTriggerableActivityBehavior;
import mobius.cmmn.engine.impl.behavior.PlanItemActivityBehavior;
import mobius.cmmn.engine.impl.persistence.entity.PlanItemInstanceEntity;
import mobius.cmmn.engine.impl.runtime.StateTransition;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.cmmn.model.PlanItemTransition;
import mobius.cmmn.model.Stage;
import mobius.common.engine.api.delegate.Expression;
import mobius.common.engine.impl.interceptor.CommandContext;

/**
 *
 */
public class StageActivityBehavior extends CoreCmmnTriggerableActivityBehavior implements PlanItemActivityBehavior {
    
    protected Stage stage;
    
    public StageActivityBehavior(Stage stage) {
        this.stage = stage;
    }
    
    @Override
    public void execute(CommandContext commandContext, PlanItemInstanceEntity planItemInstanceEntity) {
        if (planItemInstanceEntity.getPlanItem().getName() != null) {
            Expression nameExpression = CommandContextUtil.getExpressionManager(commandContext).createExpression(planItemInstanceEntity.getPlanItem().getName());
            planItemInstanceEntity.setName(nameExpression.getValue(planItemInstanceEntity).toString());
        }
        CommandContextUtil.getAgenda(commandContext).planInitStageOperation(planItemInstanceEntity);
    }
    
    @Override
    public void trigger(CommandContext commandContext, PlanItemInstanceEntity planItemInstance) {
        List<PlanItemInstanceEntity> childPlanItemInstances = planItemInstance.getChildPlanItemInstances();
        if (childPlanItemInstances != null) {
            for (PlanItemInstanceEntity childPlanItemInstance : childPlanItemInstances) {
                if (StateTransition.isPossible(planItemInstance, PlanItemTransition.COMPLETE)) {
                    CommandContextUtil.getAgenda().planCompletePlanItemInstanceOperation(childPlanItemInstance);
                }
            }
        }
        CommandContextUtil.getAgenda(commandContext).planCompletePlanItemInstanceOperation(planItemInstance);
    }
    
    @Override
    public void onStateTransition(CommandContext commandContext, DelegatePlanItemInstance planItemInstance, String transition) {
        if (PlanItemTransition.TERMINATE.equals(transition) || PlanItemTransition.EXIT.equals(transition)) {
            handleChildPlanItemInstances(commandContext, planItemInstance, transition);
        }
    }

    protected void handleChildPlanItemInstances(CommandContext commandContext, DelegatePlanItemInstance planItemInstance, String transition) {
        // The stage plan item will be deleted by the regular TerminatePlanItemOperation
        PlanItemInstanceEntity planItemInstanceEntity = (PlanItemInstanceEntity) planItemInstance;
        List<PlanItemInstanceEntity> childPlanItemInstances = planItemInstanceEntity.getChildPlanItemInstances();
        if (childPlanItemInstances != null) {
            for (PlanItemInstanceEntity childPlanItemInstance : childPlanItemInstances) {
                if (StateTransition.isPossible(planItemInstance, transition)) {
                    if (PlanItemTransition.TERMINATE.equals(transition)) {
                        CommandContextUtil.getAgenda(commandContext).planTerminatePlanItemInstanceOperation(childPlanItemInstance);
                    } else if (PlanItemTransition.EXIT.equals(transition)) {
                        CommandContextUtil.getAgenda(commandContext).planExitPlanItemInstanceOperation(childPlanItemInstance, null);
                    }
                }
            }
        }
    }

}
