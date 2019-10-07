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
package mobius.content.spring.configurator;

import mobius.common.engine.api.FlowableException;
import mobius.common.engine.impl.AbstractEngineConfiguration;
import mobius.common.spring.SpringEngineConfiguration;
import mobius.content.engine.ContentEngine;
import mobius.content.engine.configurator.ContentEngineConfigurator;
import mobius.content.spring.SpringContentEngineConfiguration;

/**
 *
 *
 */
public class SpringContentEngineConfigurator extends ContentEngineConfigurator {

    @Override
    public void configure(AbstractEngineConfiguration engineConfiguration) {
        if (contentEngineConfiguration == null) {
            contentEngineConfiguration = new SpringContentEngineConfiguration();
        } else if (!(contentEngineConfiguration instanceof SpringContentEngineConfiguration)) {
            throw new IllegalArgumentException("Expected contentEngine configuration to be of type "
                + SpringContentEngineConfiguration.class + " but was " + engineConfiguration.getClass());
        }
        initialiseCommonProperties(engineConfiguration, contentEngineConfiguration);
        SpringEngineConfiguration springEngineConfiguration = (SpringEngineConfiguration) engineConfiguration;
        ((SpringContentEngineConfiguration) contentEngineConfiguration).setTransactionManager(springEngineConfiguration.getTransactionManager());
        
        initContentEngine();
        
        initServiceConfigurations(engineConfiguration, contentEngineConfiguration);
    }

    @Override
    protected synchronized ContentEngine initContentEngine() {
        if (contentEngineConfiguration == null) {
            throw new FlowableException("ContentEngineConfiguration is required");
        }

        return contentEngineConfiguration.buildContentEngine();
    }
}
