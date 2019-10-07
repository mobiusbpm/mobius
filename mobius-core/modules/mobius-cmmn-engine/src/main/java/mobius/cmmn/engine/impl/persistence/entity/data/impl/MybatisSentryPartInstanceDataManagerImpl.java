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
package mobius.cmmn.engine.impl.persistence.entity.data.impl;

import mobius.cmmn.engine.CmmnEngineConfiguration;
import mobius.cmmn.engine.impl.persistence.entity.SentryPartInstanceEntity;
import mobius.cmmn.engine.impl.persistence.entity.SentryPartInstanceEntityImpl;
import mobius.cmmn.engine.impl.persistence.entity.data.AbstractCmmnDataManager;
import mobius.cmmn.engine.impl.persistence.entity.data.SentryPartInstanceDataManager;
import mobius.common.engine.impl.persistence.cache.CachedEntityMatcherAdapter;

import java.util.List;

/**
 *
 */
public class MybatisSentryPartInstanceDataManagerImpl extends AbstractCmmnDataManager<SentryPartInstanceEntity> implements SentryPartInstanceDataManager {
    
    protected SentryPartByCaseInstanceIdEntityMatcher sentryPartByCaseInstanceIdEntityMatched
            = new SentryPartByCaseInstanceIdEntityMatcher();
    
    protected SentryPartByPlanItemInstanceIdEntityMatcher sentryPartByPlanItemInstanceIdEntityMatched
            = new SentryPartByPlanItemInstanceIdEntityMatcher();

    public MybatisSentryPartInstanceDataManagerImpl(CmmnEngineConfiguration cmmnEngineConfiguration) {
        super(cmmnEngineConfiguration);
    }

    @Override
    public Class<? extends SentryPartInstanceEntity> getManagedEntityClass() {
        return SentryPartInstanceEntityImpl.class;
    }

    @Override
    public SentryPartInstanceEntity create() {
        return new SentryPartInstanceEntityImpl();
    }

    @Override
    public List<SentryPartInstanceEntity> findSentryPartInstancesByCaseInstanceId(String caseInstanceId) {
        return getList("selectSentryPartInstanceByCaseInstanceId", caseInstanceId, sentryPartByCaseInstanceIdEntityMatched);
    }

    @Override
    public List<SentryPartInstanceEntity> findSentryPartInstancesByCaseInstanceIdAndNullPlanItemInstanceId(String caseInstanceId) {
        return getList("selectSentryPartInstanceByCaseInstanceIdAndNullPlanItemInstanceId", caseInstanceId);
    }

    @Override
    public List<SentryPartInstanceEntity> findSentryPartInstancesByPlanItemInstanceId(String planItemInstanceId) {
        return getList("selectSentryPartInstanceByPlanItemInstanceId", planItemInstanceId, sentryPartByPlanItemInstanceIdEntityMatched);
    }

    @Override
    public void deleteByCaseInstanceId(String caseInstanceId) {
        bulkDelete("deleteSentryPartInstancesByCaseInstanceId", sentryPartByCaseInstanceIdEntityMatched, caseInstanceId);
    }
    
    
    public static class SentryPartByCaseInstanceIdEntityMatcher extends CachedEntityMatcherAdapter<SentryPartInstanceEntity> {
        
        @Override
        public boolean isRetained(SentryPartInstanceEntity sentryPartInstanceEntity, Object param) {
            return sentryPartInstanceEntity.getPlanItemInstanceId() == null
                    && sentryPartInstanceEntity.getCaseInstanceId().equals((String) param);
        }
        
    }
    
    public static class SentryPartByPlanItemInstanceIdEntityMatcher extends CachedEntityMatcherAdapter<SentryPartInstanceEntity> {
        
        @Override
        public boolean isRetained(SentryPartInstanceEntity sentryPartInstanceEntity, Object param) {
            return sentryPartInstanceEntity.getPlanItemInstanceId() != null
                    && sentryPartInstanceEntity.getPlanItemInstanceId().equals((String) param);
        }
        
    }

}
