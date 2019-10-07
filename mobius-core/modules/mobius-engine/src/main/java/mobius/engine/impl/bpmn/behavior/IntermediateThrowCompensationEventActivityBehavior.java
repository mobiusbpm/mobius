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

package mobius.engine.impl.bpmn.behavior;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import mobius.bpmn.model.Activity;
import mobius.bpmn.model.Association;
import mobius.bpmn.model.BoundaryEvent;
import mobius.bpmn.model.CompensateEventDefinition;
import mobius.bpmn.model.FlowElement;
import mobius.bpmn.model.FlowElementsContainer;
import mobius.bpmn.model.Process;
import mobius.bpmn.model.ThrowEvent;
import mobius.common.engine.api.FlowableException;
import mobius.common.engine.impl.context.Context;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.delegate.DelegateExecution;
import mobius.engine.impl.bpmn.helper.ScopeUtil;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.engine.impl.util.ProcessDefinitionUtil;
import mobius.eventsubscription.service.EventSubscriptionService;
import mobius.eventsubscription.service.impl.persistence.entity.CompensateEventSubscriptionEntity;

/**
 *
 *
 */
public class IntermediateThrowCompensationEventActivityBehavior extends FlowNodeActivityBehavior {

    private static final long serialVersionUID = 1L;

    protected final CompensateEventDefinition compensateEventDefinition;

    public IntermediateThrowCompensationEventActivityBehavior(CompensateEventDefinition compensateEventDefinition) {
        this.compensateEventDefinition = compensateEventDefinition;
    }

    @Override
    public void execute(DelegateExecution execution) {
        ThrowEvent throwEvent = (ThrowEvent) execution.getCurrentFlowElement();

        /*
         * From the BPMN 2.0 spec:
         * 
         * The Activity to be compensated MAY be supplied.
         * 
         * If an Activity is not supplied, then the compensation is broadcast to all completed Activities in the current Sub- Process (if present), or the entire Process instance (if at the global
         * level). This “throws” the compensation.
         */
        final String activityRef = compensateEventDefinition.getActivityRef();

        CommandContext commandContext = Context.getCommandContext();
        EventSubscriptionService eventSubscriptionService = CommandContextUtil.getEventSubscriptionService(commandContext);

        List<CompensateEventSubscriptionEntity> eventSubscriptions = new ArrayList<>();
        if (StringUtils.isNotEmpty(activityRef)) {
            
            // If an activity ref is provided, only that activity is compensated
            List<CompensateEventSubscriptionEntity> compensationEvents = eventSubscriptionService
                    .findCompensateEventSubscriptionsByProcessInstanceIdAndActivityId(execution.getProcessInstanceId(), activityRef);
            
            if (compensationEvents == null || compensationEvents.size() == 0) {
                // check if compensation activity was referenced directly (backwards compatibility pre 6.4.0)
                
                String processDefinitionId = execution.getProcessDefinitionId();
                Process process = ProcessDefinitionUtil.getProcess(processDefinitionId);
                if (process == null) {
                    throw new FlowableException("Process model (id = " + processDefinitionId + ") could not be found");
                }

                String compensationActivityId = null;
                FlowElement flowElement = process.getFlowElement(activityRef, true);
                if (flowElement instanceof Activity) {
                    Activity activity = (Activity) flowElement;
                    if (activity.isForCompensation()) {
                        List<Association> associations = process.findAssociationsWithTargetRefRecursive(activity.getId());
                        for (Association association : associations) {
                            FlowElement sourceElement = process.getFlowElement(association.getSourceRef(), true);
                            if (sourceElement instanceof BoundaryEvent) {
                                BoundaryEvent sourceBoundaryEvent = (BoundaryEvent) sourceElement;
                                if (sourceBoundaryEvent.getAttachedToRefId() != null) {
                                    compensationActivityId = sourceBoundaryEvent.getAttachedToRefId();
                                    break;
                                }
                            }
                        }
                    }
                }
                
                if (compensationActivityId != null) {
                    compensationEvents = eventSubscriptionService
                            .findCompensateEventSubscriptionsByProcessInstanceIdAndActivityId(execution.getProcessInstanceId(), compensationActivityId);
                }
            }

            eventSubscriptions.addAll(compensationEvents);

        } else {

            // If no activity ref is provided, it is broadcast to the current sub process / process instance
            Process process = ProcessDefinitionUtil.getProcess(execution.getProcessDefinitionId());

            FlowElementsContainer flowElementsContainer = null;
            if (throwEvent.getSubProcess() == null) {
                flowElementsContainer = process;
            } else {
                flowElementsContainer = throwEvent.getSubProcess();
            }

            for (FlowElement flowElement : flowElementsContainer.getFlowElements()) {
                if (flowElement instanceof Activity) {
                    eventSubscriptions.addAll(eventSubscriptionService
                            .findCompensateEventSubscriptionsByProcessInstanceIdAndActivityId(execution.getProcessInstanceId(), flowElement.getId()));
                }
            }

        }

        if (eventSubscriptions.isEmpty()) {
            leave(execution);
        } else {
            // TODO: implement async (waitForCompletion=false in bpmn)
            ScopeUtil.throwCompensationEvent(eventSubscriptions, execution, false);
            leave(execution);
        }
    }
}
