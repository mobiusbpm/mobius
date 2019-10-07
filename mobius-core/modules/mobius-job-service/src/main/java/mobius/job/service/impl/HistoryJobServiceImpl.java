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
package mobius.job.service.impl;

import java.util.List;

import mobius.job.api.HistoryJob;
import mobius.job.service.HistoryJobService;
import mobius.job.service.JobServiceConfiguration;
import mobius.job.service.impl.persistence.entity.HistoryJobEntity;

/**
 *
 */
public class HistoryJobServiceImpl extends ServiceImpl implements HistoryJobService {

    public HistoryJobServiceImpl(JobServiceConfiguration jobServiceConfiguration) {
        super(jobServiceConfiguration);
    }

    @Override
    public List<HistoryJob> findHistoryJobsByQueryCriteria(HistoryJobQueryImpl query) {
        return getHistoryJobEntityManager().findHistoryJobsByQueryCriteria(query);
    }
    
    @Override
    public HistoryJobEntity createHistoryJob() {
        return getHistoryJobEntityManager().create();
    }

    @Override
    public void scheduleHistoryJob(HistoryJobEntity historyJob) {
        getJobManager().scheduleHistoryJob(historyJob);
    }

    @Override
    public void deleteHistoryJob(HistoryJobEntity historyJob) {
        getHistoryJobEntityManager().delete(historyJob);
    }
}
