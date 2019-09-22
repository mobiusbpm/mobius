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
package mobius.camel;

import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;

public class FlowableConsumer extends DefaultConsumer {

    public FlowableConsumer(FlowableEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        ((FlowableEndpoint) getEndpoint()).addConsumer(this);
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        ((FlowableEndpoint) getEndpoint()).removeConsumer();
    }
}
