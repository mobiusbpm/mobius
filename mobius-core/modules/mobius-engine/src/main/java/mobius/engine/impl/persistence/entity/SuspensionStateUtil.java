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
package mobius.engine.impl.persistence.entity;

import mobius.common.engine.api.FlowableException;
import mobius.common.engine.api.delegate.event.FlowableEngineEventType;
import mobius.common.engine.api.delegate.event.FlowableEventDispatcher;
import mobius.common.engine.impl.context.Context;
import mobius.common.engine.impl.db.SuspensionState;
import mobius.common.engine.impl.identity.Authentication;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.delegate.event.impl.FlowableEventBuilder;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.task.api.history.HistoricTaskLogEntryType;
import mobius.task.service.TaskServiceConfiguration;
import mobius.task.service.impl.BaseHistoricTaskLogEntryBuilderImpl;
import mobius.task.service.impl.persistence.entity.TaskEntity;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Helper class for suspension state
 * 
 *
 */
public class SuspensionStateUtil {

    public static void setSuspensionState(ProcessDefinitionEntity processDefinitionEntity, SuspensionState state) {
        if (processDefinitionEntity.getSuspensionState() == state.getStateCode()) {
            throw new FlowableException("Cannot set suspension state '" + state + "' for " + processDefinitionEntity + "': already in state '" + state + "'.");
        }
        processDefinitionEntity.setSuspensionState(state.getStateCode());
        dispatchStateChangeEvent(processDefinitionEntity, state);
    }

    public static void setSuspensionState(ExecutionEntity executionEntity, SuspensionState state) {
        if (executionEntity.getSuspensionState() == state.getStateCode()) {
            throw new FlowableException("Cannot set suspension state '" + state + "' for " + executionEntity + "': already in state '" + state + "'.");
        }
        executionEntity.setSuspensionState(state.getStateCode());
        dispatchStateChangeEvent(executionEntity, state);
    }

    public static void setSuspensionState(TaskEntity taskEntity, SuspensionState state) {
        if (taskEntity.getSuspensionState() == state.getStateCode()) {
            throw new FlowableException("Cannot set suspension state '" + state + "' for " + taskEntity + "': already in state '" + state + "'.");
        }
        taskEntity.setSuspensionState(state.getStateCode());

        addTaskSuspensionStateEntryLog(taskEntity, state);

        dispatchStateChangeEvent(taskEntity, state);
    }

    protected static void addTaskSuspensionStateEntryLog(TaskEntity taskEntity, SuspensionState state) {
        TaskServiceConfiguration taskServiceConfiguration = CommandContextUtil.getTaskServiceConfiguration();
        if (taskServiceConfiguration.isEnableHistoricTaskLogging()) {
            BaseHistoricTaskLogEntryBuilderImpl taskLogEntryBuilder = new BaseHistoricTaskLogEntryBuilderImpl(taskEntity);
            ObjectNode data = taskServiceConfiguration.getObjectMapper().createObjectNode();
            data.put("previousSuspensionState", taskEntity.getSuspensionState());
            data.put("newSuspensionState", state.getStateCode());
            taskLogEntryBuilder.timeStamp(taskServiceConfiguration.getClock().getCurrentTime());
            taskLogEntryBuilder.userId(Authentication.getAuthenticatedUserId());
            taskLogEntryBuilder.data(data.toString());
            taskLogEntryBuilder.type(HistoricTaskLogEntryType.USER_TASK_SUSPENSIONSTATE_CHANGED.name());
            taskServiceConfiguration.getInternalHistoryTaskManager().recordHistoryUserTaskLog(taskLogEntryBuilder);
        }
    }

    protected static void dispatchStateChangeEvent(Object entity, SuspensionState state) {
        CommandContext commandContext = Context.getCommandContext();
        FlowableEventDispatcher eventDispatcher = null;
        if (commandContext != null) {
            eventDispatcher = CommandContextUtil.getEventDispatcher();
        }
        if (eventDispatcher != null && eventDispatcher.isEnabled()) {
            FlowableEngineEventType eventType = null;
            if (state == SuspensionState.ACTIVE) {
                eventType = FlowableEngineEventType.ENTITY_ACTIVATED;
            } else {
                eventType = FlowableEngineEventType.ENTITY_SUSPENDED;
            }
            eventDispatcher.dispatchEvent(FlowableEventBuilder.createEntityEvent(eventType, entity));
        }
    }

}
