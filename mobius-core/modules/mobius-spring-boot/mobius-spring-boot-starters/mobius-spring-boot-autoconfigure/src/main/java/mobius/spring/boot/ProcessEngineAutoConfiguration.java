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

import mobius.app.spring.SpringAppEngineConfiguration;
import mobius.common.engine.impl.cfg.IdGenerator;
import mobius.common.engine.impl.persistence.StrongUuidGenerator;
import mobius.engine.configurator.ProcessEngineConfigurator;
import mobius.engine.spring.configurator.SpringProcessEngineConfigurator;
import mobius.job.service.impl.asyncexecutor.AsyncExecutor;
import mobius.spring.SpringProcessEngineConfiguration;
import mobius.spring.boot.app.AppEngineAutoConfiguration;
import mobius.spring.boot.app.AppEngineServicesAutoConfiguration;
import mobius.spring.boot.app.FlowableAppProperties;
import mobius.spring.boot.condition.ConditionalOnProcessEngine;
import mobius.spring.boot.idm.FlowableIdmProperties;
import mobius.spring.boot.process.FlowableProcessProperties;
import mobius.spring.boot.process.Process;
import mobius.spring.boot.process.ProcessAsync;
import mobius.spring.boot.process.ProcessAsyncHistory;
import mobius.spring.job.service.SpringAsyncExecutor;
import mobius.spring.job.service.SpringAsyncHistoryExecutor;
import mobius.spring.job.service.SpringRejectedJobsHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

/**
 * Provides sane definitions for the various beans required to be productive with Flowable in Spring.
 *
 * @author Josh Long
 * @author Filip Hrisafov
 * @author Javier Casal
 * @author Joram Barrez
 */
@Configuration
@ConditionalOnProcessEngine
@EnableConfigurationProperties({
    FlowableProperties.class,
    FlowableMailProperties.class,
    FlowableHttpProperties.class,
    FlowableProcessProperties.class,
    FlowableAppProperties.class,
    FlowableIdmProperties.class
})
@AutoConfigureAfter(value = {
    FlowableJpaAutoConfiguration.class,
    AppEngineAutoConfiguration.class,
}, name = {
    "org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration"
})
@AutoConfigureBefore({
    AppEngineServicesAutoConfiguration.class,
})
@Import({
    FlowableJobConfiguration.class
})
public class ProcessEngineAutoConfiguration extends AbstractSpringEngineAutoConfiguration {

    protected final FlowableProcessProperties processProperties;
    protected final FlowableAppProperties appProperties;
    protected final FlowableIdmProperties idmProperties;
    protected final FlowableMailProperties mailProperties;
    protected final FlowableHttpProperties httpProperties;

    public ProcessEngineAutoConfiguration(FlowableProperties flowableProperties, FlowableProcessProperties processProperties,
        FlowableAppProperties appProperties, FlowableIdmProperties idmProperties, FlowableMailProperties mailProperties,
        FlowableHttpProperties httpProperties) {
        
        super(flowableProperties);
        this.processProperties = processProperties;
        this.appProperties = appProperties;
        this.idmProperties = idmProperties;
        this.mailProperties = mailProperties;
        this.httpProperties = httpProperties;
    }

    /**
     * The Async Executor must not be shared between the engines.
     * Therefore a dedicated one is always created.
     */
    @Bean
    @ProcessAsync
    @ConfigurationProperties(prefix = "flowable.process.async.executor")
    @ConditionalOnMissingBean(name = "processAsyncExecutor")
    public SpringAsyncExecutor processAsyncExecutor(
        ObjectProvider<TaskExecutor> taskExecutor,
        @Process ObjectProvider<TaskExecutor> processTaskExecutor,
        ObjectProvider<SpringRejectedJobsHandler> rejectedJobsHandler,
        @Process ObjectProvider<SpringRejectedJobsHandler> processRejectedJobsHandler
    ) {
        return new SpringAsyncExecutor(
            getIfAvailable(processTaskExecutor, taskExecutor),
            getIfAvailable(processRejectedJobsHandler, rejectedJobsHandler)
        );
    }
    
    @Bean
    @ProcessAsyncHistory
    @ConfigurationProperties(prefix = "flowable.process.async-history.executor")
    @ConditionalOnMissingBean(name = "asyncHistoryExecutor")
    @ConditionalOnProperty(prefix = "flowable.process", name = "async-history.enable")
    public SpringAsyncHistoryExecutor asyncHistoryExecutor(
        ObjectProvider<TaskExecutor> taskExecutor,
        @Process ObjectProvider<TaskExecutor> processTaskExecutor,
        ObjectProvider<SpringRejectedJobsHandler> rejectedJobsHandler,
        @Process ObjectProvider<SpringRejectedJobsHandler> processRejectedJobsHandler
    ) {
        return new SpringAsyncHistoryExecutor(
            getIfAvailable(processTaskExecutor, taskExecutor),
            getIfAvailable(processRejectedJobsHandler, rejectedJobsHandler)
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringProcessEngineConfiguration springProcessEngineConfiguration(DataSource dataSource, PlatformTransactionManager platformTransactionManager,
            @Process ObjectProvider<IdGenerator> processIdGenerator,
            ObjectProvider<IdGenerator> globalIdGenerator,
            @ProcessAsync ObjectProvider<AsyncExecutor> asyncExecutorProvider,
            @ProcessAsyncHistory ObjectProvider<AsyncExecutor> asyncHistoryExecutorProvider) throws IOException {

        SpringProcessEngineConfiguration conf = new SpringProcessEngineConfiguration();

        List<Resource> resources = this.discoverDeploymentResources(
            flowableProperties.getProcessDefinitionLocationPrefix(),
            flowableProperties.getProcessDefinitionLocationSuffixes(),
            flowableProperties.isCheckProcessDefinitions()
        );

        if (resources != null && !resources.isEmpty()) {
            conf.setDeploymentResources(resources.toArray(new Resource[0]));
            conf.setDeploymentName(flowableProperties.getDeploymentName());
        }

        AsyncExecutor springAsyncExecutor = asyncExecutorProvider.getIfUnique();
        if (springAsyncExecutor != null) {
            conf.setAsyncExecutor(springAsyncExecutor);
        }
        
        AsyncExecutor springAsyncHistoryExecutor = asyncHistoryExecutorProvider.getIfUnique();
        if (springAsyncHistoryExecutor != null) {
            conf.setAsyncHistoryEnabled(true);
            conf.setAsyncHistoryExecutor(springAsyncHistoryExecutor);
        }

        configureSpringEngine(conf, platformTransactionManager);
        configureEngine(conf, dataSource);

        conf.setDeploymentName(defaultText(flowableProperties.getDeploymentName(), conf.getDeploymentName()));

        conf.setDisableIdmEngine(!(flowableProperties.isDbIdentityUsed() && idmProperties.isEnabled()));

        conf.setAsyncExecutorActivate(flowableProperties.isAsyncExecutorActivate());
        conf.setAsyncHistoryExecutorActivate(flowableProperties.isAsyncHistoryExecutorActivate());

        conf.setMailServerHost(mailProperties.getHost());
        conf.setMailServerPort(mailProperties.getPort());
        conf.setMailServerUsername(mailProperties.getUsername());
        conf.setMailServerPassword(mailProperties.getPassword());
        conf.setMailServerDefaultFrom(mailProperties.getDefaultFrom());
        conf.setMailServerForceTo(mailProperties.getForceTo());
        conf.setMailServerUseSSL(mailProperties.isUseSsl());
        conf.setMailServerUseTLS(mailProperties.isUseTls());

        conf.getHttpClientConfig().setUseSystemProperties(httpProperties.isUseSystemProperties());
        conf.getHttpClientConfig().setConnectionRequestTimeout(httpProperties.getConnectionRequestTimeout());
        conf.getHttpClientConfig().setConnectTimeout(httpProperties.getConnectTimeout());
        conf.getHttpClientConfig().setDisableCertVerify(httpProperties.isDisableCertVerify());
        conf.getHttpClientConfig().setRequestRetryLimit(httpProperties.getRequestRetryLimit());
        conf.getHttpClientConfig().setSocketTimeout(httpProperties.getSocketTimeout());

        conf.setEnableProcessDefinitionHistoryLevel(processProperties.isEnableProcessDefinitionHistoryLevel());
        conf.setProcessDefinitionCacheLimit(processProperties.getDefinitionCacheLimit());
        conf.setEnableSafeBpmnXml(processProperties.isEnableSafeXml());

        conf.setHistoryLevel(flowableProperties.getHistoryLevel());
        
        conf.setActivityFontName(flowableProperties.getActivityFontName());
        conf.setAnnotationFontName(flowableProperties.getAnnotationFontName());
        conf.setLabelFontName(flowableProperties.getLabelFontName());

        conf.setFormFieldValidationEnabled(flowableProperties.isFormFieldValidationEnabled());

        IdGenerator idGenerator = getIfAvailable(processIdGenerator, globalIdGenerator);
        if (idGenerator == null) {
            idGenerator = new StrongUuidGenerator();
        }
        conf.setIdGenerator(idGenerator);

        return conf;
    }
    
    @Configuration
    @ConditionalOnBean(type = {
        "mobius.app.spring.SpringAppEngineConfiguration"
    })
    public static class ProcessEngineAppConfiguration extends BaseEngineConfigurationWithConfigurers<SpringProcessEngineConfiguration> {

        @Bean
        @ConditionalOnMissingBean(name = "processAppEngineConfigurationConfigurer")
        public EngineConfigurationConfigurer<SpringAppEngineConfiguration> processAppEngineConfigurationConfigurer(ProcessEngineConfigurator processEngineConfigurator) {
            return appEngineConfiguration -> appEngineConfiguration.addConfigurator(processEngineConfigurator);
        }

        @Bean
        @ConditionalOnMissingBean
        public ProcessEngineConfigurator processEngineConfigurator(SpringProcessEngineConfiguration processEngineConfiguration) {
            SpringProcessEngineConfigurator processEngineConfigurator = new SpringProcessEngineConfigurator();
            processEngineConfigurator.setProcessEngineConfiguration(processEngineConfiguration);
            
            processEngineConfiguration.setDisableIdmEngine(true);
            
            invokeConfigurers(processEngineConfiguration);
            
            return processEngineConfigurator;
        }
    }
}