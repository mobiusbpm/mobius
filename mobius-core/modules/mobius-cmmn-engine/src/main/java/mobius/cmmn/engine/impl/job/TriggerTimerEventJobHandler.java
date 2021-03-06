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
package mobius.cmmn.engine.impl.job;

import mobius.cmmn.engine.impl.persistence.entity.PlanItemInstanceEntity;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.job.service.JobHandler;
import mobius.job.service.impl.persistence.entity.JobEntity;
import mobius.variable.api.delegate.VariableScope;

/**
 *
 */
public class TriggerTimerEventJobHandler implements JobHandler {

    public static final String TYPE = "cmmn-trigger-timer";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void execute(JobEntity job, String configuration, VariableScope variableScope, CommandContext commandContext) {
        PlanItemInstanceEntity planItemInstance = (PlanItemInstanceEntity) variableScope;
        CommandContextUtil.getAgenda(commandContext).planTriggerPlanItemInstanceOperation(planItemInstance);
    }
    
}
