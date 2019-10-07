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
package mobius.spring.boot.cmmn;

import mobius.app.engine.AppEngine;
import mobius.cmmn.api.*;
import mobius.cmmn.engine.CmmnEngine;
import mobius.cmmn.engine.CmmnEngines;
import mobius.cmmn.spring.CmmnEngineFactoryBean;
import mobius.cmmn.spring.SpringCmmnEngineConfiguration;
import mobius.engine.ProcessEngine;
import mobius.spring.boot.BaseEngineConfigurationWithConfigurers;
import mobius.spring.boot.FlowableProperties;
import mobius.spring.boot.ProcessEngineServicesAutoConfiguration;
import mobius.spring.boot.app.AppEngineServicesAutoConfiguration;
import mobius.spring.boot.condition.ConditionalOnCmmnEngine;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration} for the CMMN Engine
 *
 *
 */
@Configuration
@ConditionalOnCmmnEngine
@EnableConfigurationProperties({
    FlowableProperties.class,
    FlowableCmmnProperties.class
})
@AutoConfigureAfter({
    CmmnEngineAutoConfiguration.class,
    AppEngineServicesAutoConfiguration.class,
    ProcessEngineServicesAutoConfiguration.class
})
public class CmmnEngineServicesAutoConfiguration {

    /**
     * If a process engine is present and no app engine that means that the CmmnEngine was created as part of the process engine.
     * Therefore extract it from the CmmnEngines.
     */
    @Configuration
    @ConditionalOnMissingBean(type = {
        "mobius.cmmn.engine.CmmnEngine",
        "mobius.engine.AppEngine"
    })
    @ConditionalOnBean(type = {
        "mobius.engine.ProcessEngine"
    })
    static class AlreadyInitializedEngineConfiguration {

        @Bean
        public CmmnEngine cmmnEngine(@SuppressWarnings("unused") ProcessEngine processEngine) {
            // The process engine needs to be injected, as otherwise it won't be initialized, which means that the CmmnEngine is not initialized yet
            if (!CmmnEngines.isInitialized()) {
                throw new IllegalStateException("CMMN engine has not been initialized");
            }
            return CmmnEngines.getDefaultCmmnEngine();
        }
    }
    
    /**
     * If an app engine is present that means that the CmmnEngine was created as part of the app engine.
     * Therefore extract it from the CmmnEngines.
     */
    @Configuration
    @ConditionalOnMissingBean(type = {
        "mobius.cmmn.engine.CmmnEngine"
    })
    @ConditionalOnBean(type = {
        "mobius.engine.AppEngine"
    })
    static class AlreadyInitializedAppEngineConfiguration {

        @Bean
        public CmmnEngine cmmnEngine(@SuppressWarnings("unused") AppEngine appEngine) {
            // The app engine needs to be injected, as otherwise it won't be initialized, which means that the CmmnEngine is not initialized yet
            if (!CmmnEngines.isInitialized()) {
                throw new IllegalStateException("CMMN engine has not been initialized");
            }
            return CmmnEngines.getDefaultCmmnEngine();
        }
    }

    /**
     * If there is no process engine configuration, then trigger a creation of the cmmn engine.
     */
    @Configuration
    @ConditionalOnMissingBean(type = {
        "mobius.cmmn.engine.CmmnEngine",
        "mobius.engine.ProcessEngine",
        "mobius.engine.AppEngine",
    })
    static class StandaloneEngineConfiguration extends BaseEngineConfigurationWithConfigurers<SpringCmmnEngineConfiguration> {

        @Bean
        public CmmnEngineFactoryBean cmmnEngine(SpringCmmnEngineConfiguration cmmnEngineConfiguration) {
            CmmnEngineFactoryBean factory = new CmmnEngineFactoryBean();
            factory.setCmmnEngineConfiguration(cmmnEngineConfiguration);
            
            invokeConfigurers(cmmnEngineConfiguration);
            
            return factory;
        }
    }

    @Bean
    public CmmnRuntimeService cmmnRuntimeService(CmmnEngine cmmnEngine) {
        return cmmnEngine.getCmmnRuntimeService();
    }

    @Bean
    public CmmnTaskService cmmnTaskService(CmmnEngine cmmnEngine) {
        return cmmnEngine.getCmmnTaskService();
    }

    @Bean
    public CmmnManagementService cmmnManagementService(CmmnEngine cmmnEngine) {
        return cmmnEngine.getCmmnManagementService();
    }

    @Bean
    public CmmnRepositoryService cmmnRepositoryService(CmmnEngine cmmnEngine) {
        return cmmnEngine.getCmmnRepositoryService();
    }

    @Bean
    public CmmnHistoryService cmmnHistoryService(CmmnEngine cmmnEngine) {
        return cmmnEngine.getCmmnHistoryService();
    }
}
