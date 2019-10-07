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

package mobius.standalone.interceptor;

import mobius.common.engine.impl.context.Context;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.impl.test.PluggableFlowableTestCase;
import org.junit.jupiter.api.Test;

/**
 *
 */
public class CommandContextTest extends PluggableFlowableTestCase {

    @Test
    public void testCommandContextGetCurrentAfterException() {
        try {
            processEngineConfiguration.getCommandExecutor().execute(new Command<Object>() {
                @Override
                public Object execute(CommandContext commandContext) {
                    throw new IllegalStateException("here i come!");
                }
            });

            fail("expected exception");
        } catch (IllegalStateException e) {
            // OK
        }

        assertNull(Context.getCommandContext());
    }
}
