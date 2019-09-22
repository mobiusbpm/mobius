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
package mobius.cdi.impl.event;

import java.util.HashSet;
import java.util.Set;

import mobius.bpmn.model.BaseElement;
import mobius.bpmn.model.BusinessRuleTask;
import mobius.bpmn.model.CallActivity;
import mobius.bpmn.model.EndEvent;
import mobius.bpmn.model.ErrorEventDefinition;
import mobius.bpmn.model.EventGateway;
import mobius.bpmn.model.EventSubProcess;
import mobius.bpmn.model.ExclusiveGateway;
import mobius.bpmn.model.FlowElement;
import mobius.bpmn.model.FlowableListener;
import mobius.bpmn.model.ImplementationType;
import mobius.bpmn.model.InclusiveGateway;
import mobius.bpmn.model.ManualTask;
import mobius.bpmn.model.ParallelGateway;
import mobius.bpmn.model.ReceiveTask;
import mobius.bpmn.model.ScriptTask;
import mobius.bpmn.model.SendTask;
import mobius.bpmn.model.SequenceFlow;
import mobius.bpmn.model.ServiceTask;
import mobius.bpmn.model.SignalEventDefinition;
import mobius.bpmn.model.StartEvent;
import mobius.bpmn.model.SubProcess;
import mobius.bpmn.model.Task;
import mobius.bpmn.model.ThrowEvent;
import mobius.bpmn.model.TimerEventDefinition;
import mobius.bpmn.model.Transaction;
import mobius.bpmn.model.UserTask;
import mobius.cdi.BusinessProcessEventType;
import mobius.engine.delegate.ExecutionListener;
import mobius.engine.delegate.TaskListener;
import mobius.engine.impl.bpmn.parser.BpmnParse;
import mobius.engine.parse.BpmnParseHandler;

/**
 * {@link BpmnParseHandler} registering the {@link CdiExecutionListener} for distributing execution events using the cdi event infrastructure
 *
 * @author Daniel Meyer
 * @author Joram Barrez
 */
public class CdiEventSupportBpmnParseHandler implements BpmnParseHandler {

    protected static final Set<Class<? extends BaseElement>> supportedTypes = new HashSet<>();

    static {
        supportedTypes.add(StartEvent.class);
        supportedTypes.add(EndEvent.class);
        supportedTypes.add(ExclusiveGateway.class);
        supportedTypes.add(InclusiveGateway.class);
        supportedTypes.add(ParallelGateway.class);
        supportedTypes.add(ScriptTask.class);
        supportedTypes.add(ServiceTask.class);
        supportedTypes.add(BusinessRuleTask.class);
        supportedTypes.add(Task.class);
        supportedTypes.add(ManualTask.class);
        supportedTypes.add(UserTask.class);
        supportedTypes.add(SubProcess.class);
        supportedTypes.add(EventSubProcess.class);
        supportedTypes.add(CallActivity.class);
        supportedTypes.add(SendTask.class);
        supportedTypes.add(ReceiveTask.class);
        supportedTypes.add(EventGateway.class);
        supportedTypes.add(Transaction.class);
        supportedTypes.add(ThrowEvent.class);

        supportedTypes.add(TimerEventDefinition.class);
        supportedTypes.add(ErrorEventDefinition.class);
        supportedTypes.add(SignalEventDefinition.class);

        supportedTypes.add(SequenceFlow.class);
    }

    @Override
    public Set<Class<? extends BaseElement>> getHandledTypes() {
        return supportedTypes;
    }

    @Override
    public void parse(BpmnParse bpmnParse, BaseElement element) {

        if (element instanceof SequenceFlow) {

            SequenceFlow sequenceFlow = (SequenceFlow) element;
            CdiExecutionListener listener = new CdiExecutionListener(sequenceFlow.getId());
            addListenerToElement(sequenceFlow, ExecutionListener.EVENTNAME_TAKE, listener);

        } else {

            if (element instanceof UserTask) {

                UserTask userTask = (UserTask) element;

                addCreateListener(userTask);
                addAssignListener(userTask);
                addCompleteListener(userTask);
                addDeleteListener(userTask);
            }

            if (element instanceof FlowElement) {

                FlowElement flowElement = (FlowElement) element;

                addStartEventListener(flowElement);
                addEndEventListener(flowElement);
            }

        }
    }

    private void addCompleteListener(UserTask userTask) {
        addListenerToUserTask(userTask, TaskListener.EVENTNAME_COMPLETE, new CdiTaskListener(userTask.getId(), BusinessProcessEventType.COMPLETE_TASK));
    }

    private void addAssignListener(UserTask userTask) {
        addListenerToUserTask(userTask, TaskListener.EVENTNAME_ASSIGNMENT, new CdiTaskListener(userTask.getId(), BusinessProcessEventType.ASSIGN_TASK));
    }

    private void addCreateListener(UserTask userTask) {
        addListenerToUserTask(userTask, TaskListener.EVENTNAME_CREATE, new CdiTaskListener(userTask.getId(), BusinessProcessEventType.CREATE_TASK));
    }

    protected void addDeleteListener(UserTask userTask) {
        addListenerToUserTask(userTask, TaskListener.EVENTNAME_DELETE, new CdiTaskListener(userTask.getId(), BusinessProcessEventType.DELETE_TASK));
    }

    protected void addStartEventListener(FlowElement flowElement) {
        CdiExecutionListener listener = new CdiExecutionListener(flowElement.getId(), BusinessProcessEventType.START_ACTIVITY);
        addListenerToElement(flowElement, ExecutionListener.EVENTNAME_START, listener);
    }

    protected void addEndEventListener(FlowElement flowElement) {
        CdiExecutionListener listener = new CdiExecutionListener(flowElement.getId(), BusinessProcessEventType.END_ACTIVITY);
        addListenerToElement(flowElement, ExecutionListener.EVENTNAME_END, listener);
    }

    protected void addListenerToElement(FlowElement flowElement, String event, Object instance) {
        FlowableListener listener = new FlowableListener();
        listener.setEvent(event);
        listener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_INSTANCE);
        listener.setInstance(instance);
        flowElement.getExecutionListeners().add(listener);
    }

    protected void addListenerToUserTask(UserTask userTask, String event, Object instance) {
        FlowableListener listener = new FlowableListener();
        listener.setEvent(event);
        listener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_INSTANCE);
        listener.setInstance(instance);
        userTask.getTaskListeners().add(listener);
    }

}
