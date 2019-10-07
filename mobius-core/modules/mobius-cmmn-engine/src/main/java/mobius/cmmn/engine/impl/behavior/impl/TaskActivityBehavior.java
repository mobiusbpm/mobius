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

import org.apache.commons.lang3.StringUtils;
import mobius.cmmn.api.delegate.DelegatePlanItemInstance;
import mobius.cmmn.api.runtime.PlanItemInstanceState;
import mobius.cmmn.engine.impl.behavior.CoreCmmnTriggerableActivityBehavior;
import mobius.cmmn.engine.impl.persistence.entity.PlanItemInstanceEntity;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.common.engine.api.FlowableException;
import mobius.common.engine.api.delegate.Expression;
import mobius.common.engine.impl.interceptor.CommandContext;

/**
 *
 */
public class TaskActivityBehavior extends CoreCmmnTriggerableActivityBehavior {

    protected boolean isBlocking;
    protected String isBlockingExpression;

    public TaskActivityBehavior(boolean isBlocking, String isBlockingExpression) {
        this.isBlocking = isBlocking;
        this.isBlockingExpression = isBlockingExpression;
    }

    @Override
    public void execute(CommandContext commandContext, PlanItemInstanceEntity planItemInstanceEntity) {
        if (!evaluateIsBlocking(planItemInstanceEntity)) {
            CommandContextUtil.getAgenda(commandContext).planCompletePlanItemInstanceOperation((PlanItemInstanceEntity) planItemInstanceEntity);
        }
    }

    protected boolean evaluateIsBlocking(DelegatePlanItemInstance planItemInstance) {
        boolean blocking = isBlocking;
        if (StringUtils.isNotEmpty(isBlockingExpression)) {
            Expression expression = CommandContextUtil.getExpressionManager().createExpression(isBlockingExpression);
            blocking = (Boolean) expression.getValue(planItemInstance);
        }
        return blocking;
    }

    @Override
    public void trigger(CommandContext commandContext, PlanItemInstanceEntity planItemInstance) {
        if (!PlanItemInstanceState.ACTIVE.equals(planItemInstance.getState())) {
            throw new FlowableException("Can only trigger a plan item that is in the ACTIVE state");
        }
        CommandContextUtil.getAgenda(commandContext).planCompletePlanItemInstanceOperation((PlanItemInstanceEntity) planItemInstance);
    }

}
