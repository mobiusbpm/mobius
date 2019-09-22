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
package mobius.spring.boot.process;

import com.fasterxml.jackson.databind.ObjectMapper;
import mobius.rest.service.api.RestResponseFactory;
import mobius.spring.boot.DispatcherServletConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * Component scan for the Process engine REST API Configuration.
 *
 * @author Filip Hrisafov
 */
@Import(DispatcherServletConfiguration.class)
@ComponentScan("mobius.rest.rest.service.api")
public class ProcessEngineRestConfiguration {
    
    @Autowired
    protected ObjectMapper objectMapper;

    @Bean
    public RestResponseFactory restResponseFactory() {
        return new RestResponseFactory(objectMapper);
    }
}