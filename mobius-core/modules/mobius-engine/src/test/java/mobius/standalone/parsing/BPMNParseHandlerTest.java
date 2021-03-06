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
 *
 *
 */
public class BPMNParseHandlerTest extends ResourceFlowableTestCase {

    public BPMNParseHandlerTest() {
        super("mobius/standalone/parsing/bpmn.parse.listener.flowable.cfg.xml");
    }

    @Test
    @Deployment
    public void testAlterProcessDefinitionKeyWhenDeploying() throws Exception {
        // Check if process-definition has different key
        assertEquals(0, repositoryService.createProcessDefinitionQuery().processDefinitionKey("oneTaskProcess").count());
        assertEquals(1, repositoryService.createProcessDefinitionQuery().processDefinitionKey("oneTaskProcess-modified").count());
    }
}
