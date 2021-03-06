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
package mobius.content.engine.configurator;

import mobius.common.engine.api.FlowableException;
import mobius.common.engine.impl.AbstractEngineConfiguration;
import mobius.common.engine.impl.AbstractEngineConfigurator;
import mobius.common.engine.impl.EngineDeployer;
import mobius.common.engine.impl.interceptor.EngineConfigurationConstants;
import mobius.common.engine.impl.persistence.entity.Entity;
import mobius.content.engine.ContentEngine;
import mobius.content.engine.ContentEngineConfiguration;
import mobius.content.engine.impl.cfg.StandaloneContentEngineConfiguration;
import mobius.content.engine.impl.db.EntityDependencyOrder;

import java.util.List;

/**
 *
 *
 */
public class ContentEngineConfigurator extends AbstractEngineConfigurator {

    protected ContentEngineConfiguration contentEngineConfiguration;
    
    @Override
    public int getPriority() {
        return EngineConfigurationConstants.PRIORITY_ENGINE_CONTENT;
    }
    
    @Override
    protected List<EngineDeployer> getCustomDeployers() {
        return null;
    }
    
    @Override
    protected String getMybatisCfgPath() {
        return ContentEngineConfiguration.DEFAULT_MYBATIS_MAPPING_FILE;
    }

    @Override
    public void configure(AbstractEngineConfiguration engineConfiguration) {
        if (contentEngineConfiguration == null) {
            contentEngineConfiguration = new StandaloneContentEngineConfiguration();
        }
        
        initialiseCommonProperties(engineConfiguration, contentEngineConfiguration);

        initContentEngine();
        
        initServiceConfigurations(engineConfiguration, contentEngineConfiguration);
    }
    
    @Override
    protected List<Class<? extends Entity>> getEntityInsertionOrder() {
        return EntityDependencyOrder.INSERT_ORDER;
    }
    
    @Override
    protected List<Class<? extends Entity>> getEntityDeletionOrder() {
        return EntityDependencyOrder.DELETE_ORDER;
    }

    protected synchronized ContentEngine initContentEngine() {
        if (contentEngineConfiguration == null) {
            throw new FlowableException("ContentEngineConfiguration is required");
        }

        return contentEngineConfiguration.buildContentEngine();
    }

    public ContentEngineConfiguration getContentEngineConfiguration() {
        return contentEngineConfiguration;
    }

    public ContentEngineConfigurator setContentEngineConfiguration(ContentEngineConfiguration contentEngineConfiguration) {
        this.contentEngineConfiguration = contentEngineConfiguration;
        return this;
    }

}
