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
package mobius.standalone.parsing;

import mobius.engine.impl.test.ResourceFlowableTestCase;
import mobius.engine.test.Deployment;
import org.junit.jupiter.api.Test;

/**
 * @author Frederik Heremans
 * @author Joram Barrez
 */
public class CustomDefaultBpmnParseHandlerTest extends ResourceFlowableTestCase {

    public CustomDefaultBpmnParseHandlerTest() {
        super("mobius/standalone/parsing/custom.default.parse.handler.flowable.cfg.xml");
    }

    @Test
    @Deployment
    public void testCustomDefaultUserTaskParsing() throws Exception {
        // The task which is created after process instance start should be
        // async
        runtimeService.startProcessInstanceByKey("customDefaultBpmnParseHandler");

        assertEquals(0, taskService.createTaskQuery().count());
        assertEquals(1, managementService.createJobQuery().count());

        managementService.executeJob(managementService.createJobQuery().singleResult().getId());
        assertEquals(1, taskService.createTaskQuery().count());
    }

}
