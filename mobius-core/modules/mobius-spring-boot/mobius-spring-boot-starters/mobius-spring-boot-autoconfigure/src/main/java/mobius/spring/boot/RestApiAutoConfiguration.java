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

import mobius.app.engine.AppEngine;
import mobius.app.rest.AppRestUrls;
import mobius.cmmn.engine.CmmnEngine;
import mobius.cmmn.rest.api.CmmnRestUrls;
import mobius.common.rest.resolver.ContentTypeResolver;
import mobius.common.rest.resolver.DefaultContentTypeResolver;
import mobius.content.engine.ContentEngine;
import mobius.content.rest.ContentRestUrls;
import mobius.dmn.engine.DmnEngine;
import mobius.dmn.rest.service.api.DmnRestUrls;
import mobius.engine.ProcessEngine;
import mobius.form.engine.FormEngine;
import mobius.form.rest.FormRestUrls;
import mobius.idm.engine.IdmEngine;
import mobius.idm.rest.service.api.IdmRestResponseFactory;
import mobius.rest.service.api.RestUrls;
import mobius.spring.boot.app.AppEngineRestConfiguration;
import mobius.spring.boot.app.AppEngineServicesAutoConfiguration;
import mobius.spring.boot.app.FlowableAppProperties;
import mobius.spring.boot.cmmn.CmmnEngineRestConfiguration;
import mobius.spring.boot.cmmn.CmmnEngineServicesAutoConfiguration;
import mobius.spring.boot.cmmn.FlowableCmmnProperties;
import mobius.spring.boot.content.ContentEngineRestConfiguration;
import mobius.spring.boot.content.ContentEngineServicesAutoConfiguration;
import mobius.spring.boot.content.FlowableContentProperties;
import mobius.spring.boot.dmn.DmnEngineRestConfiguration;
import mobius.spring.boot.dmn.DmnEngineServicesAutoConfiguration;
import mobius.spring.boot.dmn.FlowableDmnProperties;
import mobius.spring.boot.form.FlowableFormProperties;
import mobius.spring.boot.form.FormEngineRestConfiguration;
import mobius.spring.boot.form.FormEngineServicesAutoConfiguration;
import mobius.spring.boot.idm.FlowableIdmProperties;
import mobius.spring.boot.idm.IdmEngineRestConfiguration;
import mobius.spring.boot.idm.IdmEngineServicesAutoConfiguration;
import mobius.spring.boot.process.FlowableProcessProperties;
import mobius.spring.boot.process.ProcessEngineRestConfiguration;
import mobius.spring.boot.rest.BaseRestApiConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-configuration and starter for the Flowable REST APIs.
 *
 *
 * @author Josh Long
 * @author Vedran Pavic
 *
 */
@Configuration
@ConditionalOnClass(ContentTypeResolver.class)
@ConditionalOnWebApplication
@AutoConfigureAfter({
    //FIXME in order to support both 1.5.x and 2.0 we can't use MultipartAutoConfiguration (the package is changed)
    //MultipartAutoConfiguration.class,
    FlowableSecurityAutoConfiguration.class,
    AppEngineServicesAutoConfiguration.class,
    ProcessEngineServicesAutoConfiguration.class,
    CmmnEngineServicesAutoConfiguration.class,
    ContentEngineServicesAutoConfiguration.class,
    DmnEngineServicesAutoConfiguration.class,
    FormEngineServicesAutoConfiguration.class,
    IdmEngineServicesAutoConfiguration.class
})
public class RestApiAutoConfiguration {

    @Configuration
    @ConditionalOnClass(RestUrls.class)
    @ConditionalOnBean(ProcessEngine.class)
    @EnableConfigurationProperties(FlowableProcessProperties.class)
    public static class ProcessEngineRestApiConfiguration extends BaseRestApiConfiguration {

        @Bean
        public ServletRegistrationBean processService(FlowableProcessProperties properties) {
            return registerServlet(properties.getServlet(), ProcessEngineRestConfiguration.class);
        }
    }

    @Bean
    public ContentTypeResolver contentTypeResolver() {
        ContentTypeResolver resolver = new DefaultContentTypeResolver();
        return resolver;
    }
    
    @Configuration
    @ConditionalOnClass(AppRestUrls.class)
    @ConditionalOnBean(AppEngine.class)
    public static class AppEngineRestApiConfiguration extends BaseRestApiConfiguration {

        @Bean
        public ServletRegistrationBean appServlet(FlowableAppProperties properties) {
            return registerServlet(properties.getServlet(), AppEngineRestConfiguration.class);
        }
    }

    @Configuration
    @ConditionalOnClass(CmmnRestUrls.class)
    @ConditionalOnBean(CmmnEngine.class)
    public static class CmmnEngineRestApiConfiguration extends BaseRestApiConfiguration {

        @Bean
        public ServletRegistrationBean cmmnServlet(FlowableCmmnProperties properties) {
            return registerServlet(properties.getServlet(), CmmnEngineRestConfiguration.class);
        }
    }

    @Configuration
    @ConditionalOnClass(ContentRestUrls.class)
    @ConditionalOnBean(ContentEngine.class)
    public static class ContentEngineRestApiConfiguration extends BaseRestApiConfiguration {

        @Bean
        public ServletRegistrationBean contentServlet(FlowableContentProperties properties) {
            return registerServlet(properties.getServlet(), ContentEngineRestConfiguration.class);
        }
    }

    @Configuration
    @ConditionalOnClass(DmnRestUrls.class)
    @ConditionalOnBean(DmnEngine.class)
    public static class DmnEngineRestApiConfiguration extends BaseRestApiConfiguration {

        @Bean
        public ServletRegistrationBean dmnServlet(FlowableDmnProperties properties) {
            return registerServlet(properties.getServlet(), DmnEngineRestConfiguration.class);
        }
    }

    @Configuration
    @ConditionalOnClass(FormRestUrls.class)
    @ConditionalOnBean(FormEngine.class)
    public static class FormEngineRestApiConfiguration extends BaseRestApiConfiguration {

        @Bean
        public ServletRegistrationBean formServlet(FlowableFormProperties properties) {
            return registerServlet(properties.getServlet(), FormEngineRestConfiguration.class);
        }
    }

    @Configuration
    @ConditionalOnClass(IdmRestResponseFactory.class)
    @ConditionalOnBean(IdmEngine.class)
    public static class IdmEngineRestApiConfiguration extends BaseRestApiConfiguration {

        @Bean
        public ServletRegistrationBean idmServlet(FlowableIdmProperties properties) {
            return registerServlet(properties.getServlet(), IdmEngineRestConfiguration.class);
        }
    }

}
