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
package mobius.job.service.impl.asyncexecutor;

import java.util.List;

import mobius.common.engine.impl.Page;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.job.service.impl.persistence.entity.JobInfoEntity;
import mobius.job.service.impl.persistence.entity.JobInfoEntityManager;

/**
 *
 */
public class FindExpiredJobsCmd implements Command<List<? extends JobInfoEntity>> {

    protected int pageSize;
    protected JobInfoEntityManager<? extends JobInfoEntity> jobEntityManager;

    public FindExpiredJobsCmd(int pageSize, JobInfoEntityManager<? extends JobInfoEntity> jobEntityManager) {
        this.pageSize = pageSize;
        this.jobEntityManager = jobEntityManager;
    }

    @Override
    public List<? extends JobInfoEntity> execute(CommandContext commandContext) {
        return jobEntityManager.findExpiredJobs(new Page(0, pageSize));
    }

}
