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
package mobius.test.spring.boot.util;

import mobius.app.spring.SpringAppEngineConfiguration;
import mobius.cmmn.spring.SpringCmmnEngineConfiguration;
import mobius.common.engine.impl.AbstractEngineConfiguration;
import mobius.content.spring.SpringContentEngineConfiguration;
import mobius.dmn.spring.SpringDmnEngineConfiguration;
import mobius.form.spring.SpringFormEngineConfiguration;
import mobius.idm.spring.SpringIdmEngineConfiguration;
import mobius.spring.SpringProcessEngineConfiguration;
import mobius.spring.boot.EngineConfigurationConfigurer;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Configuration
public class CustomUserEngineConfigurerConfiguration {

    protected final List<Class<?>> invokedConfigurations = new ArrayList<>();

    @Bean
    public EngineConfigurationConfigurer<SpringAppEngineConfiguration> customUserSpringAppEngineConfigurer() {
        return this::configurationInvoked;
    }

    @Bean
    public EngineConfigurationConfigurer<SpringCmmnEngineConfiguration> customUserSpringCmmnEngineConfigurer() {
        return this::configurationInvoked;
    }

    @Bean
    public EngineConfigurationConfigurer<SpringContentEngineConfiguration> customUserSpringContentEngineConfigurer() {
        return this::configurationInvoked;
    }

    @Bean
    public EngineConfigurationConfigurer<SpringDmnEngineConfiguration> customUserSpringDmnEngineConfigurer() {
        return this::configurationInvoked;
    }

    @Bean
    public EngineConfigurationConfigurer<SpringFormEngineConfiguration> customUserSpringFormEngineConfigurer() {
        return this::configurationInvoked;
    }

    @Bean
    public EngineConfigurationConfigurer<SpringIdmEngineConfiguration> customUserSpringIdmEngineConfigurer() {
        return this::configurationInvoked;
    }

    @Bean
    public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> customUserSpringProcessEngineConfigurer() {
        return this::configurationInvoked;
    }

    private void configurationInvoked(AbstractEngineConfiguration engineConfiguration) {
        invokedConfigurations.add(AopUtils.getTargetClass(engineConfiguration));
    }

    public List<Class<?>> getInvokedConfigurations() {
        return invokedConfigurations;
    }
}
