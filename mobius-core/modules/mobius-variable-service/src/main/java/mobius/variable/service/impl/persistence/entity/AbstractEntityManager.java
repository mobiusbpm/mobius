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
package mobius.variable.service.impl.persistence.entity;

import mobius.common.engine.api.delegate.event.FlowableEngineEventType;
import mobius.common.engine.api.delegate.event.FlowableEventDispatcher;
import mobius.common.engine.impl.persistence.entity.Entity;
import mobius.common.engine.impl.persistence.entity.EntityManager;
import mobius.common.engine.impl.persistence.entity.data.DataManager;
import mobius.variable.service.VariableServiceConfiguration;
import mobius.variable.service.impl.FlowableVariableEventBuilder;
import mobius.variable.service.impl.persistence.AbstractManager;

/**
 *
 */
public abstract class AbstractEntityManager<EntityImpl extends Entity> extends AbstractManager implements EntityManager<EntityImpl> {

    public AbstractEntityManager(VariableServiceConfiguration variableServiceConfiguration) {
        super(variableServiceConfiguration);
    }

    /*
     * CRUD operations
     */

    @Override
    public EntityImpl findById(String entityId) {
        return getDataManager().findById(entityId);
    }

    @Override
    public EntityImpl create() {
        return getDataManager().create();
    }

    @Override
    public void insert(EntityImpl entity) {
        insert(entity, true);
    }

    @Override
    public void insert(EntityImpl entity, boolean fireCreateEvent) {
        getDataManager().insert(entity);

        if (fireCreateEvent) {
            FlowableEventDispatcher eventDispatcher = getEventDispatcher();
            if (eventDispatcher != null && eventDispatcher.isEnabled()) {
                eventDispatcher.dispatchEvent(FlowableVariableEventBuilder.createEntityEvent(FlowableEngineEventType.ENTITY_CREATED, entity));
                eventDispatcher.dispatchEvent(FlowableVariableEventBuilder.createEntityEvent(FlowableEngineEventType.ENTITY_INITIALIZED, entity));
            }
        }
        
    }

    @Override
    public EntityImpl update(EntityImpl entity) {
        return update(entity, true);
    }

    @Override
    public EntityImpl update(EntityImpl entity, boolean fireUpdateEvent) {
        EntityImpl updatedEntity = getDataManager().update(entity);

        if (fireUpdateEvent) {
            FlowableEventDispatcher eventDispatcher = getEventDispatcher();
            if (eventDispatcher != null && eventDispatcher.isEnabled()) {
                eventDispatcher.dispatchEvent(FlowableVariableEventBuilder.createEntityEvent(FlowableEngineEventType.ENTITY_UPDATED, entity));
            }
        }

        return updatedEntity;
    }

    @Override
    public void delete(String id) {
        EntityImpl entity = findById(id);
        delete(entity);
    }

    @Override
    public void delete(EntityImpl entity) {
        delete(entity, true);
    }

    @Override
    public void delete(EntityImpl entity, boolean fireDeleteEvent) {
        getDataManager().delete(entity);

        if (fireDeleteEvent) {
            FlowableEventDispatcher eventDispatcher = getEventDispatcher();
            if (eventDispatcher != null && eventDispatcher.isEnabled()) {
                eventDispatcher.dispatchEvent(FlowableVariableEventBuilder.createEntityEvent(FlowableEngineEventType.ENTITY_DELETED, entity));
            }
        }
    }

    protected abstract DataManager<EntityImpl> getDataManager();
}
