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
package mobius.test.spring.boot.cmmn;

import mobius.app.api.AppRepositoryService;
import mobius.app.api.repository.AppDefinition;
import mobius.app.api.repository.AppDeployment;
import mobius.app.engine.AppEngine;
import mobius.app.engine.AppEngineConfiguration;
import mobius.app.spring.SpringAppEngineConfiguration;
import mobius.cmmn.api.CmmnEngineConfigurationApi;
import mobius.cmmn.api.CmmnRepositoryService;
import mobius.cmmn.api.repository.CaseDefinition;
import mobius.cmmn.api.repository.CmmnDeployment;
import mobius.cmmn.engine.CmmnEngine;
import mobius.cmmn.engine.HttpClientConfig;
import mobius.cmmn.spring.SpringCmmnEngineConfiguration;
import mobius.engine.ProcessEngine;
import mobius.engine.ProcessEngineConfiguration;
import mobius.engine.impl.util.EngineServiceUtil;
import mobius.idm.spring.SpringIdmEngineConfiguration;
import mobius.spring.SpringProcessEngineConfiguration;
import mobius.spring.boot.ProcessEngineAutoConfiguration;
import mobius.spring.boot.ProcessEngineServicesAutoConfiguration;
import mobius.spring.boot.app.AppEngineAutoConfiguration;
import mobius.spring.boot.app.AppEngineServicesAutoConfiguration;
import mobius.spring.boot.cmmn.CmmnEngineAutoConfiguration;
import mobius.spring.boot.cmmn.CmmnEngineServicesAutoConfiguration;
import mobius.spring.boot.idm.IdmEngineAutoConfiguration;
import mobius.spring.boot.idm.IdmEngineServicesAutoConfiguration;
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
 * @author Filip Hrisafov
 */
public class CmmnEngineAutoConfigurationTest {

    private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(
            DataSourceAutoConfiguration.class,
            DataSourceTransactionManagerAutoConfiguration.class,
            IdmEngineAutoConfiguration.class,
            IdmEngineServicesAutoConfiguration.class,
            CmmnEngineServicesAutoConfiguration.class,
            CmmnEngineAutoConfiguration.class
        ))
        .withUserConfiguration(CustomUserEngineConfigurerConfiguration.class);

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
            CmmnEngine cmmnEngine = context.getBean(CmmnEngine.class);
            HttpClientConfig httpClientConfig = cmmnEngine.getCmmnEngineConfiguration().getHttpClientConfig();

            assertThat(httpClientConfig.isUseSystemProperties()).isTrue();
            assertThat(httpClientConfig.getConnectTimeout()).isEqualTo(250);
            assertThat(httpClientConfig.getSocketTimeout()).isEqualTo(500);
            assertThat(httpClientConfig.getConnectionRequestTimeout()).isEqualTo(1000);
            assertThat(httpClientConfig.getRequestRetryLimit()).isEqualTo(1);
            assertThat(httpClientConfig.isDisableCertVerify()).isTrue();

            deleteDeployments(cmmnEngine);
        });
    }

    @Test
    public void standaloneCmmnEngineWithBasicDataSource() {
        contextRunner.run(context -> {
            assertThat(context)
                .doesNotHaveBean(AppEngine.class)
                .doesNotHaveBean(ProcessEngine.class)
                .doesNotHaveBean("cmmnProcessEngineConfigurationConfigurer")
                .doesNotHaveBean("cmmnAppEngineConfigurationConfigurer");
            CmmnEngine cmmnEngine = context.getBean(CmmnEngine.class);
            assertThat(cmmnEngine).as("Cmmn engine").isNotNull();

            assertAllServicesPresent(context, cmmnEngine);

            assertAutoDeployment(context);

            assertThat(context).hasSingleBean(CustomUserEngineConfigurerConfiguration.class)
                .getBean(CustomUserEngineConfigurerConfiguration.class)
                .satisfies(configuration -> {
                    assertThat(configuration.getInvokedConfigurations())
                        .containsExactlyInAnyOrder(
                            SpringCmmnEngineConfiguration.class,
                            SpringIdmEngineConfiguration.class
                        );
                });

            deleteDeployments(cmmnEngine);
        });
    }

    @Test
    public void cmmnEngineWithBasicDataSourceAndProcessEngine() {
        contextRunner.withConfiguration(AutoConfigurations.of(
            HibernateJpaAutoConfiguration.class,
            ProcessEngineServicesAutoConfiguration.class,
            ProcessEngineAutoConfiguration.class
        )).run(context -> {
            assertThat(context)
                .doesNotHaveBean(AppEngine.class)
                .hasBean("cmmnProcessEngineConfigurationConfigurer")
                .doesNotHaveBean("cmmnAppEngineConfigurationConfigurer");
            ProcessEngine processEngine = context.getBean(ProcessEngine.class);
            assertThat(processEngine).as("Process engine").isNotNull();
            CmmnEngineConfigurationApi cmmnProcessConfigurationApi = cmmnEngine(processEngine);

            CmmnEngine cmmnEngine = context.getBean(CmmnEngine.class);
            assertThat(cmmnEngine.getCmmnEngineConfiguration()).as("Cmmn Engine Configuration").isEqualTo(cmmnProcessConfigurationApi);
            assertThat(cmmnEngine).as("Cmmn engine").isNotNull();

            assertAllServicesPresent(context, cmmnEngine);
            assertAutoDeployment(context);

            assertThat(context).hasSingleBean(CustomUserEngineConfigurerConfiguration.class)
                .getBean(CustomUserEngineConfigurerConfiguration.class)
                .satisfies(configuration -> {
                    assertThat(configuration.getInvokedConfigurations())
                        .containsExactlyInAnyOrder(
                            SpringCmmnEngineConfiguration.class,
                            SpringIdmEngineConfiguration.class,
                            SpringProcessEngineConfiguration.class
                        );
                });

            deleteDeployments(processEngine);
            deleteDeployments(cmmnEngine);
        });
    }
    
    @Test
    public void cmmnEngineWithBasicDataSourceAndAppEngine() {
        contextRunner.withConfiguration(AutoConfigurations.of(
            HibernateJpaAutoConfiguration.class,
            AppEngineServicesAutoConfiguration.class,
            AppEngineAutoConfiguration.class,
            ProcessEngineServicesAutoConfiguration.class,
            ProcessEngineAutoConfiguration.class
        )).run(context -> {
            assertThat(context)
                .doesNotHaveBean("cmmnProcessEngineConfigurationConfigurer")
                .hasBean("cmmnAppEngineConfigurationConfigurer");
            AppEngine appEngine = context.getBean(AppEngine.class);
            assertThat(appEngine).as("App engine").isNotNull();
            CmmnEngineConfigurationApi cmmnProcessConfigurationApi = cmmnEngine(appEngine);

            CmmnEngine cmmnEngine = context.getBean(CmmnEngine.class);
            assertThat(cmmnEngine.getCmmnEngineConfiguration()).as("Cmmn Engine Configuration").isEqualTo(cmmnProcessConfigurationApi);
            assertThat(cmmnEngine).as("Cmmn engine").isNotNull();

            assertAllServicesPresent(context, cmmnEngine);
            assertAutoDeploymentWithAppEngine(context);

            assertThat(context).hasSingleBean(CustomUserEngineConfigurerConfiguration.class)
                .getBean(CustomUserEngineConfigurerConfiguration.class)
                .satisfies(configuration -> {
                    assertThat(configuration.getInvokedConfigurations())
                        .containsExactlyInAnyOrder(
                            SpringProcessEngineConfiguration.class,
                            SpringCmmnEngineConfiguration.class,
                            SpringIdmEngineConfiguration.class,
                            SpringAppEngineConfiguration.class
                        );
                });

            deleteDeployments(appEngine);
            deleteDeployments(context.getBean(ProcessEngine.class));
            deleteDeployments(cmmnEngine);
        });
    }

    private void assertAllServicesPresent(ApplicationContext context, CmmnEngine cmmnEngine) {
        List<Method> methods = Stream.of(CmmnEngine.class.getDeclaredMethods())
            .filter(method -> !(method.getName().equals("close") || method.getName().equals("getName"))).collect(Collectors.toList());

        assertThat(methods).allSatisfy(method -> {
            try {
                assertThat(context.getBean(method.getReturnType())).as(method.getReturnType() + " bean").isEqualTo(method.invoke(cmmnEngine));
            } catch (IllegalAccessException | InvocationTargetException e) {
                fail("Failed to invoke method " + method, e);
            }
        });
    }

    private void assertAutoDeployment(ApplicationContext context) {
        CmmnRepositoryService repositoryService = context.getBean(CmmnRepositoryService.class);

        List<CaseDefinition> caseDefinitions = repositoryService.createCaseDefinitionQuery().orderByCaseDefinitionKey().asc().list();
        assertThat(caseDefinitions)
            .extracting(CaseDefinition::getKey)
            .containsExactly("case1", "case2", "case3", "case4");
        List<CmmnDeployment> deployments = repositoryService.createDeploymentQuery().list();

        assertThat(deployments)
            .hasSize(1)
            .first()
            .satisfies(deployment -> assertThat(deployment.getName()).isEqualTo("SpringBootAutoDeployment"));
    }

    private void assertAutoDeploymentWithAppEngine(ApplicationContext context) {
        CmmnRepositoryService repositoryService = context.getBean(CmmnRepositoryService.class);

        List<CaseDefinition> caseDefinitions = repositoryService.createCaseDefinitionQuery().orderByCaseDefinitionKey().asc().list();
        assertThat(caseDefinitions)
            .extracting(CaseDefinition::getKey)
            .contains("case1", "case2", "case3", "case4", "caseB");
        
        CaseDefinition caseDefinition = repositoryService.createCaseDefinitionQuery().latestVersion().caseDefinitionKey("case2").singleResult();
        assertThat(caseDefinition.getVersion()).isOne();
        
        caseDefinition = repositoryService.createCaseDefinitionQuery().latestVersion().caseDefinitionKey("caseB").singleResult();
        assertThat(caseDefinition.getVersion()).isOne();
        
        List<CmmnDeployment> deployments = repositoryService.createDeploymentQuery().list();

        assertThat(deployments).hasSize(3)
            .extracting(CmmnDeployment::getName)
            .contains("SpringBootAutoDeployment", "simple.bar", "processTask.bar");
        
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

    private static CmmnEngineConfigurationApi cmmnEngine(ProcessEngine processEngine) {
        ProcessEngineConfiguration processEngineConfiguration = processEngine.getProcessEngineConfiguration();
        return EngineServiceUtil.getCmmnEngineConfiguration(processEngineConfiguration);
    }
    
    private static CmmnEngineConfigurationApi cmmnEngine(AppEngine appEngine) {
        AppEngineConfiguration appEngineConfiguration = appEngine.getAppEngineConfiguration();
        return EngineServiceUtil.getCmmnEngineConfiguration(appEngineConfiguration);
    }
}