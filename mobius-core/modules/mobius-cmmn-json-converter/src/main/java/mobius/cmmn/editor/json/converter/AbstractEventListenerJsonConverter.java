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
package mobius.cmmn.editor.json.converter;

import org.apache.commons.lang3.StringUtils;
import mobius.cmmn.editor.constants.CmmnStencilConstants;
import mobius.cmmn.editor.json.converter.util.ListenerConverterUtil;
import mobius.cmmn.model.BaseElement;
import mobius.cmmn.model.EventListener;
import mobius.cmmn.model.PlanItem;
import mobius.cmmn.model.PlanItemDefinition;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *
 */
public abstract class AbstractEventListenerJsonConverter extends BaseCmmnJsonConverter {

    protected void convertCommonElementToJson(ObjectNode elementNode, ObjectNode propertiesNode, BaseElement baseElement) {
        PlanItemDefinition planItemDefinition = ((PlanItem) baseElement).getPlanItemDefinition();
        ListenerConverterUtil.convertLifecycleListenersToJson(objectMapper, propertiesNode, planItemDefinition);

        if (planItemDefinition instanceof EventListener) {
            EventListener eventListener = (EventListener) planItemDefinition;
            if (StringUtils.isNotEmpty(eventListener.getAvailableConditionExpression())) {
                propertiesNode.put(CmmnStencilConstants.PROPERTY_EVENT_LISTENER_AVAILABLE_CONDITION, eventListener.getAvailableConditionExpression());
            }
        }
    }

    protected void convertCommonJsonToElement(JsonNode elementNode, EventListener eventListener) {
        ListenerConverterUtil.convertJsonToLifeCycleListeners(elementNode, eventListener);

        String availableCondition = CmmnJsonConverterUtil.getPropertyValueAsString(CmmnStencilConstants.PROPERTY_EVENT_LISTENER_AVAILABLE_CONDITION, elementNode);
        if (StringUtils.isNotEmpty(availableCondition)) {
            eventListener.setAvailableConditionExpression(availableCondition);
        }
    }

}
