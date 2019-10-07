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
package mobius.cmmn.engine.impl.callback;

import mobius.cmmn.api.runtime.CaseInstanceState;
import mobius.cmmn.engine.impl.persistence.entity.PlanItemInstanceEntity;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.common.engine.impl.callback.CallbackData;
import mobius.common.engine.impl.callback.RuntimeInstanceStateChangeCallback;
import mobius.common.engine.impl.interceptor.CommandContext;

/**
 * Callback implementation for a child case instance returning it's state change to its parent.
 *
 *
 */
public class ChildCaseInstanceStateChangeCallback implements RuntimeInstanceStateChangeCallback {

    @Override
    public void stateChanged(CallbackData callbackData) {
        
        /*
         * The child case instance has the plan item instance id as callback id stored.
         * When the child case instance is finished, the plan item of the parent case 
         * needs to be triggered.
         */
        
        if (CaseInstanceState.TERMINATED.equals(callbackData.getNewState())
                || CaseInstanceState.COMPLETED.equals(callbackData.getNewState())) {
            
            CommandContext commandContext = CommandContextUtil.getCommandContext();
            PlanItemInstanceEntity planItemInstanceEntity = CommandContextUtil.getPlanItemInstanceEntityManager(commandContext)
                            .findById(callbackData.getCallbackId());
            
            if (planItemInstanceEntity != null) {
                CommandContextUtil.getAgenda(commandContext).planTriggerPlanItemInstanceOperation(planItemInstanceEntity);
            }
        }
    }

}
