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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import mobius.rest.service.BaseSpringRestTestCase;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import mobius.engine.runtime.ProcessInstance;
import mobius.engine.test.Deployment;
import mobius.rest.service.api.RestUrls;
import mobius.task.api.Task;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Test for REST-operation related to get and delete a historic process instance.
 * 
 *
 */
public class HistoricProcessInstanceResourceTest extends BaseSpringRestTestCase {

    /**
     * Test retrieval of historic process instance. GET history/historic-process-instances/{processInstanceId}
     */
    @Test
    @Deployment(resources = { "mobius/rest/service/api/repository/oneTaskProcess.bpmn20.xml" })
    public void testGetProcessInstance() throws Exception {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTaskProcess");

        CloseableHttpResponse response = executeRequest(new HttpGet(SERVER_URL_PREFIX + RestUrls.createRelativeResourceUrl(RestUrls.URL_HISTORIC_PROCESS_INSTANCE, processInstance.getId())),
                HttpStatus.SC_OK);

        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

        JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
        closeResponse(response);
        assertNotNull(responseNode);
        assertEquals(processInstance.getId(), responseNode.get("id").textValue());

        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        assertNotNull(task);
        taskService.complete(task.getId());

        response = executeRequest(new HttpDelete(SERVER_URL_PREFIX + RestUrls.createRelativeResourceUrl(RestUrls.URL_HISTORIC_PROCESS_INSTANCE, processInstance.getId())), HttpStatus.SC_NO_CONTENT);
        assertEquals(HttpStatus.SC_NO_CONTENT, response.getStatusLine().getStatusCode());
        closeResponse(response);
    }
}
