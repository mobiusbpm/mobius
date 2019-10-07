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
package mobius.cmmn.engine.impl.util;

import mobius.cmmn.api.CmmnHistoryService;
import mobius.cmmn.api.CmmnRepositoryService;
import mobius.cmmn.api.CmmnRuntimeService;
import mobius.cmmn.api.CmmnTaskService;
import mobius.cmmn.engine.CmmnEngineConfiguration;
import mobius.cmmn.engine.impl.agenda.CmmnEngineAgenda;
import mobius.cmmn.engine.impl.history.CmmnHistoryManager;
import mobius.cmmn.engine.impl.persistence.entity.*;
import mobius.cmmn.engine.impl.persistence.entity.data.TableDataManager;
import mobius.cmmn.engine.impl.runtime.CaseInstanceHelper;
import mobius.common.engine.api.FlowableException;
import mobius.common.engine.api.delegate.event.FlowableEventDispatcher;
import mobius.common.engine.impl.context.Context;
import mobius.common.engine.impl.db.DbSqlSession;
import mobius.common.engine.impl.el.ExpressionManager;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.common.engine.impl.interceptor.EngineConfigurationConstants;
import mobius.common.engine.impl.persistence.cache.EntityCache;
import mobius.content.api.ContentEngineConfigurationApi;
import mobius.content.api.ContentService;
import mobius.dmn.api.DmnEngineConfigurationApi;
import mobius.dmn.api.DmnRuleService;
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
import mobius.task.service.HistoricTaskService;
import mobius.task.service.InternalTaskAssignmentManager;
import mobius.task.service.TaskService;
import mobius.task.service.TaskServiceConfiguration;
import mobius.variable.service.HistoricVariableService;
import mobius.variable.service.VariableService;
import mobius.variable.service.VariableServiceConfiguration;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Tijs Rademakers
 */
public class CommandContextUtil {

    public static final String ATTRIBUTE_INVOLVED_CASE_INSTANCE_IDS = "ctx.attribute.involvedCaseInstanceIds";

    public static CmmnEngineConfiguration getCmmnEngineConfiguration() {
        return getCmmnEngineConfiguration(getCommandContext());
    }

    public static CmmnEngineConfiguration getCmmnEngineConfiguration(CommandContext commandContext) {
        return (CmmnEngineConfiguration) commandContext.getEngineConfigurations().get(EngineConfigurationConstants.KEY_CMMN_ENGINE_CONFIG);
    }

    public static CmmnRepositoryService getCmmnRepositoryService() {
        return getCmmnEngineConfiguration().getCmmnRepositoryService();
    }

    public static CmmnRuntimeService getCmmnRuntimeService() {
        return getCmmnEngineConfiguration().getCmmnRuntimeService();
    }
    
    public static CmmnTaskService getCmmnTaskService() {
        return getCmmnEngineConfiguration().getCmmnTaskService();
    }

    public static CmmnHistoryService getCmmnHistoryService() {
        return getCmmnEngineConfiguration().getCmmnHistoryService();
    }

    public static ExpressionManager getExpressionManager() {
        return getExpressionManager(getCommandContext());
    }

    public static ExpressionManager getExpressionManager(CommandContext commandContext) {
        return getCmmnEngineConfiguration(commandContext).getExpressionManager();
    }
    
    public static FlowableEventDispatcher getEventDispatcher() {
        return getEventDispatcher(getCommandContext());
    }
    
    public static FlowableEventDispatcher getEventDispatcher(CommandContext commandContext) {
        return getCmmnEngineConfiguration(commandContext).getEventDispatcher();
    }

    public static CmmnHistoryManager getCmmnHistoryManager() {
        return getCmmnHistoryManager(getCommandContext());
    }

    public static CmmnHistoryManager getCmmnHistoryManager(CommandContext commandContext) {
        return getCmmnEngineConfiguration(commandContext).getCmmnHistoryManager();
    }

    public static CmmnDeploymentEntityManager getCmmnDeploymentEntityManager() {
        return getCmmnDeploymentEntityManager(getCommandContext());
    }

    public static CmmnDeploymentEntityManager getCmmnDeploymentEntityManager(CommandContext commandContext) {
        return getCmmnEngineConfiguration(commandContext).getCmmnDeploymentEntityManager();
    }

    public static CmmnResourceEntityManager getCmmnResourceEntityManager() {
        return getCmmnResourceEntityManager(getCommandContext());
    }

    public static CmmnResourceEntityManager getCmmnResourceEntityManager(CommandContext commandContext) {
        return getCmmnEngineConfiguration(commandContext).getCmmnResourceEntityManager();
    }

    public static CaseDefinitionEntityManager getCaseDefinitionEntityManager() {
        return getCaseDefinitionEntityManager(getCommandContext());
    }

    public static CaseDefinitionEntityManager getCaseDefinitionEntityManager(CommandContext commandContext) {
        return getCmmnEngineConfiguration(commandContext).getCaseDefinitionEntityManager();
    }

    public static CaseInstanceEntityManager getCaseInstanceEntityManager() {
        return getCaseInstanceEntityManager(getCommandContext());
    }

    public static CaseInstanceEntityManager getCaseInstanceEntityManager(CommandContext commandContext) {
        return getCmmnEngineConfiguration(commandContext).getCaseInstanceEntityManager();
    }

    public static PlanItemInstanceEntityManager getPlanItemInstanceEntityManager() {
        return getPlanItemInstanceEntityManager(getCommandContext());
    }

    public static PlanItemInstanceEntityManager getPlanItemInstanceEntityManager(CommandContext commandContext) {
        return getCmmnEngineConfiguration(commandContext).getPlanItemInstanceEntityManager();
    }

    public static SentryPartInstanceEntityManager getSentryPartInstanceEntityManager() {
        return getSentryPartInstanceEntityManager(getCommandContext());
    }

    public static SentryPartInstanceEntityManager getSentryPartInstanceEntityManager(CommandContext commandContext) {
        return getCmmnEngineConfiguration(commandContext).getSentryPartInstanceEntityManager();
    }

    public static MilestoneInstanceEntityManager getMilestoneInstanceEntityManager() {
        return getMilestoneInstanceEntityManager(getCommandContext());
    }

    public static MilestoneInstanceEntityManager getMilestoneInstanceEntityManager(CommandContext commandContext) {
        return getCmmnEngineConfiguration(commandContext).getMilestoneInstanceEntityManager();
    }

    public static HistoricCaseInstanceEntityManager getHistoricCaseInstanceEntityManager() {
        return getHistoricCaseInstanceEntityManager(getCommandContext());
    }

    public static HistoricCaseInstanceEntityManager getHistoricCaseInstanceEntityManager(CommandContext commandContext) {
        return getCmmnEngineConfiguration(commandContext).getHistoricCaseInstanceEntityManager();
    }

    public static HistoricMilestoneInstanceEntityManager getHistoricMilestoneInstanceEntityManager() {
        return getHistoricMilestoneInstanceEntityManager(getCommandContext());
    }

    public static HistoricMilestoneInstanceEntityManager getHistoricMilestoneInstanceEntityManager(CommandContext commandContext) {
        return getCmmnEngineConfiguration(commandContext).getHistoricMilestoneInstanceEntityManager();
    }

    public static HistoricPlanItemInstanceEntityManager getHistoricPlanItemInstanceEntityManager() {
        return getHistoricPlanItemInstanceEntityManager(getCommandContext());
    }

    public static HistoricPlanItemInstanceEntityManager getHistoricPlanItemInstanceEntityManager(CommandContext commandContext) {
        return getCmmnEngineConfiguration(commandContext).getHistoricPlanItemInstanceEntityManager();
    }

    public static TableDataManager getTableDataManager() {
        return getTableDataManager(getCommandContext());
    }

    public static TableDataManager getTableDataManager(CommandContext commandContext) {
        return getCmmnEngineConfiguration(commandContext).getTableDataManager();
    }

    public static VariableService getVariableService() {
        return getVariableService(getCommandContext());
    }

    public static VariableService getVariableService(CommandContext commandContext) {
        VariableService variableService = null;
        VariableServiceConfiguration variableServiceConfiguration = getVariableServiceConfiguration(commandContext);
        if (variableServiceConfiguration != null) {
            variableService = variableServiceConfiguration.getVariableService();
        }
        return variableService;
    }

    public static HistoricVariableService getHistoricVariableService() {
        return getHistoricVariableService(getCommandContext());
    }

    public static HistoricVariableService getHistoricVariableService(CommandContext commandContext) {
        HistoricVariableService historicVariableService = null;
        VariableServiceConfiguration variableServiceConfiguration = getVariableServiceConfiguration(commandContext);
        if (variableServiceConfiguration != null) {
            historicVariableService = variableServiceConfiguration.getHistoricVariableService();
        }
        return historicVariableService;
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
    
    // IDM ENGINE

    public static IdmEngineConfigurationApi getIdmEngineConfiguration() {
        return getIdmEngineConfiguration(getCommandContext());
    }

    public static IdmEngineConfigurationApi getIdmEngineConfiguration(CommandContext commandContext) {
        return (IdmEngineConfigurationApi) commandContext.getEngineConfigurations().get(EngineConfigurationConstants.KEY_IDM_ENGINE_CONFIG);
    }

    public static IdmIdentityService getIdmIdentityService() {
        IdmIdentityService identityService = null;
        IdmEngineConfigurationApi idmEngineConfiguration = getIdmEngineConfiguration();
        if (idmEngineConfiguration != null) {
            identityService = idmEngineConfiguration.getIdmIdentityService();
        }

        return identityService;
    }
    
    // IDENTITY LINK SERVICE

    public static IdentityLinkServiceConfiguration getIdentityLinkServiceConfiguration() {
        return getIdentityLinkServiceConfiguration(getCommandContext());
    }

    public static IdentityLinkServiceConfiguration getIdentityLinkServiceConfiguration(CommandContext commandContext) {
        return (IdentityLinkServiceConfiguration) commandContext.getCurrentEngineConfiguration().getServiceConfigurations()
                        .get(EngineConfigurationConstants.KEY_IDENTITY_LINK_SERVICE_CONFIG);
    }

    public static IdentityLinkService getIdentityLinkService() {
        return getIdentityLinkService(getCommandContext());
    }

    public static IdentityLinkService getIdentityLinkService(CommandContext commandContext) {
        return getIdentityLinkServiceConfiguration(commandContext).getIdentityLinkService();
    }
    
    public static HistoricIdentityLinkService getHistoricIdentityLinkService() {
        return getHistoricIdentityLinkService(getCommandContext());
    }

    public static HistoricIdentityLinkService getHistoricIdentityLinkService(CommandContext commandContext) {
        return getIdentityLinkServiceConfiguration(commandContext).getHistoricIdentityLinkService();
    }
    
    // ENTITY LINK SERVICE

    public static EntityLinkServiceConfiguration getEntityLinkServiceConfiguration() {
        return getEntityLinkServiceConfiguration(getCommandContext());
    }

    public static EntityLinkServiceConfiguration getEntityLinkServiceConfiguration(CommandContext commandContext) {
        return (EntityLinkServiceConfiguration) commandContext.getCurrentEngineConfiguration().getServiceConfigurations()
                        .get(EngineConfigurationConstants.KEY_ENTITY_LINK_SERVICE_CONFIG);
    }

    public static EntityLinkService getEntityLinkService() {
        return getEntityLinkService(getCommandContext());
    }

    public static EntityLinkService getEntityLinkService(CommandContext commandContext) {
        return getEntityLinkServiceConfiguration(commandContext).getEntityLinkService();
    }
    
    public static HistoricEntityLinkService getHistoricEntityLinkService() {
        return getHistoricEntityLinkService(getCommandContext());
    }

    public static HistoricEntityLinkService getHistoricEntityLinkService(CommandContext commandContext) {
        return getEntityLinkServiceConfiguration(commandContext).getHistoricEntityLinkService();
    }
    
    // EVENT SUBSCRIPTION SERVICE

    public static EventSubscriptionServiceConfiguration getEventSubscriptionServiceConfiguration() {
        return getEventSubscriptionServiceConfiguration(getCommandContext());
    }

    public static EventSubscriptionServiceConfiguration getEventSubscriptionServiceConfiguration(CommandContext commandContext) {
        return (EventSubscriptionServiceConfiguration) commandContext.getCurrentEngineConfiguration().getServiceConfigurations()
                        .get(EngineConfigurationConstants.KEY_EVENT_SUBSCRIPTION_SERVICE_CONFIG);
    }

    public static EventSubscriptionService getEventSubscriptionService() {
        return getEventSubscriptionService(getCommandContext());
    }

    public static EventSubscriptionService getEventSubscriptionService(CommandContext commandContext) {
        return getEventSubscriptionServiceConfiguration(commandContext).getEventSubscriptionService();
    }
    
    // VARIABLE SERVICE

    public static VariableServiceConfiguration getVariableServiceConfiguration() {
        return getVariableServiceConfiguration(getCommandContext());
    }

    public static VariableServiceConfiguration getVariableServiceConfiguration(CommandContext commandContext) {
        return (VariableServiceConfiguration) commandContext.getCurrentEngineConfiguration().getServiceConfigurations()
                        .get(EngineConfigurationConstants.KEY_VARIABLE_SERVICE_CONFIG);
    }
    
    // TASK SERVICE

    public static TaskService getTaskService() {
        return getTaskService(getCommandContext());
    }

    public static TaskService getTaskService(CommandContext commandContext) {
        return getTaskServiceConfiguration(commandContext).getTaskService();
    }

    public static HistoricTaskService getHistoricTaskService() {
        return getHistoricTaskService(getCommandContext());
    }

    public static HistoricTaskService getHistoricTaskService(CommandContext commandContext) {
        return getTaskServiceConfiguration(commandContext).getHistoricTaskService();
    }

    public static TaskServiceConfiguration getTaskServiceConfiguration() {
        return getTaskServiceConfiguration(getCommandContext());
    }

    public static TaskServiceConfiguration getTaskServiceConfiguration(CommandContext commandContext) {
        return (TaskServiceConfiguration) commandContext.getCurrentEngineConfiguration().getServiceConfigurations()
                        .get(EngineConfigurationConstants.KEY_TASK_SERVICE_CONFIG);
    }

    public static CmmnEngineAgenda getAgenda() {
        return getAgenda(getCommandContext());
    }

    public static CmmnEngineAgenda getAgenda(CommandContext commandContext) {
        return commandContext.getSession(CmmnEngineAgenda.class);
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
    public static void addInvolvedCaseInstanceId(CommandContext commandContext, String caseInstanceId) {
        if (caseInstanceId != null) {
            Set<String> involvedCaseInstanceIds = null;
            Object obj = commandContext.getAttribute(ATTRIBUTE_INVOLVED_CASE_INSTANCE_IDS);
            if (obj != null) {
                involvedCaseInstanceIds = (Set<String>) obj;
            } else {
                involvedCaseInstanceIds = new HashSet<>(1); // typically will be only 1 entry
                commandContext.addAttribute(ATTRIBUTE_INVOLVED_CASE_INSTANCE_IDS, involvedCaseInstanceIds);
            }
            involvedCaseInstanceIds.add(caseInstanceId);
        }
    }

    @SuppressWarnings("unchecked")
    public static Set<String> getInvolvedCaseInstanceIds(CommandContext commandContext) {
         Object obj = commandContext.getAttribute(ATTRIBUTE_INVOLVED_CASE_INSTANCE_IDS);
         if (obj != null) {
             return (Set<String>) obj;
         }
         return null;
    }

    public static CaseInstanceHelper getCaseInstanceHelper() {
        return getCaseInstanceHelper(getCommandContext());
    }

    public static CaseInstanceHelper getCaseInstanceHelper(CommandContext commandContext) {
        return getCmmnEngineConfiguration(commandContext).getCaseInstanceHelper();
    }

    public static CommandContext getCommandContext() {
        return Context.getCommandContext();
    }

    public static DmnEngineConfigurationApi getDmnEngineConfiguration(CommandContext commandContext) {
        return (DmnEngineConfigurationApi) commandContext.getEngineConfigurations().get(EngineConfigurationConstants.KEY_DMN_ENGINE_CONFIG);
    }

    public static DmnRuleService getDmnRuleService(CommandContext commandContext) {
        DmnEngineConfigurationApi dmnEngineConfiguration = getDmnEngineConfiguration(commandContext);
        if (dmnEngineConfiguration == null) {
            throw new FlowableException("Dmn engine is not configured");
        }
        return dmnEngineConfiguration.getDmnRuleService();
    }

    public static InternalTaskAssignmentManager getInternalTaskAssignmentManager(CommandContext commandContext) {
        return getCmmnEngineConfiguration(commandContext).getTaskServiceConfiguration().getInternalTaskAssignmentManager();
    }

    public static InternalTaskAssignmentManager getInternalTaskAssignmentManager() {
        return getInternalTaskAssignmentManager(getCommandContext());
    }

}
