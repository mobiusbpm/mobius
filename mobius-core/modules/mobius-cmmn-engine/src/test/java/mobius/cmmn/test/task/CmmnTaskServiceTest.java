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
package mobius.cmmn.test.task;

import static java.util.stream.Collectors.toSet;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import mobius.cmmn.api.history.HistoricCaseInstance;
import mobius.cmmn.api.runtime.CaseInstance;
import mobius.cmmn.api.runtime.PlanItemInstance;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.cmmn.engine.interceptor.CreateHumanTaskAfterContext;
import mobius.cmmn.engine.interceptor.CreateHumanTaskBeforeContext;
import mobius.cmmn.engine.interceptor.CreateHumanTaskInterceptor;
import mobius.cmmn.engine.test.CmmnDeployment;
import mobius.cmmn.engine.test.FlowableCmmnTestCase;
import mobius.common.engine.api.FlowableException;
import mobius.common.engine.api.scope.ScopeTypes;
import mobius.common.engine.impl.history.HistoryLevel;
import mobius.common.engine.impl.interceptor.CommandExecutor;
import mobius.entitylink.api.EntityLink;
import mobius.entitylink.api.EntityLinkService;
import mobius.entitylink.api.EntityLinkType;
import mobius.entitylink.api.HierarchyType;
import mobius.entitylink.api.history.HistoricEntityLink;
import mobius.entitylink.api.history.HistoricEntityLinkService;
import mobius.identitylink.api.IdentityLinkType;
import mobius.identitylink.service.impl.persistence.entity.IdentityLinkEntityImpl;
import mobius.task.api.Task;
import mobius.task.api.history.HistoricTaskInstance;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 *
 */
public class CmmnTaskServiceTest extends FlowableCmmnTestCase {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    @CmmnDeployment
    public void testOneHumanTaskCase() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneHumanTaskCase").start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        assertNotNull(task);
        assertEquals("The Task", task.getName());
        assertEquals("This is a test documentation", task.getDescription());
        assertEquals("johnDoe", task.getAssignee());

        if (cmmnEngineConfiguration.getHistoryLevel().isAtLeast(HistoryLevel.ACTIVITY)) {
            HistoricTaskInstance historicTaskInstance = cmmnHistoryService.createHistoricTaskInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
            assertNotNull(historicTaskInstance);
            assertNull(historicTaskInstance.getEndTime());
        }

        cmmnTaskService.complete(task.getId());
        assertCaseInstanceEnded(caseInstance);

        if (cmmnEngineConfiguration.getHistoryLevel().isAtLeast(HistoryLevel.ACTIVITY)) {
            HistoricTaskInstance historicTaskInstance = cmmnHistoryService.createHistoricTaskInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
            assertNotNull(historicTaskInstance);
            assertEquals("The Task", historicTaskInstance.getName());
            assertEquals("This is a test documentation", historicTaskInstance.getDescription());
            assertNotNull(historicTaskInstance.getEndTime());
        }
    }

    @Test
    @CmmnDeployment
    public void testOneHumanTaskExpressionCase() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                        .caseDefinitionKey("oneHumanTaskCase")
                        .variable("var1", "A")
                        .variable("var2", "YES")
                        .start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        assertNotNull(task);
        assertEquals("The Task A", task.getName());
        assertEquals("This is a test YES", task.getDescription());
        assertEquals("johnDoe", task.getAssignee());
        
        cmmnTaskService.complete(task.getId());
        assertCaseInstanceEnded(caseInstance);
        
        if (cmmnEngineConfiguration.getHistoryLevel().isAtLeast(HistoryLevel.ACTIVITY)) {
            HistoricTaskInstance historicTaskInstance = cmmnHistoryService.createHistoricTaskInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
            assertNotNull(historicTaskInstance);
            assertEquals("The Task A", historicTaskInstance.getName());
            assertEquals("This is a test YES", historicTaskInstance.getDescription());
            assertNotNull(historicTaskInstance.getEndTime());
        }
    }

    @Test
    @CmmnDeployment(resources = "mobius/cmmn/test/task/CmmnTaskServiceTest.testOneHumanTaskCase.cmmn")
    public void testOneHumanTaskVariableScopeExpressionCase() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneHumanTaskCase").start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();

        this.expectedException.expect(FlowableException.class);
        this.expectedException.expectMessage("Error while evaluating expression: ${caseInstance.name}");
        cmmnTaskService.complete(task.getId(), Collections.singletonMap(
                "${caseInstance.name}", "newCaseName"
            )
        );
    }

    @Test
    @CmmnDeployment(resources = "mobius/cmmn/test/task/CmmnTaskServiceTest.testOneHumanTaskCase.cmmn")
    public void testOneHumanTaskCompleteSetCaseName() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneHumanTaskCase").start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();

        this.expectedException.expect(FlowableException.class);
        this.expectedException.expectMessage("Error while evaluating expression: ${name}");
        cmmnTaskService.complete(task.getId(), Collections.singletonMap(
                "${name}", "newCaseName"
            )
        );
    }

    @Test
    @CmmnDeployment(resources = "mobius/cmmn/test/task/CmmnTaskServiceTest.testOneHumanTaskCase.cmmn")
    public void testOneHumanTaskCaseScopeExpression() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                        .caseDefinitionKey("oneHumanTaskCase")
                        .start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        cmmnTaskService.setVariable(task.getId(), "variableToUpdate", "VariableValue");

        cmmnTaskService.complete(task.getId(), Collections.singletonMap(
                "${variableToUpdate}", "updatedVariableValue"
            )
        );
        HistoricCaseInstance historicCaseInstance = cmmnHistoryService.createHistoricCaseInstanceQuery().caseInstanceId(caseInstance.getId()).
                includeCaseVariables().singleResult();
        assertThat(historicCaseInstance.getCaseVariables().get("variableToUpdate"), is("updatedVariableValue"));
    }

    @Test
    @CmmnDeployment(resources = "mobius/cmmn/test/task/CmmnTaskServiceTest.testOneHumanTaskCase.cmmn")
    public void testOneHumanTaskTaskScopeExpression() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                        .caseDefinitionKey("oneHumanTaskCase")
                        .start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        cmmnTaskService.setVariableLocal(task.getId(), "variableToUpdate", "VariableValue");

        cmmnTaskService.complete(task.getId(), Collections.singletonMap(
                "${variableToUpdate}", "updatedVariableValue"
            )
        );
        HistoricTaskInstance historicTaskInstance = cmmnHistoryService.createHistoricTaskInstanceQuery().caseInstanceId(caseInstance.getId()).
                includeTaskLocalVariables().singleResult();
        assertThat(historicTaskInstance.getTaskLocalVariables().get("variableToUpdate"), is("updatedVariableValue"));
    }

    @Test
    @CmmnDeployment(resources = "mobius/cmmn/test/task/CmmnTaskServiceTest.testOneHumanTaskCase.cmmn")
    public void testSetCaseNameByExpression() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                .variable("varToUpdate", "initialValue")
                .caseDefinitionKey("oneHumanTaskCase")
                .start();

        cmmnRuntimeService.setVariable(caseInstance.getId(), "${varToUpdate}", "newValue");

        CaseInstance updatedCaseInstance = cmmnRuntimeService.createCaseInstanceQuery().
                caseInstanceId(caseInstance.getId()).
                includeCaseVariables().
                singleResult();
        assertThat(updatedCaseInstance.getCaseVariables().get("varToUpdate"), is("newValue"));
    }

    @Test
    @CmmnDeployment
    public void testTriggerOneHumanTaskCaseProgrammatically() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneHumanTaskCase").start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().singleResult();
        assertEquals(planItemInstance.getId(), task.getSubScopeId());
        assertEquals(planItemInstance.getCaseInstanceId(), task.getScopeId());
        assertEquals(planItemInstance.getCaseDefinitionId(), task.getScopeDefinitionId());
        assertEquals(ScopeTypes.CMMN, task.getScopeType());
        
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
        assertEquals(0, cmmnTaskService.createTaskQuery().count());
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    public void testCreateTaskWithBuilderAndScopes() {
        Task task = cmmnTaskService.createTaskBuilder().name("builderTask").
            scopeId("testScopeId").
            scopeType("testScopeType").
            create();

        try {
            Task taskFromQuery = cmmnTaskService.createTaskQuery().taskId(task.getId()).singleResult();
            assertThat(taskFromQuery.getScopeId(), is("testScopeId"));
            assertThat(taskFromQuery.getScopeType(), is("testScopeType"));
        } finally {
            cmmnTaskService.deleteTask(task.getId(), true);
        }
    }

    @Test
    public void testCreateTaskWithBuilderWithoutScopes() {
        Task task = cmmnTaskService.createTaskBuilder().name("builderTask").
            create();
        try {
            Task taskFromQuery = cmmnTaskService.createTaskQuery().taskId(task.getId()).singleResult();
            assertThat(taskFromQuery.getScopeId(), nullValue());
            assertThat(taskFromQuery.getScopeType(), nullValue());
        } finally {
            cmmnTaskService.deleteTask(task.getId(), true);
        }
    }

    @Test
    @CmmnDeployment
    public void testEntityLinkCreation() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("entityLinkCreation").start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        assertNotNull(task);

        CommandExecutor commandExecutor = cmmnEngine.getCmmnEngineConfiguration().getCommandExecutor();

        List<EntityLink> entityLinks = commandExecutor.execute(commandContext -> {
            EntityLinkService entityLinkService = CommandContextUtil.getEntityLinkService(commandContext);

            return entityLinkService.findEntityLinksByScopeIdAndType(caseInstance.getId(), ScopeTypes.CMMN, EntityLinkType.CHILD);
        });

        assertEquals(1, entityLinks.size());
        assertEquals(HierarchyType.ROOT, entityLinks.get(0).getHierarchyType());

        cmmnTaskService.complete(task.getId());
        assertCaseInstanceEnded(caseInstance);

        List<HistoricEntityLink> entityLinksByScopeIdAndType = commandExecutor.execute(commandContext -> {
            HistoricEntityLinkService historicEntityLinkService = CommandContextUtil.getHistoricEntityLinkService(commandContext);

            return historicEntityLinkService.findHistoricEntityLinksByScopeIdAndScopeType(caseInstance.getId(), ScopeTypes.CMMN, EntityLinkType.CHILD);
        });

        assertEquals(1, entityLinksByScopeIdAndType.size());
        assertEquals(HierarchyType.ROOT, entityLinksByScopeIdAndType.get(0).getHierarchyType());
    }
    
    @Test
    @CmmnDeployment(resources="mobius/cmmn/test/task/CmmnTaskServiceTest.testOneHumanTaskCase.cmmn")
    public void testCreateHumanTaskInterceptor() {
        TestCreateHumanTaskInterceptor testCreateHumanTaskInterceptor = new TestCreateHumanTaskInterceptor();
        cmmnEngineConfiguration.setCreateHumanTaskInterceptor(testCreateHumanTaskInterceptor);
        
        try {
            CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneHumanTaskCase").start();
            Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
            assertNotNull(task);
            assertEquals("The Task", task.getName());
            assertEquals("This is a test documentation", task.getDescription());
            assertEquals("johnDoe", task.getAssignee());
            assertEquals("testCategory", task.getCategory());
            
            assertEquals(1, testCreateHumanTaskInterceptor.getBeforeCreateHumanTaskCounter());
            assertEquals(1, testCreateHumanTaskInterceptor.getAfterCreateHumanTaskCounter());
            
        } finally {
            cmmnEngineConfiguration.setCreateHumanTaskInterceptor(null);
        }
    }

    private static Set<IdentityLinkEntityImpl> getDefaultIdentityLinks() {
        IdentityLinkEntityImpl identityLinkEntityCandidateUser = new IdentityLinkEntityImpl();
        identityLinkEntityCandidateUser.setUserId("testUserFromBuilder");
        identityLinkEntityCandidateUser.setType(IdentityLinkType.CANDIDATE);
        IdentityLinkEntityImpl identityLinkEntityCandidateGroup = new IdentityLinkEntityImpl();
        identityLinkEntityCandidateGroup.setGroupId("testGroupFromBuilder");
        identityLinkEntityCandidateGroup.setType(IdentityLinkType.CANDIDATE);

        return Stream.of(
                identityLinkEntityCandidateUser,
                identityLinkEntityCandidateGroup
        ).collect(toSet());
    }
    
    protected class TestCreateHumanTaskInterceptor implements CreateHumanTaskInterceptor {
        
        protected int beforeCreateHumanTaskCounter = 0;
        protected int afterCreateHumanTaskCounter = 0;
        
        @Override
        public void beforeCreateHumanTask(CreateHumanTaskBeforeContext context) {
            beforeCreateHumanTaskCounter++;
            context.setCategory("testCategory");
        }

        @Override
        public void afterCreateHumanTask(CreateHumanTaskAfterContext context) {
            afterCreateHumanTaskCounter++;
        }

        public int getBeforeCreateHumanTaskCounter() {
            return beforeCreateHumanTaskCounter;
        }

        public int getAfterCreateHumanTaskCounter() {
            return afterCreateHumanTaskCounter;
        }
    }

}
