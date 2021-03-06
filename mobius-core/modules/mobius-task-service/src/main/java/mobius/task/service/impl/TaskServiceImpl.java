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
package mobius.task.service.impl;

import mobius.common.engine.impl.service.CommonServiceImpl;
import mobius.task.api.Task;
import mobius.task.api.TaskBuilder;
import mobius.task.api.TaskQuery;
import mobius.task.service.TaskService;
import mobius.task.service.TaskServiceConfiguration;
import mobius.task.service.impl.persistence.entity.TaskEntity;
import mobius.task.service.impl.persistence.entity.TaskEntityManager;

import java.util.List;

/**
 *
 *
 */
public class TaskServiceImpl extends CommonServiceImpl<TaskServiceConfiguration> implements TaskService {
    public TaskServiceImpl(TaskServiceConfiguration taskServiceConfiguration) {
        super(taskServiceConfiguration);
    }

    @Override
    public TaskEntity getTask(String id) {
        return getTaskEntityManager().findById(id);
    }

    @Override
    public List<TaskEntity> findTasksByExecutionId(String executionId) {
        return getTaskEntityManager().findTasksByExecutionId(executionId);
    }

    @Override
    public List<TaskEntity> findTasksByProcessInstanceId(String processInstanceId) {
        return getTaskEntityManager().findTasksByProcessInstanceId(processInstanceId);
    }

    @Override
    public List<Task> findTasksByParentTaskId(String parentTaskId) {
        return getTaskEntityManager().findTasksByParentTaskId(parentTaskId);
    }

    @Override
    public List<TaskEntity> findTasksBySubScopeIdScopeType(String subScopeId, String scopeType) {
        return getTaskEntityManager().findTasksBySubScopeIdAndScopeType(subScopeId, scopeType);
    }

    @Override
    public TaskQuery createTaskQuery() {
        return new TaskQueryImpl();
    }

    @Override
    public void changeTaskAssignee(TaskEntity taskEntity, String userId) {
        getTaskEntityManager().changeTaskAssignee(taskEntity, userId);
    }

    @Override
    public void changeTaskOwner(TaskEntity taskEntity, String ownerId) {
        getTaskEntityManager().changeTaskOwner(taskEntity, ownerId);
    }

    @Override
    public void updateTaskTenantIdForDeployment(String deploymentId, String tenantId) {
        getTaskEntityManager().updateTaskTenantIdForDeployment(deploymentId, tenantId);
    }

    @Override
    public void updateTask(TaskEntity taskEntity, boolean fireUpdateEvent) {
        getTaskEntityManager().update(taskEntity, fireUpdateEvent);
    }

    @Override
    public void updateAllTaskRelatedEntityCountFlags(boolean configProperty) {
        getTaskEntityManager().updateAllTaskRelatedEntityCountFlags(configProperty);
    }

    @Override
    public TaskEntity createTask() {
        return getTaskEntityManager().create();
    }

    @Override
    public void insertTask(TaskEntity taskEntity, boolean fireCreateEvent) {
        getTaskEntityManager().insert(taskEntity, fireCreateEvent);
    }

    @Override
    public void deleteTask(TaskEntity task, boolean fireEvents) {
        getTaskEntityManager().delete(task, fireEvents);
    }

    @Override
    public void deleteTasksByExecutionId(String executionId) {
        getTaskEntityManager().deleteTasksByExecutionId(executionId);
    }

    public TaskEntityManager getTaskEntityManager() {
        return configuration.getTaskEntityManager();
    }

    @Override
    public TaskEntity createTask(TaskBuilder taskBuilder) {
        return getTaskEntityManager().createTask(taskBuilder);
    }
}
