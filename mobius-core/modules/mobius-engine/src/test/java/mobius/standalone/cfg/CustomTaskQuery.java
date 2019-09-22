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
package mobius.standalone.cfg;

import java.util.List;

import mobius.common.engine.impl.query.AbstractQuery;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.common.engine.impl.interceptor.CommandExecutor;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.task.service.impl.TaskQueryProperty;

/**
 * @author Bassam Al-Sarori
 * 
 */
public class CustomTaskQuery extends AbstractQuery<CustomTaskQuery, CustomTask> {

    private static final long serialVersionUID = 1L;

    protected boolean unOwned;
    protected String taskId;
    protected String owner;

    public CustomTaskQuery(CommandExecutor commandExecutor) {
        super(commandExecutor);
    }

    public CustomTaskQuery taskId(String taskId) {
        this.taskId = taskId;
        return this;
    }

    public CustomTaskQuery taskOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public CustomTaskQuery orderByTaskPriority() {
        return orderBy(TaskQueryProperty.PRIORITY);
    }

    public CustomTaskQuery unOwned() {
        unOwned = true;
        return this;
    }

    public boolean getUnOwned() {
        return unOwned;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CustomTask> executeList(CommandContext commandContext) {
        return CommandContextUtil.getDbSqlSession(commandContext).selectList("selectCustomTaskByQueryCriteria", this);
    }

    @Override
    public long executeCount(CommandContext commandContext) {
        return (Long) CommandContextUtil.getDbSqlSession(commandContext).selectOne("selectCustomTaskCountByQueryCriteria", this);
    }
}
