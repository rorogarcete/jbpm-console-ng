/*
 * Copyright 2012 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

package org.jbpm.console.ng.ht.backend.server;

import java.util.ArrayList;
import java.util.List;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.services.task.audit.impl.model.AuditTask;
import org.jbpm.services.task.audit.impl.model.GroupAuditTask;
import org.jbpm.services.task.audit.impl.model.UserAuditTask;
import org.kie.internal.task.api.model.InternalTaskSummary;

public class TaskSummaryHelper {
    public static List<TaskSummary> adaptCollection(List<org.kie.api.task.model.TaskSummary> taskSums) {
        List<TaskSummary> taskSummaries = new ArrayList<TaskSummary>(taskSums.size());
        for (org.kie.api.task.model.TaskSummary taskSum : taskSums) {
            taskSummaries.add(new TaskSummary(
                    taskSum.getId(), 
                    taskSum.getProcessInstanceId(),
                    taskSum.getName(),
                    taskSum.getSubject(),
                    taskSum.getDescription(),
                    (taskSum.getStatus() != null) ? taskSum.getStatus().name() : "",
                    taskSum.getPriority(),
                    taskSum.isSkipable(),
                    (taskSum.getActualOwner() != null) ? taskSum.getActualOwner().getId() : "",
                    (taskSum.getCreatedBy() != null) ? taskSum.getCreatedBy().getId() : "",
                    taskSum.getCreatedOn(),
                    taskSum.getActivationTime(),
                    taskSum.getExpirationTime(),
                    taskSum.getProcessId(),
                    taskSum.getProcessSessionId(),
                    ((InternalTaskSummary) taskSum).getSubTaskStrategy().name(),
                    (int) ((InternalTaskSummary) taskSum).getParentId(), taskSum.getPotentialOwners()));
        }
        return taskSummaries;
    }
    
    public static TaskSummary adapt(org.kie.api.task.model.TaskSummary taskSum) {
        return new TaskSummary(
                    taskSum.getId(), 
                    taskSum.getProcessInstanceId(),
                    taskSum.getName(),
                    taskSum.getSubject(),
                    taskSum.getDescription(),
                    (taskSum.getStatus() != null) ? taskSum.getStatus().name() : "",
                    taskSum.getPriority(),
                    taskSum.isSkipable(),
                    (taskSum.getActualOwner() != null) ? taskSum.getActualOwner().getId() : "",
                    (taskSum.getCreatedBy() != null) ? taskSum.getCreatedBy().getId() : "",
                    taskSum.getCreatedOn(),
                    taskSum.getActivationTime(),
                    taskSum.getExpirationTime(),
                    taskSum.getProcessId(),
                    taskSum.getProcessSessionId(),
                    ((InternalTaskSummary) taskSum).getSubTaskStrategy().name(),
                    (int) ((InternalTaskSummary) taskSum).getParentId(), taskSum.getPotentialOwners());
    
    }
    
    public static List<TaskSummary> adaptUserAuditCollection(List<UserAuditTask> userTaskAudits) {
        List<TaskSummary> taskSummaries = new ArrayList<TaskSummary>(userTaskAudits.size());
        for (UserAuditTask taskAudit : userTaskAudits) {
            taskSummaries.add(adaptUserAudit(taskAudit));
        }
        return taskSummaries;
    }

    public static TaskSummary adaptUserAudit(UserAuditTask taskAudit) {
//        List<String> potOwners = new ArrayList<String>(1);
//        if(taskAudit instanceof GroupAuditTask){
//            potOwners.add(((GroupAuditTask)taskAudit).getPotentialOwners());
//        }
                
        return new TaskSummary(taskAudit.getTaskId(), 
                        taskAudit.getProcessInstanceId(), 
                        taskAudit.getName(),
                        "",
                        taskAudit.getDescription(),
                        taskAudit.getStatus(),
                        taskAudit.getPriority(),
                        true, 
                        taskAudit.getActualOwner(), 
                        taskAudit.getCreatedBy(),
                        taskAudit.getCreatedOn(),
                        taskAudit.getActivationTime(),
                        taskAudit.getDueDate(),
                        taskAudit.getProcessId(),
                        taskAudit.getProcessSessionId(),
                        "",
                        (int)taskAudit.getParentId(), 
                        null);
    }
}
