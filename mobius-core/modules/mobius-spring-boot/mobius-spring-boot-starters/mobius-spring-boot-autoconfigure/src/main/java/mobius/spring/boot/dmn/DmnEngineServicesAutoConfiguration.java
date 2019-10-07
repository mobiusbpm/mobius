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
package mobius.spring.boot.dmn;

import mobius.app.engine.AppEngine;
import mobius.dmn.api.DmnHistoryService;
import mobius.dmn.api.DmnManagementService;
import mobius.dmn.api.DmnRepositoryService;
import mobius.dmn.api.DmnRuleService;
import mobius.dmn.engine.DmnEngine;
import mobius.dmn.engine.DmnEngines;
import mobius.dmn.spring.DmnEngineFactoryBean;
import mobius.dmn.spring.SpringDmnEngineConfiguration;
import mobius.engine.ProcessEngine;
import mobius.spring.boot.BaseEngineConfigurationWithConfigurers;
import mobius.spring.boot.FlowableProperties;
import mobius.spring.boot.ProcessEngineServicesAutoConfiguration;
import mobius.spring.boot.app.AppEngineServicesAutoConfiguration;
import mobius.spring.boot.condition.ConditionalOnDmnEngine;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration} for the DMN Engine
 *
 *
 */
@Configuration
@ConditionalOnDmnEngine
@EnableConfigurationProperties({
    FlowableProperties.class,
    FlowableDmnProperties.class
})
@AutoConfigureAfter({
    DmnEngineAutoConfiguration.class,
    AppEngineServicesAutoConfiguration.class,
    ProcessEngineServicesAutoConfiguration.class
})
public class DmnEngineServicesAutoConfiguration {

    /**
     * If a process engine is present that means that the DmnEngine was created as part of it.
     * Therefore extract it from the DmnEngines.
     */
    @Configuration
    @ConditionalOnMissingBean(type = {
        "mobius.dmn.engine.DmnEngine",
        "mobius.engine.AppEngine"
    })
    @ConditionalOnBean(type = {
        "mobius.engine.ProcessEngine"
    })
    static class AlreadyInitializedEngineConfiguration {
        @Bean
        public DmnEngine dmnEngine(@SuppressWarnings("unused") ProcessEngine processEngine) {
            // The process engine needs to be injected, as otherwise it won't be initialized, which means that the DmnEngine is not initialized yet
            if (!DmnEngines.isInitialized()) {
                throw new IllegalStateException("DMN engine has not been initialized");
            }
            return DmnEngines.getDefaultDmnEngine();
        }
    }
    
    /**
     * If an app engine is present that means that the DmnEngine was created as part of the app engine.
     * Therefore extract it from the DmnEngines.
     */
    @Configuration
    @ConditionalOnMissingBean(type = {
        "mobius.dmn.engine.DmnEngine"
    })
    @ConditionalOnBean(type = {
        "mobius.engine.AppEngine"
    })
    static class AlreadyInitializedAppEngineConfiguration {

        @Bean
        public DmnEngine dmnEngine(@SuppressWarnings("unused") AppEngine appEngine) {
            // The app engine needs to be injected, as otherwise it won't be initialized, which means that the DmnEngine is not initialized yet
            if (!DmnEngines.isInitialized()) {
                throw new IllegalStateException("DMN engine has not been initialized");
            }
            return DmnEngines.getDefaultDmnEngine();
        }
    }

    /**
     * If there is no process engine configuration, then trigger a creation of the dmn engine.
     */
    @Configuration
    @ConditionalOnMissingBean(type = {
        "mobius.dmn.engine.DmnEngine",
        "mobius.engine.ProcessEngine",
        "mobius.engine.AppEngine"
    })
    static class StandaloneEngineConfiguration extends BaseEngineConfigurationWithConfigurers<SpringDmnEngineConfiguration> {

        @Bean
        public DmnEngineFactoryBean dmnEngine(SpringDmnEngineConfiguration dmnEngineConfiguration) {
            DmnEngineFactoryBean factory = new DmnEngineFactoryBean();
            factory.setDmnEngineConfiguration(dmnEngineConfiguration);
            
            invokeConfigurers(dmnEngineConfiguration);
            
            return factory;
        }
    }

    @Bean
    public DmnManagementService dmnManagementService(DmnEngine dmnEngine) {
        return dmnEngine.getDmnManagementService();
    }

    @Bean
    public DmnRepositoryService dmnRepositoryService(DmnEngine dmnEngine) {
        return dmnEngine.getDmnRepositoryService();
    }

    @Bean
    public DmnRuleService dmnRuleService(DmnEngine dmnEngine) {
        return dmnEngine.getDmnRuleService();
    }

    @Bean
    public DmnHistoryService dmnHistoryService(DmnEngine dmnEngine) {
        return dmnEngine.getDmnHistoryService();
    }
}
