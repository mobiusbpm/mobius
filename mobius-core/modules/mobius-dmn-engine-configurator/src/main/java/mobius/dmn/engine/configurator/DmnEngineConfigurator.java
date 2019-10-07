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
package mobius.dmn.engine.configurator;

import mobius.common.engine.api.FlowableException;
import mobius.common.engine.impl.AbstractEngineConfiguration;
import mobius.common.engine.impl.AbstractEngineConfigurator;
import mobius.common.engine.impl.EngineDeployer;
import mobius.common.engine.impl.interceptor.EngineConfigurationConstants;
import mobius.common.engine.impl.persistence.entity.Entity;
import mobius.dmn.engine.DmnEngine;
import mobius.dmn.engine.DmnEngineConfiguration;
import mobius.dmn.engine.deployer.DmnDeployer;
import mobius.dmn.engine.impl.cfg.StandaloneInMemDmnEngineConfiguration;
import mobius.dmn.engine.impl.db.EntityDependencyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tijs Rademakers
 *
 */
public class DmnEngineConfigurator extends AbstractEngineConfigurator {

    protected DmnEngineConfiguration dmnEngineConfiguration;
    
    @Override
    public int getPriority() {
        return EngineConfigurationConstants.PRIORITY_ENGINE_DMN;
    }
    
    @Override
    protected List<EngineDeployer> getCustomDeployers() {
        List<EngineDeployer> deployers = new ArrayList<>();
        deployers.add(new DmnDeployer());
        return deployers;
    }
    
    @Override
    protected String getMybatisCfgPath() {
        return DmnEngineConfiguration.DEFAULT_MYBATIS_MAPPING_FILE;
    }

    @Override
    public void configure(AbstractEngineConfiguration engineConfiguration) {
        if (dmnEngineConfiguration == null) {
            dmnEngineConfiguration = new StandaloneInMemDmnEngineConfiguration();
        }
        
        initialiseCommonProperties(engineConfiguration, dmnEngineConfiguration);

        initDmnEngine();
        
        initServiceConfigurations(engineConfiguration, dmnEngineConfiguration);
    }
    
    @Override
    protected List<Class<? extends Entity>> getEntityInsertionOrder() {
        return EntityDependencyOrder.INSERT_ORDER;
    }
    
    @Override
    protected List<Class<? extends Entity>> getEntityDeletionOrder() {
        return EntityDependencyOrder.DELETE_ORDER;
    }

    protected synchronized DmnEngine initDmnEngine() {
        if (dmnEngineConfiguration == null) {
            throw new FlowableException("DmnEngineConfiguration is required");
        }

        return dmnEngineConfiguration.buildDmnEngine();
    }

    public DmnEngineConfiguration getDmnEngineConfiguration() {
        return dmnEngineConfiguration;
    }

    public DmnEngineConfigurator setDmnEngineConfiguration(DmnEngineConfiguration dmnEngineConfiguration) {
        this.dmnEngineConfiguration = dmnEngineConfiguration;
        return this;
    }

}
