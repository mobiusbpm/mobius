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
package mobius.cmmn.rest.api;

import mobius.cmmn.api.history.HistoricCaseInstance;
import mobius.cmmn.api.history.HistoricCaseInstanceQuery;
import mobius.cmmn.api.history.HistoricMilestoneInstance;
import mobius.cmmn.api.history.HistoricMilestoneInstanceQuery;
import mobius.cmmn.api.history.HistoricPlanItemInstance;
import mobius.cmmn.api.history.HistoricPlanItemInstanceQuery;
import mobius.cmmn.api.history.HistoricVariableInstanceQuery;
import mobius.cmmn.api.repository.CaseDefinition;
import mobius.cmmn.api.repository.CaseDefinitionQuery;
import mobius.cmmn.api.repository.CmmnDeployment;
import mobius.cmmn.api.repository.CmmnDeploymentBuilder;
import mobius.cmmn.api.repository.CmmnDeploymentQuery;
import mobius.cmmn.api.runtime.CaseInstance;
import mobius.cmmn.api.runtime.CaseInstanceBuilder;
import mobius.cmmn.api.runtime.CaseInstanceQuery;
import mobius.cmmn.api.runtime.PlanItemInstance;
import mobius.cmmn.api.runtime.PlanItemInstanceQuery;
import mobius.cmmn.rest.api.history.caze.HistoricCaseInstanceQueryRequest;
import mobius.cmmn.rest.api.history.milestone.HistoricMilestoneInstanceQueryRequest;
import mobius.cmmn.rest.api.history.planitem.HistoricPlanItemInstanceQueryRequest;
import mobius.cmmn.rest.api.history.task.HistoricTaskInstanceQueryRequest;
import mobius.cmmn.rest.api.history.variable.HistoricVariableInstanceQueryRequest;
import mobius.cmmn.rest.api.runtime.caze.CaseInstanceCreateRequest;
import mobius.cmmn.rest.api.runtime.caze.CaseInstanceQueryRequest;
import mobius.cmmn.rest.api.runtime.planitem.PlanItemInstanceQueryRequest;
import mobius.cmmn.rest.api.runtime.task.TaskActionRequest;
import mobius.cmmn.rest.api.runtime.task.TaskQueryRequest;
import mobius.cmmn.rest.api.runtime.task.TaskRequest;
import mobius.job.api.DeadLetterJobQuery;
import mobius.job.api.Job;
import mobius.job.api.JobQuery;
import mobius.job.api.SuspendedJobQuery;
import mobius.job.api.TimerJobQuery;
import mobius.task.api.Task;
import mobius.task.api.TaskQuery;
import mobius.task.api.history.HistoricTaskInstance;
import mobius.task.api.history.HistoricTaskInstanceQuery;
import mobius.variable.api.history.HistoricVariableInstance;

public interface CmmnRestApiInterceptor {

    void accessTaskInfoById(Task task);
    
    void accessTaskInfoWithQuery(TaskQuery taskQuery, TaskQueryRequest request);
    
    void createTask(Task task, TaskRequest request);
    
    void updateTask(Task task, TaskRequest request);

    void deleteTask(Task task);
    
    void executeTaskAction(Task task, TaskActionRequest actionRequest);
    
    void accessCaseInstanceInfoById(CaseInstance caseInstance);

    void accessCaseInstanceInfoWithQuery(CaseInstanceQuery caseInstanceQuery, CaseInstanceQueryRequest request);
    
    void createCaseInstance(CaseInstanceBuilder caseInstanceBuilder, CaseInstanceCreateRequest request);
    
    void deleteCaseInstance(CaseInstance caseInstance);
    
    void doCaseInstanceAction(CaseInstance caseInstance, RestActionRequest actionRequest);
    
    void accessPlanItemInstanceInfoById(PlanItemInstance planItemInstance);

    void accessPlanItemInstanceInfoWithQuery(PlanItemInstanceQuery planItemInstanceQuery, PlanItemInstanceQueryRequest request);
    
    void doPlanItemInstanceAction(PlanItemInstance planItemInstance, RestActionRequest actionRequest);
    
    void accessCaseDefinitionById(CaseDefinition caseDefinition);
    
    void accessCaseDefinitionsWithQuery(CaseDefinitionQuery caseDefinitionQuery);
    
    void accessDeploymentById(CmmnDeployment deployment);
    
    void accessDeploymentsWithQuery(CmmnDeploymentQuery deploymentQuery);
    
    void executeNewDeploymentForTenantId(String tenantId);

    void enhanceDeployment(CmmnDeploymentBuilder cmmnDeploymentBuilder);
    
    void deleteDeployment(CmmnDeployment deployment);
    
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
    
    void accessHistoryCaseInfoById(HistoricCaseInstance historicCaseInstance);
    
    void accessHistoryCaseInfoWithQuery(HistoricCaseInstanceQuery historicCaseInstanceQuery, HistoricCaseInstanceQueryRequest request);
    
    void deleteHistoricCase(HistoricCaseInstance historicCaseInstance);
    
    void accessStageOverview(CaseInstance caseInstance);

    void accessHistoryMilestoneInfoById(HistoricMilestoneInstance historicMilestoneInstance);
    
    void accessHistoryMilestoneInfoWithQuery(HistoricMilestoneInstanceQuery historicMilestoneInstanceQuery, HistoricMilestoneInstanceQueryRequest request);
    
    void accessHistoryPlanItemInfoById(HistoricPlanItemInstance historicPlanItemInstance);
    
    void accessHistoryPlanItemInfoWithQuery(HistoricPlanItemInstanceQuery historicPlanItemInstanceQuery, HistoricPlanItemInstanceQueryRequest request);
    
    void accessHistoryVariableInfoById(HistoricVariableInstance historicVariableInstance);
    
    void accessHistoryVariableInfoWithQuery(HistoricVariableInstanceQuery historicVariableInstanceQuery, HistoricVariableInstanceQueryRequest request);
}
