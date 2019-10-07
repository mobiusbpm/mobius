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

import java.io.IOException;
import java.util.List;

import javax.sql.DataSource;

import mobius.app.spring.SpringAppEngineConfiguration;
import mobius.form.engine.configurator.FormEngineConfigurator;
import mobius.form.spring.SpringFormEngineConfiguration;
import mobius.form.spring.configurator.SpringFormEngineConfigurator;
import mobius.spring.SpringProcessEngineConfiguration;
import mobius.spring.boot.AbstractSpringEngineAutoConfiguration;
import mobius.spring.boot.BaseEngineConfigurationWithConfigurers;
import mobius.spring.boot.EngineConfigurationConfigurer;
import mobius.spring.boot.FlowableProperties;
import mobius.spring.boot.ProcessEngineAutoConfiguration;
import mobius.spring.boot.ProcessEngineServicesAutoConfiguration;
import mobius.spring.boot.app.AppEngineAutoConfiguration;
import mobius.spring.boot.app.AppEngineServicesAutoConfiguration;
import mobius.spring.boot.condition.ConditionalOnFormEngine;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

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
    AppEngineAutoConfiguration.class,
    ProcessEngineAutoConfiguration.class,
})
@AutoConfigureBefore({
    AppEngineServicesAutoConfiguration.class,
    ProcessEngineServicesAutoConfiguration.class,
})
public class FormEngineAutoConfiguration extends AbstractSpringEngineAutoConfiguration {

    protected final FlowableFormProperties formProperties;

    public FormEngineAutoConfiguration(FlowableProperties flowableProperties, FlowableFormProperties formProperties) {
        super(flowableProperties);
        this.formProperties = formProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringFormEngineConfiguration formEngineConfiguration(
        DataSource dataSource,
        PlatformTransactionManager platformTransactionManager
    ) throws IOException {
        
        SpringFormEngineConfiguration configuration = new SpringFormEngineConfiguration();

        List<Resource> resources = this.discoverDeploymentResources(
            formProperties.getResourceLocation(),
            formProperties.getResourceSuffixes(),
            formProperties.isDeployResources()
        );

        if (resources != null && !resources.isEmpty()) {
            configuration.setDeploymentResources(resources.toArray(new Resource[0]));
            configuration.setDeploymentName(formProperties.getDeploymentName());
        }

        configureSpringEngine(configuration, platformTransactionManager);
        configureEngine(configuration, dataSource);

        return configuration;
    }

    @Configuration
    @ConditionalOnBean(type = {
        "mobius.spring.SpringProcessEngineConfiguration"
    })
    @ConditionalOnMissingBean(type = {
        "mobius.app.spring.SpringAppEngineConfiguration"
    })
    public static class FormEngineProcessConfiguration extends BaseEngineConfigurationWithConfigurers<SpringFormEngineConfiguration> {

        @Bean
        @ConditionalOnMissingBean(name = "formProcessEngineConfigurationConfigurer")
        public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> formProcessEngineConfigurationConfigurer(
            FormEngineConfigurator formEngineConfigurator) {
            
            return processEngineConfiguration -> processEngineConfiguration.addConfigurator(formEngineConfigurator);
        }

        @Bean
        @ConditionalOnMissingBean
        public FormEngineConfigurator formEngineConfigurator(SpringFormEngineConfiguration configuration) {
            SpringFormEngineConfigurator formEngineConfigurator = new SpringFormEngineConfigurator();
            formEngineConfigurator.setFormEngineConfiguration(configuration);
            invokeConfigurers(configuration);
            
            return formEngineConfigurator;
        }
    }
    
    @Configuration
    @ConditionalOnBean(type = {
        "mobius.app.spring.SpringAppEngineConfiguration"
    })
    public static class FormEngineAppEngineConfiguration extends BaseEngineConfigurationWithConfigurers<SpringFormEngineConfiguration> {

        @Bean
        @ConditionalOnMissingBean(name = "formAppEngineConfigurationConfigurer")
        public EngineConfigurationConfigurer<SpringAppEngineConfiguration> formAppEngineConfigurationConfigurer(
            FormEngineConfigurator formEngineConfigurator) {
            
            return appEngineConfiguration -> appEngineConfiguration.addConfigurator(formEngineConfigurator);
        }

        @Bean
        @ConditionalOnMissingBean
        public FormEngineConfigurator formEngineConfigurator(SpringFormEngineConfiguration configuration) {
            SpringFormEngineConfigurator formEngineConfigurator = new SpringFormEngineConfigurator();
            formEngineConfigurator.setFormEngineConfiguration(configuration);

            invokeConfigurers(configuration);
            
            return formEngineConfigurator;
        }
    }
}

