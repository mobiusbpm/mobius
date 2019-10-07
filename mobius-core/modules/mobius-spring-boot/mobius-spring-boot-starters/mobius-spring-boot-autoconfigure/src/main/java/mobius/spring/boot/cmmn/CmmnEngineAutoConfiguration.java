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

import java.io.IOException;
import java.util.List;

import javax.sql.DataSource;

import mobius.app.spring.SpringAppEngineConfiguration;
import mobius.cmmn.engine.configurator.CmmnEngineConfigurator;
import mobius.cmmn.spring.SpringCmmnEngineConfiguration;
import mobius.cmmn.spring.configurator.SpringCmmnEngineConfigurator;
import mobius.job.service.impl.asyncexecutor.AsyncExecutor;
import mobius.spring.SpringProcessEngineConfiguration;
import mobius.spring.boot.AbstractSpringEngineAutoConfiguration;
import mobius.spring.boot.BaseEngineConfigurationWithConfigurers;
import mobius.spring.boot.EngineConfigurationConfigurer;
import mobius.spring.boot.FlowableHttpProperties;
import mobius.spring.boot.FlowableJobConfiguration;
import mobius.spring.boot.FlowableProperties;
import mobius.spring.boot.ProcessEngineAutoConfiguration;
import mobius.spring.boot.ProcessEngineServicesAutoConfiguration;
import mobius.spring.boot.app.AppEngineAutoConfiguration;
import mobius.spring.boot.app.AppEngineServicesAutoConfiguration;
import mobius.spring.boot.app.FlowableAppProperties;
import mobius.spring.boot.condition.ConditionalOnCmmnEngine;
import mobius.spring.boot.idm.FlowableIdmProperties;
import mobius.spring.job.service.SpringAsyncExecutor;
import mobius.spring.job.service.SpringRejectedJobsHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration} for the CMMN engine
 *
 *
 */
@Configuration
@ConditionalOnCmmnEngine
@EnableConfigurationProperties({
    FlowableProperties.class,
    FlowableIdmProperties.class,
    FlowableCmmnProperties.class,
    FlowableAppProperties.class,
    FlowableHttpProperties.class
})
@AutoConfigureAfter(value = {
    AppEngineAutoConfiguration.class,
    ProcessEngineAutoConfiguration.class,
}, name = {
    "org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration"
})
@AutoConfigureBefore({
    AppEngineServicesAutoConfiguration.class,
    ProcessEngineServicesAutoConfiguration.class
})
@Import({
    FlowableJobConfiguration.class
})
public class CmmnEngineAutoConfiguration extends AbstractSpringEngineAutoConfiguration {

    protected final FlowableCmmnProperties cmmnProperties;
    protected final FlowableIdmProperties idmProperties;
    protected final FlowableHttpProperties httpProperties;

    public CmmnEngineAutoConfiguration(FlowableProperties flowableProperties, FlowableCmmnProperties cmmnProperties, FlowableIdmProperties idmProperties,
        FlowableHttpProperties httpProperties) {
        super(flowableProperties);
        this.cmmnProperties = cmmnProperties;
        this.idmProperties = idmProperties;
        this.httpProperties = httpProperties;
    }

    /**
     * The Async Executor must not be shared between the engines.
     * Therefore a dedicated one is always created.
     */
    @Bean
    @Cmmn
    @ConfigurationProperties(prefix = "flowable.cmmn.async.executor")
    @ConditionalOnMissingBean(name = "cmmnAsyncExecutor")
    public SpringAsyncExecutor cmmnAsyncExecutor(
        ObjectProvider<TaskExecutor> taskExecutor,
        @Cmmn ObjectProvider<TaskExecutor> cmmnTaskExecutor,
        ObjectProvider<SpringRejectedJobsHandler> rejectedJobsHandler,
        @Cmmn ObjectProvider<SpringRejectedJobsHandler> cmmnRejectedJobsHandler
    ) {
        return new SpringAsyncExecutor(
            getIfAvailable(cmmnTaskExecutor, taskExecutor),
            getIfAvailable(cmmnRejectedJobsHandler, rejectedJobsHandler)
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringCmmnEngineConfiguration cmmnEngineConfiguration(DataSource dataSource, PlatformTransactionManager platformTransactionManager,
        @Cmmn ObjectProvider<AsyncExecutor> asyncExecutorProvider)
        throws IOException {
        
        SpringCmmnEngineConfiguration configuration = new SpringCmmnEngineConfiguration();

        List<Resource> resources = this.discoverDeploymentResources(
            cmmnProperties.getResourceLocation(),
            cmmnProperties.getResourceSuffixes(),
            cmmnProperties.isDeployResources()
        );

        if (resources != null && !resources.isEmpty()) {
            configuration.setDeploymentResources(resources.toArray(new Resource[0]));
            configuration.setDeploymentName(cmmnProperties.getDeploymentName());
        }

        AsyncExecutor asyncExecutor = asyncExecutorProvider.getIfUnique();
        if (asyncExecutor != null) {
            configuration.setAsyncExecutor(asyncExecutor);
        }

        configureSpringEngine(configuration, platformTransactionManager);
        configureEngine(configuration, dataSource);

        configuration.setDeploymentName(defaultText(cmmnProperties.getDeploymentName(), configuration.getDeploymentName()));

        configuration.setDisableIdmEngine(!idmProperties.isEnabled());

        configuration.setAsyncExecutorActivate(flowableProperties.isAsyncExecutorActivate());

        configuration.getHttpClientConfig().setUseSystemProperties(httpProperties.isUseSystemProperties());
        configuration.getHttpClientConfig().setConnectionRequestTimeout(httpProperties.getConnectionRequestTimeout());
        configuration.getHttpClientConfig().setConnectTimeout(httpProperties.getConnectTimeout());
        configuration.getHttpClientConfig().setDisableCertVerify(httpProperties.isDisableCertVerify());
        configuration.getHttpClientConfig().setRequestRetryLimit(httpProperties.getRequestRetryLimit());
        configuration.getHttpClientConfig().setSocketTimeout(httpProperties.getSocketTimeout());

        //TODO Can it have different then the Process engine?
        configuration.setHistoryLevel(flowableProperties.getHistoryLevel());

        configuration.setEnableSafeCmmnXml(cmmnProperties.isEnableSafeXml());

        configuration.setFormFieldValidationEnabled(flowableProperties.isFormFieldValidationEnabled());

        return configuration;
    }

    @Configuration
    @ConditionalOnBean(type = {
        "mobius.spring.SpringProcessEngineConfiguration"
    })
    @ConditionalOnMissingBean(type = {
        "mobius.app.spring.SpringAppEngineConfiguration"
    })
    public static class CmmnEngineProcessConfiguration extends BaseEngineConfigurationWithConfigurers<SpringCmmnEngineConfiguration> {

        @Bean
        @ConditionalOnMissingBean(name = "cmmnProcessEngineConfigurationConfigurer")
        public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> cmmnProcessEngineConfigurationConfigurer(
            CmmnEngineConfigurator cmmnEngineConfigurator) {
            return processEngineConfiguration -> processEngineConfiguration.addConfigurator(cmmnEngineConfigurator);
        }

        @Bean
        @ConditionalOnMissingBean
        public CmmnEngineConfigurator cmmnEngineConfigurator(SpringCmmnEngineConfiguration cmmnEngineConfiguration) {
            SpringCmmnEngineConfigurator cmmnEngineConfigurator = new SpringCmmnEngineConfigurator();
            cmmnEngineConfigurator.setCmmnEngineConfiguration(cmmnEngineConfiguration);

            cmmnEngineConfiguration.setDisableIdmEngine(true);
            
            invokeConfigurers(cmmnEngineConfiguration);
            
            return cmmnEngineConfigurator;
        }
    }
    
    @Configuration
    @ConditionalOnBean(type = {
        "mobius.app.spring.SpringAppEngineConfiguration"
    })
    public static class CmmnEngineAppConfiguration extends BaseEngineConfigurationWithConfigurers<SpringCmmnEngineConfiguration> {

        @Bean
        @ConditionalOnMissingBean(name = "cmmnAppEngineConfigurationConfigurer")
        public EngineConfigurationConfigurer<SpringAppEngineConfiguration> cmmnAppEngineConfigurationConfigurer(CmmnEngineConfigurator cmmnEngineConfigurator) {
            return appEngineConfiguration -> appEngineConfiguration.addConfigurator(cmmnEngineConfigurator);
        }

        @Bean
        @ConditionalOnMissingBean
        public CmmnEngineConfigurator cmmnEngineConfigurator(SpringCmmnEngineConfiguration cmmnEngineConfiguration) {
            SpringCmmnEngineConfigurator cmmnEngineConfigurator = new SpringCmmnEngineConfigurator();
            cmmnEngineConfigurator.setCmmnEngineConfiguration(cmmnEngineConfiguration);

            cmmnEngineConfiguration.setDisableIdmEngine(true);
            
            invokeConfigurers(cmmnEngineConfiguration);
            
            return cmmnEngineConfigurator;
        }
    }
}
