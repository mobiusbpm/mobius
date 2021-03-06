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
package mobius.content.engine.test;

import mobius.content.api.ContentService;
import mobius.content.engine.ContentEngine;
import mobius.content.engine.ContentEngineConfiguration;
import org.junit.Before;
import org.junit.Rule;

/**
 * Parent class for internal Flowable Form tests.
 * 
 * Boots up a dmn engine and caches it.
 * 
 * When using H2 and the default schema name, it will also boot the H2 webapp (reachable with browser on http://localhost:8082/)
 * 
 *
 *
 */
public class AbstractFlowableContentTest {

    public static String H2_TEST_JDBC_URL = "jdbc:h2:mem:flowablecontent;DB_CLOSE_DELAY=1000";

    @Rule
    public FlowableContentRule rule = new FlowableContentRule();

    protected static ContentEngine cachedContentEngine;
    protected ContentEngineConfiguration contentEngineConfiguration;
    protected ContentService contentService;

    @Before
    public void initFormEngine() {
        if (cachedContentEngine == null) {
            cachedContentEngine = rule.getContentEngine();
        }

        this.contentEngineConfiguration = cachedContentEngine.getContentEngineConfiguration();
        this.contentService = cachedContentEngine.getContentService();
    }

}
