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
package mobius.standalone.jpa;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import mobius.engine.impl.test.ResourceFlowableTestCase;
import mobius.engine.runtime.ProcessInstance;
import mobius.engine.test.Deployment;
import mobius.task.api.Task;
import mobius.variable.service.impl.types.EntityManagerSession;
import mobius.variable.service.impl.types.EntityManagerSessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test for JPA enhancement support
 * 
 * @author <a href="mailto:eugene.khrustalev@gmail.com">Eugene Khrustalev</a>
 */
@Tag("jpa")
public class JPAEnhancedVariableTest extends ResourceFlowableTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(JPAEnhancedVariableTest.class);
    private EntityManagerFactory entityManagerFactory;

    private FieldAccessJPAEntity fieldEntity;
    private FieldAccessJPAEntity fieldEntity2;
    private PropertyAccessJPAEntity propertyEntity;

    public JPAEnhancedVariableTest() {
        super("mobius/standalone/jpa/flowable.cfg.xml");
    }

    @BeforeEach
    protected void setUp() {
        entityManagerFactory = ((EntityManagerSessionFactory) processEngineConfiguration.getSessionFactories().get(EntityManagerSession.class))
            .getEntityManagerFactory();
        setupJPAVariables();
    }

    private void setupJPAVariables() {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();

        fieldEntity = new FieldAccessJPAEntity();
        fieldEntity.setId(1L);
        fieldEntity.setValue("fieldEntity");
        em.persist(fieldEntity);

        propertyEntity = new PropertyAccessJPAEntity();
        propertyEntity.setId(1L);
        propertyEntity.setValue("propertyEntity");
        em.persist(propertyEntity);

        em.flush();
        em.getTransaction().commit();
        em.close();

        // load enhanced versions of entities
        em = entityManagerFactory.createEntityManager();

        fieldEntity = em.find(FieldAccessJPAEntity.class, fieldEntity.getId());
        propertyEntity = em.find(PropertyAccessJPAEntity.class, propertyEntity.getId());

        em.getTransaction().begin();

        fieldEntity2 = new FieldAccessJPAEntity();
        fieldEntity2.setId(2L);
        fieldEntity2.setValue("fieldEntity2");
        em.persist(fieldEntity2);

        em.flush();
        em.getTransaction().commit();
        em.close();
    }

    private Task getTask(ProcessInstance instance) {
        return processEngine.getTaskService().createTaskQuery().processInstanceId(instance.getProcessInstanceId()).includeProcessVariables().singleResult();
    }

    @Test
    @Deployment(resources = { "mobius/standalone/jpa/JPAVariableTest.testStoreJPAEntityAsVariable.bpmn20.xml" })
    public void testEnhancedEntityVariables() throws Exception {
        // test if enhancement is used
        if (FieldAccessJPAEntity.class == fieldEntity.getClass() || PropertyAccessJPAEntity.class == propertyEntity.getClass()) {
            LOGGER.warn("Entity enhancement is not used");
            return;
        }

        // start process with enhanced jpa variables
        Map<String, Object> params = new HashMap<>();
        params.put("fieldEntity", fieldEntity);
        params.put("propertyEntity", propertyEntity);
        ProcessInstance instance = processEngine.getRuntimeService().startProcessInstanceByKey("JPAVariableProcess", params);

        Task task = getTask(instance);
        for (Map.Entry<String, Object> entry : task.getProcessVariables().entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();
            if (name.equals("fieldEntity")) {
                assertTrue(value instanceof FieldAccessJPAEntity);
            } else if (name.equals("propertyEntity")) {
                assertTrue(value instanceof PropertyAccessJPAEntity);
            } else {
                fail();
            }
        }
    }

    @Test
    @Deployment(resources = { "mobius/standalone/jpa/JPAVariableTest.testStoreJPAEntityAsVariable.bpmn20.xml" })
    public void testEnhancedEntityListVariables() throws Exception {
        // test if enhancement is used
        if (FieldAccessJPAEntity.class == fieldEntity.getClass() || PropertyAccessJPAEntity.class == propertyEntity.getClass()) {
            LOGGER.warn("Entity enhancement is not used");
            return;
        }

        // start process with lists of enhanced jpa variables
        Map<String, Object> params = new HashMap<>();
        params.put("list1", Arrays.asList(fieldEntity, fieldEntity));
        params.put("list2", Arrays.asList(propertyEntity, propertyEntity));
        ProcessInstance instance = processEngine.getRuntimeService().startProcessInstanceByKey("JPAVariableProcess", params);

        Task task = getTask(instance);
        List list = (List) task.getProcessVariables().get("list1");
        assertEquals(2, list.size());
        assertTrue(list.get(0) instanceof FieldAccessJPAEntity);
        assertTrue(list.get(1) instanceof FieldAccessJPAEntity);

        list = (List) task.getProcessVariables().get("list2");
        assertEquals(2, list.size());
        assertTrue(list.get(0) instanceof PropertyAccessJPAEntity);
        assertTrue(list.get(1) instanceof PropertyAccessJPAEntity);

        // start process with enhanced and persisted only jpa variables in the
        // same list
        params.putAll(Collections.singletonMap("list", Arrays.asList(fieldEntity, fieldEntity2)));
        instance = processEngine.getRuntimeService().startProcessInstanceByKey("JPAVariableProcess", params);

        task = getTask(instance);
        list = (List) task.getProcessVariables().get("list");
        assertEquals(2, list.size());
        assertTrue(list.get(0) instanceof FieldAccessJPAEntity);
        assertEquals(1L, (long) ((FieldAccessJPAEntity) list.get(0)).getId());
        assertTrue(list.get(1) instanceof FieldAccessJPAEntity);
        assertEquals(2L, (long) ((FieldAccessJPAEntity) list.get(1)).getId());

        // shuffle list and start a new process
        params.putAll(Collections.singletonMap("list", Arrays.asList(fieldEntity2, fieldEntity)));
        instance = processEngine.getRuntimeService().startProcessInstanceByKey("JPAVariableProcess", params);

        task = getTask(instance);
        list = (List) task.getProcessVariables().get("list");
        assertEquals(2, list.size());
        assertTrue(list.get(0) instanceof FieldAccessJPAEntity);
        assertEquals(2L, (long) ((FieldAccessJPAEntity) list.get(0)).getId());
        assertTrue(list.get(1) instanceof FieldAccessJPAEntity);
        assertEquals(1L, (long) ((FieldAccessJPAEntity) list.get(1)).getId());

        // start process with mixed jpa entities in list
        try {
            params = new HashMap<>();
            params.put("list", Arrays.asList(fieldEntity, propertyEntity));
            instance = processEngine.getRuntimeService().startProcessInstanceByKey("JPAVariableProcess", params);
            fail();
        } catch (Exception e) {
            /* do nothing */
        }
    }
}
