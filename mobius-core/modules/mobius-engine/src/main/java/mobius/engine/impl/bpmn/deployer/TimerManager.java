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
package mobius.engine.impl.bpmn.deployer;

import java.util.ArrayList;
import java.util.List;

import mobius.bpmn.model.EventDefinition;
import mobius.bpmn.model.FlowElement;
import mobius.bpmn.model.Process;
import mobius.bpmn.model.StartEvent;
import mobius.bpmn.model.TimerEventDefinition;
import mobius.common.engine.impl.context.Context;
import mobius.common.engine.impl.util.CollectionUtil;
import mobius.engine.ProcessEngineConfiguration;
import mobius.engine.impl.jobexecutor.TimerEventHandler;
import mobius.engine.impl.jobexecutor.TimerStartEventJobHandler;
import mobius.engine.impl.persistence.entity.ProcessDefinitionEntity;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.engine.impl.util.TimerUtil;
import mobius.job.service.TimerJobService;
import mobius.job.service.impl.cmd.CancelJobsCmd;
import mobius.job.service.impl.persistence.entity.TimerJobEntity;

/**
 * Manages timers for newly-deployed process definitions and their previous versions.
 */
public class TimerManager {

    protected void removeObsoleteTimers(ProcessDefinitionEntity processDefinition) {
        List<TimerJobEntity> jobsToDelete = null;

        if (processDefinition.getTenantId() != null && !ProcessEngineConfiguration.NO_TENANT_ID.equals(processDefinition.getTenantId())) {
            jobsToDelete = CommandContextUtil.getTimerJobService().findJobsByTypeAndProcessDefinitionKeyAndTenantId(
                    TimerStartEventJobHandler.TYPE, processDefinition.getKey(), processDefinition.getTenantId());
        } else {
            jobsToDelete = CommandContextUtil.getTimerJobService()
                    .findJobsByTypeAndProcessDefinitionKeyNoTenantId(TimerStartEventJobHandler.TYPE, processDefinition.getKey());
        }

        if (jobsToDelete != null) {
            for (TimerJobEntity job : jobsToDelete) {
                new CancelJobsCmd(job.getId()).execute(Context.getCommandContext());
            }
        }
    }

    protected void scheduleTimers(ProcessDefinitionEntity processDefinition, Process process) {
        TimerJobService timerJobService = CommandContextUtil.getTimerJobService();
        List<TimerJobEntity> timers = getTimerDeclarations(processDefinition, process);
        for (TimerJobEntity timer : timers) {
            timerJobService.scheduleTimerJob(timer);
        }
    }

    protected List<TimerJobEntity> getTimerDeclarations(ProcessDefinitionEntity processDefinition, Process process) {
        List<TimerJobEntity> timers = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(process.getFlowElements())) {
            for (FlowElement element : process.getFlowElements()) {
                if (element instanceof StartEvent) {
                    StartEvent startEvent = (StartEvent) element;
                    if (CollectionUtil.isNotEmpty(startEvent.getEventDefinitions())) {
                        EventDefinition eventDefinition = startEvent.getEventDefinitions().get(0);
                        if (eventDefinition instanceof TimerEventDefinition) {
                            TimerEventDefinition timerEventDefinition = (TimerEventDefinition) eventDefinition;
                            TimerJobEntity timerJob = TimerUtil.createTimerEntityForTimerEventDefinition(timerEventDefinition, false, null, TimerStartEventJobHandler.TYPE,
                                    TimerEventHandler.createConfiguration(startEvent.getId(), timerEventDefinition.getEndDate(), timerEventDefinition.getCalendarName()));

                            if (timerJob != null) {
                                timerJob.setProcessDefinitionId(processDefinition.getId());

                                if (processDefinition.getTenantId() != null) {
                                    timerJob.setTenantId(processDefinition.getTenantId());
                                }
                                timers.add(timerJob);
                            }

                        }
                    }
                }
            }
        }

        return timers;
    }
}
