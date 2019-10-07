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
package mobius.mule;

import mobius.engine.ProcessEngine;
import mobius.engine.ProcessEngines;
import mobius.engine.RepositoryService;
import mobius.engine.RuntimeService;
import mobius.engine.repository.Deployment;
import mobius.engine.runtime.ProcessInstance;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Esteban Robles Luna
 *
 */
public class MuleVMTest extends AbstractMuleTest {

    @Test
    public void send() throws Exception {
        Assert.assertTrue(muleContext.isStarted());

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment().addClasspathResource("mobius/mule/testVM.bpmn20.xml").deploy();

        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("muleProcess");
        Assert.assertFalse(processInstance.isEnded());
        Object result = runtimeService.getVariable(processInstance.getProcessInstanceId(), "theVariable");
        Assert.assertEquals(30, result);
        runtimeService.deleteProcessInstance(processInstance.getId(), "test");

        processEngine.getHistoryService().deleteHistoricProcessInstance(processInstance.getId());
        repositoryService.deleteDeployment(deployment.getId());
        assertAndEnsureCleanDb(processEngine);
        ProcessEngines.destroy();
    }

    @Override
    protected String getConfigFile() {
        return "mule-config.xml";
    }
}
