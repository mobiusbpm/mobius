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

package mobius.rest.service.api.history;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import mobius.common.engine.impl.identity.Authentication;
import mobius.engine.runtime.ProcessInstance;
import mobius.engine.test.Deployment;
import mobius.rest.service.BaseSpringRestTestCase;
import mobius.rest.service.api.RestUrls;
import mobius.task.api.Task;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.junit.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * Test for REST-operation related to the historic process instance query resource.
 * 
 *
 */
public class HistoricProcessInstanceQueryResourceTest extends BaseSpringRestTestCase {

    /**
     * Test querying historic process instance based on variables. POST query/historic-process-instances
     */
    @Test
    @Deployment
    public void testQueryProcessInstancesWithVariables() throws Exception {
        HashMap<String, Object> processVariables = new HashMap<>();
        processVariables.put("stringVar", "Azerty");
        processVariables.put("intVar", 67890);
        processVariables.put("booleanVar", false);

        Authentication.setAuthenticatedUserId("historyQueryAndSortUser");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTaskProcess", processVariables);
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        taskService.complete(task.getId());

        ProcessInstance processInstance2 = runtimeService.startProcessInstanceByKey("oneTaskProcess", processVariables);

        String url = RestUrls.createRelativeResourceUrl(RestUrls.URL_HISTORIC_PROCESS_INSTANCE_QUERY);

        // Process variables
        ObjectNode requestNode = objectMapper.createObjectNode();
        ArrayNode variableArray = objectMapper.createArrayNode();
        ObjectNode variableNode = objectMapper.createObjectNode();
        variableArray.add(variableNode);
        requestNode.set("variables", variableArray);

        // String equals
        variableNode.put("name", "stringVar");
        variableNode.put("value", "Azerty");
        variableNode.put("operation", "equals");
        assertResultsPresentInPostDataResponse(url, requestNode, processInstance.getId(), processInstance2.getId());

        // Integer equals
        variableNode.removeAll();
        variableNode.put("name", "intVar");
        variableNode.put("value", 67890);
        variableNode.put("operation", "equals");
        assertResultsPresentInPostDataResponse(url, requestNode, processInstance.getId(), processInstance2.getId());

        // Boolean equals
        variableNode.removeAll();
        variableNode.put("name", "booleanVar");
        variableNode.put("value", false);
        variableNode.put("operation", "equals");
        assertResultsPresentInPostDataResponse(url, requestNode, processInstance.getId(), processInstance2.getId());

        // String not equals
        variableNode.removeAll();
        variableNode.put("name", "stringVar");
        variableNode.put("value", "ghijkl");
        variableNode.put("operation", "notEquals");
        assertResultsPresentInPostDataResponse(url, requestNode, processInstance.getId(), processInstance2.getId());

        // Integer not equals
        variableNode.removeAll();
        variableNode.put("name", "intVar");
        variableNode.put("value", 45678);
        variableNode.put("operation", "notEquals");
        assertResultsPresentInPostDataResponse(url, requestNode, processInstance.getId(), processInstance2.getId());

        // Boolean not equals
        variableNode.removeAll();
        variableNode.put("name", "booleanVar");
        variableNode.put("value", true);
        variableNode.put("operation", "notEquals");
        assertResultsPresentInPostDataResponse(url, requestNode, processInstance.getId(), processInstance2.getId());

        // String equals ignore case
        variableNode.removeAll();
        variableNode.put("name", "stringVar");
        variableNode.put("value", "azeRTY");
        variableNode.put("operation", "equalsIgnoreCase");
        assertResultsPresentInPostDataResponse(url, requestNode, processInstance.getId(), processInstance2.getId());

        // String not equals ignore case (not supported)
        variableNode.removeAll();
        variableNode.put("name", "stringVar");
        variableNode.put("value", "HIJKLm");
        variableNode.put("operation", "notEqualsIgnoreCase");
        assertErrorResult(url, requestNode, HttpStatus.SC_BAD_REQUEST);

        // String equals without value
        variableNode.removeAll();
        variableNode.put("value", "Azerty");
        variableNode.put("operation", "equals");
        assertResultsPresentInPostDataResponse(url, requestNode, processInstance.getId(), processInstance2.getId());

        // String equals with non existing value
        variableNode.removeAll();
        variableNode.put("value", "Azerty2");
        variableNode.put("operation", "equals");
        assertResultsPresentInPostDataResponse(url, requestNode);

        // String like ignore case
        variableNode.removeAll();
        variableNode.put("name", "stringVar");
        variableNode.put("value", "azerty");
        variableNode.put("operation", "likeIgnoreCase");
        assertResultsPresentInPostDataResponse(url, requestNode, processInstance.getId(), processInstance2.getId());

        variableNode.removeAll();
        variableNode.put("name", "stringVar");
        variableNode.put("value", "azerty2");
        variableNode.put("operation", "likeIgnoreCase");
        assertResultsPresentInPostDataResponse(url, requestNode);

        requestNode = objectMapper.createObjectNode();
        requestNode.put("finished", true);
        assertResultsPresentInPostDataResponse(url, requestNode, processInstance.getId());

        requestNode = objectMapper.createObjectNode();
        requestNode.put("finished", false);
        assertResultsPresentInPostDataResponse(url, requestNode, processInstance2.getId());

        requestNode = objectMapper.createObjectNode();
        requestNode.put("processDefinitionId", processInstance.getProcessDefinitionId());
        assertResultsPresentInPostDataResponse(url, requestNode, processInstance.getId(), processInstance2.getId());

        requestNode = objectMapper.createObjectNode();
        requestNode.put("processDefinitionKey", "oneTaskProcess");
        assertResultsPresentInPostDataResponse(url, requestNode, processInstance.getId(), processInstance2.getId());

        requestNode = objectMapper.createObjectNode();
        requestNode.put("processDefinitionKey", "oneTaskProcess");

        HttpPost httpPost = new HttpPost(SERVER_URL_PREFIX + url + "?sort=startTime");
        httpPost.setEntity(new StringEntity(requestNode.toString()));
        CloseableHttpResponse response = executeRequest(httpPost, HttpStatus.SC_OK);

        // Check status and size
        JsonNode dataNode = objectMapper.readTree(response.getEntity().getContent()).get("data");
        closeResponse(response);
        assertEquals(2, dataNode.size());
        JsonNode valueNode = dataNode.get(0);
        assertEquals(processInstance.getId(), valueNode.get("id").asText());
        assertEquals(processInstance2.getId(), dataNode.get(1).get("id").asText());
        
        assertEquals("The One Task Process", valueNode.get("processDefinitionName").asText());
        assertEquals("One task process description", valueNode.get("processDefinitionDescription").asText());
        assertThat(valueNode.has("startTime")).as("has startTime").isTrue();
        assertThat(valueNode.get("startUserId").textValue()).as("startUserId").isEqualTo(processInstance.getStartUserId());
    }
}
