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
package mobius.cmmn.engine.impl;

import mobius.cmmn.api.CmmnHistoryService;
import mobius.cmmn.api.CmmnManagementService;
import mobius.cmmn.api.CmmnRepositoryService;
import mobius.cmmn.api.CmmnRuntimeService;
import mobius.cmmn.api.CmmnTaskService;
import mobius.cmmn.engine.CmmnEngine;
import mobius.cmmn.engine.CmmnEngineConfiguration;
import mobius.cmmn.engine.CmmnEngines;
import mobius.common.engine.impl.interceptor.CommandExecutor;
import mobius.job.service.impl.asyncexecutor.AsyncExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Joram Barrez
 */
public class CmmnEngineImpl implements CmmnEngine {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CmmnEngineImpl.class);

    protected String name;
    protected CmmnEngineConfiguration cmmnEngineConfiguration;
    protected CmmnRuntimeService cmmnRuntimeService;
    protected CmmnTaskService cmmnTaskService;
    protected CmmnManagementService cmmnManagementService;
    protected CmmnRepositoryService cmmnRepositoryService;
    protected CmmnHistoryService cmmnHistoryService;
    
    protected AsyncExecutor asyncExecutor;
    protected AsyncExecutor asyncHistoryExecutor;
    
    public CmmnEngineImpl(CmmnEngineConfiguration cmmnEngineConfiguration) {
        this.cmmnEngineConfiguration = cmmnEngineConfiguration;
        this.name = cmmnEngineConfiguration.getEngineName();
        this.cmmnRuntimeService = cmmnEngineConfiguration.getCmmnRuntimeService();
        this.cmmnTaskService = cmmnEngineConfiguration.getCmmnTaskService();
        this.cmmnManagementService = cmmnEngineConfiguration.getCmmnManagementService();
        this.cmmnRepositoryService = cmmnEngineConfiguration.getCmmnRepositoryService();
        this.cmmnHistoryService = cmmnEngineConfiguration.getCmmnHistoryService();
        
        this.asyncExecutor = cmmnEngineConfiguration.getAsyncExecutor();
        this.asyncHistoryExecutor = cmmnEngineConfiguration.getAsyncHistoryExecutor();
        
        if (cmmnEngineConfiguration.getSchemaManagementCmd() != null) {
            CommandExecutor commandExecutor = cmmnEngineConfiguration.getCommandExecutor();
            commandExecutor.execute(cmmnEngineConfiguration.getSchemaCommandConfig(), cmmnEngineConfiguration.getSchemaManagementCmd());
        }

        if (asyncExecutor != null && asyncExecutor.isAutoActivate()) {
            asyncExecutor.start();
        }

        // When running together with the bpmn engine, the asyncHistoryExecutor is shared by default.
        // However, calling multiple times .start() won't do anything (the method returns if already running),
        // so no need to check this case specically here.
        if (asyncHistoryExecutor != null && asyncHistoryExecutor.isAutoActivate()) {
            asyncHistoryExecutor.start();
        }

        LOGGER.info("CmmnEngine {} created", name);
        
        CmmnEngines.registerCmmnEngine(this);
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public void close() {
        CmmnEngines.unregister(this);
        
        if (asyncExecutor != null && asyncExecutor.isActive()) {
            asyncExecutor.shutdown();
        }
        if (asyncHistoryExecutor != null && asyncHistoryExecutor.isActive()) {
            asyncHistoryExecutor.shutdown();
        }
        cmmnEngineConfiguration.close();

    }
    
    @Override
    public CmmnEngineConfiguration getCmmnEngineConfiguration() {
        return cmmnEngineConfiguration;
    }

    public void setCmmnEngineConfiguration(CmmnEngineConfiguration cmmnEngineConfiguration) {
        this.cmmnEngineConfiguration = cmmnEngineConfiguration;
    }
    
    @Override
    public CmmnRuntimeService getCmmnRuntimeService() {
        return cmmnRuntimeService;
    }

    public void setCmmnRuntimeService(CmmnRuntimeService cmmnRuntimeService) {
        this.cmmnRuntimeService = cmmnRuntimeService;
    }
    
    @Override
    public CmmnTaskService getCmmnTaskService() {
        return cmmnTaskService;
    }

    public void setCmmnTaskService(CmmnTaskService cmmnTaskService) {
        this.cmmnTaskService = cmmnTaskService;
    }

    @Override
    public CmmnManagementService getCmmnManagementService() {
        return cmmnManagementService;
    }

    public void setCmmnManagementService(CmmnManagementService cmmnManagementService) {
        this.cmmnManagementService = cmmnManagementService;
    }

    @Override
    public CmmnRepositoryService getCmmnRepositoryService() {
        return cmmnRepositoryService;
    }
    
    public void setCmmnRepositoryService(CmmnRepositoryService cmmnRepositoryService) {
        this.cmmnRepositoryService = cmmnRepositoryService;
    }

    @Override
    public CmmnHistoryService getCmmnHistoryService() {
        return cmmnHistoryService;
    }

    public void setCmmnHistoryService(CmmnHistoryService cmmnHistoryService) {
        this.cmmnHistoryService = cmmnHistoryService;
    }
    
}