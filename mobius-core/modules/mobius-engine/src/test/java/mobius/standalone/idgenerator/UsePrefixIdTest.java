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
package mobius.standalone.idgenerator;

import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import mobius.task.api.Task;
import org.assertj.core.api.Assertions;
import mobius.engine.history.HistoricActivityInstance;
import mobius.engine.history.HistoricProcessInstance;
import mobius.engine.impl.test.ResourceFlowableTestCase;
import mobius.engine.repository.ProcessDefinition;
import mobius.engine.test.Deployment;
import mobius.variable.api.history.HistoricVariableInstance;
import org.junit.jupiter.api.Test;

public class UsePrefixIdTest extends ResourceFlowableTestCase {

    public UsePrefixIdTest() throws Exception {
        super("mobius/standalone/idgenerator/prefixid.test.flowable.cfg.xml");
    }

    @Test
    @Deployment(resources = "mobius/standalone/idgenerator/prefixidtest.bpmn20.xml")
    public void testUuidGeneratorUsage() {
        mobius.engine.repository.Deployment deployment = repositoryService.createDeploymentQuery()
            .processDefinitionKey("simpleProcess")
            .singleResult();
        assertThat(deployment.getId(), startsWith("PRC-"));
        
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
            .processDefinitionKey("simpleProcess")
            .singleResult();
        assertThat(processDefinition.getId(), startsWith("PRC-"));

        ExecutorService executorService = Executors.newFixedThreadPool(3);

        // Start processes
        Map<String, Object> varMap = new HashMap<>();
        varMap.put("testPrefixVar", "tested");
        for (int i = 0; i < 5; i++) {
            executorService.execute(() -> {
                try {
                    runtimeService.startProcessInstanceByKey("simpleProcess", varMap);
                } catch (Exception e) {
                    e.printStackTrace();
                    fail();
                }
            });
        }

        // Complete tasks
        executorService.execute(() -> {
            boolean tasksFound = true;
            while (tasksFound) {

                List<Task> tasks = taskService.createTaskQuery().list();
                for (Task task : tasks) {
                    assertThat(task.getId(), startsWith("TSK-"));
                    taskService.complete(task.getId());
                }

                tasksFound = taskService.createTaskQuery().count() > 0;

                if (!tasksFound) {
                    try {
                        Thread.sleep(1500L); // just to be sure
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    tasksFound = taskService.createTaskQuery().count() > 0;
                }
            }
        });

        try {
            executorService.shutdown();
            executorService.awaitTermination(20, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(5, historyService.createHistoricProcessInstanceQuery().processDefinitionKey("simpleProcess").count());
        List<HistoricProcessInstance> processInstances = historyService.createHistoricProcessInstanceQuery().list();
        for (HistoricProcessInstance historicProcessInstance : processInstances) {
            assertThat(historicProcessInstance.getId(), startsWith("PRC-"));
        }
        
        List<HistoricActivityInstance> activityInstances = historyService.createHistoricActivityInstanceQuery()
                .processDefinitionId(processDefinition.getId())
                .list();
        for (HistoricActivityInstance activityInstance : activityInstances) {
            assertThat(activityInstance.getId(), startsWith("PRC-"));
        }
        
        historyService.createHistoricTaskInstanceQuery()
                .processDefinitionId(processDefinition.getId())
                .list()
                .forEach(historicTask -> assertThat(historicTask.getId(), startsWith("TSK-")) );
        
        List<HistoricVariableInstance> variableInstances = historyService.createHistoricVariableInstanceQuery()
                .variableName("testPrefixVar")
                .list();
        assertEquals(5, variableInstances.size());
        for (HistoricVariableInstance variableInstance : variableInstances) {
            assertThat(variableInstance.getId(), startsWith("VAR-"));
        }
    }

    @Test
    void testUUIDGeneratorProcessDefinitionId() {
        mobius.engine.repository.Deployment deployment = repositoryService.createDeployment()
            .addClasspathResource("mobius/standalone/idgenerator/prefixidtest.bpmn20.xml")
            .deploy();

        try {

            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("simpleProcess")
                .singleResult();

            Assertions.assertThat(processDefinition.getId()).startsWith("PRC-simpleProcess:1:");

        } finally {
            repositoryService.deleteDeployment(deployment.getId(), true);
        }
    }

    @Test
    void testUUIDGeneratorProcessDefinitionIdWithLongDefinitionKey() {
        mobius.engine.repository.Deployment deployment = repositoryService.createDeployment()
            .addClasspathResource("mobius/standalone/idgenerator/prefixidtest-long-key.bpmn20.xml")
            .deploy();

        try {

            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("simpleProcessWithAVeryLongKey")
                .singleResult();

            Assertions.assertThat(processDefinition.getId())
                .startsWith("PRC-")
                .doesNotContain("simpleProcess");

        } finally {
            repositoryService.deleteDeployment(deployment.getId(), true);
        }
    }
}
