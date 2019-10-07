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

import mobius.cmmn.api.delegate.DelegatePlanItemInstance;
import mobius.cmmn.engine.impl.behavior.CmmnActivityBehavior;
import mobius.cmmn.engine.impl.behavior.CoreCmmnTriggerableActivityBehavior;
import mobius.cmmn.engine.impl.behavior.PlanItemActivityBehavior;
import mobius.cmmn.engine.impl.persistence.entity.PlanItemInstanceEntity;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.common.engine.impl.interceptor.CommandContext;

/**
 * {@link CmmnActivityBehavior} implementation for the CMMN Event Listener.
 *
 *
 */
public class GenericEventListenerActivityBehaviour extends CoreCmmnTriggerableActivityBehavior implements PlanItemActivityBehavior {

    @Override
    public void onStateTransition(CommandContext commandContext, DelegatePlanItemInstance planItemInstance, String transition) {

    }

    @Override
    public void execute(CommandContext commandContext, PlanItemInstanceEntity planItemInstanceEntity) {
        CommandContextUtil.getAgenda(commandContext).planOccurPlanItemInstanceOperation(planItemInstanceEntity);
    }

    @Override
    public void trigger(CommandContext commandContext, PlanItemInstanceEntity planItemInstanceEntity) {
        execute(commandContext, planItemInstanceEntity);
    }

}
