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

package mobius.job.api;

import java.util.Date;

import mobius.common.engine.api.query.Query;

/**
 * Allows programmatic querying of {@link Job}s.
 *
 *
 *
 */
public interface DeadLetterJobQuery extends Query<DeadLetterJobQuery, Job> {

    /** Only select jobs with the given id */
    DeadLetterJobQuery jobId(String jobId);

    /** Only select jobs which exist for the given process instance. **/
    DeadLetterJobQuery processInstanceId(String processInstanceId);

    /** Only select jobs which exist for the given execution */
    DeadLetterJobQuery executionId(String executionId);

    /** Select jobs which have given job handler type */
    DeadLetterJobQuery handlerType(String handlerType);

    /** Only select jobs which exist for the given process definition id */
    DeadLetterJobQuery processDefinitionId(String processDefinitionId);
    
    /** Only select jobs which exist for the given element id */
    DeadLetterJobQuery elementId(String elementId);
    
    /** Only select jobs which exist for the given element name */
    DeadLetterJobQuery elementName(String elementName);
    
    /** Only select jobs for the given scope identifier. */
    DeadLetterJobQuery scopeId(String scopeId);

    /** Only select jobs for the given sub scope identifier. */
    DeadLetterJobQuery subScopeId(String subScopeId);
    
    /** Only select jobs for the given scope type. */
    DeadLetterJobQuery scopeType(String scopeType);
    
    /** Only select jobs for the given scope definition identifier. */
    DeadLetterJobQuery scopeDefinitionId(String scopeDefinitionId);
    
    /** Only select jobs for the given case instance. */
    DeadLetterJobQuery caseInstanceId(String caseInstanceId);
    
    /** Only select jobs for the given case definition. */
    DeadLetterJobQuery caseDefinitionId(String caseDefinitionId);
    
    /** Only select jobs for the given plan item instance.  */
    DeadLetterJobQuery planItemInstanceId(String planItemInstanceId);

    /**
     * Only select jobs which are executable, ie. duedate is null or duedate is in the past
     **/
    DeadLetterJobQuery executable();

    /**
     * Only select jobs that are timers. Cannot be used together with {@link #messages()}
     */
    DeadLetterJobQuery timers();

    /**
     * Only select jobs that are messages. Cannot be used together with {@link #timers()}
     */
    DeadLetterJobQuery messages();

    /** Only select jobs where the duedate is lower than the given date. */
    DeadLetterJobQuery duedateLowerThan(Date date);

    /** Only select jobs where the duedate is higher then the given date. */
    DeadLetterJobQuery duedateHigherThan(Date date);

    /** Only select jobs that failed due to an exception. */
    DeadLetterJobQuery withException();

    /** Only select jobs that failed due to an exception with the given message. */
    DeadLetterJobQuery exceptionMessage(String exceptionMessage);

    /**
     * Only select jobs that have the given tenant id.
     */
    DeadLetterJobQuery jobTenantId(String tenantId);

    /**
     * Only select jobs with a tenant id like the given one.
     */
    DeadLetterJobQuery jobTenantIdLike(String tenantIdLike);

    /**
     * Only select jobs that do not have a tenant id.
     */
    DeadLetterJobQuery jobWithoutTenantId();

    // sorting //////////////////////////////////////////

    /**
     * Order by job id (needs to be followed by {@link #asc()} or {@link #desc()}).
     */
    DeadLetterJobQuery orderByJobId();

    /**
     * Order by duedate (needs to be followed by {@link #asc()} or {@link #desc()}).
     */
    DeadLetterJobQuery orderByJobDuedate();

    /**
     * Order by retries (needs to be followed by {@link #asc()} or {@link #desc()}).
     */
    DeadLetterJobQuery orderByJobRetries();

    /**
     * Order by process instance id (needs to be followed by {@link #asc()} or {@link #desc()}).
     */
    DeadLetterJobQuery orderByProcessInstanceId();

    /**
     * Order by execution id (needs to be followed by {@link #asc()} or {@link #desc()}).
     */
    DeadLetterJobQuery orderByExecutionId();

    /**
     * Order by tenant id (needs to be followed by {@link #asc()} or {@link #desc()}).
     */
    DeadLetterJobQuery orderByTenantId();

}
