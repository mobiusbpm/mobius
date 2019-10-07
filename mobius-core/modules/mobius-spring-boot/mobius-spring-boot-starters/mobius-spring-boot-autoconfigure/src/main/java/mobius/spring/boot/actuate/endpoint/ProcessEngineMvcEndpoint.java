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
package mobius.spring.boot.actuate.endpoint;

import java.io.InputStream;

import mobius.bpmn.BpmnAutoLayout;
import mobius.bpmn.model.BpmnModel;
import mobius.engine.RepositoryService;
import mobius.engine.repository.ProcessDefinition;
import mobius.image.ProcessDiagramGenerator;
import mobius.image.impl.DefaultProcessDiagramGenerator;
import org.springframework.boot.actuate.endpoint.web.annotation.EndpointWebExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Renders a valid running BPMN process definition as a BPMN diagram.
 *
 * This is duplicated functionality in the full REST API implementation.
 *
 *
 * @author Josh Long
 */
@EndpointWebExtension(endpoint = ProcessEngineEndpoint.class)
public class ProcessEngineMvcEndpoint {

    private final RepositoryService repositoryService;

    public ProcessEngineMvcEndpoint(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    /**
     * Look up the process definition by key. For example, this is <A href="http://localhost:8080/activiti/processes/fulfillmentProcess">process-diagram for</A> a process definition named
     * {@code fulfillmentProcess}.
     */
    @RequestMapping(value = "/processes/{processDefinitionKey:.*}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public ResponseEntity processDefinitionDiagram(@PathVariable String processDefinitionKey) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey)
                .latestVersion()
                .singleResult();
        if (processDefinition == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        ProcessDiagramGenerator processDiagramGenerator = new DefaultProcessDiagramGenerator();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());

        if (bpmnModel.getLocationMap().size() == 0) {
            BpmnAutoLayout autoLayout = new BpmnAutoLayout(bpmnModel);
            autoLayout.execute();
        }

        InputStream is = processDiagramGenerator.generateJpgDiagram(bpmnModel);
        return ResponseEntity.ok(new InputStreamResource(is));
    }

}
