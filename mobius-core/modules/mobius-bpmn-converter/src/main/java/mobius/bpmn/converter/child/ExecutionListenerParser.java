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
package mobius.bpmn.converter.child;

import org.apache.commons.lang3.StringUtils;
import mobius.bpmn.model.BaseElement;
import mobius.bpmn.model.FlowableListener;
import mobius.bpmn.model.HasExecutionListeners;
import mobius.bpmn.model.SequenceFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ExecutionListenerParser extends FlowableListenerParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionListenerParser.class);

    @Override
    public String getElementName() {
        return ELEMENT_EXECUTION_LISTENER;
    }

    @Override
    public void addListenerToParent(FlowableListener listener, BaseElement parentElement) {
        if (parentElement instanceof HasExecutionListeners) {
            if (StringUtils.isEmpty(listener.getEvent()) && parentElement instanceof SequenceFlow) {
                // No event type on a sequenceflow = 'take' implied
                listener.setEvent("take");
            }
            ((HasExecutionListeners) parentElement).getExecutionListeners().add(listener);
        }
    }
}
