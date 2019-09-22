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
package mobius.rest.conf.jpa;

import mobius.common.engine.impl.history.HistoryLevel;
import mobius.common.engine.impl.interceptor.EngineConfigurationConstants;
import mobius.engine.*;
import mobius.engine.impl.cfg.ProcessEngineConfigurationImpl;
import mobius.form.api.FormRepositoryService;
import mobius.form.engine.FormEngineConfiguration;
import mobius.form.spring.SpringFormEngineConfiguration;
import mobius.form.spring.configurator.SpringFormEngineConfigurator;
import mobius.idm.api.IdmEngineConfigurationApi;
import mobius.idm.api.IdmIdentityService;
import mobius.spring.ProcessEngineFactoryBean;
import mobius.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
public class JPAFlowableEngineConfiguration {

    @Autowired
    protected DataSource dataSource;

    @Autowired
    protected PlatformTransactionManager transactionManager;

    @Autowired
    protected EntityManagerFactory entityManagerFactory;

    @Bean(name = "processEngineFactoryBean")
    public ProcessEngineFactoryBean processEngineFactoryBean() {
        ProcessEngineFactoryBean factoryBean = new ProcessEngineFactoryBean();
        factoryBean.setProcessEngineConfiguration(processEngineConfiguration());
        return factoryBean;
    }

    @Bean(name = "processEngine")
    public ProcessEngine processEngine() {
        // Safe to call the getObject() on the @Bean annotated
        // processEngineFactoryBean(), will be
        // the fully initialized object instanced from the factory and will NOT
        // be created more than once
        try {
            return processEngineFactoryBean().getObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean(name = "processEngineConfiguration")
    public ProcessEngineConfigurationImpl processEngineConfiguration() {
        SpringProcessEngineConfiguration processEngineConfiguration = new SpringProcessEngineConfiguration();
        processEngineConfiguration.setDataSource(dataSource);
        processEngineConfiguration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        processEngineConfiguration.setTransactionManager(transactionManager);
        processEngineConfiguration.setAsyncExecutorActivate(false);
        processEngineConfiguration.setJpaEntityManagerFactory(entityManagerFactory);
        processEngineConfiguration.setJpaHandleTransaction(false);
        processEngineConfiguration.setJpaHandleTransaction(false);
        processEngineConfiguration.setHistoryLevel(HistoryLevel.FULL);
        return processEngineConfiguration;
    }
    
    @Bean
    public SpringFormEngineConfigurator formEngineConfigurator() {
        SpringFormEngineConfigurator formEngineConfigurator =  new SpringFormEngineConfigurator();
        formEngineConfigurator.setFormEngineConfiguration(formEngineConfiguration());
        return formEngineConfigurator;
    }
    
    @Bean(name = "formEngineConfiguration")
    public FormEngineConfiguration formEngineConfiguration() {
        SpringFormEngineConfiguration formEngineConfiguration = new SpringFormEngineConfiguration();
        formEngineConfiguration.setDataSource(dataSource);
        formEngineConfiguration.setDatabaseSchemaUpdate(FormEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        formEngineConfiguration.setTransactionManager(transactionManager);
        return formEngineConfiguration;
    }

    @Bean
    public RepositoryService repositoryService() {
        return processEngine().getRepositoryService();
    }

    @Bean
    public RuntimeService runtimeService() {
        return processEngine().getRuntimeService();
    }

    @Bean
    public TaskService taskService() {
        return processEngine().getTaskService();
    }

    @Bean
    public HistoryService historyService() {
        return processEngine().getHistoryService();
    }

    @Bean
    public FormService formService() {
        return processEngine().getFormService();
    }

    @Bean
    public IdentityService identityService() {
        return processEngine().getIdentityService();
    }
    
    @Bean
    public IdmIdentityService idmIdentityService() {
        return ((IdmEngineConfigurationApi) processEngine().getProcessEngineConfiguration().getEngineConfigurations()
                .get(EngineConfigurationConstants.KEY_IDM_ENGINE_CONFIG)).getIdmIdentityService();
    }

    @Bean
    public ManagementService managementService() {
        return processEngine().getManagementService();
    }
    
    @Bean
    public DynamicBpmnService dynamicBpmnService() {
        return processEngine().getDynamicBpmnService();
    }
    
    @Bean
    public FormRepositoryService formRepositoryService(ProcessEngine processEngine) {
        return formEngineConfiguration().getFormRepositoryService();
    }
    
    @Bean
    public mobius.form.api.FormService formEngineFormService(ProcessEngine processEngine) {
        return formEngineConfiguration().getFormService();
    }
}
