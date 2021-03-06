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
package mobius.spring.boot;

import mobius.common.engine.api.identity.AuthenticationContext;
import mobius.common.engine.impl.identity.Authentication;
import mobius.idm.api.IdmIdentityService;
import mobius.spring.boot.idm.IdmEngineServicesAutoConfiguration;
import mobius.spring.security.FlowableUserDetailsService;
import mobius.spring.security.SpringSecurityAuthenticationContext;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Installs a Spring Security adapter for the Flowable {@link IdmIdentityService}.
 *
 * @author Josh Long
 */
@Configuration
@ConditionalOnClass({
    AuthenticationManager.class,
    IdmIdentityService.class,
    FlowableUserDetailsService.class,
    GlobalAuthenticationConfigurerAdapter.class
})
@ConditionalOnBean(IdmIdentityService.class)
@AutoConfigureBefore(org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class)
@AutoConfigureAfter({
    IdmEngineServicesAutoConfiguration.class,
    ProcessEngineAutoConfiguration.class
})
public class FlowableSecurityAutoConfiguration {

    @Configuration
    @ConditionalOnClass(AuthenticationContext.class)
    public static class SpringSecurityAuthenticationContextConfiguration {

        public SpringSecurityAuthenticationContextConfiguration(ObjectProvider<AuthenticationContext> authenticationContext) {
            AuthenticationContext context = authenticationContext.getIfAvailable();
            if (context == null) {
                context = new SpringSecurityAuthenticationContext();
            }

            Authentication.setAuthenticationContext(context);
        }
    }

    @Bean
    @ConditionalOnMissingBean(UserDetailsService.class)
    public FlowableUserDetailsService flowableUserDetailsService(IdmIdentityService identityService) {
        return new FlowableUserDetailsService(identityService);
    }
}
