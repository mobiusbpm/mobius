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
package mobius.cmmn.engine.impl.function;

import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import mobius.cmmn.api.runtime.PlanItemInstanceState;
import mobius.cmmn.engine.impl.persistence.entity.CaseInstanceEntity;
import mobius.cmmn.engine.impl.persistence.entity.PlanItemInstanceEntity;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.cmmn.engine.impl.util.PlanItemInstanceContainerUtil;
import mobius.cmmn.model.EventListener;
import mobius.cmmn.model.PlanItemDefinition;

/**
 *
 */
public class IsStageCompletableExpressionFunction extends AbstractCmmnExpressionFunction {

    public IsStageCompletableExpressionFunction() {
        super("isStageCompletable");
    }

    @Override
    protected boolean isMultiParameterFunction() {
        return false;
    }

    @Override
    protected boolean isNoParameterMethod() {
        return true;
    }

    public static boolean isStageCompletable(Object object) {
        if (object instanceof PlanItemInstanceEntity) {
            PlanItemInstanceEntity planItemInstanceEntity = (PlanItemInstanceEntity) object;

            if (planItemInstanceEntity.isStage()) {
                return planItemInstanceEntity.isCompleteable();

            } else if (planItemInstanceEntity.getStageInstanceId() != null) {
                PlanItemInstanceEntity stagePlanItemInstanceEntity = planItemInstanceEntity.getStagePlanItemInstanceEntity();

                // Special care needed for the event listeners with an available condition: a new evaluation needs to be done
                // as the completable only gets set at the end of the evaluation cycle.

                PlanItemDefinition planItemDefinition = planItemInstanceEntity.getPlanItem().getPlanItemDefinition();
                if (PlanItemInstanceState.AVAILABLE.equals(planItemInstanceEntity.getState())
                        && planItemDefinition instanceof EventListener
                        && (StringUtils.isNotEmpty(((EventListener) planItemDefinition).getAvailableConditionExpression()))) {
                    return PlanItemInstanceContainerUtil.isEndStateReachedForAllRequiredChildPlanItems(stagePlanItemInstanceEntity, Collections.singletonList(planItemInstanceEntity.getId()));

                } else {
                    return stagePlanItemInstanceEntity.isCompleteable();

                }

            } else {
                CaseInstanceEntity caseInstanceEntity = CommandContextUtil.getCaseInstanceEntityManager().findById(planItemInstanceEntity.getCaseInstanceId());
                return caseInstanceEntity.isCompleteable();

            }

        } else if (object instanceof CaseInstanceEntity) {
            CaseInstanceEntity caseInstanceEntity = (CaseInstanceEntity) object;
            return caseInstanceEntity.isCompleteable();

        }
        return false;
    }

}
