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
package mobius.idm.engine.impl;

import mobius.common.engine.impl.interceptor.CommandExecutor;
import mobius.idm.api.IdmIdentityService;
import mobius.idm.api.IdmManagementService;
import mobius.idm.engine.IdmEngine;
import mobius.idm.engine.IdmEngineConfiguration;
import mobius.idm.engine.IdmEngines;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class IdmEngineImpl implements IdmEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdmEngineImpl.class);

    protected String name;
    protected IdmIdentityService identityService;
    protected IdmManagementService managementService;
    protected IdmEngineConfiguration engineConfiguration;
    protected CommandExecutor commandExecutor;

    public IdmEngineImpl(IdmEngineConfiguration engineConfiguration) {
        this.engineConfiguration = engineConfiguration;
        this.name = engineConfiguration.getEngineName();
        this.identityService = engineConfiguration.getIdmIdentityService();
        this.managementService = engineConfiguration.getIdmManagementService();
        this.commandExecutor = engineConfiguration.getCommandExecutor();

        if (engineConfiguration.getSchemaManagementCmd() != null) {
            engineConfiguration.getCommandExecutor().execute(engineConfiguration.getSchemaCommandConfig(), engineConfiguration.getSchemaManagementCmd());
        }

        if (name == null) {
            LOGGER.info("default flowable IdmEngine created");
        } else {
            LOGGER.info("IdmEngine {} created", name);
        }

        IdmEngines.registerIdmEngine(this);
    }

    @Override
    public void close() {
        IdmEngines.unregister(this);
        engineConfiguration.close();
    }

    // getters and setters
    // //////////////////////////////////////////////////////

    @Override
    public String getName() {
        return name;
    }

    @Override
    public IdmIdentityService getIdmIdentityService() {
        return identityService;
    }

    @Override
    public IdmManagementService getIdmManagementService() {
        return managementService;
    }

    @Override
    public IdmEngineConfiguration getIdmEngineConfiguration() {
        return engineConfiguration;
    }
}
