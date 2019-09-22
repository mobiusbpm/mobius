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
package mobius.spring.boot.idm;

import java.util.Objects;

import javax.sql.DataSource;

import mobius.app.spring.SpringAppEngineConfiguration;
import mobius.idm.engine.configurator.IdmEngineConfigurator;
import mobius.idm.spring.SpringIdmEngineConfiguration;
import mobius.idm.spring.authentication.SpringEncoder;
import mobius.idm.spring.configurator.SpringIdmEngineConfigurator;
import mobius.spring.SpringProcessEngineConfiguration;
import mobius.spring.boot.AbstractEngineAutoConfiguration;
import mobius.spring.boot.BaseEngineConfigurationWithConfigurers;
import mobius.spring.boot.EngineConfigurationConfigurer;
import mobius.spring.boot.FlowableProperties;
import mobius.spring.boot.ProcessEngineAutoConfiguration;
import mobius.spring.boot.ProcessEngineServicesAutoConfiguration;
import mobius.spring.boot.app.AppEngineAutoConfiguration;
import mobius.spring.boot.app.AppEngineServicesAutoConfiguration;
import mobius.spring.boot.condition.ConditionalOnIdmEngine;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration} for the Idm engine
 *
 * @author Filip Hrisafov
 */
@Configuration
@ConditionalOnIdmEngine
@EnableConfigurationProperties({
    FlowableProperties.class,
    FlowableIdmProperties.class
})
@AutoConfigureAfter({
    AppEngineAutoConfiguration.class,
    ProcessEngineAutoConfiguration.class,
})
@AutoConfigureBefore({
    AppEngineServicesAutoConfiguration.class,
    ProcessEngineServicesAutoConfiguration.class
})
public class IdmEngineAutoConfiguration extends AbstractEngineAutoConfiguration {

    protected final FlowableIdmProperties idmProperties;

    public IdmEngineAutoConfiguration(FlowableProperties flowableProperties, FlowableIdmProperties idmProperties) {
        super(flowableProperties);
        this.idmProperties = idmProperties;
    }

    @ConditionalOnClass(PasswordEncoder.class)
    @Configuration
    @ConditionalOnProperty(prefix = "flowable.idm.ldap", name = "enabled", havingValue = "false", matchIfMissing = true)
    public static class PasswordEncoderConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public PasswordEncoder passwordEncoder(FlowableIdmProperties idmProperties) {
            PasswordEncoder encoder;
            String encoderType = idmProperties.getPasswordEncoder();
            if (Objects.equals("spring_bcrypt", encoderType)) {
                encoder = new BCryptPasswordEncoder();
            } else if (encoderType != null && encoderType.startsWith("spring_delegating")) {
                encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
                if (encoderType.equals("spring_delegating_bcrypt")) {
                    ((DelegatingPasswordEncoder) encoder).setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder());
                } else if (encoderType.equals("spring_delegating_noop")) {
                    ((DelegatingPasswordEncoder) encoder).setDefaultPasswordEncoderForMatches(NoOpPasswordEncoder.getInstance());
                }
            } else {
                encoder = NoOpPasswordEncoder.getInstance();
            }

            return encoder;
        }

        @Bean
        @ConditionalOnBean(PasswordEncoder.class)
        @ConditionalOnMissingBean(name = "passwordEncoderIdmEngineConfigurationConfigurer")
        public EngineConfigurationConfigurer<SpringIdmEngineConfiguration> passwordEncoderIdmEngineConfigurationConfigurer(PasswordEncoder passwordEncoder) {
            return idmEngineConfiguration -> idmEngineConfiguration.setPasswordEncoder(new SpringEncoder(passwordEncoder));
        }

    }

    @Bean
    @ConditionalOnMissingBean
    public SpringIdmEngineConfiguration idmEngineConfiguration(DataSource dataSource, PlatformTransactionManager platformTransactionManager) {
        SpringIdmEngineConfiguration configuration = new SpringIdmEngineConfiguration();

        configuration.setTransactionManager(platformTransactionManager);
        configureEngine(configuration, dataSource);

        return configuration;
    }

    @Configuration
    @ConditionalOnBean(type = {
        "mobius.spring.SpringProcessEngineConfiguration"
    })
    @ConditionalOnMissingBean(type = {
        "mobius.app.spring.SpringAppEngineConfiguration"
    })
    public static class IdmEngineProcessConfiguration extends BaseEngineConfigurationWithConfigurers<SpringIdmEngineConfiguration> {

        @Bean
        @ConditionalOnMissingBean(name = "idmProcessEngineConfigurationConfigurer")
        public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> idmProcessEngineConfigurationConfigurer(
            IdmEngineConfigurator idmEngineConfigurator
        ) {
            return processEngineConfiguration -> processEngineConfiguration.setIdmEngineConfigurator(idmEngineConfigurator);
        }

        @Bean
        @ConditionalOnMissingBean
        public IdmEngineConfigurator idmEngineConfigurator(SpringIdmEngineConfiguration configuration) {
            SpringIdmEngineConfigurator idmEngineConfigurator = new SpringIdmEngineConfigurator();
            idmEngineConfigurator.setIdmEngineConfiguration(configuration);
            
            invokeConfigurers(configuration);
            
            return idmEngineConfigurator;
        }
    }
    
    @Configuration
    @ConditionalOnBean(type = {
        "mobius.app.spring.SpringAppEngineConfiguration"
    })
    public static class IdmEngineAppConfiguration extends BaseEngineConfigurationWithConfigurers<SpringIdmEngineConfiguration> {

        @Bean
        @ConditionalOnMissingBean(name = "idmAppEngineConfigurationConfigurer")
        public EngineConfigurationConfigurer<SpringAppEngineConfiguration> idmAppEngineConfigurationConfigurer(
            IdmEngineConfigurator idmEngineConfigurator
        ) {
            return appEngineConfiguration -> appEngineConfiguration.setIdmEngineConfigurator(idmEngineConfigurator);
        }

        @Bean
        @ConditionalOnMissingBean
        public IdmEngineConfigurator idmEngineConfigurator(SpringIdmEngineConfiguration configuration) {
            SpringIdmEngineConfigurator idmEngineConfigurator = new SpringIdmEngineConfigurator();
            idmEngineConfigurator.setIdmEngineConfiguration(configuration);
            
            invokeConfigurers(configuration);
            
            return idmEngineConfigurator;
        }
    }
}

