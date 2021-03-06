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

package mobius.engine.impl.jobexecutor;

import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.engine.impl.util.EventSubscriptionUtil;
import mobius.eventsubscription.service.EventSubscriptionService;
import mobius.eventsubscription.service.impl.persistence.entity.EventSubscriptionEntity;
import mobius.job.service.JobHandler;
import mobius.job.service.impl.persistence.entity.JobEntity;
import mobius.variable.api.delegate.VariableScope;

/**
 * @author Daniel Meyer
 *
 */
public class ProcessEventJobHandler implements JobHandler {

    public static final String TYPE = "event";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void execute(JobEntity job, String configuration, VariableScope variableScope, CommandContext commandContext) {

        EventSubscriptionService eventSubscriptionService = CommandContextUtil.getEventSubscriptionService(commandContext);

        // lookup subscription:
        EventSubscriptionEntity eventSubscriptionEntity = eventSubscriptionService.findById(configuration);

        // if event subscription is null, ignore
        if (eventSubscriptionEntity != null) {
            EventSubscriptionUtil.eventReceived(eventSubscriptionEntity, null, false);
        }

    }

}
