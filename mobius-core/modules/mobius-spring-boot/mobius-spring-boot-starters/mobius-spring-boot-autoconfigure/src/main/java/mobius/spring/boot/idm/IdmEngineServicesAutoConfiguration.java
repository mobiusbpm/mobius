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
package mobius.spring.boot.idm;

import mobius.app.engine.AppEngine;
import mobius.engine.ProcessEngine;
import mobius.idm.api.IdmIdentityService;
import mobius.idm.api.IdmManagementService;
import mobius.idm.engine.IdmEngine;
import mobius.idm.engine.IdmEngines;
import mobius.idm.spring.IdmEngineFactoryBean;
import mobius.idm.spring.SpringIdmEngineConfiguration;
import mobius.spring.boot.BaseEngineConfigurationWithConfigurers;
import mobius.spring.boot.FlowableProperties;
import mobius.spring.boot.ProcessEngineServicesAutoConfiguration;
import mobius.spring.boot.app.AppEngineServicesAutoConfiguration;
import mobius.spring.boot.condition.ConditionalOnIdmEngine;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration} for the Idm Engine
 *
 * @author Filip Hrisafov
 */
@Configuration
@ConditionalOnIdmEngine
@EnableConfigurationProperties({
    FlowableProperties.class,
    FlowableIdmProperties.class
})
@AutoConfigureAfter({
    IdmEngineAutoConfiguration.class,
    AppEngineServicesAutoConfiguration.class,
    ProcessEngineServicesAutoConfiguration.class,
})
public class IdmEngineServicesAutoConfiguration {

    /**
     * If a process engine is present that means that the IdmEngine was created as part of it.
     * Therefore extract it from the IdmEngines.
     */
    @Configuration
    @ConditionalOnMissingBean(type = {
        "mobius.idm.engine.IdmEngine",
        "mobius.engine.AppEngine"
    })
    @ConditionalOnBean(type = {
        "mobius.engine.ProcessEngine"
    })
    static class AlreadyInitializedEngineConfiguration {

        @Bean
        public IdmEngine idmEngine(@SuppressWarnings("unused") ProcessEngine processEngine) {
            // The process engine needs to be injected, as otherwise it won't be initialized, which means that the IdmEngine is not initialized yet
            if (!IdmEngines.isInitialized()) {
                throw new IllegalStateException("Idm engine has not been initialized");
            }
            return IdmEngines.getDefaultIdmEngine();
        }
    }
    
    /**
     * If an app engine is present that means that the IdmEngine was created as part of it.
     * Therefore extract it from the IdmEngines.
     */
    @Configuration
    @ConditionalOnMissingBean(type = {
        "mobius.idm.engine.IdmEngine",
    })
    @ConditionalOnBean(type = {
        "mobius.engine.AppEngine"
    })
    static class AlreadyInitializedAppEngineConfiguration {

        @Bean
        public IdmEngine idmEngine(@SuppressWarnings("unused") AppEngine appEngine) {
            // The process engine needs to be injected, as otherwise it won't be initialized, which means that the IdmEngine is not initialized yet
            if (!IdmEngines.isInitialized()) {
                throw new IllegalStateException("Idm engine has not been initialized");
            }
            return IdmEngines.getDefaultIdmEngine();
        }
    }

    /**
     * If there is no process engine configuration, then trigger a creation of the idm engine.
     */
    @Configuration
    @ConditionalOnMissingBean(type = {
        "mobius.idm.engine.IdmEngine",
        "mobius.engine.ProcessEngine",
        "mobius.engine.AppEngine"
    })
    static class StandaloneEngineConfiguration extends BaseEngineConfigurationWithConfigurers<SpringIdmEngineConfiguration> {

        @Bean
        public IdmEngineFactoryBean idmEngine(SpringIdmEngineConfiguration idmEngineConfiguration) {
            IdmEngineFactoryBean factory = new IdmEngineFactoryBean();
            factory.setIdmEngineConfiguration(idmEngineConfiguration);
            
            invokeConfigurers(idmEngineConfiguration);
            
            return factory;
        }
    }

    @Bean
    public IdmManagementService idmManagementService(IdmEngine idmEngine) {
        return idmEngine.getIdmManagementService();
    }

    @Bean
    public IdmIdentityService idmIdentityService(IdmEngine idmEngine) {
        return idmEngine.getIdmIdentityService();
    }
}
