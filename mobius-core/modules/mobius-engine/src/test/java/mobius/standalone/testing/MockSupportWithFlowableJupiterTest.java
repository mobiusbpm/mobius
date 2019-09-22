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
package mobius.standalone.testing;

import static org.assertj.core.api.Assertions.assertThat;

import mobius.engine.ProcessEngine;
import mobius.engine.test.Deployment;
import mobius.engine.test.FlowableTest;
import mobius.engine.test.mock.FlowableMockSupport;
import mobius.engine.test.mock.MockServiceTask;
import mobius.engine.test.mock.NoOpServiceTasks;
import mobius.standalone.testing.helpers.ServiceTaskTestMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Filip Hrisafov
 */
@FlowableTest
@MockServiceTask(originalClassName = "com.yourcompany.delegate", mockedClass = ServiceTaskTestMock.class)
@MockServiceTask(originalClassName = "com.yourcompany.anotherDelegate", mockedClassName = "mobius.standalone.testing.helpers.ServiceTaskTestMock")
class MockSupportWithFlowableJupiterTest {

    private ProcessEngine processEngine;

    @BeforeEach
    void setUp(FlowableMockSupport mockSupport, ProcessEngine processEngine) {
        this.processEngine = processEngine;
        ServiceTaskTestMock.CALL_COUNT.set(0);
    }

    @Test
    @Deployment
    void testClassDelegateMockSupport() {
        assertThat(ServiceTaskTestMock.CALL_COUNT).hasValue(0);
        processEngine.getRuntimeService().startProcessInstanceByKey("mockSupportTest");
        assertThat(ServiceTaskTestMock.CALL_COUNT).hasValue(1);
    }

    @Test
    @Deployment
    void testClassDelegateStringMockSupport() {
        assertThat(ServiceTaskTestMock.CALL_COUNT).hasValue(0);
        processEngine.getRuntimeService().startProcessInstanceByKey("mockSupportTest");
        assertThat(ServiceTaskTestMock.CALL_COUNT).hasValue(1);
    }

    @Test
    @Deployment
    @MockServiceTask(originalClassName = "com.yourcompany.custom.delegate", mockedClassName = "mobius.standalone.testing.helpers.ServiceTaskTestMock")
    void testMockedServiceTaskAnnotation() {
        assertThat(ServiceTaskTestMock.CALL_COUNT).hasValue(0);
        processEngine.getRuntimeService().startProcessInstanceByKey("mockSupportTest");
        assertThat(ServiceTaskTestMock.CALL_COUNT).hasValue(1);
    }

    @Test
    @Deployment(resources = { "mobius/standalone/testing/MockSupportWithFlowableJupiterTest.testMockedServiceTaskAnnotation.bpmn20.xml" })
    @MockServiceTask(id = "serviceTask", mockedClassName = "mobius.standalone.testing.helpers.ServiceTaskTestMock")
    void testMockedServiceTaskByIdAnnotation() {
        assertThat(ServiceTaskTestMock.CALL_COUNT).hasValue(0);
        processEngine.getRuntimeService().startProcessInstanceByKey("mockSupportTest");
        assertThat(ServiceTaskTestMock.CALL_COUNT).hasValue(1);
    }

    @Test
    @Deployment
    @MockServiceTask(originalClassName = "com.yourcompany.delegate1", mockedClassName = "mobius.standalone.testing.helpers.ServiceTaskTestMock")
    @MockServiceTask(originalClassName = "com.yourcompany.delegate2", mockedClassName = "mobius.standalone.testing.helpers.ServiceTaskTestMock")
    void testMockedServiceTasksAnnotation() {
        assertThat(ServiceTaskTestMock.CALL_COUNT).hasValue(0);
        processEngine.getRuntimeService().startProcessInstanceByKey("mockSupportTest");
        assertThat(ServiceTaskTestMock.CALL_COUNT).hasValue(2);
    }

    @Test
    @Deployment
    @NoOpServiceTasks
    void testNoOpServiceTasksAnnotation(FlowableMockSupport mockSupport) {
        assertThat(mockSupport.getNrOfNoOpServiceTaskExecutions()).isEqualTo(0);
        processEngine.getRuntimeService().startProcessInstanceByKey("mockSupportTest");
        assertThat(mockSupport.getNrOfNoOpServiceTaskExecutions()).isEqualTo(5);

        assertThat(mockSupport.getExecutedNoOpServiceTaskDelegateClassNames())
            .containsExactly(
                "com.yourcompany.delegate1",
                "com.yourcompany.delegate2",
                "com.yourcompany.delegate3",
                "com.yourcompany.delegate4",
                "com.yourcompany.delegate5"
            );
    }

    @Test
    @Deployment(resources = { "mobius/standalone/testing/MockSupportWithFlowableJupiterTest.testNoOpServiceTasksAnnotation.bpmn20.xml" })
    @NoOpServiceTasks(ids = { "serviceTask1", "serviceTask3", "serviceTask5" }, classNames = { "com.yourcompany.delegate2", "com.yourcompany.delegate4" })
    void testNoOpServiceTasksWithIdsAnnotation(FlowableMockSupport mockSupport) {
        assertThat(mockSupport.getNrOfNoOpServiceTaskExecutions()).isEqualTo(0);
        processEngine.getRuntimeService().startProcessInstanceByKey("mockSupportTest");
        assertThat(mockSupport.getNrOfNoOpServiceTaskExecutions()).isEqualTo(5);

        assertThat(mockSupport.getExecutedNoOpServiceTaskDelegateClassNames())
            .containsExactly(
                "com.yourcompany.delegate1",
                "com.yourcompany.delegate2",
                "com.yourcompany.delegate3",
                "com.yourcompany.delegate4",
                "com.yourcompany.delegate5"
            );
    }

}
