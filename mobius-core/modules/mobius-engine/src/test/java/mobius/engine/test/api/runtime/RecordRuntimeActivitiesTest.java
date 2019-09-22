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
package mobius.engine.test.api.runtime;

import mobius.engine.ProcessEngine;
import mobius.engine.ProcessEngineConfiguration;
import mobius.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import mobius.engine.impl.test.AbstractTestCase;
import mobius.engine.repository.Deployment;
import org.junit.jupiter.api.Test;

public class RecordRuntimeActivitiesTest extends AbstractTestCase {

    @Test
    public void mandatoryRecordRuntimeActivities() {
        ProcessEngineConfiguration processEngineConfiguration = new StandaloneInMemProcessEngineConfiguration()
            .setJdbcUrl("jdbc:h2:mem:RecordRuntimeActivitiesTest");
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();

        Deployment deployment = processEngine.getRepositoryService().createDeployment()
            .addClasspathResource("mobius/engine/test/api/oneTaskProcess.bpmn20.xml")
            .deploy();

        try {
            processEngine.getRuntimeService().startProcessInstanceByKey("oneTaskProcess");

            assertTrue(processEngine.getRuntimeService().createActivityInstanceQuery().count() > 0L);
        } finally {
            processEngine.getRepositoryService().deleteDeployment(deployment.getId(), true);
            processEngine.close();
        }

    }

}
