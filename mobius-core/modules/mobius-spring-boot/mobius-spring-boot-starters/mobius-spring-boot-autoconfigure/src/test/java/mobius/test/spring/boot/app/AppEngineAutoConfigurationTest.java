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
package mobius.test.spring.boot.app;

import mobius.app.api.AppRepositoryService;
import mobius.app.api.repository.AppDefinition;
import mobius.app.api.repository.AppDeployment;
import mobius.app.engine.AppEngine;
import mobius.app.engine.AppEngineConfiguration;
import mobius.app.spring.SpringAppEngineConfiguration;
import mobius.common.engine.impl.interceptor.EngineConfigurationConstants;
import mobius.engine.ProcessEngine;
import mobius.engine.ProcessEngineConfiguration;
import mobius.engine.impl.db.DbIdGenerator;
import mobius.engine.runtime.ProcessInstance;
import mobius.idm.spring.SpringIdmEngineConfiguration;
import mobius.spring.SpringProcessEngineConfiguration;
import mobius.spring.boot.EngineConfigurationConfigurer;
import mobius.spring.boot.ProcessEngineAutoConfiguration;
import mobius.spring.boot.ProcessEngineServicesAutoConfiguration;
import mobius.spring.boot.app.AppEngineAutoConfiguration;
import mobius.spring.boot.app.AppEngineServicesAutoConfiguration;
import mobius.spring.boot.idm.IdmEngineAutoConfiguration;
import mobius.spring.boot.idm.IdmEngineServicesAutoConfiguration;
import mobius.task.api.Task;
import mobius.test.spring.boot.util.CustomUserEngineConfigurerConfiguration;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static mobius.test.spring.boot.util.DeploymentCleanerUtil.deleteDeployments;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 *
 *
 */
public class AppEngineAutoConfigurationTest {

    private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(
            DataSourceAutoConfiguration.class,
            DataSourceTransactionManagerAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class,
            AppEngineServicesAutoConfiguration.class,
            AppEngineAutoConfiguration.class,
            IdmEngineAutoConfiguration.class,
            IdmEngineServicesAutoConfiguration.class
        ))
        .withUserConfiguration(CustomUserEngineConfigurerConfiguration.class);

    @Test
    public void standaloneAppEngineWithBasicDatasource() {
        contextRunner
            .run(context -> {
                AppEngine appEngine = context.getBean(AppEngine.class);
                assertThat(appEngine).as("App engine").isNotNull();

                assertAllServicesPresent(context, appEngine);
                assertAutoDeployment(context);

                deleteDeployments(appEngine);

                assertThat(context).hasSingleBean(CustomUserEngineConfigurerConfiguration.class)
                    .getBean(CustomUserEngineConfigurerConfiguration.class)
                    .satisfies(configuration -> {
                        assertThat(configuration.getInvokedConfigurations())
                            .containsExactly(
                                SpringIdmEngineConfiguration.class,
                                SpringAppEngineConfiguration.class
                            );
                    });
            });
    }
    
    @Test
    public void appEngineWithBasicDataSourceAndProcessEngine() {
        contextRunner.withConfiguration(AutoConfigurations.of(
            ProcessEngineServicesAutoConfiguration.class,
            ProcessEngineAutoConfiguration.class
        )).run(context -> {
            AppEngine appEngine = context.getBean(AppEngine.class);
            assertThat(appEngine).as("App engine").isNotNull();
            ProcessEngineConfiguration processConfiguration = processEngine(appEngine);

        ProcessEngine processEngine = context.getBean(ProcessEngine.class);
        ProcessEngineConfiguration processEngineConfiguration =processEngine.getProcessEngineConfiguration();
        assertThat(processEngineConfiguration).as("Proccess Engine Configuration").isEqualTo(processConfiguration);
        assertThat(processEngine).as("Process engine").isNotNull();

            assertAllServicesPresent(context, appEngine);
            assertAutoDeployment(context);

            processEngineConfiguration.getIdentityService().setAuthenticatedUserId("test");
            ProcessInstance processInstance = processEngineConfiguration.getRuntimeService().startProcessInstanceByKey("vacationRequest");
            Task task = processEngineConfiguration.getTaskService().createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
            assertThat(task).isNotNull();

            deleteDeployments(appEngine);
            deleteDeployments(processEngine);

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
        });
    }

    @Test
    public void appEngineWithProcessEngineAndTaskIdGenerator() {
        contextRunner.withUserConfiguration(CustomIdGeneratorConfiguration.class
        ).withConfiguration(AutoConfigurations.of(
            ProcessEngineServicesAutoConfiguration.class,
            ProcessEngineAutoConfiguration.class
        )).run(context -> {
            ProcessEngine processEngine = context.getBean(ProcessEngine.class);
            ProcessEngineConfiguration processEngineConfiguration = processEngine.getProcessEngineConfiguration();
            assertThat(processEngineConfiguration.getIdGenerator().getNextId()).as("Process id generator must be DB id generator").doesNotContain("-");
            
            AppEngine appEngine = context.getBean(AppEngine.class);
            deleteDeployments(appEngine);
            deleteDeployments(processEngine);
        });
    }

    private void assertAllServicesPresent(ApplicationContext context, AppEngine appEngine) {
        List<Method> methods = Stream.of(AppEngine.class.getDeclaredMethods())
            .filter(method -> !(method.getName().equals("close") || method.getName().equals("getName"))).collect(Collectors.toList());

        assertThat(methods).allSatisfy(method -> {
            try {
                assertThat(context.getBean(method.getReturnType())).as(method.getReturnType() + " bean").isEqualTo(method.invoke(appEngine));
            } catch (IllegalAccessException | InvocationTargetException e) {
                fail("Failed to invoke method " + method, e);
            }
        });
    }

    private void assertAutoDeployment(ApplicationContext context) {
        AppRepositoryService appRepositoryService = context.getBean(AppRepositoryService.class);

        List<AppDefinition> definitions = appRepositoryService.createAppDefinitionQuery().orderByAppDefinitionKey().asc().list();
        assertThat(definitions)
            .extracting(AppDefinition::getKey)
            .containsExactlyInAnyOrder("simpleApp", "vacationRequestApp");
        List<AppDeployment> deployments = appRepositoryService.createDeploymentQuery().orderByDeploymentName().asc().list();

        assertThat(deployments)
            .hasSize(3)
            .first()
            .satisfies(deployment -> assertThat(deployment.getName()).isEqualTo("processTask.bar"));
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

}
