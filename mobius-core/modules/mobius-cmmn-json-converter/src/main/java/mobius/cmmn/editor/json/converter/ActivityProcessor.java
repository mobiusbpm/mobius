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

import java.util.Map;

import mobius.cmmn.editor.json.converter.CmmnJsonConverter.CmmnModelIdHelper;
import mobius.cmmn.editor.json.model.CmmnModelInfo;
import mobius.cmmn.model.BaseElement;
import mobius.cmmn.model.CmmnModel;
import mobius.cmmn.model.Stage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 *
 */
public interface ActivityProcessor {

    public void processPlanItems(Stage stage, CmmnModel model, ArrayNode shapesArrayNode,
            Map<String, CmmnModelInfo> formKeyMap, Map<String, CmmnModelInfo> decisionTableKeyMap, double subProcessX, double subProcessY);

    public void processJsonElements(JsonNode shapesArrayNode, JsonNode modelNode, BaseElement parentElement,
            Map<String, JsonNode> shapeMap, Map<String, String> formMap, Map<String, String> decisionTableMap, 
            Map<String, String> caseModelMap, Map<String, String> processModelMap, CmmnModel bpmnModel, CmmnModelIdHelper cmmnModelIdHelper);
}
