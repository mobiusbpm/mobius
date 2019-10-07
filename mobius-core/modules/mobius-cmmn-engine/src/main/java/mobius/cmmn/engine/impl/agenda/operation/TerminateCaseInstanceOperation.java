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

import mobius.cmmn.api.runtime.CaseInstanceState;
import mobius.cmmn.engine.impl.persistence.entity.PlanItemInstanceEntity;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.common.engine.impl.interceptor.CommandContext;

/**
 *
 */
public class TerminateCaseInstanceOperation extends AbstractDeleteCaseInstanceOperation {
    
    protected boolean manualTermination;
    protected String exitCriterionId;

    public TerminateCaseInstanceOperation(CommandContext commandContext, String caseInstanceId, boolean manualTermination, String exitCriterionId) {
        super(commandContext, caseInstanceId);
        this.manualTermination = manualTermination;
        this.exitCriterionId = exitCriterionId;
    }

    @Override
    protected String getNewState() {
        return CaseInstanceState.TERMINATED;
    }
    
    @Override
    protected void changeStateForChildPlanItemInstance(PlanItemInstanceEntity planItemInstanceEntity) {
        if (manualTermination) {
            CommandContextUtil.getAgenda(commandContext).planTerminatePlanItemInstanceOperation(planItemInstanceEntity);
        } else {
            CommandContextUtil.getAgenda(commandContext).planExitPlanItemInstanceOperation(planItemInstanceEntity, exitCriterionId);
        }
    }
    
    @Override
    protected String getDeleteReason() {
        return "cmmn-state-transition-terminate-case";
    }

}
