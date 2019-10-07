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
package mobius.test.spring.boot.process;

import mobius.app.api.AppRepositoryService;
import mobius.app.api.repository.AppDefinition;
import mobius.app.api.repository.AppDeployment;
import mobius.app.engine.AppEngine;
import mobius.app.engine.AppEngineConfiguration;
import mobius.app.spring.SpringAppEngineConfiguration;
import mobius.common.engine.impl.cfg.IdGenerator;
import mobius.common.engine.impl.interceptor.EngineConfigurationConstants;
import mobius.common.engine.impl.persistence.StrongUuidGenerator;
import mobius.engine.ProcessEngine;
import mobius.engine.ProcessEngineConfiguration;
import mobius.engine.RepositoryService;
import mobius.engine.cfg.HttpClientConfig;
import mobius.engine.impl.db.DbIdGenerator;
import mobius.engine.repository.Deployment;
import mobius.engine.repository.ProcessDefinition;
import mobius.idm.spring.SpringIdmEngineConfiguration;
import mobius.spring.SpringProcessEngineConfiguration;
import mobius.spring.boot.EngineConfigurationConfigurer;
import mobius.spring.boot.ProcessEngineAutoConfiguration;
import mobius.spring.boot.ProcessEngineServicesAutoConfiguration;
import mobius.spring.boot.app.AppEngineAutoConfiguration;
import mobius.spring.boot.app.AppEngineServicesAutoConfiguration;
import mobius.spring.boot.idm.IdmEngineAutoConfiguration;
import mobius.spring.boot.idm.IdmEngineServicesAutoConfiguration;
import mobius.spring.boot.process.Process;
import mobius.test.spring.boot.util.CustomUserEngineConfigurerConfiguration;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static mobius.test.spring.boot.util.DeploymentCleanerUtil.deleteDeployments;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 *
 */
public class ProcessEngineAutoConfigurationTest {
    
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(
            ProcessEngineServicesAutoConfiguration.class,
            ProcessEngineAutoConfiguration.class,
            DataSourceTransactionManagerAutoConfiguration.class,
            DataSourceAutoConfiguration.class,
            TransactionAutoConfiguration.class
        ))
        .withUserConfiguration(CustomUserEngineConfigurerConfiguration.class)
        .withClassLoader(new FilteredClassLoader(EntityManagerFactory.class));

    @Test
    public void httpProperties() {
        contextRunner.withPropertyValues(
            "flowable.http.useSystemProperties=true",
            "flowable.http.connectTimeout=PT0.250S",
            "flowable.http.socketTimeout=PT0.500S",
            "flowable.http.connectionRequestTimeout=PT1S",
            "flowable.http.requestRetryLimit=1",
            "flowable.http.disableCertVerify=true"
        ).run(context -> {
            ProcessEngine processEngine = context.getBean(ProcessEngine.class);
            HttpClientConfig httpClientConfig = processEngine.getProcessEngineConfiguration().getHttpClientConfig();

            assertThat(httpClientConfig.isUseSystemProperties()).isTrue();
            assertThat(httpClientConfig.getConnectTimeout()).isEqualTo(250);
            assertThat(httpClientConfig.getSocketTimeout()).isEqualTo(500);
            assertThat(httpClientConfig.getConnectionRequestTimeout()).isEqualTo(1000);
            assertThat(httpClientConfig.getRequestRetryLimit()).isEqualTo(1);
            assertThat(httpClientConfig.isDisableCertVerify()).isTrue();

            deleteDeployments(processEngine);
        });
    }

    @Test
    public void standaloneProcessEngineWithBasicDatasource() {
        contextRunner.run(context -> {
            assertThat(context).as("Process engine").hasSingleBean(ProcessEngine.class);
            assertThat(context)
                .doesNotHaveBean(AppEngine.class)
                .doesNotHaveBean(IdGenerator.class)
                .doesNotHaveBean("processAppEngineConfigurationConfigurer");

            ProcessEngine processEngine = context.getBean(ProcessEngine.class);

            assertThat(processEngine.getProcessEngineConfiguration().getIdGenerator()).isInstanceOf(StrongUuidGenerator.class);

            assertAllServicesPresent(context, processEngine);
            assertAutoDeployment(context);

            assertThat(context).hasSingleBean(CustomUserEngineConfigurerConfiguration.class)
                .getBean(CustomUserEngineConfigurerConfiguration.class)
                .satisfies(configuration -> {
                    assertThat(configuration.getInvokedConfigurations())
                        .containsExactly(
                            SpringProcessEngineConfiguration.class
                        );
                });

            deleteDeployments(processEngine);
        });
    }
    
    @Test
    public void processEngineWithBasicDataSourceAndAppEngine() {
        contextRunner.withConfiguration(AutoConfigurations.of(
            DataSourceTransactionManagerAutoConfiguration.class,
            AppEngineServicesAutoConfiguration.class,
            AppEngineAutoConfiguration.class,
            IdmEngineAutoConfiguration.class,
            IdmEngineServicesAutoConfiguration.class
        )).run(context -> {
            assertThat(context).hasBean("processAppEngineConfigurationConfigurer");
            AppEngine appEngine = context.getBean(AppEngine.class);
            assertThat(appEngine).as("App engine").isNotNull();
            ProcessEngineConfiguration processConfiguration = processEngine(appEngine);

            ProcessEngine processEngine = context.getBean(ProcessEngine.class);
            assertThat(processEngine.getProcessEngineConfiguration()).as("Proccess Engine Configuration").isEqualTo(processConfiguration);
            assertThat(processEngine).as("Process engine").isNotNull();

            assertAllServicesPresent(context, processEngine);
            assertAutoDeploymentWithAppEngine(context);

            assertThat(context).hasSingleBean(CustomUserEngineConfigurerConfiguration.class)
                .getBean(CustomUserEngineConfigurerConfiguration.class)
                .satisfies(configuration -> {
                    assertThat(configuration.getInvokedConfigurations())
                        .containsExactly(
                            SpringProcessEngineConfiguration.class,
                            SpringIdmEngineConfiguration.class,
                            SpringAppEngineConfiguration.class
                        );
                });

            deleteDeployments(appEngine);
            deleteDeployments(processEngine);
        });
    }

    @Test
    public void processEngineWithCustomIdGenerator() {
        contextRunner.withUserConfiguration(CustomIdGeneratorConfiguration.class)
            .run(context -> {
                assertThat(context).as("Process engine").hasSingleBean(ProcessEngine.class);
                assertThat(context).as("IdGenerator").doesNotHaveBean(IdGenerator.class);

                ProcessEngine processEngine = context.getBean(ProcessEngine.class);

                ProcessEngineConfiguration engineConfiguration = processEngine.getProcessEngineConfiguration();
                assertThat(engineConfiguration.getIdGenerator())
                    .isInstanceOfSatisfying(DbIdGenerator.class, dbIdGenerator -> {
                        assertThat(dbIdGenerator.getIdBlockSize()).isEqualTo(engineConfiguration.getIdBlockSize());
                        assertThat(dbIdGenerator.getCommandExecutor()).isEqualTo(engineConfiguration.getCommandExecutor());
                        assertThat(dbIdGenerator.getCommandConfig())
                            .isEqualToComparingFieldByField(engineConfiguration.getDefaultCommandConfig().transactionRequiresNew());
                    });
            });
    }

    @Test
    public void processEngineWithCustomIdGeneratorAsBean() {
        contextRunner.withUserConfiguration(CustomBeanIdGeneratorConfiguration.class)
            .run(context -> {
                assertThat(context)
                    .as("Process engine").hasSingleBean(ProcessEngine.class)
                    .as("Id generator").hasSingleBean(IdGenerator.class);

                ProcessEngine processEngine = context.getBean(ProcessEngine.class);

                ProcessEngineConfiguration engineConfiguration = processEngine.getProcessEngineConfiguration();
                assertThat(engineConfiguration.getIdGenerator())
                    .isInstanceOfSatisfying(DbIdGenerator.class, dbIdGenerator -> {
                        assertThat(dbIdGenerator.getIdBlockSize()).isEqualTo(engineConfiguration.getIdBlockSize());
                        assertThat(dbIdGenerator.getCommandExecutor()).isEqualTo(engineConfiguration.getCommandExecutor());
                        assertThat(dbIdGenerator.getCommandConfig())
                            .isEqualToComparingFieldByField(engineConfiguration.getDefaultCommandConfig().transactionRequiresNew());
                    })
                    .isEqualTo(context.getBean(IdGenerator.class));
            });
    }

    @Test
    public void processEngineWithMultipleCustomIdGeneratorsAsBean() {
        contextRunner.withUserConfiguration(
            CustomBeanIdGeneratorConfiguration.class,
            SecondCustomBeanIdGeneratorConfiguration.class
        ).run(context -> {
            assertThat(context)
                .as("Process engine").hasSingleBean(ProcessEngine.class)
                .as("Custom Id generator").hasBean("customIdGenerator")
                .as("Second Custom Id generator").hasBean("secondCustomIdGenerator");

            Map<String, IdGenerator> idGenerators = context.getBeansOfType(IdGenerator.class);
            assertThat(idGenerators).containsOnlyKeys("customIdGenerator", "secondCustomIdGenerator");

            IdGenerator customIdGenerator = idGenerators.get("customIdGenerator");
            assertThat(customIdGenerator).isInstanceOf(DbIdGenerator.class);

            IdGenerator secondCustomIdGenerator = idGenerators.get("secondCustomIdGenerator");
            assertThat(secondCustomIdGenerator).isInstanceOf(StrongUuidGenerator.class);

            ProcessEngine processEngine = context.getBean(ProcessEngine.class);

            ProcessEngineConfiguration engineConfiguration = processEngine.getProcessEngineConfiguration();
            assertThat(engineConfiguration.getIdGenerator())
                .isInstanceOf(StrongUuidGenerator.class)
                .isNotEqualTo(customIdGenerator)
                .isNotEqualTo(secondCustomIdGenerator);
        });
    }

    @Test
    public void processEngineWithMultipleCustomIdGeneratorsAndAQualifiedProcessOneAsBean() {
        contextRunner.withUserConfiguration(
            CustomBeanIdGeneratorConfiguration.class,
            SecondCustomBeanIdGeneratorConfiguration.class,
            ProcessQualifiedCustomBeanIdGeneratorConfiguration.class
        ).run(context -> {
            assertThat(context)
                .as("Process engine").hasSingleBean(ProcessEngine.class)
                .as("Custom Id generator").hasBean("customIdGenerator")
                .as("Second Custom Id generator").hasBean("secondCustomIdGenerator")
                .as("Process Custom Id generator").hasBean("processQualifiedCustomIdGenerator");

            Map<String, IdGenerator> idGenerators = context.getBeansOfType(IdGenerator.class);
            assertThat(idGenerators).containsOnlyKeys(
                "customIdGenerator",
                "secondCustomIdGenerator",
                "processQualifiedCustomIdGenerator"
            );

            IdGenerator customIdGenerator = idGenerators.get("customIdGenerator");
            assertThat(customIdGenerator).isInstanceOf(DbIdGenerator.class);

            IdGenerator secondCustomIdGenerator = idGenerators.get("secondCustomIdGenerator");
            assertThat(secondCustomIdGenerator).isInstanceOf(StrongUuidGenerator.class);

            IdGenerator processCustomIdGenerator = idGenerators.get("processQualifiedCustomIdGenerator");
            assertThat(processCustomIdGenerator).isInstanceOf(StrongUuidGenerator.class);

            ProcessEngine processEngine = context.getBean(ProcessEngine.class);

            ProcessEngineConfiguration engineConfiguration = processEngine.getProcessEngineConfiguration();
            assertThat(engineConfiguration.getIdGenerator())
                .isInstanceOf(StrongUuidGenerator.class)
                .isNotEqualTo(customIdGenerator)
                .isNotEqualTo(secondCustomIdGenerator)
                .isEqualTo(processCustomIdGenerator);
        });
    }

    private void assertAllServicesPresent(ApplicationContext context, ProcessEngine processEngine) {
        List<Method> methods = Stream.of(ProcessEngine.class.getDeclaredMethods())
            .filter(method -> !(method.getName().equals("close") || method.getName().equals("getName"))).collect(Collectors.toList());

        assertThat(methods).allSatisfy(method -> {
            try {
                assertThat(context.getBean(method.getReturnType())).as(method.getReturnType() + " bean").isEqualTo(method.invoke(processEngine));
            } catch (IllegalAccessException | InvocationTargetException e) {
                fail("Failed to invoke method " + method, e);
            }
        });
    }

    private void assertAutoDeployment(ApplicationContext context) {
        RepositoryService repositoryService = context.getBean(RepositoryService.class);

        List<ProcessDefinition> definitions = repositoryService.createProcessDefinitionQuery().orderByProcessDefinitionKey().asc().list();
        assertThat(definitions)
            .extracting(ProcessDefinition::getKey)
            .containsExactly("integrationGatewayProcess", "waiter");
        List<Deployment> deployments = repositoryService.createDeploymentQuery().list();

        assertThat(deployments)
            .hasSize(1)
            .first()
            .satisfies(deployment -> assertThat(deployment.getName()).isEqualTo("SpringBootAutoDeployment"));
    }
    
    private void assertAutoDeploymentWithAppEngine(ApplicationContext context) {
        RepositoryService repositoryService = context.getBean(RepositoryService.class);

        List<ProcessDefinition> definitions = repositoryService.createProcessDefinitionQuery().orderByProcessDefinitionKey().asc().list();
        assertThat(definitions)
            .extracting(ProcessDefinition::getKey)
            .containsExactly("inclusiveGateway", "integrationGatewayProcess", "simpleTasks", "vacationRequest", "waiter");
        
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().latestVersion().processDefinitionKey("simpleTasks").singleResult();
        assertThat(processDefinition.getVersion()).isOne();
        
        processDefinition = repositoryService.createProcessDefinitionQuery().latestVersion().processDefinitionKey("integrationGatewayProcess").singleResult();
        assertThat(processDefinition.getVersion()).isOne();
        
        List<Deployment> deployments = repositoryService.createDeploymentQuery().list();

        assertThat(deployments).hasSize(4)
            .extracting(Deployment::getName)
            .contains("SpringBootAutoDeployment", "simple.bar", "vacationRequest.zip", "processTask.bar");
        
        AppRepositoryService appRepositoryService = context.getBean(AppRepositoryService.class);
        List<AppDefinition> appDefinitions = appRepositoryService.createAppDefinitionQuery().list();
        
        assertThat(appDefinitions)
            .extracting(AppDefinition::getKey)
            .contains("simpleApp", "vacationRequestApp");
        
        AppDefinition appDefinition = appRepositoryService.createAppDefinitionQuery().latestVersion().appDefinitionKey("simpleApp").singleResult();
        assertThat(appDefinition.getVersion()).isOne();
        
        appDefinition = appRepositoryService.createAppDefinitionQuery().latestVersion().appDefinitionKey("vacationRequestApp").singleResult();
        assertThat(appDefinition.getVersion()).isOne();
        
        List<AppDeployment> appDeployments = appRepositoryService.createDeploymentQuery().list();
        assertThat(appDeployments).hasSize(3)
            .extracting(AppDeployment::getName)
            .contains("simple.bar", "vacationRequest.zip", "processTask.bar");
    }
    
    private static ProcessEngineConfiguration processEngine(AppEngine appEngine) {
        AppEngineConfiguration appEngineConfiguration = appEngine.getAppEngineConfiguration();
        return (ProcessEngineConfiguration) appEngineConfiguration.getEngineConfigurations().get(
                EngineConfigurationConstants.KEY_PROCESS_ENGINE_CONFIG);
    }

    @Configuration
    static class CustomIdGeneratorConfiguration {

        @Bean
        public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> customIdGeneratorConfigurer() {
            return engineConfiguration -> engineConfiguration.setIdGenerator(new DbIdGenerator());
        }
    }

    @Configuration
    static class CustomBeanIdGeneratorConfiguration {

        @Bean
        public IdGenerator customIdGenerator() {
            return new DbIdGenerator();
        }
    }

    @Configuration
    static class SecondCustomBeanIdGeneratorConfiguration {

        @Bean
        public IdGenerator secondCustomIdGenerator() {
            return new StrongUuidGenerator();
        }
    }

    @Configuration
    static class ProcessQualifiedCustomBeanIdGeneratorConfiguration {

        @Bean
        @Process
        public IdGenerator processQualifiedCustomIdGenerator() {
            return new StrongUuidGenerator();
        }
    }
}
