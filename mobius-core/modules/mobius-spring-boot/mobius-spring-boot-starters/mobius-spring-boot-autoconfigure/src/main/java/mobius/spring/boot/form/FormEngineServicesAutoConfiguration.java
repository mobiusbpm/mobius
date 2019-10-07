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
package mobius.spring.boot.form;

import mobius.app.engine.AppEngine;
import mobius.engine.ProcessEngine;
import mobius.form.api.FormManagementService;
import mobius.form.api.FormRepositoryService;
import mobius.form.api.FormService;
import mobius.form.engine.FormEngine;
import mobius.form.engine.FormEngines;
import mobius.form.spring.FormEngineFactoryBean;
import mobius.form.spring.SpringFormEngineConfiguration;
import mobius.spring.boot.BaseEngineConfigurationWithConfigurers;
import mobius.spring.boot.FlowableProperties;
import mobius.spring.boot.ProcessEngineServicesAutoConfiguration;
import mobius.spring.boot.app.AppEngineServicesAutoConfiguration;
import mobius.spring.boot.condition.ConditionalOnFormEngine;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto configuration for the form engine.
 *
 *
 * @author Javier Casal
 */
@Configuration
@ConditionalOnFormEngine
@EnableConfigurationProperties({
    FlowableProperties.class,
    FlowableFormProperties.class
})
@AutoConfigureAfter({
    FormEngineAutoConfiguration.class,
    AppEngineServicesAutoConfiguration.class,
    ProcessEngineServicesAutoConfiguration.class
})
public class FormEngineServicesAutoConfiguration {


    /**
     * If a process engine is present that means that the FormEngine was created as part of it.
     * Therefore extract it from the FormEngines.
     */
    @Configuration
    @ConditionalOnMissingBean(type = {
        "mobius.form.engine.FormEngine",
        "mobius.engine.AppEngine"
    })
    @ConditionalOnBean(type = {
        "mobius.engine.ProcessEngine"
    })
    static class AlreadyInitializedFormEngineConfiguration {
        @Bean
        public FormEngine formEngine(@SuppressWarnings("unused") ProcessEngine processEngine) {
            // The process engine needs to be injected, as otherwise it won't be initialized, which means that the FormEngine is not initialized yet
            if (!FormEngines.isInitialized()) {
                throw new IllegalStateException("Form engine has not been initialized");
            }
            return FormEngines.getDefaultFormEngine();
        }
    }
    
    /**
     * If an app engine is present that means that the FormEngine was created as part of the app engine.
     * Therefore extract it from the FormEngines.
     */
    @Configuration
    @ConditionalOnMissingBean(type = {
        "mobius.form.engine.FormEngine"
    })
    @ConditionalOnBean(type = {
        "mobius.engine.AppEngine"
    })
    static class AlreadyInitializedAppEngineConfiguration {

        @Bean
        public FormEngine formEngine(@SuppressWarnings("unused") AppEngine appEngine) {
            // The app engine needs to be injected, as otherwise it won't be initialized, which means that the FormEngine is not initialized yet
            if (!FormEngines.isInitialized()) {
                throw new IllegalStateException("Form engine has not been initialized");
            }
            return FormEngines.getDefaultFormEngine();
        }
    }
    
    /**
     * If there is no process engine configuration, then trigger a creation of the form engine.
     */
    @Configuration
    @ConditionalOnMissingBean(type = {
        "mobius.form.engine.FormEngine",
        "mobius.engine.ProcessEngine",
        "mobius.engine.AppEngine"
    })
    static class StandaloneFormEngineConfiguration extends BaseEngineConfigurationWithConfigurers<SpringFormEngineConfiguration> {

        @Bean
        public FormEngineFactoryBean formEngine(SpringFormEngineConfiguration formEngineConfiguration) {
            FormEngineFactoryBean factory = new FormEngineFactoryBean();
            factory.setFormEngineConfiguration(formEngineConfiguration);
            
            invokeConfigurers(formEngineConfiguration);
            
            return factory;
        }
    }

    @Bean
    public FormService formService(FormEngine formEngine) {
        return formEngine.getFormService();
    }

    @Bean
    public FormRepositoryService formRepositoryService(FormEngine formEngine) {
        return formEngine.getFormRepositoryService();
    }

    @Bean
    public FormManagementService formManagementService(FormEngine formEngine) {
        return formEngine.getFormManagementService();
    }
}

