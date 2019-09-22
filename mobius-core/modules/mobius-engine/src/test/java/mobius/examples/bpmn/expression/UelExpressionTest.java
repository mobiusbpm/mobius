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

package mobius.examples.bpmn.expression;

import mobius.common.engine.impl.util.CollectionUtil;
import mobius.engine.impl.test.PluggableFlowableTestCase;
import mobius.engine.runtime.ProcessInstance;
import mobius.engine.test.Deployment;
import mobius.task.api.Task;
import org.junit.jupiter.api.Test;

/**
 * @author Joram Barrez
 */
public class UelExpressionTest extends PluggableFlowableTestCase {

    @Test
    @Deployment
    public void testValueAndMethodExpression() {
        // An order of price 150 is a standard order (goes through an UEL value
        // expression)
        UelExpressionTestOrder order = new UelExpressionTestOrder(150);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("uelExpressions", CollectionUtil.singletonMap("order", order));
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        assertEquals("Standard service", task.getName());

        // While an order of 300, gives us a premium service (goes through an
        // UEL method expression)
        order = new UelExpressionTestOrder(300);
        processInstance = runtimeService.startProcessInstanceByKey("uelExpressions", CollectionUtil.singletonMap("order", order));
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        assertEquals("Premium service", task.getName());

    }

}