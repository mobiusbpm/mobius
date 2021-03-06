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
package mobius.task.service.impl.util;

import mobius.common.engine.api.delegate.event.FlowableEngineEventType;
import mobius.common.engine.api.delegate.event.FlowableEventDispatcher;
import mobius.task.service.impl.persistence.CountingTaskEntity;
import mobius.task.service.impl.persistence.entity.TaskEntity;
import mobius.variable.service.impl.FlowableVariableEventBuilder;
import mobius.variable.service.impl.persistence.entity.VariableInstanceEntity;

/**
 *
 */
public class CountingTaskUtil {

    public static void handleDeleteVariableInstanceEntityCount(VariableInstanceEntity variableInstance, boolean fireDeleteEvent) {
        if (variableInstance.getTaskId() != null && isTaskRelatedEntityCountEnabledGlobally()) {
            CountingTaskEntity countingTaskEntity = (CountingTaskEntity) CommandContextUtil.getTaskEntityManager().findById(variableInstance.getTaskId());
            if (isTaskRelatedEntityCountEnabled(countingTaskEntity)) {
                countingTaskEntity.setVariableCount(countingTaskEntity.getVariableCount() - 1);
            }
        }

        FlowableEventDispatcher eventDispatcher = CommandContextUtil.getTaskServiceConfiguration().getEventDispatcher();
        if (fireDeleteEvent && eventDispatcher != null && eventDispatcher.isEnabled()) {
            eventDispatcher.dispatchEvent(FlowableVariableEventBuilder.createEntityEvent(FlowableEngineEventType.ENTITY_DELETED, variableInstance));

            eventDispatcher.dispatchEvent(FlowableVariableEventBuilder.createVariableEvent(FlowableEngineEventType.VARIABLE_DELETED,
                            variableInstance.getName(), null, variableInstance.getType(), variableInstance.getTaskId(),
                            variableInstance.getExecutionId(), variableInstance.getProcessInstanceId(), variableInstance.getProcessDefinitionId(),
                            variableInstance.getScopeId(), variableInstance.getScopeType()));
        }
    }

    public static void handleInsertVariableInstanceEntityCount(VariableInstanceEntity variableInstance) {
        if (variableInstance.getTaskId() != null && isTaskRelatedEntityCountEnabledGlobally()) {
            CountingTaskEntity countingTaskEntity = (CountingTaskEntity) CommandContextUtil.getTaskEntityManager().findById(variableInstance.getTaskId());
            if (isTaskRelatedEntityCountEnabled(countingTaskEntity)) {
                countingTaskEntity.setVariableCount(countingTaskEntity.getVariableCount() + 1);
            }
        }
    }

    /**
     * Check if the Task Relationship Count performance improvement is enabled.
     */
    public static boolean isTaskRelatedEntityCountEnabledGlobally() {
        if (CommandContextUtil.getTaskServiceConfiguration() == null) {
            return false;
        }
        
        return CommandContextUtil.getTaskServiceConfiguration().isEnableTaskRelationshipCounts();
    }

    public static boolean isTaskRelatedEntityCountEnabled(TaskEntity taskEntity) {
        if (taskEntity instanceof CountingTaskEntity) {
            return isTaskRelatedEntityCountEnabled((CountingTaskEntity) taskEntity);
        }
        return false;
    }

    /**
     * Similar functionality with <b>ExecutionRelatedEntityCount</b>, but on the TaskEntity level.
     */
    public static boolean isTaskRelatedEntityCountEnabled(CountingTaskEntity taskEntity) {
        return isTaskRelatedEntityCountEnabledGlobally() && taskEntity.isCountEnabled();
    }
}
