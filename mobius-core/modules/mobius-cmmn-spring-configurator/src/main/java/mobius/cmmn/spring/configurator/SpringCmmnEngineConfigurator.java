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
package mobius.cmmn.spring.configurator;

import mobius.cmmn.engine.CmmnEngine;
import mobius.cmmn.engine.configurator.CmmnEngineConfigurator;
import mobius.cmmn.spring.SpringCmmnEngineConfiguration;
import mobius.cmmn.spring.SpringCmmnExpressionManager;
import mobius.common.engine.api.FlowableException;
import mobius.common.engine.impl.AbstractEngineConfiguration;
import mobius.common.engine.impl.interceptor.EngineConfigurationConstants;
import mobius.common.spring.SpringEngineConfiguration;
import mobius.spring.SpringProcessEngineConfiguration;

/**
 * @author Tijs Rademakers
 * @author Joram Barrez
 */
public class SpringCmmnEngineConfigurator extends CmmnEngineConfigurator {

    @Override
    public void configure(AbstractEngineConfiguration engineConfiguration) {
        if (cmmnEngineConfiguration == null) {
            cmmnEngineConfiguration = new SpringCmmnEngineConfiguration();
        }

        if (!(cmmnEngineConfiguration instanceof SpringCmmnEngineConfiguration)) {
            throw new FlowableException("SpringCmmnEngineConfigurator accepts only SpringCmmnEngineConfiguration. " + cmmnEngineConfiguration.getClass().getName());
        }

        initialiseCommonProperties(engineConfiguration, cmmnEngineConfiguration);

        SpringEngineConfiguration springEngineConfiguration = (SpringEngineConfiguration) engineConfiguration;
        
        SpringProcessEngineConfiguration springProcessEngineConfiguration = null;
        if (springEngineConfiguration instanceof SpringProcessEngineConfiguration) {
            springProcessEngineConfiguration = (SpringProcessEngineConfiguration) springEngineConfiguration;
        } else {
            AbstractEngineConfiguration processEngineConfiguration = engineConfiguration.getEngineConfigurations().get(
                    EngineConfigurationConstants.KEY_PROCESS_ENGINE_CONFIG);
            if (processEngineConfiguration instanceof SpringProcessEngineConfiguration) {
                springProcessEngineConfiguration = (SpringProcessEngineConfiguration) processEngineConfiguration;
            }
        }
        
        if (springProcessEngineConfiguration != null) {
           copyProcessEngineProperties(springProcessEngineConfiguration);
        }

        ((SpringCmmnEngineConfiguration) cmmnEngineConfiguration).setTransactionManager(springEngineConfiguration.getTransactionManager());
        if (cmmnEngineConfiguration.getExpressionManager() == null) {
            cmmnEngineConfiguration.setExpressionManager(new SpringCmmnExpressionManager(
                springEngineConfiguration.getApplicationContext(), springEngineConfiguration.getBeans()));
        }

        initCmmnEngine();

        initServiceConfigurations(engineConfiguration, cmmnEngineConfiguration);
    }

    @Override
    protected synchronized CmmnEngine initCmmnEngine() {
        if (cmmnEngineConfiguration == null) {
            throw new FlowableException("CmmnEngineConfiguration is required");
        }

        return cmmnEngineConfiguration.buildCmmnEngine();
    }

    @Override
    public SpringCmmnEngineConfiguration getCmmnEngineConfiguration() {
        return (SpringCmmnEngineConfiguration) cmmnEngineConfiguration;
    }

    public SpringCmmnEngineConfigurator setCmmnEngineConfiguration(SpringCmmnEngineConfiguration cmmnEngineConfiguration) {
        this.cmmnEngineConfiguration = cmmnEngineConfiguration;
        return this;
    }

}
