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
package mobius.test.spring.boot;

import mobius.app.engine.AppEngine;
import mobius.app.spring.SpringAppEngineConfiguration;
import mobius.app.spring.SpringAppExpressionManager;
import mobius.cmmn.api.CmmnHistoryService;
import mobius.cmmn.api.CmmnRuntimeService;
import mobius.cmmn.api.runtime.CaseInstance;
import mobius.cmmn.api.runtime.PlanItemInstance;
import mobius.cmmn.api.runtime.PlanItemInstanceState;
import mobius.cmmn.engine.CmmnEngine;
import mobius.cmmn.spring.SpringCmmnEngineConfiguration;
import mobius.cmmn.spring.SpringCmmnExpressionManager;
import mobius.cmmn.spring.configurator.SpringCmmnEngineConfigurator;
import mobius.common.engine.impl.cfg.SpringBeanFactoryProxyMap;
import mobius.common.engine.impl.interceptor.EngineConfigurationConstants;
import mobius.content.engine.ContentEngine;
import mobius.content.spring.SpringContentEngineConfiguration;
import mobius.content.spring.configurator.SpringContentEngineConfigurator;
import mobius.dmn.engine.DmnEngine;
import mobius.dmn.spring.SpringDmnEngineConfiguration;
import mobius.dmn.spring.SpringDmnExpressionManager;
import mobius.dmn.spring.configurator.SpringDmnEngineConfigurator;
import mobius.engine.ProcessEngine;
import mobius.engine.RuntimeService;
import mobius.engine.TaskService;
import mobius.engine.spring.configurator.SpringProcessEngineConfigurator;
import mobius.form.engine.FormEngine;
import mobius.form.spring.SpringFormEngineConfiguration;
import mobius.form.spring.SpringFormExpressionManager;
import mobius.form.spring.configurator.SpringFormEngineConfigurator;
import mobius.idm.engine.IdmEngine;
import mobius.idm.spring.SpringIdmEngineConfiguration;
import mobius.idm.spring.configurator.SpringIdmEngineConfigurator;
import mobius.spring.SpringExpressionManager;
import mobius.spring.SpringProcessEngineConfiguration;
import mobius.spring.boot.ProcessEngineAutoConfiguration;
import mobius.spring.boot.ProcessEngineServicesAutoConfiguration;
import mobius.spring.boot.app.AppEngineAutoConfiguration;
import mobius.spring.boot.app.AppEngineServicesAutoConfiguration;
import mobius.spring.boot.cmmn.CmmnEngineAutoConfiguration;
import mobius.spring.boot.cmmn.CmmnEngineServicesAutoConfiguration;
import mobius.spring.boot.content.ContentEngineAutoConfiguration;
import mobius.spring.boot.content.ContentEngineServicesAutoConfiguration;
import mobius.spring.boot.dmn.DmnEngineAutoConfiguration;
import mobius.spring.boot.dmn.DmnEngineServicesAutoConfiguration;
import mobius.spring.boot.form.FormEngineAutoConfiguration;
import mobius.spring.boot.form.FormEngineServicesAutoConfiguration;
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

import java.util.List;

import static mobius.test.spring.boot.util.DeploymentCleanerUtil.deleteDeployments;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.Assert.assertEquals;

/**
 * @author Filip Hrisafov
 */
public class AllEnginesAutoConfigurationTest {

    private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(
            DataSourceAutoConfiguration.class,
            DataSourceTransactionManagerAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class,
            AppEngineServicesAutoConfiguration.class,
            AppEngineAutoConfiguration.class,
            IdmEngineAutoConfiguration.class,
            IdmEngineServicesAutoConfiguration.class,
            CmmnEngineAutoConfiguration.class,
            CmmnEngineServicesAutoConfiguration.class,
            ContentEngineAutoConfiguration.class,
            ContentEngineServicesAutoConfiguration.class,
            DmnEngineAutoConfiguration.class,
            DmnEngineServicesAutoConfiguration.class,
            FormEngineAutoConfiguration.class,
            FormEngineServicesAutoConfiguration.class,
            ProcessEngineAutoConfiguration.class,
            ProcessEngineServicesAutoConfiguration.class
        ))
        .withUserConfiguration(CustomUserEngineConfigurerConfiguration.class);

    @Test
    public void usingAllAutoConfigurationsTogetherShouldWorkCorrectly() {
        contextRunner.run(context -> {
            assertThat(context)
                .hasSingleBean(AppEngine.class)
                .hasSingleBean(CmmnEngine.class)
                .hasSingleBean(ContentEngine.class)
                .hasSingleBean(DmnEngine.class)
                .hasSingleBean(FormEngine.class)
                .hasSingleBean(IdmEngine.class)
                .hasSingleBean(ProcessEngine.class)
                .hasSingleBean(SpringAppEngineConfiguration.class)
                .hasSingleBean(SpringCmmnEngineConfiguration.class)
                .hasSingleBean(SpringContentEngineConfiguration.class)
                .hasSingleBean(SpringDmnEngineConfiguration.class)
                .hasSingleBean(SpringFormEngineConfiguration.class)
                .hasSingleBean(SpringIdmEngineConfiguration.class)
                .hasSingleBean(SpringProcessEngineConfiguration.class)
                .hasSingleBean(SpringCmmnEngineConfigurator.class)
                .hasSingleBean(SpringContentEngineConfigurator.class)
                .hasSingleBean(SpringDmnEngineConfigurator.class)
                .hasSingleBean(SpringFormEngineConfigurator.class)
                .hasSingleBean(SpringIdmEngineConfigurator.class)
                .hasSingleBean(SpringProcessEngineConfigurator.class);

            SpringAppEngineConfiguration appEngineConfiguration = context.getBean(SpringAppEngineConfiguration.class);
            SpringCmmnEngineConfiguration cmmnEngineConfiguration = context.getBean(SpringCmmnEngineConfiguration.class);
            SpringContentEngineConfiguration contentEngineConfiguration = context.getBean(SpringContentEngineConfiguration.class);
            SpringDmnEngineConfiguration dmnEngineConfiguration = context.getBean(SpringDmnEngineConfiguration.class);
            SpringFormEngineConfiguration formEngineConfiguration = context.getBean(SpringFormEngineConfiguration.class);
            SpringIdmEngineConfiguration idmEngineConfiguration = context.getBean(SpringIdmEngineConfiguration.class);
            SpringProcessEngineConfiguration processEngineConfiguration = context.getBean(SpringProcessEngineConfiguration.class);

            assertThat(appEngineConfiguration.getEngineConfigurations())
                .as("AppEngine configurations")
                .containsOnly(
                    entry(EngineConfigurationConstants.KEY_APP_ENGINE_CONFIG, appEngineConfiguration),
                    entry(EngineConfigurationConstants.KEY_CMMN_ENGINE_CONFIG, cmmnEngineConfiguration),
                    entry(EngineConfigurationConstants.KEY_DMN_ENGINE_CONFIG, dmnEngineConfiguration),
                    entry(EngineConfigurationConstants.KEY_CONTENT_ENGINE_CONFIG, contentEngineConfiguration),
                    entry(EngineConfigurationConstants.KEY_FORM_ENGINE_CONFIG, formEngineConfiguration),
                    entry(EngineConfigurationConstants.KEY_IDM_ENGINE_CONFIG, idmEngineConfiguration),
                    entry(EngineConfigurationConstants.KEY_PROCESS_ENGINE_CONFIG, processEngineConfiguration)
                )
                .containsAllEntriesOf(cmmnEngineConfiguration.getEngineConfigurations())
                .containsAllEntriesOf(dmnEngineConfiguration.getEngineConfigurations())
                .containsAllEntriesOf(contentEngineConfiguration.getEngineConfigurations())
                .containsAllEntriesOf(formEngineConfiguration.getEngineConfigurations())
                .containsAllEntriesOf(idmEngineConfiguration.getEngineConfigurations())
                .containsAllEntriesOf(processEngineConfiguration.getEngineConfigurations());

            SpringCmmnEngineConfigurator cmmnConfigurator = context.getBean(SpringCmmnEngineConfigurator.class);
            SpringContentEngineConfigurator contentConfigurator = context.getBean(SpringContentEngineConfigurator.class);
            SpringDmnEngineConfigurator dmnConfigurator = context.getBean(SpringDmnEngineConfigurator.class);
            SpringFormEngineConfigurator formConfigurator = context.getBean(SpringFormEngineConfigurator.class);
            SpringIdmEngineConfigurator idmConfigurator = context.getBean(SpringIdmEngineConfigurator.class);
            SpringProcessEngineConfigurator processConfigurator = context.getBean(SpringProcessEngineConfigurator.class);
            assertThat(appEngineConfiguration.getConfigurators())
                .as("AppEngineConfiguration configurators")
                .containsExactly(
                    processConfigurator,
                    contentConfigurator,
                    dmnConfigurator,
                    formConfigurator,
                    cmmnConfigurator
                );

            assertThat(cmmnEngineConfiguration.getIdmEngineConfigurator())
                .as("CmmnEngineConfiguration idmEngineConfigurator")
                .isNull();
            assertThat(processEngineConfiguration.getIdmEngineConfigurator())
                .as("ProcessEngineConfiguration idmEngineConfigurator")
                .isNull();
            assertThat(appEngineConfiguration.getIdmEngineConfigurator())
                .as("AppEngineConfiguration idmEngineConfigurator")
                .isSameAs(idmConfigurator);
            
            assertThat(appEngineConfiguration.getExpressionManager()).isInstanceOf(SpringAppExpressionManager.class);
            assertThat(appEngineConfiguration.getExpressionManager().getBeans()).isNull();
            assertThat(processEngineConfiguration.getExpressionManager()).isInstanceOf(SpringExpressionManager.class);
            assertThat(processEngineConfiguration.getExpressionManager().getBeans()).isInstanceOf(
                    SpringBeanFactoryProxyMap.class);
            assertThat(cmmnEngineConfiguration.getExpressionManager()).isInstanceOf(SpringCmmnExpressionManager.class);
            assertThat(cmmnEngineConfiguration.getExpressionManager().getBeans()).isInstanceOf(SpringBeanFactoryProxyMap.class);
            assertThat(dmnEngineConfiguration.getExpressionManager()).isInstanceOf(SpringDmnExpressionManager.class);
            assertThat(dmnEngineConfiguration.getExpressionManager().getBeans()).isInstanceOf(SpringBeanFactoryProxyMap.class);
            assertThat(formEngineConfiguration.getExpressionManager()).isInstanceOf(SpringFormExpressionManager.class);
            assertThat(formEngineConfiguration.getExpressionManager().getBeans()).isInstanceOf(SpringBeanFactoryProxyMap.class);

            deleteDeployments(context.getBean(AppEngine.class));
            deleteDeployments(context.getBean(CmmnEngine.class));
            deleteDeployments(context.getBean(DmnEngine.class));
            deleteDeployments(context.getBean(FormEngine.class));
            deleteDeployments(context.getBean(ProcessEngine.class));
        });

    }
    
    @Test
    public void testInclusiveGatewayProcessTask() {
        contextRunner.run((context -> {
            SpringCmmnEngineConfiguration cmmnEngineConfiguration = context.getBean(SpringCmmnEngineConfiguration.class);
            SpringProcessEngineConfiguration processEngineConfiguration = context.getBean(SpringProcessEngineConfiguration.class);
            
            CmmnRuntimeService cmmnRuntimeService = cmmnEngineConfiguration.getCmmnRuntimeService();
            CmmnHistoryService cmmnHistoryService = cmmnEngineConfiguration.getCmmnHistoryService();
            RuntimeService runtimeService = processEngineConfiguration.getRuntimeService();
            TaskService taskService = processEngineConfiguration.getTaskService();
            
            CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
            assertEquals(0, cmmnHistoryService.createHistoricMilestoneInstanceQuery().count());
            assertEquals(0, runtimeService.createProcessInstanceQuery().count());

            List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery()
                    .caseInstanceId(caseInstance.getId())
                    .planItemDefinitionId("theTask")
                    .planItemInstanceState(PlanItemInstanceState.ACTIVE)
                    .list();
            assertEquals(1, planItemInstances.size());
            cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(0).getId());
            assertEquals("No process instance started", 1L, runtimeService.createProcessInstanceQuery().count());
            
            assertEquals(2, taskService.createTaskQuery().count());
            
            List<Task> tasks = taskService.createTaskQuery().list();
            taskService.complete(tasks.get(0).getId());
            taskService.complete(tasks.get(1).getId());
            
            assertEquals(0, taskService.createTaskQuery().count());
            assertEquals(0, runtimeService.createProcessInstanceQuery().count());
            
            planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery()
                    .caseInstanceId(caseInstance.getId())
                    .planItemDefinitionId("theTask2")
                    .list();
            assertEquals(1, planItemInstances.size());
            assertEquals("Task Two", planItemInstances.get(0).getName());
            assertEquals(PlanItemInstanceState.ENABLED, planItemInstances.get(0).getState());
        }));
    }
}
