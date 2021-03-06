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

package mobius.engine.migration;

import java.util.Map;

import mobius.engine.impl.migration.ProcessInstanceMigrationValidationResult;

/**
 * @author Dennis Federico
 */
public interface ProcessInstanceMigrationBuilder {

    /**
     * Creates a ProcessInstanceMigrationBuilder using the values of a ProcessInstanceMigrationDocument
     *
     * @param processInstanceMigrationDocument
     * @return
     * @see ProcessInstanceMigrationDocument
     */
    ProcessInstanceMigrationBuilder fromProcessInstanceMigrationDocument(ProcessInstanceMigrationDocument processInstanceMigrationDocument);

    /**
     * Specifies the process definition to migrate to, using the process definition id
     *
     * @param processDefinitionId
     * @return
     * @see mobius.engine.repository.ProcessDefinition
     */
    ProcessInstanceMigrationBuilder migrateToProcessDefinition(String processDefinitionId);

    /**
     * Specifies the process definition to migrate to, identified by its key and version
     *
     * @param processDefinitionKey
     * @param processDefinitionVersion
     * @return
     * @see mobius.engine.repository.ProcessDefinition
     */
    ProcessInstanceMigrationBuilder migrateToProcessDefinition(String processDefinitionKey, int processDefinitionVersion);

    /**
     * Specifies the process definition to migrate to, identified by its key and version and tenantId
     *
     * @param processDefinitionKey
     * @param processDefinitionVersion
     * @param processDefinitionTenantId
     * @return
     * @see mobius.engine.repository.ProcessDefinition
     */
    ProcessInstanceMigrationBuilder migrateToProcessDefinition(String processDefinitionKey, int processDefinitionVersion, String processDefinitionTenantId);

    /**
     * Specifies the tenantId of the process definition to migrate to
     *
     * @param processDefinitionTenantId
     * @return
     */
    ProcessInstanceMigrationBuilder withMigrateToProcessDefinitionTenantId(String processDefinitionTenantId);

    /**
     * Adds an activity mapping to the process instance migration plan. Addition order is relevant and "auto-mapping" has priority. There can only be one mapping for a given "from" activity.
     *
     * @param mapping
     * @return
     * @see ActivityMigrationMapping
     */
    ProcessInstanceMigrationBuilder addActivityMigrationMapping(ActivityMigrationMapping mapping);

    /**
     * Specifies a process instance variable that will also be available during the process migration (ie. to resolve callActivity calledElement expressions of the new process definition - if any)
     *
     * @param variableName
     * @param variableValue
     * @return
     */
    ProcessInstanceMigrationBuilder withProcessInstanceVariable(String variableName, Object variableValue);

    /**
     * Specified process instances variables that will also be available during process migration
     *
     * @param variables
     * @return
     */
    ProcessInstanceMigrationBuilder withProcessInstanceVariables(Map<String, Object> variables);

    /**
     * Builds a ProcessInstanceMigrationDocument
     *
     * @return
     * @see ProcessInstanceMigrationDocument
     */
    ProcessInstanceMigrationDocument getProcessInstanceMigrationDocument();

    /**
     * Starts the process instance migration for a process identified with the submitted processInstanceId
     *
     * @param processInstanceId
     */
    void migrate(String processInstanceId);

    /**
     * Validates this process instance migration instructions for a given process instance identified by its processInstanceId
     *
     * @param processInstanceId
     * @return a ProcessInstanceMigrationValidationResult that contains validation error messages - if any
     * @see ProcessInstanceMigrationValidationResult
     */
    ProcessInstanceMigrationValidationResult validateMigration(String processInstanceId);

    /**
     * Starts the process instance migration for all process instances of a given process definition identified by the process definition id.
     *
     * @param processDefinitionId
     */
    void migrateProcessInstances(String processDefinitionId);

    /**
     * Validates this process instance migration instruction for each process instance of a given process definition identified by the process definition id.
     *
     * @param processDefinitionId
     * @return a ProcessInstanceMigrationValidationResult that contains validation error messages - if any
     * @see ProcessInstanceMigrationValidationResult
     */
    ProcessInstanceMigrationValidationResult validateMigrationOfProcessInstances(String processDefinitionId);

    /**
     * Starts the process instance migration for all process instances of a given process definition identified by the process definition key and version (optional tenantId).
     *
     * @param processDefinitionKey
     * @param processDefinitionVersion
     * @param processDefinitionTenantId
     */
    void migrateProcessInstances(String processDefinitionKey, int processDefinitionVersion, String processDefinitionTenantId);

    /**
     * Validates this process instance migration instruction for each process instance of a given process definition identified by the process definition key and version (optional tenantId).
     *
     * @param processDefinitionKey
     * @param processDefinitionVersion
     * @param processDefinitionTenantId
     * @return a ProcessInstanceMigrationValidationResult that contains validation error messages - if any
     * @see ProcessInstanceMigrationValidationResult
     */
    ProcessInstanceMigrationValidationResult validateMigrationOfProcessInstances(String processDefinitionKey, int processDefinitionVersion, String processDefinitionTenantId);

}
