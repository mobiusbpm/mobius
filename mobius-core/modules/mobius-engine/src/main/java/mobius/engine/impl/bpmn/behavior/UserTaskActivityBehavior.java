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

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import mobius.bpmn.model.UserTask;
import mobius.common.engine.api.FlowableException;
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.api.delegate.Expression;
import mobius.common.engine.api.delegate.event.FlowableEngineEventType;
import mobius.common.engine.api.delegate.event.FlowableEventDispatcher;
import mobius.common.engine.impl.calendar.BusinessCalendar;
import mobius.common.engine.impl.calendar.DueDateBusinessCalendar;
import mobius.common.engine.impl.el.ExpressionManager;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.DynamicBpmnConstants;
import mobius.engine.delegate.DelegateExecution;
import mobius.engine.delegate.TaskListener;
import mobius.engine.impl.bpmn.helper.DynamicPropertyUtil;
import mobius.engine.impl.bpmn.helper.SkipExpressionUtil;
import mobius.engine.impl.cfg.ProcessEngineConfigurationImpl;
import mobius.engine.impl.context.BpmnOverrideContext;
import mobius.engine.impl.persistence.entity.ExecutionEntity;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.engine.impl.util.IdentityLinkUtil;
import mobius.engine.impl.util.TaskHelper;
import mobius.engine.interceptor.CreateUserTaskAfterContext;
import mobius.engine.interceptor.CreateUserTaskBeforeContext;
import mobius.identitylink.service.impl.persistence.entity.IdentityLinkEntity;
import mobius.task.service.TaskService;
import mobius.task.service.impl.FlowableTaskEventBuilder;
import mobius.task.service.impl.persistence.entity.TaskEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *
 */
public class UserTaskActivityBehavior extends TaskActivityBehavior {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserTaskActivityBehavior.class);

    protected UserTask userTask;

    public UserTaskActivityBehavior(UserTask userTask) {
        this.userTask = userTask;
    }

    @Override
    public void execute(DelegateExecution execution) {
        CommandContext commandContext = CommandContextUtil.getCommandContext();
        TaskService taskService = CommandContextUtil.getTaskService(commandContext);

        TaskEntity task = taskService.createTask();
        task.setExecutionId(execution.getId());
        task.setTaskDefinitionKey(userTask.getId());

        String activeTaskName = null;
        String activeTaskDescription = null;
        String activeTaskDueDate = null;
        String activeTaskPriority = null;
        String activeTaskCategory = null;
        String activeTaskFormKey = null;
        String activeTaskSkipExpression = null;
        String activeTaskAssignee = null;
        String activeTaskOwner = null;
        List<String> activeTaskCandidateUsers = null;
        List<String> activeTaskCandidateGroups = null;

        ProcessEngineConfigurationImpl processEngineConfiguration = CommandContextUtil.getProcessEngineConfiguration(commandContext);
        ExpressionManager expressionManager = processEngineConfiguration.getExpressionManager();

        if (processEngineConfiguration.isEnableProcessDefinitionInfoCache()) {
            ObjectNode taskElementProperties = BpmnOverrideContext.getBpmnOverrideElementProperties(userTask.getId(), execution.getProcessDefinitionId());
            activeTaskName = DynamicPropertyUtil.getActiveValue(userTask.getName(), DynamicBpmnConstants.USER_TASK_NAME, taskElementProperties);
            activeTaskDescription = DynamicPropertyUtil.getActiveValue(userTask.getDocumentation(), DynamicBpmnConstants.USER_TASK_DESCRIPTION, taskElementProperties);
            activeTaskDueDate = DynamicPropertyUtil.getActiveValue(userTask.getDueDate(), DynamicBpmnConstants.USER_TASK_DUEDATE, taskElementProperties);
            activeTaskPriority = DynamicPropertyUtil.getActiveValue(userTask.getPriority(), DynamicBpmnConstants.USER_TASK_PRIORITY, taskElementProperties);
            activeTaskCategory = DynamicPropertyUtil.getActiveValue(userTask.getCategory(), DynamicBpmnConstants.USER_TASK_CATEGORY, taskElementProperties);
            activeTaskFormKey = DynamicPropertyUtil.getActiveValue(userTask.getFormKey(), DynamicBpmnConstants.USER_TASK_FORM_KEY, taskElementProperties);
            activeTaskSkipExpression = DynamicPropertyUtil.getActiveValue(userTask.getSkipExpression(), DynamicBpmnConstants.TASK_SKIP_EXPRESSION, taskElementProperties);
            activeTaskAssignee = DynamicPropertyUtil.getActiveValue(userTask.getAssignee(), DynamicBpmnConstants.USER_TASK_ASSIGNEE, taskElementProperties);
            activeTaskOwner = DynamicPropertyUtil.getActiveValue(userTask.getOwner(), DynamicBpmnConstants.USER_TASK_OWNER, taskElementProperties);
            activeTaskCandidateUsers = getActiveValueList(userTask.getCandidateUsers(), DynamicBpmnConstants.USER_TASK_CANDIDATE_USERS, taskElementProperties);
            activeTaskCandidateGroups = getActiveValueList(userTask.getCandidateGroups(), DynamicBpmnConstants.USER_TASK_CANDIDATE_GROUPS, taskElementProperties);

        } else {
            activeTaskName = userTask.getName();
            activeTaskDescription = userTask.getDocumentation();
            activeTaskDueDate = userTask.getDueDate();
            activeTaskPriority = userTask.getPriority();
            activeTaskCategory = userTask.getCategory();
            activeTaskFormKey = userTask.getFormKey();
            activeTaskSkipExpression = userTask.getSkipExpression();
            activeTaskAssignee = userTask.getAssignee();
            activeTaskOwner = userTask.getOwner();
            activeTaskCandidateUsers = userTask.getCandidateUsers();
            activeTaskCandidateGroups = userTask.getCandidateGroups();
        }
        
        CreateUserTaskBeforeContext beforeContext = new CreateUserTaskBeforeContext(userTask, execution, activeTaskName, activeTaskDescription, activeTaskDueDate, 
                        activeTaskPriority, activeTaskCategory, activeTaskFormKey, activeTaskSkipExpression, activeTaskAssignee, activeTaskOwner, 
                        activeTaskCandidateUsers, activeTaskCandidateGroups);
        
        if (processEngineConfiguration.getCreateUserTaskInterceptor() != null) {
            processEngineConfiguration.getCreateUserTaskInterceptor().beforeCreateUserTask(beforeContext);
        }

        if (StringUtils.isNotEmpty(beforeContext.getName())) {
            String name = null;
            try {
                Object nameValue = expressionManager.createExpression(beforeContext.getName()).getValue(execution);
                if (nameValue != null) {
                    name = nameValue.toString();
                }
            } catch (FlowableException e) {
                name = beforeContext.getName();
                LOGGER.warn("property not found in task name expression {}", e.getMessage());
            }
            task.setName(name);
        }

        if (StringUtils.isNotEmpty(beforeContext.getDescription())) {
            String description = null;
            try {
                Object descriptionValue = expressionManager.createExpression(beforeContext.getDescription()).getValue(execution);
                if (descriptionValue != null) {
                    description = descriptionValue.toString();
                }
            } catch (FlowableException e) {
                description = beforeContext.getDescription();
                LOGGER.warn("property not found in task description expression {}", e.getMessage());
            }
            task.setDescription(description);
        }

        if (StringUtils.isNotEmpty(beforeContext.getDueDate())) {
            Object dueDate = expressionManager.createExpression(beforeContext.getDueDate()).getValue(execution);
            if (dueDate != null) {
                if (dueDate instanceof Date) {
                    task.setDueDate((Date) dueDate);
                } else if (dueDate instanceof String) {
                    String businessCalendarName = null;
                    if (StringUtils.isNotEmpty(userTask.getBusinessCalendarName())) {
                        businessCalendarName = expressionManager.createExpression(userTask.getBusinessCalendarName()).getValue(execution).toString();
                    } else {
                        businessCalendarName = DueDateBusinessCalendar.NAME;
                    }

                    BusinessCalendar businessCalendar = CommandContextUtil.getProcessEngineConfiguration(commandContext).getBusinessCalendarManager()
                            .getBusinessCalendar(businessCalendarName);
                    task.setDueDate(businessCalendar.resolveDuedate((String) dueDate));

                } else {
                    throw new FlowableIllegalArgumentException("Due date expression does not resolve to a Date or Date string: " + activeTaskDueDate);
                }
            }
        }

        if (StringUtils.isNotEmpty(beforeContext.getPriority())) {
            final Object priority = expressionManager.createExpression(beforeContext.getPriority()).getValue(execution);
            if (priority != null) {
                if (priority instanceof String) {
                    try {
                        task.setPriority(Integer.valueOf((String) priority));
                    } catch (NumberFormatException e) {
                        throw new FlowableIllegalArgumentException("Priority does not resolve to a number: " + priority, e);
                    }
                } else if (priority instanceof Number) {
                    task.setPriority(((Number) priority).intValue());
                } else {
                    throw new FlowableIllegalArgumentException("Priority expression does not resolve to a number: " + activeTaskPriority);
                }
            }
        }

        if (StringUtils.isNotEmpty(beforeContext.getCategory())) {
            String category = null;
            try {
                Object categoryValue = expressionManager.createExpression(beforeContext.getCategory()).getValue(execution);
                if (categoryValue != null) {
                    category = categoryValue.toString();
                }
            }  catch (FlowableException e) {
                category = beforeContext.getCategory();
                LOGGER.warn("property not found in task category expression {}", e.getMessage());
            }
            task.setCategory(category);
        }

        if (StringUtils.isNotEmpty(beforeContext.getFormKey())) {
            String formKey = null;
            try {
                Object formKeyValue = expressionManager.createExpression(beforeContext.getFormKey()).getValue(execution);
                if (formKeyValue != null) {
                    formKey = formKeyValue.toString();
                }
            } catch (FlowableException e) {
                formKey = beforeContext.getFormKey();
                LOGGER.warn("property not found in task formKey expression {}", e.getMessage());
            }
            task.setFormKey(formKey);
        }
        
        boolean skipUserTask = SkipExpressionUtil.isSkipExpressionEnabled(beforeContext.getSkipExpression(), userTask.getId(), execution, commandContext)
                    && SkipExpressionUtil.shouldSkipFlowElement(beforeContext.getSkipExpression(), userTask.getId(), execution, commandContext);

        TaskHelper.insertTask(task, (ExecutionEntity) execution, !skipUserTask, (!skipUserTask && processEngineConfiguration.isEnableEntityLinks()));

        // Handling assignments need to be done after the task is inserted, to have an id
        if (!skipUserTask) {
            handleAssignments(taskService, beforeContext.getAssignee(), beforeContext.getOwner(),
                            beforeContext.getCandidateUsers(), beforeContext.getCandidateGroups(), task, expressionManager, execution);
            
            processEngineConfiguration.getListenerNotificationHelper().executeTaskListeners(task, TaskListener.EVENTNAME_CREATE);

            // All properties set, now firing 'create' events
            FlowableEventDispatcher eventDispatcher = CommandContextUtil.getTaskServiceConfiguration(commandContext).getEventDispatcher();
            if (eventDispatcher != null  && eventDispatcher.isEnabled()) {
                eventDispatcher.dispatchEvent(
                        FlowableTaskEventBuilder.createEntityEvent(FlowableEngineEventType.TASK_CREATED, task));
            }
            
        } else {
            TaskHelper.deleteTask(task, null, false, false, false); // false: no events fired for skipped user task
            leave(execution);
        }
        
        if (processEngineConfiguration.getCreateUserTaskInterceptor() != null) {
            CreateUserTaskAfterContext afterContext = new CreateUserTaskAfterContext(userTask, task, execution);
            processEngineConfiguration.getCreateUserTaskInterceptor().afterCreateUserTask(afterContext);
        }
    }

    @Override
    public void trigger(DelegateExecution execution, String signalName, Object signalData) {
        List<TaskEntity> taskEntities = CommandContextUtil.getTaskService().findTasksByExecutionId(execution.getId()); // Should be only one
        for (TaskEntity taskEntity : taskEntities) {
            if (!taskEntity.isDeleted()) {
                throw new FlowableException("UserTask should not be signalled before complete");
            }
        }

        leave(execution);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void handleAssignments(TaskService taskService, String assignee, String owner, List<String> candidateUsers,
            List<String> candidateGroups, TaskEntity task, ExpressionManager expressionManager, DelegateExecution execution) {

        if (StringUtils.isNotEmpty(assignee)) {
            Object assigneeExpressionValue = expressionManager.createExpression(assignee).getValue(execution);
            String assigneeValue = null;
            if (assigneeExpressionValue != null) {
                assigneeValue = assigneeExpressionValue.toString();
            }

            if (StringUtils.isNotEmpty(assigneeValue)) {
                TaskHelper.changeTaskAssignee(task, assigneeValue);
            }
        }

        if (StringUtils.isNotEmpty(owner)) {
            Object ownerExpressionValue = expressionManager.createExpression(owner).getValue(execution);
            String ownerValue = null;
            if (ownerExpressionValue != null) {
                ownerValue = ownerExpressionValue.toString();
            }

            if (StringUtils.isNotEmpty(ownerValue)) {
                TaskHelper.changeTaskOwner(task, ownerValue);
            }
        }

        if (candidateGroups != null && !candidateGroups.isEmpty()) {
            for (String candidateGroup : candidateGroups) {
                Expression groupIdExpr = expressionManager.createExpression(candidateGroup);
                Object value = groupIdExpr.getValue(execution);
                if (value != null) {
                    if (value instanceof Collection) {
                        List<IdentityLinkEntity> identityLinkEntities = CommandContextUtil.getIdentityLinkService().addCandidateGroups(task.getId(), (Collection) value);
                        IdentityLinkUtil.handleTaskIdentityLinkAdditions(task, identityLinkEntities);
                        
                    } else {
                        String strValue = value.toString();
                        if (StringUtils.isNotEmpty(strValue)) {
                            List<String> candidates = extractCandidates(strValue);
                            List<IdentityLinkEntity> identityLinkEntities = CommandContextUtil.getIdentityLinkService().addCandidateGroups(task.getId(), candidates);
                            IdentityLinkUtil.handleTaskIdentityLinkAdditions(task, identityLinkEntities);
                        }
                    }
                }
            }
        }

        if (candidateUsers != null && !candidateUsers.isEmpty()) {
            for (String candidateUser : candidateUsers) {
                Expression userIdExpr = expressionManager.createExpression(candidateUser);
                Object value = userIdExpr.getValue(execution);
                if (value != null) {
                    if (value instanceof Collection) {
                        List<IdentityLinkEntity> identityLinkEntities = CommandContextUtil.getIdentityLinkService().addCandidateUsers(task.getId(), (Collection) value);
                        IdentityLinkUtil.handleTaskIdentityLinkAdditions(task, identityLinkEntities);

                    } else {
                        String strValue = value.toString();
                        if (StringUtils.isNotEmpty(strValue)) {
                            List<String> candidates = extractCandidates(strValue);
                            List<IdentityLinkEntity> identityLinkEntities = CommandContextUtil.getIdentityLinkService().addCandidateUsers(task.getId(), candidates);
                            IdentityLinkUtil.handleTaskIdentityLinkAdditions(task, identityLinkEntities);
                        }
                        
                    }
                }
            }
        }

        if (userTask.getCustomUserIdentityLinks() != null && !userTask.getCustomUserIdentityLinks().isEmpty()) {

            for (String customUserIdentityLinkType : userTask.getCustomUserIdentityLinks().keySet()) {
                for (String userIdentityLink : userTask.getCustomUserIdentityLinks().get(customUserIdentityLinkType)) {
                    Expression idExpression = expressionManager.createExpression(userIdentityLink);
                    Object value = idExpression.getValue(execution);
                    if (value instanceof Collection) {
                        Iterator userIdSet = ((Collection) value).iterator();
                        while (userIdSet.hasNext()) {
                            IdentityLinkEntity identityLinkEntity = CommandContextUtil.getIdentityLinkService().createTaskIdentityLink(
                                            task.getId(), userIdSet.next().toString(), null, customUserIdentityLinkType);
                            IdentityLinkUtil.handleTaskIdentityLinkAddition(task, identityLinkEntity);
                        }
                        
                    } else {
                        List<String> userIds = extractCandidates(value.toString());
                        for (String userId : userIds) {
                            IdentityLinkEntity identityLinkEntity = CommandContextUtil.getIdentityLinkService().createTaskIdentityLink(task.getId(), userId, null, customUserIdentityLinkType);
                            IdentityLinkUtil.handleTaskIdentityLinkAddition(task, identityLinkEntity);
                        }
                        
                    }

                }
            }

        }

        if (userTask.getCustomGroupIdentityLinks() != null && !userTask.getCustomGroupIdentityLinks().isEmpty()) {

            for (String customGroupIdentityLinkType : userTask.getCustomGroupIdentityLinks().keySet()) {
                for (String groupIdentityLink : userTask.getCustomGroupIdentityLinks().get(customGroupIdentityLinkType)) {

                    Expression idExpression = expressionManager.createExpression(groupIdentityLink);
                    Object value = idExpression.getValue(execution);
                    if (value instanceof Collection) {
                        Iterator groupIdSet = ((Collection) value).iterator();
                        while (groupIdSet.hasNext()) {
                            IdentityLinkEntity identityLinkEntity = CommandContextUtil.getIdentityLinkService().createTaskIdentityLink(
                                            task.getId(), null, groupIdSet.next().toString(), customGroupIdentityLinkType);
                            IdentityLinkUtil.handleTaskIdentityLinkAddition(task, identityLinkEntity);
                        }
                        
                    } else {
                        List<String> groupIds = extractCandidates(value.toString());
                        for (String groupId : groupIds) {
                            IdentityLinkEntity identityLinkEntity = CommandContextUtil.getIdentityLinkService().createTaskIdentityLink(
                                            task.getId(), null, groupId, customGroupIdentityLinkType);
                            IdentityLinkUtil.handleTaskIdentityLinkAddition(task, identityLinkEntity);
                        }
                        
                    }

                }
            }

        }

    }

    /**
     * Extract a candidate list from a string.
     * 
     * @param str
     * @return
     */
    protected List<String> extractCandidates(String str) {
        return Arrays.asList(str.split("[\\s]*,[\\s]*"));
    }
}