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
package mobius.cmmn.engine.impl.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import mobius.cmmn.engine.CmmnEngineConfiguration;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.common.engine.api.FlowableException;
import mobius.common.engine.api.scope.ScopeTypes;
import mobius.common.engine.api.variable.VariableContainer;
import mobius.common.engine.impl.el.ExpressionManager;
import mobius.common.engine.impl.history.HistoryLevel;
import mobius.common.engine.impl.identity.Authentication;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.task.api.Task;
import mobius.task.api.history.HistoricTaskInstance;
import mobius.task.api.history.HistoricTaskLogEntryType;
import mobius.task.service.HistoricTaskService;
import mobius.task.service.TaskService;
import mobius.task.service.TaskServiceConfiguration;
import mobius.task.service.delegate.TaskListener;
import mobius.task.service.impl.BaseHistoricTaskLogEntryBuilderImpl;
import mobius.task.service.impl.persistence.CountingTaskEntity;
import mobius.task.service.impl.persistence.entity.HistoricTaskInstanceEntity;
import mobius.task.service.impl.persistence.entity.TaskEntity;
import mobius.variable.service.impl.persistence.entity.VariableByteArrayRef;
import mobius.variable.service.impl.persistence.entity.VariableInstanceEntity;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *
 */
public class TaskHelper {

    public static void insertTask(TaskEntity taskEntity, boolean fireCreateEvent) {
        CommandContextUtil.getTaskService().insertTask(taskEntity, fireCreateEvent);

        if (taskEntity.getOwner() != null) {
            addOwnerIdentityLink(taskEntity);
        }

        if (taskEntity.getAssignee() != null) {
            addAssigneeIdentityLinks(taskEntity);
            CommandContextUtil.getCmmnEngineConfiguration().getListenerNotificationHelper().executeTaskListeners(taskEntity, TaskListener.EVENTNAME_ASSIGNMENT);
        }

    }

    public static void deleteTask(String taskId, String deleteReason, boolean cascade) {
        TaskEntity task = CommandContextUtil.getTaskService().getTask(taskId);
        if (task != null) {
            if (task.getScopeId() != null && ScopeTypes.CMMN.equals(task.getScopeType())) {
                throw new FlowableException("The task cannot be deleted because is part of a running case instance");
            } else if (task.getExecutionId() != null) {
                throw new FlowableException("The task cannot be deleted because is part of a running process instance");
            }
            deleteTask(task, deleteReason, cascade, true);
            
        } else if (cascade) {
            deleteHistoricTaskLogEntries(taskId);
            deleteHistoricTask(taskId);
        }
    }

    public static void deleteTask(TaskEntity task, String deleteReason, boolean cascade, boolean fireEvents) {
        if (!task.isDeleted()) {
            task.setDeleted(true);

            CommandContext commandContext = CommandContextUtil.getCommandContext();
            TaskService taskService = CommandContextUtil.getTaskService(commandContext);
            List<Task> subTasks = taskService.findTasksByParentTaskId(task.getId());
            for (Task subTask : subTasks) {
                deleteTask((TaskEntity) subTask, deleteReason, cascade, fireEvents);
            }

            CountingTaskEntity countingTaskEntity = (CountingTaskEntity) task;
            
            if (countingTaskEntity.isCountEnabled() && countingTaskEntity.getIdentityLinkCount() > 0) {    
                CommandContextUtil.getIdentityLinkService(commandContext).deleteIdentityLinksByTaskId(task.getId());
            }
            
            if (countingTaskEntity.isCountEnabled() && countingTaskEntity.getVariableCount() > 0) {
                
                Map<String, VariableInstanceEntity> taskVariables = task.getVariableInstanceEntities();
                ArrayList<VariableByteArrayRef> variableByteArrayRefs = new ArrayList<>();
                for (VariableInstanceEntity variableInstanceEntity : taskVariables.values()) {
                    if (variableInstanceEntity.getByteArrayRef() != null && variableInstanceEntity.getByteArrayRef().getId() != null) {
                        variableByteArrayRefs.add(variableInstanceEntity.getByteArrayRef());
                    }
                }
                
                for (VariableByteArrayRef variableByteArrayRef : variableByteArrayRefs) {
                    CommandContextUtil.getVariableServiceConfiguration(commandContext).getByteArrayEntityManager().deleteByteArrayById(variableByteArrayRef.getId());
                }
                
                if (!taskVariables.isEmpty()) {
                    CommandContextUtil.getVariableService(commandContext).deleteVariablesByTaskId(task.getId());
                }
                
                CommandContextUtil.getVariableService(commandContext).deleteVariablesByTaskId(task.getId());
            }
            
            if (cascade) {
                deleteHistoricTask(task.getId());
                deleteHistoricTaskLogEntries(task.getId());
            } else {
                CommandContextUtil.getCmmnHistoryManager(commandContext)
                    .recordTaskEnd(task, deleteReason, commandContext.getCurrentEngineConfiguration().getClock().getCurrentTime());
            }

            CommandContextUtil.getCmmnEngineConfiguration(commandContext).getListenerNotificationHelper().executeTaskListeners(task, TaskListener.EVENTNAME_DELETE);
            CommandContextUtil.getTaskService().deleteTask(task, fireEvents);
        }
    }

    public static void changeTaskAssignee(TaskEntity taskEntity, String assignee) {
        if ((taskEntity.getAssignee() != null && !taskEntity.getAssignee().equals(assignee))
                || (taskEntity.getAssignee() == null && assignee != null)) {
            
            CommandContextUtil.getTaskService().changeTaskAssignee(taskEntity, assignee);
            CommandContextUtil.getCmmnEngineConfiguration().getListenerNotificationHelper().executeTaskListeners(taskEntity, TaskListener.EVENTNAME_ASSIGNMENT);

            if (taskEntity.getId() != null) {
                addAssigneeIdentityLinks(taskEntity);
            }
        }
    }
    
    public static void changeTaskOwner(TaskEntity taskEntity, String owner) {
        if ((taskEntity.getOwner() != null && !taskEntity.getOwner().equals(owner))
                || (taskEntity.getOwner() == null && owner != null)) {

            CommandContextUtil.getTaskService().changeTaskOwner(taskEntity, owner);

            if (taskEntity.getId() != null) {
                addOwnerIdentityLink(taskEntity);
            }
        }
    }
    
    protected static void addAssigneeIdentityLinks(TaskEntity taskEntity) {
        CmmnEngineConfiguration cmmnEngineConfiguration = CommandContextUtil.getCmmnEngineConfiguration();
        if (cmmnEngineConfiguration.getIdentityLinkInterceptor() != null) {
            cmmnEngineConfiguration.getIdentityLinkInterceptor().handleAddAssigneeIdentityLinkToTask(taskEntity, taskEntity.getAssignee());
        }
    }

    protected static void addOwnerIdentityLink(TaskEntity taskEntity) {
        CmmnEngineConfiguration cmmnEngineConfiguration = CommandContextUtil.getCmmnEngineConfiguration();
        if (cmmnEngineConfiguration.getIdentityLinkInterceptor() != null) {
            cmmnEngineConfiguration.getIdentityLinkInterceptor().handleAddOwnerIdentityLinkToTask(taskEntity, taskEntity.getOwner());
        }
    }

    public static void deleteHistoricTask(String taskId) {
        if (CommandContextUtil.getCmmnEngineConfiguration().getHistoryLevel() != HistoryLevel.NONE) {
            HistoricTaskService historicTaskService = CommandContextUtil.getHistoricTaskService();
            HistoricTaskInstanceEntity historicTaskInstance = historicTaskService.getHistoricTask(taskId);
            if (historicTaskInstance != null) {
    
                List<HistoricTaskInstanceEntity> subTasks = historicTaskService.findHistoricTasksByParentTaskId(historicTaskInstance.getId());
                for (HistoricTaskInstance subTask : subTasks) {
                    deleteHistoricTask(subTask.getId());
                    deleteHistoricTaskLogEntries(subTask.getId());
                }
    
                CommandContextUtil.getHistoricVariableService().deleteHistoricVariableInstancesByTaskId(taskId);
                CommandContextUtil.getHistoricIdentityLinkService().deleteHistoricIdentityLinksByTaskId(taskId);
    
                historicTaskService.deleteHistoricTask(historicTaskInstance);
            }
        }
    }

    public static void deleteHistoricTaskLogEntries(String taskId) {
        if (CommandContextUtil.getTaskServiceConfiguration().isEnableHistoricTaskLogging()) {
            CommandContextUtil.getHistoricTaskService().deleteHistoricTaskLogEntriesForTaskId(taskId);
        }
    }

    public static void logUserTaskCompleted(TaskEntity taskEntity) {
        TaskServiceConfiguration taskServiceConfiguration = CommandContextUtil.getTaskServiceConfiguration();
        if (taskServiceConfiguration.isEnableHistoricTaskLogging()) {
            BaseHistoricTaskLogEntryBuilderImpl taskLogEntryBuilder = new BaseHistoricTaskLogEntryBuilderImpl(taskEntity);
            ObjectNode data = taskServiceConfiguration.getObjectMapper().createObjectNode();
            taskLogEntryBuilder.timeStamp(taskServiceConfiguration.getClock().getCurrentTime());
            taskLogEntryBuilder.userId(Authentication.getAuthenticatedUserId());
            taskLogEntryBuilder.data(data.toString());
            taskLogEntryBuilder.type(HistoricTaskLogEntryType.USER_TASK_COMPLETED.name());
            taskServiceConfiguration.getInternalHistoryTaskManager().recordHistoryUserTaskLog(taskLogEntryBuilder);
        }
    }

    public static boolean isFormFieldValidationEnabled(VariableContainer variableContainer,
        CmmnEngineConfiguration cmmnEngineConfiguration, String formFieldValidationExpression) {
        if (StringUtils.isNotEmpty(formFieldValidationExpression)) {
            Boolean formFieldValidation = getBoolean(formFieldValidationExpression);
            if (formFieldValidation != null) {
                return formFieldValidation;
            }

            if (variableContainer != null) {
                ExpressionManager expressionManager = cmmnEngineConfiguration.getExpressionManager();
                Boolean formFieldValidationValue = getBoolean(
                    expressionManager.createExpression(formFieldValidationExpression).getValue(variableContainer)
                );
                if (formFieldValidationValue == null) {
                    throw new FlowableException("Unable to resolve formFieldValidationExpression to boolean value");
                }
                return formFieldValidationValue;
            }
            throw new FlowableException("Unable to resolve formFieldValidationExpression without variable container");
        }
        return true;
    }

    protected static Boolean getBoolean(Object booleanObject) {
        if (booleanObject instanceof Boolean) {
            return (Boolean) booleanObject;
        }
        if (booleanObject instanceof String) {
            if ("true".equalsIgnoreCase((String) booleanObject)) {
                return Boolean.TRUE;
            }
            if ("false".equalsIgnoreCase((String) booleanObject)) {
                return Boolean.FALSE;
            }
        }
        return null;
    }

}
