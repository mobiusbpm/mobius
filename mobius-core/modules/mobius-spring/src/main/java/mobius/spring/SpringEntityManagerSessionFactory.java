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

package mobius.spring;

import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.common.engine.impl.interceptor.Session;
import mobius.common.engine.impl.interceptor.SessionFactory;
import mobius.variable.service.impl.types.EntityManagerSession;
import mobius.variable.service.impl.types.EntityManagerSessionImpl;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Session Factory for {@link EntityManagerSession}.
 * <p>
 * Must be used when the {@link EntityManagerFactory} is managed by Spring. This implementation will retrieve the {@link EntityManager} bound to the thread by Spring in case a transaction already
 * started.
 * 
 * @author Joram Barrez
 */
public class SpringEntityManagerSessionFactory implements SessionFactory {

    protected EntityManagerFactory entityManagerFactory;
    protected boolean handleTransactions;
    protected boolean closeEntityManager;

    public SpringEntityManagerSessionFactory(Object entityManagerFactory, boolean handleTransactions, boolean closeEntityManager) {
        this.entityManagerFactory = (EntityManagerFactory) entityManagerFactory;
        this.handleTransactions = handleTransactions;
        this.closeEntityManager = closeEntityManager;
    }

    @Override
    public Class<?> getSessionType() {
        return EntityManagerFactory.class;
    }

    @Override
    public Session openSession(CommandContext commandContext) {
        EntityManager entityManager = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory);
        if (entityManager == null) {
            return new EntityManagerSessionImpl(entityManagerFactory, handleTransactions, closeEntityManager);
        }
        return new EntityManagerSessionImpl(entityManagerFactory, entityManager, false, false);
    }

}
