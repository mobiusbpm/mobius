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
package mobius.test.spring.boot.content;

import mobius.app.engine.AppEngine;
import mobius.app.engine.AppEngineConfiguration;
import mobius.app.spring.SpringAppEngineConfiguration;
import mobius.content.api.ContentEngineConfigurationApi;
import mobius.content.engine.ContentEngine;
import mobius.content.spring.SpringContentEngineConfiguration;
import mobius.engine.ProcessEngine;
import mobius.engine.ProcessEngineConfiguration;
import mobius.engine.impl.util.EngineServiceUtil;
import mobius.spring.SpringProcessEngineConfiguration;
import mobius.spring.boot.ProcessEngineAutoConfiguration;
import mobius.spring.boot.ProcessEngineServicesAutoConfiguration;
import mobius.spring.boot.app.AppEngineAutoConfiguration;
import mobius.spring.boot.app.AppEngineServicesAutoConfiguration;
import mobius.spring.boot.content.ContentEngineAutoConfiguration;
import mobius.spring.boot.content.ContentEngineServicesAutoConfiguration;
import mobius.test.spring.boot.util.CustomUserEngineConfigurerConfiguration;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ApplicationContext;

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
 */
public class ContentEngineAutoConfigurationTest {

    private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(
            DataSourceAutoConfiguration.class,
            DataSourceTransactionManagerAutoConfiguration.class,
            ContentEngineServicesAutoConfiguration.class,
            ContentEngineAutoConfiguration.class
        ))
        .withUserConfiguration(CustomUserEngineConfigurerConfiguration.class);

    @Test
    public void standaloneContentEngineWithBasicDataSource() {
        contextRunner.run(context -> {
            assertThat(context)
                .doesNotHaveBean(AppEngine.class)
                .doesNotHaveBean(ProcessEngine.class)
                .doesNotHaveBean("contentProcessEngineConfigurationConfigurer")
                .doesNotHaveBean("contentAppEngineConfigurationConfigurer");
            ContentEngine contentEngine = context.getBean(ContentEngine.class);
            assertThat(contentEngine).as("Content engine").isNotNull();
            assertAllServicesPresent(context, contentEngine);

            assertThat(context).hasSingleBean(CustomUserEngineConfigurerConfiguration.class)
                .getBean(CustomUserEngineConfigurerConfiguration.class)
                .satisfies(configuration -> {
                    assertThat(configuration.getInvokedConfigurations())
                        .containsExactly(
                            SpringContentEngineConfiguration.class
                        );
                });
        });
    }

    @Test
    public void contentEngineWithBasicDataSourceAndProcessEngine() {
        contextRunner.withConfiguration(AutoConfigurations.of(
            HibernateJpaAutoConfiguration.class,
            ProcessEngineAutoConfiguration.class,
            ProcessEngineServicesAutoConfiguration.class
        )).run(context -> {
            assertThat(context)
                .doesNotHaveBean(AppEngine.class)
                .hasBean("contentProcessEngineConfigurationConfigurer")
                .doesNotHaveBean("contentAppEngineConfigurationConfigurer");
            ProcessEngine processEngine = context.getBean(ProcessEngine.class);
            assertThat(processEngine).as("Process engine").isNotNull();
            ContentEngineConfigurationApi contentProcessConfigurationApi = contentEngine(processEngine);

            ContentEngine contentEngine = context.getBean(ContentEngine.class);
            assertThat(contentEngine).as("Content engine").isNotNull();

            assertThat(contentEngine.getContentEngineConfiguration()).as("Content Engine Configuration").isEqualTo(contentProcessConfigurationApi);

            assertAllServicesPresent(context, contentEngine);

            assertThat(context).hasSingleBean(CustomUserEngineConfigurerConfiguration.class)
                .getBean(CustomUserEngineConfigurerConfiguration.class)
                .satisfies(configuration -> {
                    assertThat(configuration.getInvokedConfigurations())
                        .containsExactly(
                            SpringContentEngineConfiguration.class,
                            SpringProcessEngineConfiguration.class
                        );
                });

            deleteDeployments(processEngine);
        });
    }
    
    @Test
    public void contentEngineWithBasicDataSourceAndAppEngine() {

        contextRunner.withConfiguration(AutoConfigurations.of(
            HibernateJpaAutoConfiguration.class,
            AppEngineServicesAutoConfiguration.class,
            AppEngineAutoConfiguration.class,
            ProcessEngineServicesAutoConfiguration.class,
            ProcessEngineAutoConfiguration.class
        )).run(context -> {
            assertThat(context)
                .doesNotHaveBean("contentProcessEngineConfigurationConfigurer")
                .hasBean("contentAppEngineConfigurationConfigurer");
            AppEngine appEngine = context.getBean(AppEngine.class);
            assertThat(appEngine).as("App engine").isNotNull();
            ContentEngineConfigurationApi contentProcessConfigurationApi = contentEngine(appEngine);

            ContentEngine contentEngine = context.getBean(ContentEngine.class);
            assertThat(contentEngine).as("Content engine").isNotNull();

            assertThat(contentEngine.getContentEngineConfiguration()).as("Content Engine Configuration").isEqualTo(contentProcessConfigurationApi);

            assertAllServicesPresent(context, contentEngine);

            assertThat(context).hasSingleBean(CustomUserEngineConfigurerConfiguration.class)
                .getBean(CustomUserEngineConfigurerConfiguration.class)
                .satisfies(configuration -> {
                    assertThat(configuration.getInvokedConfigurations())
                        .containsExactly(
                            SpringProcessEngineConfiguration.class,
                            SpringContentEngineConfiguration.class,
                            SpringAppEngineConfiguration.class
                        );
                });

            deleteDeployments(appEngine);
            deleteDeployments(context.getBean(ProcessEngine.class));
        });
    }

    private void assertAllServicesPresent(ApplicationContext context, ContentEngine contentEngine) {
        List<Method> methods = Stream.of(ContentEngine.class.getDeclaredMethods())
            .filter(method -> !(method.getName().equals("close") || method.getName().equals("getName")))
            .collect(Collectors.toList());

        assertThat(methods).allSatisfy(method -> {
            try {
                assertThat(context.getBean(method.getReturnType()))
                    .as(method.getReturnType() + " bean")
                    .isEqualTo(method.invoke(contentEngine));
            } catch (IllegalAccessException | InvocationTargetException e) {
                fail("Failed to invoke method " + method, e);
            }
        });
    }

    private static ContentEngineConfigurationApi contentEngine(ProcessEngine processEngine) {
        ProcessEngineConfiguration processEngineConfiguration = processEngine.getProcessEngineConfiguration();
        return EngineServiceUtil.getContentEngineConfiguration(processEngineConfiguration);
    }
    
    private static ContentEngineConfigurationApi contentEngine(AppEngine appEngine) {
        AppEngineConfiguration appEngineConfiguration = appEngine.getAppEngineConfiguration();
        return EngineServiceUtil.getContentEngineConfiguration(appEngineConfiguration);
    }
}
