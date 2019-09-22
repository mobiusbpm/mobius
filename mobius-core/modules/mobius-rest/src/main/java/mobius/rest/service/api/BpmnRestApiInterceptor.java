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
package mobius.rest.service.api;

import mobius.rest.service.api.repository.ModelRequest;
import mobius.engine.form.FormData;
import mobius.engine.history.HistoricActivityInstanceQuery;
import mobius.engine.history.HistoricDetail;
import mobius.engine.history.HistoricDetailQuery;
import mobius.engine.history.HistoricProcessInstance;
import mobius.engine.history.HistoricProcessInstanceQuery;
import mobius.engine.repository.Deployment;
import mobius.engine.repository.DeploymentBuilder;
import mobius.engine.repository.DeploymentQuery;
import mobius.engine.repository.Model;
import mobius.engine.repository.ModelQuery;
import mobius.engine.repository.ProcessDefinition;
import mobius.engine.repository.ProcessDefinitionQuery;
import mobius.engine.runtime.Execution;
import mobius.engine.runtime.ExecutionQuery;
import mobius.engine.runtime.ProcessInstance;
import mobius.engine.runtime.ProcessInstanceBuilder;
import mobius.engine.runtime.ProcessInstanceQuery;
import mobius.eventsubscription.api.EventSubscription;
import mobius.eventsubscription.api.EventSubscriptionQuery;
import mobius.idm.api.Group;
import mobius.idm.api.GroupQuery;
import mobius.idm.api.User;
import mobius.idm.api.UserQuery;
import mobius.job.api.DeadLetterJobQuery;
import mobius.job.api.Job;
import mobius.job.api.JobQuery;
import mobius.job.api.SuspendedJobQuery;
import mobius.job.api.TimerJobQuery;
import mobius.rest.service.api.form.SubmitFormRequest;
import mobius.rest.service.api.history.HistoricActivityInstanceQueryRequest;
import mobius.rest.service.api.history.HistoricDetailQueryRequest;
import mobius.rest.service.api.history.HistoricProcessInstanceQueryRequest;
import mobius.rest.service.api.history.HistoricTaskInstanceQueryRequest;
import mobius.rest.service.api.history.HistoricTaskLogEntryQueryRequest;
import mobius.rest.service.api.history.HistoricVariableInstanceQueryRequest;
import mobius.rest.service.api.identity.GroupRequest;
import mobius.rest.service.api.identity.UserRequest;
import mobius.rest.service.api.runtime.process.ExecutionActionRequest;
import mobius.rest.service.api.runtime.process.ExecutionChangeActivityStateRequest;
import mobius.rest.service.api.runtime.process.ExecutionQueryRequest;
import mobius.rest.service.api.runtime.process.InjectActivityRequest;
import mobius.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import mobius.rest.service.api.runtime.process.ProcessInstanceQueryRequest;
import mobius.rest.service.api.runtime.process.SignalEventReceivedRequest;
import mobius.rest.service.api.runtime.task.TaskActionRequest;
import mobius.rest.service.api.runtime.task.TaskQueryRequest;
import mobius.rest.service.api.runtime.task.TaskRequest;
import mobius.task.api.Task;
import mobius.task.api.TaskQuery;
import mobius.task.api.history.HistoricTaskInstance;
import mobius.task.api.history.HistoricTaskInstanceQuery;
import mobius.task.api.history.HistoricTaskLogEntryQuery;
import mobius.variable.api.history.HistoricVariableInstance;
import mobius.variable.api.history.HistoricVariableInstanceQuery;

public interface BpmnRestApiInterceptor {

    void accessTaskInfoById(Task task);
    
    void accessTaskInfoWithQuery(TaskQuery taskQuery, TaskQueryRequest request);
    
    void createTask(Task task, TaskRequest request);
    
    void updateTask(Task task, TaskRequest request);

    void deleteTask(Task task);
    
    void executeTaskAction(Task task, TaskActionRequest actionRequest);
    
    void accessExecutionInfoById(Execution execution);

    void accessExecutionInfoWithQuery(ExecutionQuery executionQuery, ExecutionQueryRequest request);
    
    void doExecutionActionRequest(ExecutionActionRequest executionActionRequest);
    
    void accessProcessInstanceInfoById(ProcessInstance processInstance);

    void accessProcessInstanceInfoWithQuery(ProcessInstanceQuery processInstanceQuery, ProcessInstanceQueryRequest request);
    
    void createProcessInstance(ProcessInstanceBuilder processInstanceBuilder, ProcessInstanceCreateRequest request);
    
    void deleteProcessInstance(ProcessInstance processInstance);
    
    void sendSignal(SignalEventReceivedRequest signalEventReceivedRequest);
    
    void changeActivityState(ExecutionChangeActivityStateRequest changeActivityStateRequest);
    
    void migrateProcessInstance(String processInstanceId, String migrationDocument);
    
    void injectActivity(InjectActivityRequest injectActivityRequest);
    
    void accessEventSubscriptionById(EventSubscription eventSubscription);
    
    void accessEventSubscriptionInfoWithQuery(EventSubscriptionQuery eventSubscriptionQuery);
    
    void accessProcessDefinitionById(ProcessDefinition processDefinition);
    
    void accessProcessDefinitionsWithQuery(ProcessDefinitionQuery processDefinitionQuery);
    
    void accessDeploymentById(Deployment deployment);
    
    void accessDeploymentsWithQuery(DeploymentQuery deploymentQuery);
    
    void executeNewDeploymentForTenantId(String tenantId);

    void enhanceDeployment(DeploymentBuilder deploymentBuilder);
    
    void deleteDeployment(Deployment deployment);
    
    void accessModelInfoById(Model model);
    
    void accessModelInfoWithQuery(ModelQuery modelQuery);
    
    void createModel(Model model, ModelRequest request);
    
    void accessJobInfoById(Job job);
    
    void accessJobInfoWithQuery(JobQuery jobQuery);
    
    void accessTimerJobInfoWithQuery(TimerJobQuery jobQuery);
    
    void accessSuspendedJobInfoWithQuery(SuspendedJobQuery jobQuery);
    
    void accessDeadLetterJobInfoWithQuery(DeadLetterJobQuery jobQuery);
    
    void deleteJob(Job job);
    
    void accessManagementInfo();
    
    void accessTableInfo();
    
    void accessHistoryTaskInfoById(HistoricTaskInstance historicTaskInstance);
    
    void accessHistoryTaskInfoWithQuery(HistoricTaskInstanceQuery historicTaskInstanceQuery, HistoricTaskInstanceQueryRequest request);
    
    void deleteHistoricTask(HistoricTaskInstance historicTaskInstance);
    
    void accessHistoryProcessInfoById(HistoricProcessInstance historicProcessInstance);
    
    void accessHistoryProcessInfoWithQuery(HistoricProcessInstanceQuery historicProcessInstanceQuery, HistoricProcessInstanceQueryRequest request);
    
    void deleteHistoricProcess(HistoricProcessInstance historicProcessInstance);
    
    void accessHistoryActivityInfoWithQuery(HistoricActivityInstanceQuery historicActivityInstanceQuery, HistoricActivityInstanceQueryRequest request);
    
    void accessHistoryDetailById(HistoricDetail historicDetail);
    
    void accessHistoryDetailInfoWithQuery(HistoricDetailQuery historicDetailQuery, HistoricDetailQueryRequest request);
    
    void accessHistoryVariableInfoById(HistoricVariableInstance historicVariableInstance);
    
    void accessHistoryVariableInfoWithQuery(HistoricVariableInstanceQuery historicVariableInstanceQuery, HistoricVariableInstanceQueryRequest request);

    void accessHistoricTaskLogWithQuery(HistoricTaskLogEntryQuery historicTaskLogEntryQuery, HistoricTaskLogEntryQueryRequest request);

    void accessGroupInfoById(Group group);
    
    void accessGroupInfoWithQuery(GroupQuery groupQuery);
    
    void createGroup(GroupRequest groupRequest);
    
    void deleteGroup(Group group);
    
    void accessUserInfoById(User user);
    
    void accessUserInfoWithQuery(UserQuery userQuery);
    
    void createUser(UserRequest userRequest);
    
    void deleteUser(User user);
    
    void accessFormData(FormData formData);
    
    void submitFormData(SubmitFormRequest formRequest);
}
