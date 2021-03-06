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

import mobius.cmmn.editor.constants.CmmnStencilConstants;
import mobius.cmmn.editor.json.converter.CmmnJsonConverter.CmmnModelIdHelper;
import mobius.cmmn.editor.json.converter.util.ListenerConverterUtil;
import mobius.cmmn.model.BaseElement;
import mobius.cmmn.model.CmmnModel;
import mobius.cmmn.model.PlanItem;
import mobius.cmmn.model.ProcessTask;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *
 */
public class ProcessTaskJsonConverter extends BaseChildTaskCmmnJsonConverter implements ProcessModelAwareConverter {
    
    protected Map<String, String> processModelMap;
    
    public static void fillTypes(Map<String, Class<? extends BaseCmmnJsonConverter>> convertersToCmmnMap,
            Map<Class<? extends BaseElement>, Class<? extends BaseCmmnJsonConverter>> convertersToJsonMap) {
        fillJsonTypes(convertersToCmmnMap);
        fillCmmnTypes(convertersToJsonMap);
    }

    public static void fillJsonTypes(Map<String, Class<? extends BaseCmmnJsonConverter>> convertersToBpmnMap) {
        convertersToBpmnMap.put(STENCIL_TASK_PROCESS, ProcessTaskJsonConverter.class);
    }

    public static void fillCmmnTypes(Map<Class<? extends BaseElement>, Class<? extends BaseCmmnJsonConverter>> convertersToJsonMap) {
        convertersToJsonMap.put(ProcessTask.class, ProcessTaskJsonConverter.class);
    }
    
    @Override
    protected String getStencilId(BaseElement baseElement) {
        return CmmnStencilConstants.STENCIL_TASK_PROCESS;
    }

    @Override
    protected void convertElementToJson(ObjectNode elementNode, ObjectNode propertiesNode, ActivityProcessor processor,
            BaseElement baseElement, CmmnModel cmmnModel) {
        
        ProcessTask processTask = (ProcessTask) ((PlanItem) baseElement).getPlanItemDefinition();

        if (processTask.getFallbackToDefaultTenant() != null) {
            propertiesNode.put(PROPERTY_FALLBACK_TO_DEFAULT_TENANT, processTask.getFallbackToDefaultTenant());
        }
        
        ListenerConverterUtil.convertLifecycleListenersToJson(objectMapper, propertiesNode, processTask);

        if (processTask.getInParameters() != null && !processTask.getInParameters().isEmpty()) {
            ObjectNode inParametersNode = propertiesNode.putObject(CmmnStencilConstants.PROPERTY_PROCESS_IN_PARAMETERS);
            ArrayNode inParametersArray = inParametersNode.putArray("inParameters");
            readIOParameters(processTask.getInParameters(), inParametersArray);
        }
        if (processTask.getOutParameters() != null && !processTask.getOutParameters().isEmpty()) {
            ObjectNode outParametersNode = propertiesNode.putObject(CmmnStencilConstants.PROPERTY_PROCESS_OUT_PARAMETERS);
            ArrayNode outParametersArray = outParametersNode.putArray("outParameters");
            readIOParameters(processTask.getOutParameters(), outParametersArray);
        }
    }

    @Override
    protected BaseElement convertJsonToElement(JsonNode elementNode, JsonNode modelNode, ActivityProcessor processor,
            BaseElement parentElement, Map<String, JsonNode> shapeMap, CmmnModel cmmnModel, CmmnModelIdHelper cmmnModelIdHelper) {
        ProcessTask task = new ProcessTask();
        
        JsonNode processModelReferenceNode = CmmnJsonConverterUtil.getProperty(PROPERTY_PROCESS_REFERENCE, elementNode);
        if (processModelReferenceNode != null && processModelReferenceNode.has("id") && !processModelReferenceNode.get("id").isNull()) {

            String processModelId = processModelReferenceNode.get("id").asText();
            if (processModelMap != null) {
                String processModelKey = processModelMap.get(processModelId);
                task.setProcessRef(processModelKey);
            }
        }

        JsonNode processTaskInParametersNode = CmmnJsonConverterUtil.getProperty(CmmnStencilConstants.PROPERTY_PROCESS_IN_PARAMETERS, elementNode);
        if (processTaskInParametersNode != null && processTaskInParametersNode.has("inParameters") && !processTaskInParametersNode.get("inParameters").isNull()) {
            JsonNode inParametersNode =  processTaskInParametersNode.get("inParameters");
            task.setInParameters(readIOParameters(inParametersNode));
        }

        JsonNode processTaskOutParametersNode = CmmnJsonConverterUtil.getProperty(CmmnStencilConstants.PROPERTY_PROCESS_OUT_PARAMETERS, elementNode);
        if (processTaskOutParametersNode != null && processTaskOutParametersNode.has("outParameters") && !processTaskOutParametersNode.get("outParameters").isNull()) {
            JsonNode outParametersNode =  processTaskOutParametersNode.get("outParameters");
            task.setOutParameters(readIOParameters(outParametersNode));
        }

        JsonNode fallbackToDefaultTenant = CmmnJsonConverterUtil.getProperty(CmmnStencilConstants.PROPERTY_FALLBACK_TO_DEFAULT_TENANT, elementNode);
        if (fallbackToDefaultTenant != null) {
            task.setFallbackToDefaultTenant(fallbackToDefaultTenant.booleanValue());
        }

        ListenerConverterUtil.convertJsonToLifeCycleListeners(elementNode, task);

        return task;
    }

    @Override
    public void setProcessModelMap(Map<String, String> processModelMap) {
        this.processModelMap = processModelMap;
    }
}
