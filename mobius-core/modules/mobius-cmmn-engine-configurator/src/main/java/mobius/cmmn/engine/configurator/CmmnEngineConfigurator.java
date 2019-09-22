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
package mobius.cmmn.engine.configurator;

import mobius.cmmn.api.CallbackTypes;
import mobius.cmmn.engine.CmmnEngine;
import mobius.cmmn.engine.CmmnEngineConfiguration;
import mobius.cmmn.engine.configurator.impl.cmmn.DefaultCaseInstanceService;
import mobius.cmmn.engine.configurator.impl.deployer.CmmnDeployer;
import mobius.cmmn.engine.configurator.impl.process.DefaultProcessInstanceService;
import mobius.cmmn.engine.impl.callback.ChildProcessInstanceStateChangeCallback;
import mobius.cmmn.engine.impl.db.EntityDependencyOrder;
import mobius.common.engine.api.FlowableException;
import mobius.common.engine.impl.AbstractEngineConfiguration;
import mobius.common.engine.impl.AbstractEngineConfigurator;
import mobius.common.engine.impl.EngineDeployer;
import mobius.common.engine.impl.callback.RuntimeInstanceStateChangeCallback;
import mobius.common.engine.impl.interceptor.EngineConfigurationConstants;
import mobius.common.engine.impl.persistence.entity.Entity;
import mobius.engine.impl.cfg.ProcessEngineConfigurationImpl;
import mobius.job.service.JobServiceConfiguration;
import mobius.job.service.impl.asyncexecutor.AsyncExecutor;

import java.util.*;

/**
 * @author Joram Barrez
 */
public class CmmnEngineConfigurator extends AbstractEngineConfigurator {

    protected CmmnEngineConfiguration cmmnEngineConfiguration;

    @Override
    public int getPriority() {
        return EngineConfigurationConstants.PRIORITY_ENGINE_CMMN;
    }

    @Override
    protected List<EngineDeployer> getCustomDeployers() {
        return Collections.singletonList(new CmmnDeployer());
    }

    @Override
    protected String getMybatisCfgPath() {
        return CmmnEngineConfiguration.DEFAULT_MYBATIS_MAPPING_FILE;
    }
    
    @Override
    public void beforeInit(AbstractEngineConfiguration engineConfiguration) {
        super.beforeInit(engineConfiguration);
        
        // When async history is enabled on the bpmn engine, it also gets enabled on the cmmn engine.
        // The same async history executor will be shared between the engine instances (see in the configure method),
        // which will be instantiated by the bpmn engine. However, some properties need to be set here (before instantiation)
        // to have an async history executor that works for both engines
        ProcessEngineConfigurationImpl processEngineConfiguration = getProcessEngineConfiguration(engineConfiguration);
        if (processEngineConfiguration != null && processEngineConfiguration.isAsyncHistoryEnabled()) {
            processEngineConfiguration.setHistoryJobExecutionScope(JobServiceConfiguration.JOB_EXECUTION_SCOPE_ALL);
        }
        
    }

    @Override
    public void configure(AbstractEngineConfiguration engineConfiguration) {
        if (cmmnEngineConfiguration == null) {
            cmmnEngineConfiguration = new CmmnEngineConfiguration();
        }

        initialiseCommonProperties(engineConfiguration, cmmnEngineConfiguration);

        ProcessEngineConfigurationImpl processEngineConfiguration = getProcessEngineConfiguration(engineConfiguration);
        if (processEngineConfiguration != null) {
            copyProcessEngineProperties(processEngineConfiguration);
            
        }

        cmmnEngineConfiguration.setExecuteServiceSchemaManagers(false);

        initCmmnEngine();

        initServiceConfigurations(engineConfiguration, cmmnEngineConfiguration);
    }

    protected void copyProcessEngineProperties(ProcessEngineConfigurationImpl processEngineConfiguration) {
        initProcessInstanceService(processEngineConfiguration);
        initProcessInstanceStateChangedCallbacks(processEngineConfiguration);
        initCaseInstanceService(processEngineConfiguration);
        
        cmmnEngineConfiguration.setEnableTaskRelationshipCounts(processEngineConfiguration.getPerformanceSettings().isEnableTaskRelationshipCounts());
        cmmnEngineConfiguration.setTaskQueryLimit(processEngineConfiguration.getTaskQueryLimit());
        cmmnEngineConfiguration.setHistoricTaskQueryLimit(processEngineConfiguration.getHistoricTaskQueryLimit());
        // use the same query limit for executions/processes and cases
        cmmnEngineConfiguration.setCaseQueryLimit(processEngineConfiguration.getExecutionQueryLimit());
        cmmnEngineConfiguration.setHistoricCaseQueryLimit(processEngineConfiguration.getHistoricProcessInstancesQueryLimit());
        
        if (processEngineConfiguration.isAsyncHistoryEnabled()) {
            AsyncExecutor asyncHistoryExecutor = processEngineConfiguration.getAsyncHistoryExecutor();
            
            // Inject the async history executor from the process engine. 
            // The job handlers will be added in the CmmnEngineConfiguration itself
            cmmnEngineConfiguration.setAsyncHistoryEnabled(true);
            cmmnEngineConfiguration.setAsyncHistoryExecutor(asyncHistoryExecutor);
            cmmnEngineConfiguration.setAsyncHistoryJsonGroupingEnabled(processEngineConfiguration.isAsyncHistoryJsonGroupingEnabled());
            cmmnEngineConfiguration.setAsyncHistoryJsonGroupingThreshold(processEngineConfiguration.getAsyncHistoryJsonGroupingThreshold());
            cmmnEngineConfiguration.setAsyncHistoryJsonGzipCompressionEnabled(processEngineConfiguration.isAsyncHistoryJsonGzipCompressionEnabled());
            
            // See the beforeInit
            ((CmmnEngineConfiguration) cmmnEngineConfiguration).setHistoryJobExecutionScope(JobServiceConfiguration.JOB_EXECUTION_SCOPE_ALL);
        }
    }
    
    protected ProcessEngineConfigurationImpl getProcessEngineConfiguration(AbstractEngineConfiguration engineConfiguration) {
        if (engineConfiguration.getEngineConfigurations().containsKey(EngineConfigurationConstants.KEY_PROCESS_ENGINE_CONFIG)) {
            return (ProcessEngineConfigurationImpl) engineConfiguration.getEngineConfigurations()
                            .get(EngineConfigurationConstants.KEY_PROCESS_ENGINE_CONFIG);
        }
        return null;
    }

    protected void initProcessInstanceService(ProcessEngineConfigurationImpl processEngineConfiguration) {
        cmmnEngineConfiguration.setProcessInstanceService(new DefaultProcessInstanceService(processEngineConfiguration));
    }
    
    protected void initCaseInstanceService(ProcessEngineConfigurationImpl processEngineConfiguration) {
        processEngineConfiguration.setCaseInstanceService(new DefaultCaseInstanceService(cmmnEngineConfiguration));
    }

    protected void initProcessInstanceStateChangedCallbacks(ProcessEngineConfigurationImpl processEngineConfiguration) {
        if (processEngineConfiguration.getProcessInstanceStateChangedCallbacks() == null) {
            processEngineConfiguration.setProcessInstanceStateChangedCallbacks(new HashMap<>());
        }
        Map<String, List<RuntimeInstanceStateChangeCallback>> callbacks = processEngineConfiguration.getProcessInstanceStateChangedCallbacks();
        if (!callbacks.containsKey(CallbackTypes.PLAN_ITEM_CHILD_PROCESS)) {
            callbacks.put(CallbackTypes.PLAN_ITEM_CHILD_PROCESS, new ArrayList<>());
        }
        callbacks.get(CallbackTypes.PLAN_ITEM_CHILD_PROCESS).add(new ChildProcessInstanceStateChangeCallback(cmmnEngineConfiguration));
    }

    @Override
    protected List<Class<? extends Entity>> getEntityInsertionOrder() {
        return EntityDependencyOrder.INSERT_ORDER;
    }

    @Override
    protected List<Class<? extends Entity>> getEntityDeletionOrder() {
        return EntityDependencyOrder.DELETE_ORDER;
    }

    protected synchronized CmmnEngine initCmmnEngine() {
        if (cmmnEngineConfiguration == null) {
            throw new FlowableException("CmmnEngineConfiguration is required");
        }

        return cmmnEngineConfiguration.buildCmmnEngine();
    }

    public CmmnEngineConfiguration getCmmnEngineConfiguration() {
        return cmmnEngineConfiguration;
    }

    public CmmnEngineConfigurator setCmmnEngineConfiguration(CmmnEngineConfiguration cmmnEngineConfiguration) {
        this.cmmnEngineConfiguration = cmmnEngineConfiguration;
        return this;
    }
}
