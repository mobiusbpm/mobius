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

import java.util.List;

import mobius.bpmn.model.TimerEventDefinition;
import mobius.engine.delegate.DelegateExecution;
import mobius.engine.history.DeleteReason;
import mobius.engine.impl.jobexecutor.TimerEventHandler;
import mobius.engine.impl.jobexecutor.TriggerTimerEventJobHandler;
import mobius.engine.impl.persistence.entity.ExecutionEntity;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.engine.impl.util.TimerUtil;
import mobius.job.service.JobService;
import mobius.job.service.impl.persistence.entity.JobEntity;
import mobius.job.service.impl.persistence.entity.TimerJobEntity;

public class IntermediateCatchTimerEventActivityBehavior extends IntermediateCatchEventActivityBehavior {

    private static final long serialVersionUID = 1L;

    protected TimerEventDefinition timerEventDefinition;

    public IntermediateCatchTimerEventActivityBehavior(TimerEventDefinition timerEventDefinition) {
        this.timerEventDefinition = timerEventDefinition;
    }

    @Override
    public void execute(DelegateExecution execution) {
        // end date should be ignored for intermediate timer events.
        TimerJobEntity timerJob = TimerUtil.createTimerEntityForTimerEventDefinition(timerEventDefinition, false, (ExecutionEntity) execution, TriggerTimerEventJobHandler.TYPE,
                TimerEventHandler.createConfiguration(execution.getCurrentActivityId(), null, timerEventDefinition.getCalendarName()));

        if (timerJob != null) {
            CommandContextUtil.getTimerJobService().scheduleTimerJob(timerJob);
        }
    }

    @Override
    public void eventCancelledByEventGateway(DelegateExecution execution) {
        JobService jobService = CommandContextUtil.getJobService();
        List<JobEntity> jobEntities = jobService.findJobsByExecutionId(execution.getId());

        for (JobEntity jobEntity : jobEntities) { // Should be only one
            jobService.deleteJob(jobEntity);
        }

        CommandContextUtil.getExecutionEntityManager().deleteExecutionAndRelatedData((ExecutionEntity) execution,
                DeleteReason.EVENT_BASED_GATEWAY_CANCEL, false);
    }

}
