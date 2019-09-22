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

import mobius.app.spring.SpringAppEngineConfiguration;
import mobius.common.engine.impl.persistence.StrongUuidGenerator;
import mobius.spring.boot.AbstractSpringEngineAutoConfiguration;
import mobius.spring.boot.FlowableProperties;
import mobius.spring.boot.condition.ConditionalOnAppEngine;
import mobius.spring.boot.idm.FlowableIdmProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

/**
 * Provides sane definitions for the various beans required to be productive with Flowable in Spring.
 *
 * @author Tijs Rademakers
 */
@Configuration
@ConditionalOnAppEngine
@EnableConfigurationProperties({
    FlowableProperties.class,
    FlowableAppProperties.class,
    FlowableIdmProperties.class
})
public class AppEngineAutoConfiguration extends AbstractSpringEngineAutoConfiguration {

    protected final FlowableAppProperties appProperties;
    protected final FlowableIdmProperties idmProperties;

    public AppEngineAutoConfiguration(FlowableProperties flowableProperties, FlowableAppProperties appProperties,
        FlowableIdmProperties idmProperties) {
        super(flowableProperties);
        this.appProperties = appProperties;
        this.idmProperties = idmProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringAppEngineConfiguration springAppEngineConfiguration(DataSource dataSource, PlatformTransactionManager platformTransactionManager) throws IOException {

        SpringAppEngineConfiguration conf = new SpringAppEngineConfiguration();

        List<Resource> resources = this.discoverDeploymentResources(
            appProperties.getResourceLocation(),
            appProperties.getResourceSuffixes(),
            appProperties.isDeployResources()
        );

        if (resources != null && !resources.isEmpty()) {
            conf.setDeploymentResources(resources.toArray(new Resource[0]));
        }

        configureSpringEngine(conf, platformTransactionManager);
        configureEngine(conf, dataSource);

        conf.setIdGenerator(new StrongUuidGenerator());

        return conf;
    }
}