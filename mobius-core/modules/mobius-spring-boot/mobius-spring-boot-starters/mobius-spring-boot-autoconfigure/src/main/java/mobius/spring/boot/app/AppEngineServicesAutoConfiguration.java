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
package mobius.spring.boot.app;

import mobius.app.api.AppManagementService;
import mobius.app.api.AppRepositoryService;
import mobius.app.engine.AppEngine;
import mobius.app.spring.AppEngineFactoryBean;
import mobius.app.spring.SpringAppEngineConfiguration;
import mobius.spring.boot.BaseEngineConfigurationWithConfigurers;
import mobius.spring.boot.FlowableProperties;
import mobius.spring.boot.condition.ConditionalOnAppEngine;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides sane definitions for the various beans required to be productive with Flowable in Spring.
 *
 * @author Tijs Rademakers
 */
@Configuration
@ConditionalOnAppEngine
@EnableConfigurationProperties({
    FlowableProperties.class,
    FlowableAppProperties.class
})
@AutoConfigureAfter({
    AppEngineAutoConfiguration.class
})
public class AppEngineServicesAutoConfiguration extends BaseEngineConfigurationWithConfigurers<SpringAppEngineConfiguration> {

    @Bean(name="flowableAppEngine")
    public AppEngineFactoryBean appEngine(SpringAppEngineConfiguration configuration) throws Exception {
        AppEngineFactoryBean appEngineFactoryBean = new AppEngineFactoryBean();
        appEngineFactoryBean.setAppEngineConfiguration(configuration);
        
        invokeConfigurers(configuration);
        
        return appEngineFactoryBean;
    }

    @Bean
    @ConditionalOnMissingBean
    public AppRepositoryService appRepositoryServiceBean(AppEngine appEngine) {
        return appEngine.getAppRepositoryService();
    }

    @Bean
    @ConditionalOnMissingBean
    public AppManagementService appManagementServiceBean(AppEngine appEngine) {
        return appEngine.getAppManagementService();
    }
}