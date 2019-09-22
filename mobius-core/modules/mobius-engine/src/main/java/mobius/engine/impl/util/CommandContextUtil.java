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
package mobius.engine.impl.util;

import java.util.HashMap;
import java.util.Map;

import mobius.common.engine.api.delegate.event.FlowableEventDispatcher;
import mobius.common.engine.impl.context.Context;
import mobius.common.engine.impl.db.DbSqlSession;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.common.engine.impl.interceptor.EngineConfigurationConstants;
import mobius.common.engine.impl.persistence.cache.EntityCache;
import mobius.content.api.ContentEngineConfigurationApi;
import mobius.content.api.ContentService;
import mobius.dmn.api.DmnEngineConfigurationApi;
import mobius.dmn.api.DmnManagementService;
import mobius.dmn.api.DmnRepositoryService;
import mobius.dmn.api.DmnRuleService;
import mobius.engine.FlowableEngineAgenda;
import mobius.engine.impl.cfg.ProcessEngineConfigurationImpl;
import mobius.engine.impl.history.HistoryManager;
import mobius.engine.impl.persistence.entity.ActivityInstanceEntityManager;
import mobius.engine.impl.persistence.entity.AttachmentEntityManager;
import mobius.engine.impl.persistence.entity.ByteArrayEntityManager;
import mobius.engine.impl.persistence.entity.CommentEntityManager;
import mobius.engine.impl.persistence.entity.DeploymentEntityManager;
import mobius.engine.impl.persistence.entity.EventLogEntryEntityManager;
import mobius.engine.impl.persistence.entity.ExecutionEntity;
import mobius.engine.impl.persistence.entity.ExecutionEntityManager;
import mobius.engine.impl.persistence.entity.HistoricActivityInstanceEntityManager;
import mobius.engine.impl.persistence.entity.HistoricDetailEntityManager;
import mobius.engine.impl.persistence.entity.HistoricProcessInstanceEntityManager;
import mobius.engine.impl.persistence.entity.ModelEntityManager;
import mobius.engine.impl.persistence.entity.ProcessDefinitionEntityManager;
import mobius.engine.impl.persistence.entity.ProcessDefinitionInfoEntityManager;
import mobius.engine.impl.persistence.entity.PropertyEntityManager;
import mobius.engine.impl.persistence.entity.ResourceEntityManager;
import mobius.engine.impl.persistence.entity.TableDataManager;
import mobius.entitylink.api.EntityLinkService;
import mobius.entitylink.api.history.HistoricEntityLinkService;
import mobius.entitylink.service.EntityLinkServiceConfiguration;
import mobius.eventsubscription.service.EventSubscriptionService;
import mobius.eventsubscription.service.EventSubscriptionServiceConfiguration;
import mobius.form.api.FormEngineConfigurationApi;
import mobius.form.api.FormManagementService;
import mobius.form.api.FormRepositoryService;
import mobius.form.api.FormService;
import mobius.identitylink.service.HistoricIdentityLinkService;
import mobius.identitylink.service.IdentityLinkService;
import mobius.identitylink.service.IdentityLinkServiceConfiguration;
import mobius.idm.api.IdmEngineConfigurationApi;
import mobius.idm.api.IdmIdentityService;
import mobius.job.service.HistoryJobService;
import mobius.job.service.JobService;
import mobius.job.service.JobServiceConfiguration;
import mobius.job.service.TimerJobService;
import mobius.job.service.impl.asyncexecutor.FailedJobCommandFactory;
import mobius.task.service.HistoricTaskService;
import mobius.task.service.InternalTaskAssignmentManager;
import mobius.task.service.TaskService;
import mobius.task.service.TaskServiceConfiguration;
import mobius.variable.service.HistoricVariableService;
import mobius.variable.service.VariableService;
import mobius.variable.service.VariableServiceConfiguration;

public class CommandContextUtil {
    
    public static final String ATTRIBUTE_INVOLVED_EXECUTIONS = "ctx.attribute.involvedExecutions";
    
    public static ProcessEngineConfigurationImpl getProcessEngineConfiguration() {
        return getProcessEngineConfiguration(getCommandContext());
    }
    
    public static ProcessEngineConfigurationImpl getProcessEngineConfiguration(CommandContext commandContext) {
        if (commandContext != null) {
            return (ProcessEngineConfigurationImpl) commandContext.getEngineConfigurations().get(EngineConfigurationConstants.KEY_PROCESS_ENGINE_CONFIG);
        }
        return null;
    }
    
    // VARIABLE SERVICE
    public static VariableServiceConfiguration getVariableServiceConfiguration() {
        return getVariableServiceConfiguration(getCommandContext());
    }
    
    public static VariableServiceConfiguration getVariableServiceConfiguration(CommandContext commandContext) {
        return (VariableServiceConfiguration) getProcessEngineConfiguration(commandContext).getServiceConfigurations()
                        .get(EngineConfigurationConstants.KEY_VARIABLE_SERVICE_CONFIG);
    }
    
    public static VariableService getVariableService() {
        return getVariableService(getCommandContext());
    }
    
    public static VariableService getVariableService(CommandContext commandContext) {
        VariableService variableService = null;
        VariableServiceConfiguration variableServiceConfiguration = getVariableServiceConfiguration();
        if (variableServiceConfiguration != null) {
            variableService = variableServiceConfiguration.getVariableService();
        }
        
        return variableService;
    }
    
    public static HistoricVariableService getHistoricVariableService() {
        HistoricVariableService historicVariableService = null;
        VariableServiceConfiguration variableServiceConfiguration = getVariableServiceConfiguration();
        if (variableServiceConfiguration != null) {
            historicVariableService = variableServiceConfiguration.getHistoricVariableService();
        }
        
        return historicVariableService;
    }
    
    // IDENTITY LINK SERVICE
    public static IdentityLinkServiceConfiguration getIdentityLinkServiceConfiguration() {
        return getIdentityLinkServiceConfiguration(getCommandContext());
    }
    
    public static IdentityLinkServiceConfiguration getIdentityLinkServiceConfiguration(CommandContext commandContext) {
        return (IdentityLinkServiceConfiguration) getProcessEngineConfiguration(commandContext).getServiceConfigurations()
                        .get(EngineConfigurationConstants.KEY_IDENTITY_LINK_SERVICE_CONFIG);
    }
    
    public static IdentityLinkService getIdentityLinkService() {
        return getIdentityLinkService(getCommandContext());
    }
    
    public static IdentityLinkService getIdentityLinkService(CommandContext commandContext) {
        IdentityLinkService identityLinkService = null;
        IdentityLinkServiceConfiguration identityLinkServiceConfiguration = getIdentityLinkServiceConfiguration(commandContext);
        if (identityLinkServiceConfiguration != null) {
            identityLinkService = identityLinkServiceConfiguration.getIdentityLinkService();
        }
        
        return identityLinkService;
    }
    
    public static HistoricIdentityLinkService getHistoricIdentityLinkService() {
        HistoricIdentityLinkService historicIdentityLinkService = null;
        IdentityLinkServiceConfiguration identityLinkServiceConfiguration = getIdentityLinkServiceConfiguration();
        if (identityLinkServiceConfiguration != null) {
            historicIdentityLinkService = identityLinkServiceConfiguration.getHistoricIdentityLinkService();
        }
        
        return historicIdentityLinkService;
    }
    
    // ENTITY LINK SERVICE
    public static EntityLinkServiceConfiguration getEntityLinkServiceConfiguration() {
        return getEntityLinkServiceConfiguration(getCommandContext());
    }
    
    public static EntityLinkServiceConfiguration getEntityLinkServiceConfiguration(CommandContext commandContext) {
        return (EntityLinkServiceConfiguration) getProcessEngineConfiguration(commandContext).getServiceConfigurations()
                        .get(EngineConfigurationConstants.KEY_ENTITY_LINK_SERVICE_CONFIG);
    }
    
    public static EntityLinkService getEntityLinkService() {
        return getEntityLinkService(getCommandContext());
    }
    
    public static EntityLinkService getEntityLinkService(CommandContext commandContext) {
        EntityLinkService entityLinkService = null;
        EntityLinkServiceConfiguration entityLinkServiceConfiguration = getEntityLinkServiceConfiguration(commandContext);
        if (entityLinkServiceConfiguration != null) {
            entityLinkService = entityLinkServiceConfiguration.getEntityLinkService();
        }
        
        return entityLinkService;
    }
    
    public static HistoricEntityLinkService getHistoricEntityLinkService() {
        HistoricEntityLinkService historicEntityLinkService = null;
        EntityLinkServiceConfiguration entityLinkServiceConfiguration = getEntityLinkServiceConfiguration();
        if (entityLinkServiceConfiguration != null) {
            historicEntityLinkService = entityLinkServiceConfiguration.getHistoricEntityLinkService();
        }
        
        return historicEntityLinkService;
    }
    
    // EVENT SUBSCRIPTION SERVICE
    public static EventSubscriptionServiceConfiguration getEventSubscriptionServiceConfiguration() {
        return getEventSubscriptionServiceConfiguration(getCommandContext());
    }
    
    public static EventSubscriptionServiceConfiguration getEventSubscriptionServiceConfiguration(CommandContext commandContext) {
        return (EventSubscriptionServiceConfiguration) getProcessEngineConfiguration(commandContext).getServiceConfigurations()
                        .get(EngineConfigurationConstants.KEY_EVENT_SUBSCRIPTION_SERVICE_CONFIG);
    }
    
    public static EventSubscriptionService getEventSubscriptionService() {
        return getEventSubscriptionService(getCommandContext());
    }
    
    public static EventSubscriptionService getEventSubscriptionService(CommandContext commandContext) {
        EventSubscriptionService eventSubscriptionService = null;
        EventSubscriptionServiceConfiguration eventSubscriptionServiceConfiguration = getEventSubscriptionServiceConfiguration(commandContext);
        if (eventSubscriptionServiceConfiguration != null) {
            eventSubscriptionService = eventSubscriptionServiceConfiguration.getEventSubscriptionService();
        }
        
        return eventSubscriptionService;
    }
    
    // TASK SERVICE
    public static TaskServiceConfiguration getTaskServiceConfiguration() {
        return getTaskServiceConfiguration(getCommandContext());
    }
    
    public static TaskServiceConfiguration getTaskServiceConfiguration(CommandContext commandContext) {
        return (TaskServiceConfiguration) getProcessEngineConfiguration(commandContext).getServiceConfigurations()
                        .get(EngineConfigurationConstants.KEY_TASK_SERVICE_CONFIG);
    }
    
    public static TaskService getTaskService() {
        return getTaskService(getCommandContext());
    }
    
    public static TaskService getTaskService(CommandContext commandContext) {
        TaskService taskService = null;
        TaskServiceConfiguration taskServiceConfiguration = getTaskServiceConfiguration(commandContext);
        if (taskServiceConfiguration != null) {
            taskService = taskServiceConfiguration.getTaskService();
        }
        return taskService;
    }
    
    public static HistoricTaskService getHistoricTaskService() {
        return getHistoricTaskService(getCommandContext());
    }

    public static HistoricTaskService getHistoricTaskService(CommandContext commandContext) {
        HistoricTaskService historicTaskService = null;
        TaskServiceConfiguration taskServiceConfiguration = getTaskServiceConfiguration(commandContext);
        if (taskServiceConfiguration != null) {
            historicTaskService = taskServiceConfiguration.getHistoricTaskService();
        }

        return historicTaskService;
    }

    // JOB SERVICE
    public static JobServiceConfiguration getJobServiceConfiguration() {
        return getJobServiceConfiguration(getCommandContext());
    }
    
    public static JobServiceConfiguration getJobServiceConfiguration(CommandContext commandContext) {
        return (JobServiceConfiguration) getProcessEngineConfiguration(commandContext).getServiceConfigurations()
                        .get(EngineConfigurationConstants.KEY_JOB_SERVICE_CONFIG);
    }
    
    public static JobService getJobService() {
        return getJobService(getCommandContext());
    }
    
    public static JobService getJobService(CommandContext commandContext) {
        JobService jobService = null;
        JobServiceConfiguration jobServiceConfiguration = getJobServiceConfiguration(commandContext);
        if (jobServiceConfiguration != null) {
            jobService = jobServiceConfiguration.getJobService();
        }
        
        return jobService;
    }
    
    public static TimerJobService getTimerJobService() {
        return getTimerJobService(getCommandContext());
    }
    
    public static TimerJobService getTimerJobService(CommandContext commandContext) {
        TimerJobService timerJobService = null;
        JobServiceConfiguration jobServiceConfiguration = getJobServiceConfiguration(commandContext);
        if (jobServiceConfiguration != null) {
            timerJobService = jobServiceConfiguration.getTimerJobService();
        }
        
        return timerJobService;
    }
    
    public static HistoryJobService getHistoryJobService() {
        return getHistoryJobService(getCommandContext());
    }
    
    public static HistoryJobService getHistoryJobService(CommandContext commandContext) {
        HistoryJobService historyJobService = null;
        JobServiceConfiguration jobServiceConfiguration = getJobServiceConfiguration(commandContext);
        if (jobServiceConfiguration != null) {
            historyJobService = jobServiceConfiguration.getHistoryJobService();
        }
        
        return historyJobService;
    }
    
    // IDM ENGINE
    
    public static IdmEngineConfigurationApi getIdmEngineConfiguration() {
        return getIdmEngineConfiguration(getCommandContext());
    }
    
    public static IdmEngineConfigurationApi getIdmEngineConfiguration(CommandContext commandContext) {
        return (IdmEngineConfigurationApi) commandContext.getEngineConfigurations().get(EngineConfigurationConstants.KEY_IDM_ENGINE_CONFIG);
    }
    
    public static IdmIdentityService getIdmIdentityService() {
        IdmIdentityService idmIdentityService = null;
        IdmEngineConfigurationApi idmEngineConfiguration = getIdmEngineConfiguration();
        if (idmEngineConfiguration != null) {
            idmIdentityService = idmEngineConfiguration.getIdmIdentityService();
        }
        
        return idmIdentityService;
    }
    
    // DMN ENGINE
    
    public static DmnEngineConfigurationApi getDmnEngineConfiguration() {
        return getDmnEngineConfiguration(getCommandContext());
    }
    
    public static DmnEngineConfigurationApi getDmnEngineConfiguration(CommandContext commandContext) {
        return (DmnEngineConfigurationApi) commandContext.getEngineConfigurations().get(EngineConfigurationConstants.KEY_DMN_ENGINE_CONFIG);
    }
    
    public static DmnRepositoryService getDmnRepositoryService() {
        DmnRepositoryService dmnRepositoryService = null;
        DmnEngineConfigurationApi dmnEngineConfiguration = getDmnEngineConfiguration();
        if (dmnEngineConfiguration != null) {
            dmnRepositoryService = dmnEngineConfiguration.getDmnRepositoryService();
        }
        
        return dmnRepositoryService;
    }
    
    public static DmnRuleService getDmnRuleService() {
        DmnRuleService dmnRuleService = null;
        DmnEngineConfigurationApi dmnEngineConfiguration = getDmnEngineConfiguration();
        if (dmnEngineConfiguration != null) {
            dmnRuleService = dmnEngineConfiguration.getDmnRuleService();
        }
        
        return dmnRuleService;
    }
    
    public static DmnManagementService getDmnManagementService() {
        DmnManagementService dmnManagementService = null;
        DmnEngineConfigurationApi dmnEngineConfiguration = getDmnEngineConfiguration();
        if (dmnEngineConfiguration != null) {
            dmnManagementService = dmnEngineConfiguration.getDmnManagementService();
        }
        
        return dmnManagementService;
    }
    
    // FORM ENGINE
    
    public static FormEngineConfigurationApi getFormEngineConfiguration() {
        return getFormEngineConfiguration(getCommandContext());
    }
    
    public static FormEngineConfigurationApi getFormEngineConfiguration(CommandContext commandContext) {
        return (FormEngineConfigurationApi) commandContext.getEngineConfigurations().get(EngineConfigurationConstants.KEY_FORM_ENGINE_CONFIG);
    }
    
    public static FormRepositoryService getFormRepositoryService() {
        return getFormRepositoryService(getCommandContext());
    }
    
    public static FormRepositoryService getFormRepositoryService(CommandContext commandContext) {
        FormRepositoryService formRepositoryService = null;
        FormEngineConfigurationApi formEngineConfiguration = getFormEngineConfiguration(commandContext);
        if (formEngineConfiguration != null) {
            formRepositoryService = formEngineConfiguration.getFormRepositoryService();
        }
        
        return formRepositoryService;
    }
    
    public static FormService getFormService() {
        return getFormService(getCommandContext());
    }
    
    public static FormService getFormService(CommandContext commandContext) {
        FormService formService = null;
        FormEngineConfigurationApi formEngineConfiguration = getFormEngineConfiguration(commandContext);
        if (formEngineConfiguration != null) {
            formService = formEngineConfiguration.getFormService();
        }
        
        return formService;
    }
    
    public static FormManagementService getFormManagementService() {
        return getFormManagementService(getCommandContext());
    }
    
    public static FormManagementService getFormManagementService(CommandContext commandContext) {
        FormManagementService formManagementService = null;
        FormEngineConfigurationApi formEngineConfiguration = getFormEngineConfiguration(commandContext);
        if (formEngineConfiguration != null) {
            formManagementService = formEngineConfiguration.getFormManagementService();
        }
        
        return formManagementService;
    }
    
    // CONTENT ENGINE
    
    public static ContentEngineConfigurationApi getContentEngineConfiguration() {
        return getContentEngineConfiguration(getCommandContext());
    }
    
    public static ContentEngineConfigurationApi getContentEngineConfiguration(CommandContext commandContext) {
        return (ContentEngineConfigurationApi) commandContext.getEngineConfigurations().get(EngineConfigurationConstants.KEY_CONTENT_ENGINE_CONFIG);
    }
    
    public static ContentService getContentService() {
        return getContentService(getCommandContext());
    }
    
    public static ContentService getContentService(CommandContext commandContext) {
        ContentService contentService = null;
        ContentEngineConfigurationApi contentEngineConfiguration = getContentEngineConfiguration(commandContext);
        if (contentEngineConfiguration != null) {
            contentService = contentEngineConfiguration.getContentService();
        }
        
        return contentService;
    }
    
    public static FlowableEngineAgenda getAgenda() {
        return getAgenda(getCommandContext());
    }
    
    public static FlowableEngineAgenda getAgenda(CommandContext commandContext) {
        return commandContext.getSession(FlowableEngineAgenda.class);
    }
    
    public static DbSqlSession getDbSqlSession() {
        return getDbSqlSession(getCommandContext());
    }
    
    public static DbSqlSession getDbSqlSession(CommandContext commandContext) {
        return commandContext.getSession(DbSqlSession.class);
    }
    
    public static EntityCache getEntityCache() {
        return getEntityCache(getCommandContext());
    }
    
    public static EntityCache getEntityCache(CommandContext commandContext) {
        return commandContext.getSession(EntityCache.class);
    }
    
    @SuppressWarnings("unchecked")
    public static void addInvolvedExecution(CommandContext commandContext, ExecutionEntity executionEntity) {
        if (executionEntity.getId() != null) {
            Map<String, ExecutionEntity> involvedExecutions = null;
            Object obj = commandContext.getAttribute(ATTRIBUTE_INVOLVED_EXECUTIONS);
            if (obj != null) {
                involvedExecutions = (Map<String, ExecutionEntity>) obj;
            } else {
                involvedExecutions = new HashMap<>();
                commandContext.addAttribute(ATTRIBUTE_INVOLVED_EXECUTIONS, involvedExecutions);
            }
            involvedExecutions.put(executionEntity.getId(), executionEntity);
        }
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, ExecutionEntity> getInvolvedExecutions(CommandContext commandContext) {
         Object obj = commandContext.getAttribute(ATTRIBUTE_INVOLVED_EXECUTIONS);
         if (obj != null) {
             return (Map<String, ExecutionEntity>) obj;
         }
         return null;
    }
    
    public static boolean hasInvolvedExecutions(CommandContext commandContext) {
        return getInvolvedExecutions(commandContext) != null;
    }
    
    public static TableDataManager getTableDataManager() {
        return getTableDataManager(getCommandContext());
    }
    
    public static TableDataManager getTableDataManager(CommandContext commandContext) {
        return getProcessEngineConfiguration(commandContext).getTableDataManager();
    }
    
    public static ByteArrayEntityManager getByteArrayEntityManager() {
        return getByteArrayEntityManager(getCommandContext());
    }
    
    public static ByteArrayEntityManager getByteArrayEntityManager(CommandContext commandContext) {
        return getProcessEngineConfiguration(commandContext).getByteArrayEntityManager();
    }
    
    public static ResourceEntityManager getResourceEntityManager() {
        return getResourceEntityManager(getCommandContext());
    }
    
    public static ResourceEntityManager getResourceEntityManager(CommandContext commandContext) {
        return getProcessEngineConfiguration(commandContext).getResourceEntityManager();
    }
    
    public static DeploymentEntityManager getDeploymentEntityManager() {
        return getDeploymentEntityManager(getCommandContext());
    }
    
    public static DeploymentEntityManager getDeploymentEntityManager(CommandContext commandContext) {
        return getProcessEngineConfiguration(commandContext).getDeploymentEntityManager();
    }
    
    public static PropertyEntityManager getPropertyEntityManager() {
        return getPropertyEntityManager(getCommandContext());
    }
    
    public static PropertyEntityManager getPropertyEntityManager(CommandContext commandContext) {
        return getProcessEngineConfiguration(commandContext).getPropertyEntityManager();
    }
    
    public static ProcessDefinitionEntityManager getProcessDefinitionEntityManager() {
        return getProcessDefinitionEntityManager(getCommandContext());
    }
    
    public static ProcessDefinitionEntityManager getProcessDefinitionEntityManager(CommandContext commandContext) {
        return getProcessEngineConfiguration(commandContext).getProcessDefinitionEntityManager();
    }
    
    public static ProcessDefinitionInfoEntityManager getProcessDefinitionInfoEntityManager() {
        return getProcessDefinitionInfoEntityManager(getCommandContext());
    }
    
    public static ProcessDefinitionInfoEntityManager getProcessDefinitionInfoEntityManager(CommandContext commandContext) {
        return getProcessEngineConfiguration(commandContext).getProcessDefinitionInfoEntityManager();
    }
    
    public static ExecutionEntityManager getExecutionEntityManager() {
        return getExecutionEntityManager(getCommandContext());
    }
    
    public static ExecutionEntityManager getExecutionEntityManager(CommandContext commandContext) {
        return getProcessEngineConfiguration(commandContext).getExecutionEntityManager();
    }
    
    public static CommentEntityManager getCommentEntityManager() {
        return getCommentEntityManager(getCommandContext());
    }
    
    public static CommentEntityManager getCommentEntityManager(CommandContext commandContext) {
        return getProcessEngineConfiguration(commandContext).getCommentEntityManager();
    }
    
    public static ModelEntityManager getModelEntityManager() {
        return getModelEntityManager(getCommandContext());
    }
    
    public static ModelEntityManager getModelEntityManager(CommandContext commandContext) {
        return getProcessEngineConfiguration(commandContext).getModelEntityManager();
    }
    
    public static HistoryManager getHistoryManager() {
        return getHistoryManager(getCommandContext());
    }
    
    public static HistoricProcessInstanceEntityManager getHistoricProcessInstanceEntityManager() {
        return getHistoricProcessInstanceEntityManager(getCommandContext());
    }
    
    public static HistoricProcessInstanceEntityManager getHistoricProcessInstanceEntityManager(CommandContext commandContext) {
        return getProcessEngineConfiguration(commandContext).getHistoricProcessInstanceEntityManager();
    }
    
    public static ActivityInstanceEntityManager getActivityInstanceEntityManager() {
        return getActivityInstanceEntityManager(getCommandContext());
    }
    
    public static ActivityInstanceEntityManager getActivityInstanceEntityManager(CommandContext commandContext) {
        return getProcessEngineConfiguration(commandContext).getActivityInstanceEntityManager();
    }

    public static HistoricActivityInstanceEntityManager getHistoricActivityInstanceEntityManager() {
        return getHistoricActivityInstanceEntityManager(getCommandContext());
    }

    public static HistoricActivityInstanceEntityManager getHistoricActivityInstanceEntityManager(CommandContext commandContext) {
        return getProcessEngineConfiguration(commandContext).getHistoricActivityInstanceEntityManager();
    }

    public static HistoryManager getHistoryManager(CommandContext commandContext) {
        return getProcessEngineConfiguration(commandContext).getHistoryManager();
    }
    
    public static HistoricDetailEntityManager getHistoricDetailEntityManager() {
        return getHistoricDetailEntityManager(getCommandContext());
    }
    
    public static HistoricDetailEntityManager getHistoricDetailEntityManager(CommandContext commandContext) {
        return getProcessEngineConfiguration(commandContext).getHistoricDetailEntityManager();
    }
    
    public static AttachmentEntityManager getAttachmentEntityManager() {
        return getAttachmentEntityManager(getCommandContext());
    }
    
    public static AttachmentEntityManager getAttachmentEntityManager(CommandContext commandContext) {
        return getProcessEngineConfiguration(commandContext).getAttachmentEntityManager();
    }
    
    public static EventLogEntryEntityManager getEventLogEntryEntityManager() {
        return getEventLogEntryEntityManager(getCommandContext());
    }
    
    public static EventLogEntryEntityManager getEventLogEntryEntityManager(CommandContext commandContext) {
        return getProcessEngineConfiguration(commandContext).getEventLogEntryEntityManager();
    }
    
    public static FlowableEventDispatcher getEventDispatcher() {
        return getEventDispatcher(getCommandContext());
    }
    
    public static FlowableEventDispatcher getEventDispatcher(CommandContext commandContext) {
        return getProcessEngineConfiguration(commandContext).getEventDispatcher();
    }
    
    public static FailedJobCommandFactory getFailedJobCommandFactory() {
        return getFailedJobCommandFactory(getCommandContext());
    }
    
    public static FailedJobCommandFactory getFailedJobCommandFactory(CommandContext commandContext) {
        return getProcessEngineConfiguration(commandContext).getFailedJobCommandFactory();
    }
    
    public static ProcessInstanceHelper getProcessInstanceHelper() {
        return getProcessInstanceHelper(getCommandContext());
    }
    
    public static ProcessInstanceHelper getProcessInstanceHelper(CommandContext commandContext) {
        return getProcessEngineConfiguration(commandContext).getProcessInstanceHelper();
    }
    
    public static CommandContext getCommandContext() {
        return Context.getCommandContext();
    }

    public static InternalTaskAssignmentManager getInternalTaskAssignmentManager(CommandContext commandContext) {
        return getTaskServiceConfiguration(commandContext).getInternalTaskAssignmentManager();
    }

    public static InternalTaskAssignmentManager getInternalTaskAssignmentManager() {
        return getInternalTaskAssignmentManager(getCommandContext());
    }

}