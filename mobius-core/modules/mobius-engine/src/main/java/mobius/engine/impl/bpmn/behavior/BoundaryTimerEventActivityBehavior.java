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

import mobius.bpmn.model.BoundaryEvent;
import mobius.bpmn.model.TimerEventDefinition;
import mobius.common.engine.api.FlowableException;
import mobius.engine.delegate.DelegateExecution;
import mobius.engine.impl.jobexecutor.TimerEventHandler;
import mobius.engine.impl.jobexecutor.TriggerTimerEventJobHandler;
import mobius.engine.impl.persistence.entity.ExecutionEntity;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.engine.impl.util.TimerUtil;
import mobius.job.service.impl.persistence.entity.TimerJobEntity;

/**
 *
 */
public class BoundaryTimerEventActivityBehavior extends BoundaryEventActivityBehavior {

    private static final long serialVersionUID = 1L;

    protected TimerEventDefinition timerEventDefinition;

    public BoundaryTimerEventActivityBehavior(TimerEventDefinition timerEventDefinition, boolean interrupting) {
        super(interrupting);
        this.timerEventDefinition = timerEventDefinition;
    }

    @Override
    public void execute(DelegateExecution execution) {

        ExecutionEntity executionEntity = (ExecutionEntity) execution;
        if (!(execution.getCurrentFlowElement() instanceof BoundaryEvent)) {
            throw new FlowableException("Programmatic error: " + this.getClass() + " should not be used for anything else than a boundary event");
        }

        TimerJobEntity timerJob = TimerUtil.createTimerEntityForTimerEventDefinition(timerEventDefinition, interrupting, executionEntity, TriggerTimerEventJobHandler.TYPE,
                TimerEventHandler.createConfiguration(execution.getCurrentActivityId(), timerEventDefinition.getEndDate(), timerEventDefinition.getCalendarName()));
        if (timerJob != null) {
            CommandContextUtil.getTimerJobService().scheduleTimerJob(timerJob);
        }
    }

}