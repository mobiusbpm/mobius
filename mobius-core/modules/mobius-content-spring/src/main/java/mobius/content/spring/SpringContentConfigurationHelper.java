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

package mobius.content.spring;

import java.net.URL;
import java.util.Map;

import mobius.common.engine.api.FlowableException;
import mobius.content.engine.ContentEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.UrlResource;

/**
 * @author Tijs Rademakers
 */
public class SpringContentConfigurationHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringContentConfigurationHelper.class);

    public static ContentEngine buildContentEngine(URL resource) {
        LOGGER.debug("==== BUILDING SPRING APPLICATION CONTEXT AND CONTENT ENGINE =========================================");

        ApplicationContext applicationContext = new GenericXmlApplicationContext(new UrlResource(resource));
        Map<String, ContentEngine> beansOfType = applicationContext.getBeansOfType(ContentEngine.class);
        if ((beansOfType == null) || beansOfType.isEmpty()) {
            throw new FlowableException("no " + ContentEngine.class.getName() + " defined in the application context " + resource.toString());
        }

        ContentEngine contentEngine = beansOfType.values().iterator().next();

        LOGGER.debug("==== SPRING CONTENT ENGINE CREATED ==================================================================");
        return contentEngine;
    }

}
