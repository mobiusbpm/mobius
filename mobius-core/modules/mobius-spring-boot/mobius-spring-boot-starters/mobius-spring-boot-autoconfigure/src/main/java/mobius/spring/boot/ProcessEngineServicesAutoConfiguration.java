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
package mobius.spring.boot;

import mobius.app.engine.AppEngine;
import mobius.engine.*;
import mobius.spring.ProcessEngineFactoryBean;
import mobius.spring.SpringProcessEngineConfiguration;
import mobius.spring.boot.app.AppEngineServicesAutoConfiguration;
import mobius.spring.boot.condition.ConditionalOnProcessEngine;
import mobius.spring.boot.process.FlowableProcessProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides sane definitions for the various beans required to be productive with Flowable in Spring.
 *
 * @author Josh Long
 *
 * @author Javier Casal
 */
@Configuration
@ConditionalOnProcessEngine
@EnableConfigurationProperties({
    FlowableProperties.class,
    FlowableProcessProperties.class,
})
@AutoConfigureAfter({
    ProcessEngineAutoConfiguration.class,
    AppEngineServicesAutoConfiguration.class
})
public class ProcessEngineServicesAutoConfiguration {

    /**
     * If an app engine is present that means that the ProcessEngine was created as part of the app engine.
     * Therefore extract it from the ProcessEngines.
     */
    @Configuration
    @ConditionalOnMissingBean(type = {
        "mobius.engine.ProcessEngine"
    })
    @ConditionalOnBean(type = {
        "mobius.engine.AppEngine"
    })
    static class AlreadyInitializedAppEngineConfiguration {

        @Bean
        public ProcessEngine processEngine(@SuppressWarnings("unused") @Autowired AppEngine appEngine) {
            // The app engine needs to be injected, as otherwise it won't be initialized, which means that the ProcessEngine is not initialized yet
            if (!ProcessEngines.isInitialized()) {
                throw new IllegalStateException("BPMN engine has not been initialized");
            }
            return ProcessEngines.getDefaultProcessEngine();
        }
    }

    /**
     * If there is no app engine configuration, then trigger a creation of the process engine.
     */
    @Configuration
    @ConditionalOnMissingBean(type = {
        "mobius.engine.ProcessEngine",
        "mobius.engine.AppEngine",
    })
    static class StandaloneEngineConfiguration extends BaseEngineConfigurationWithConfigurers<SpringProcessEngineConfiguration> {
        
        @Bean
        public ProcessEngineFactoryBean processEngine(SpringProcessEngineConfiguration configuration) throws Exception {
            ProcessEngineFactoryBean processEngineFactoryBean = new ProcessEngineFactoryBean();
            processEngineFactoryBean.setProcessEngineConfiguration(configuration);
            
            invokeConfigurers(configuration);
            
            return processEngineFactoryBean;
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public RuntimeService runtimeServiceBean(ProcessEngine processEngine) {
        return processEngine.getRuntimeService();
    }

    @Bean
    @ConditionalOnMissingBean
    public RepositoryService repositoryServiceBean(ProcessEngine processEngine) {
        return processEngine.getRepositoryService();
    }

    @Bean
    @ConditionalOnMissingBean
    public TaskService taskServiceBean(ProcessEngine processEngine) {
        return processEngine.getTaskService();
    }

    @Bean
    @ConditionalOnMissingBean
    public HistoryService historyServiceBean(ProcessEngine processEngine) {
        return processEngine.getHistoryService();
    }

    @Bean
    @ConditionalOnMissingBean
    public ManagementService managementServiceBean(ProcessEngine processEngine) {
        return processEngine.getManagementService();
    }
    
    @Bean
    @ConditionalOnMissingBean
    public DynamicBpmnService dynamicBpmnServiceBean(ProcessEngine processEngine) {
        return processEngine.getDynamicBpmnService();
    }

    @Bean
    @ConditionalOnMissingBean
    public FormService formServiceBean(ProcessEngine processEngine) {
        return processEngine.getFormService();
    }

    @Bean
    @ConditionalOnMissingBean
    public IdentityService identityServiceBean(ProcessEngine processEngine) {
        return processEngine.getIdentityService();
    }
}
