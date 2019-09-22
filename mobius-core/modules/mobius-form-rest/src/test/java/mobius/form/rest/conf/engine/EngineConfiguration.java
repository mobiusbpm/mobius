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
package mobius.form.rest.conf.engine;

import mobius.common.engine.impl.interceptor.EngineConfigurationConstants;
import mobius.form.api.FormManagementService;
import mobius.form.api.FormRepositoryService;
import mobius.form.api.FormService;
import mobius.form.engine.FormEngine;
import mobius.form.engine.FormEngineConfiguration;
import mobius.form.spring.FormEngineFactoryBean;
import mobius.idm.api.IdmEngineConfigurationApi;
import mobius.idm.api.IdmIdentityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class EngineConfiguration {

    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource ds = new SimpleDriverDataSource();
        ds.setDriverClass(org.h2.Driver.class);

        // Connection settings
        ds.setUrl("jdbc:h2:mem:flowable;DB_CLOSE_DELAY=1000");
        ds.setUsername("sa");

        return ds;
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }

    @Bean(name = "formEngineFactoryBean")
    public FormEngineFactoryBean formEngineFactoryBean() {
        FormEngineFactoryBean factoryBean = new FormEngineFactoryBean();
        factoryBean.setFormEngineConfiguration(formEngineConfiguration());
        return factoryBean;
    }

    @Bean(name = "formEngine")
    public FormEngine formEngine() {
        // Safe to call the getObject() on the @Bean annotated formEngineFactoryBean(), will be
        // the fully initialized object instanced from the factory and will NOT be created more than once
        try {
            return formEngineFactoryBean().getObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean(name = "formEngineConfiguration")
    public FormEngineConfiguration formEngineConfiguration() {

        TestSpringFormEngineConfiguration formEngineConfiguration = new TestSpringFormEngineConfiguration();
        formEngineConfiguration.setDataSource(dataSource());
        formEngineConfiguration.setDatabaseSchemaUpdate(FormEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        formEngineConfiguration.setTransactionManager(annotationDrivenTransactionManager());
        return formEngineConfiguration;
    }

    @Bean
    public FormRepositoryService formRepositoryService() {
        return formEngine().getFormRepositoryService();
    }

    @Bean
    public FormManagementService managementService() {
        return formEngine().getFormManagementService();
    }

    @Bean
    public FormService formService() {
        return formEngine().getFormService();
    }

    @Bean
    public IdmIdentityService idmIdentityService() {
        return ((IdmEngineConfigurationApi) formEngine().getFormEngineConfiguration().getEngineConfigurations()
                .get(EngineConfigurationConstants.KEY_IDM_ENGINE_CONFIG)).getIdmIdentityService();
    }

}
