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
import static org.junit.Assert.assertTrue;

import java.util.List;

import mobius.rest.service.BaseSpringRestTestCase;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import mobius.engine.runtime.ProcessInstance;
import mobius.engine.task.Comment;
import mobius.engine.test.Deployment;
import mobius.rest.service.api.RestUrls;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Frederik Heremans
 */
public class HistoricProcessInstanceCommentResourceTest extends BaseSpringRestTestCase {

    /**
     * Test getting all comments for a historic process instance. GET history/historic-process-instances/{processInstanceId}/comments
     */
    @Test
    @Deployment(resources = { "mobius/rest/service/api/repository/oneTaskProcess.bpmn20.xml" })
    public void testGetComments() throws Exception {
        ProcessInstance pi = null;

        try {
            pi = runtimeService.startProcessInstanceByKey("oneTaskProcess");

            // Add a comment as "kermit"
            identityService.setAuthenticatedUserId("kermit");
            Comment comment = taskService.addComment(null, pi.getId(), "This is a comment...");
            identityService.setAuthenticatedUserId(null);

            CloseableHttpResponse response = executeRequest(new HttpGet(SERVER_URL_PREFIX + RestUrls.createRelativeResourceUrl(RestUrls.URL_HISTORIC_PROCESS_INSTANCE_COMMENT_COLLECTION, pi.getId())),
                    HttpStatus.SC_OK);

            assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

            JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
            closeResponse(response);
            assertNotNull(responseNode);
            assertTrue(responseNode.isArray());
            assertEquals(1, responseNode.size());

            ObjectNode commentNode = (ObjectNode) responseNode.get(0);
            assertEquals("kermit", commentNode.get("author").textValue());
            assertEquals("This is a comment...", commentNode.get("message").textValue());
            assertEquals(comment.getId(), commentNode.get("id").textValue());
            assertTrue(commentNode.get("processInstanceUrl").textValue().endsWith(RestUrls.createRelativeResourceUrl(RestUrls.URL_HISTORIC_PROCESS_INSTANCE_COMMENT, pi.getId(), comment.getId())));
            assertEquals(pi.getProcessInstanceId(), commentNode.get("processInstanceId").asText());
            assertTrue(commentNode.get("taskUrl").isNull());
            assertTrue(commentNode.get("taskId").isNull());

            // Test with unexisting task
            closeResponse(executeRequest(new HttpGet(SERVER_URL_PREFIX + RestUrls.createRelativeResourceUrl(RestUrls.URL_TASK_COMMENT_COLLECTION, "unexistingtask")), HttpStatus.SC_NOT_FOUND));

        } finally {
            if (pi != null) {
                List<Comment> comments = taskService.getProcessInstanceComments(pi.getId());
                for (Comment c : comments) {
                    taskService.deleteComment(c.getId());
                }
            }
        }
    }

    /**
     * Test creating a comment for a process instance. POST history/historic-process-instances/{processInstanceId}/comments
     */
    @Test
    @Deployment(resources = { "mobius/rest/service/api/repository/oneTaskProcess.bpmn20.xml" })
    public void testCreateComment() throws Exception {
        ProcessInstance pi = null;

        try {
            pi = runtimeService.startProcessInstanceByKey("oneTaskProcess");

            HttpPost httpPost = new HttpPost(SERVER_URL_PREFIX + RestUrls.createRelativeResourceUrl(RestUrls.URL_HISTORIC_PROCESS_INSTANCE_COMMENT_COLLECTION, pi.getId()));
            ObjectNode requestNode = objectMapper.createObjectNode();
            requestNode.put("message", "This is a comment...");
            httpPost.setEntity(new StringEntity(requestNode.toString()));

            CloseableHttpResponse response = executeRequest(httpPost, HttpStatus.SC_CREATED);

            assertEquals(HttpStatus.SC_CREATED, response.getStatusLine().getStatusCode());

            List<Comment> commentsOnProcess = taskService.getProcessInstanceComments(pi.getId());
            assertNotNull(commentsOnProcess);
            assertEquals(1, commentsOnProcess.size());

            JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
            closeResponse(response);
            assertNotNull(responseNode);
            assertEquals("kermit", responseNode.get("author").textValue());
            assertEquals("This is a comment...", responseNode.get("message").textValue());
            assertEquals(commentsOnProcess.get(0).getId(), responseNode.get("id").textValue());
            assertTrue(responseNode.get("processInstanceUrl").textValue()
                    .endsWith(RestUrls.createRelativeResourceUrl(RestUrls.URL_HISTORIC_PROCESS_INSTANCE_COMMENT, pi.getId(), commentsOnProcess.get(0).getId())));
            assertEquals(pi.getProcessInstanceId(), responseNode.get("processInstanceId").asText());
            assertTrue(responseNode.get("taskUrl").isNull());
            assertTrue(responseNode.get("taskId").isNull());

        } finally {
            if (pi != null) {
                List<Comment> comments = taskService.getProcessInstanceComments(pi.getId());
                for (Comment c : comments) {
                    taskService.deleteComment(c.getId());
                }
            }
        }
    }

    /**
     * Test getting a comment for a historic process instance. GET history/historic -process-instances/{processInstanceId}/comments/{commentId}
     */
    @Test
    @Deployment(resources = { "mobius/rest/service/api/repository/oneTaskProcess.bpmn20.xml" })
    public void testGetComment() throws Exception {
        ProcessInstance pi = null;

        try {
            pi = runtimeService.startProcessInstanceByKey("oneTaskProcess");

            // Add a comment as "kermit"
            identityService.setAuthenticatedUserId("kermit");
            Comment comment = taskService.addComment(null, pi.getId(), "This is a comment...");
            identityService.setAuthenticatedUserId(null);

            CloseableHttpResponse response = executeRequest(new HttpGet(SERVER_URL_PREFIX + RestUrls.createRelativeResourceUrl(RestUrls.URL_HISTORIC_PROCESS_INSTANCE_COMMENT, pi.getId(), comment.getId())),
                    200);

            assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

            JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
            closeResponse(response);
            assertNotNull(responseNode);

            assertEquals("kermit", responseNode.get("author").textValue());
            assertEquals("This is a comment...", responseNode.get("message").textValue());
            assertEquals(comment.getId(), responseNode.get("id").textValue());
            assertTrue(responseNode.get("processInstanceUrl").textValue().endsWith(RestUrls.createRelativeResourceUrl(RestUrls.URL_HISTORIC_PROCESS_INSTANCE_COMMENT, pi.getId(), comment.getId())));
            assertEquals(pi.getProcessInstanceId(), responseNode.get("processInstanceId").asText());
            assertTrue(responseNode.get("taskUrl").isNull());
            assertTrue(responseNode.get("taskId").isNull());

            // Test with unexisting process-instance
            closeResponse(executeRequest(new HttpGet(SERVER_URL_PREFIX + RestUrls.createRelativeResourceUrl(RestUrls.URL_HISTORIC_PROCESS_INSTANCE_COMMENT, "unexistinginstance", "123")),
                    HttpStatus.SC_NOT_FOUND));

            closeResponse(executeRequest(new HttpGet(SERVER_URL_PREFIX + RestUrls.createRelativeResourceUrl(RestUrls.URL_HISTORIC_PROCESS_INSTANCE_COMMENT, pi.getId(), "unexistingcomment")),
                    HttpStatus.SC_NOT_FOUND));

        } finally {
            if (pi != null) {
                List<Comment> comments = taskService.getProcessInstanceComments(pi.getId());
                for (Comment c : comments) {
                    taskService.deleteComment(c.getId());
                }
            }
        }
    }

    /**
     * Test deleting a comment for a task. DELETE runtime/tasks/{taskId}/comments/{commentId}
     */
    @Test
    @Deployment(resources = { "mobius/rest/service/api/repository/oneTaskProcess.bpmn20.xml" })
    public void testDeleteComment() throws Exception {
        ProcessInstance pi = null;

        try {
            pi = runtimeService.startProcessInstanceByKey("oneTaskProcess");

            // Add a comment as "kermit"
            identityService.setAuthenticatedUserId("kermit");
            Comment comment = taskService.addComment(null, pi.getId(), "This is a comment...");
            identityService.setAuthenticatedUserId(null);

            closeResponse(executeRequest(new HttpDelete(SERVER_URL_PREFIX + RestUrls.createRelativeResourceUrl(RestUrls.URL_HISTORIC_PROCESS_INSTANCE_COMMENT, pi.getId(), comment.getId())),
                    HttpStatus.SC_NO_CONTENT));

            // Test with unexisting instance
            closeResponse(executeRequest(new HttpGet(SERVER_URL_PREFIX + RestUrls.createRelativeResourceUrl(RestUrls.URL_HISTORIC_PROCESS_INSTANCE_COMMENT, "unexistinginstance", "123")),
                    HttpStatus.SC_NOT_FOUND));

            // Test with unexisting comment
            closeResponse(executeRequest(new HttpGet(SERVER_URL_PREFIX + RestUrls.createRelativeResourceUrl(RestUrls.URL_HISTORIC_PROCESS_INSTANCE_COMMENT, pi.getId(), "unexistingcomment")),
                    HttpStatus.SC_NOT_FOUND));

        } finally {
            if (pi != null) {
                List<Comment> comments = taskService.getProcessInstanceComments(pi.getId());
                for (Comment c : comments) {
                    taskService.deleteComment(c.getId());
                }
            }
        }
    }
}
