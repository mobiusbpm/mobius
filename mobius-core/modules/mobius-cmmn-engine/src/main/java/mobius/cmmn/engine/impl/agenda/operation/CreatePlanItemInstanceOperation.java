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
import mobius.cmmn.engine.impl.history.CmmnHistoryManager;
import mobius.cmmn.engine.impl.persistence.entity.PlanItemInstanceEntity;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.cmmn.model.PlanItemTransition;
import mobius.common.engine.impl.interceptor.CommandContext;

/**
 *
 */
public class CreatePlanItemInstanceOperation extends AbstractChangePlanItemInstanceStateOperation {

    public CreatePlanItemInstanceOperation(CommandContext commandContext, PlanItemInstanceEntity planItemInstanceEntity) {
        super(commandContext, planItemInstanceEntity);
    }

    @Override
    protected void internalExecute() {
        if (hasRepetitionRule(planItemInstanceEntity)) {
            //Increase repetition counter, value is kept from the previous instance of the repetition
            //@see CmmOpertion.copyAndInsertPlanItemInstance used by @see EvaluateCriteriaOperation and @see AbstractDeletePlanItemInstanceOperation
            //Or if its the first instance of the repetition, this call sets the counter to 1
            setRepetitionCounter(planItemInstanceEntity, getRepetitionCounter(planItemInstanceEntity) + 1);
        }

        CmmnHistoryManager cmmnHistoryManager = CommandContextUtil.getCmmnHistoryManager(commandContext);
        cmmnHistoryManager.recordPlanItemInstanceCreated(planItemInstanceEntity);

        //Extending classes might override getNewState, so need to check the available state again
        if (getNewState().equals(PlanItemInstanceState.AVAILABLE)) {
            planItemInstanceEntity.setLastAvailableTime(getCurrentTime(commandContext));
            cmmnHistoryManager.recordPlanItemInstanceAvailable(planItemInstanceEntity);
        }
    }

    @Override
    protected String getNewState() {
        if (isEventListenerWithAvailableCondition(planItemInstanceEntity.getPlanItem())) {
            return PlanItemInstanceState.UNAVAILABLE;
        } else {
            return PlanItemInstanceState.AVAILABLE;
        }
    }

    @Override
    protected String getLifeCycleTransition() {
        return PlanItemTransition.CREATE;
    }

}
