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
package mobius.spring.boot.actuate.info;

import mobius.spring.boot.EndpointAutoConfiguration;
import mobius.spring.boot.RestApiAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.info.ConditionalOnEnabledInfoContributor;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 */
@Configuration
@AutoConfigureBefore(EndpointAutoConfiguration.class)
@AutoConfigureAfter(RestApiAutoConfiguration.class)
@ConditionalOnClass({
    InfoContributor.class,
    ConditionalOnEnabledInfoContributor.class
})
public class FlowableInfoAutoConfiguration {

    @Bean
    @ConditionalOnEnabledInfoContributor("flowable")
    public FlowableInfoContributor flowableInfoContributor() {
        return new FlowableInfoContributor();
    }
}
