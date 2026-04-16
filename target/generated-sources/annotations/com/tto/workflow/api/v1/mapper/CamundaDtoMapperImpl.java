package com.tto.workflow.api.v1.mapper;

import com.tto.workflow.api.v1.dto.camunda.DeploymentDto;
import com.tto.workflow.api.v1.dto.camunda.EventSubscriptionDto;
import com.tto.workflow.api.v1.dto.camunda.ExecutionDto;
import com.tto.workflow.api.v1.dto.camunda.ProcessDefinitionDto;
import com.tto.workflow.api.v1.dto.camunda.ProcessInstanceDto;
import com.tto.workflow.api.v1.dto.camunda.TaskDto;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Generated;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-16T19:55:57+0000",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (Eclipse Adoptium)"
)
@Component
public class CamundaDtoMapperImpl implements CamundaDtoMapper {

    @Override
    public DeploymentDto toDeploymentDto(Deployment deployment) {
        if ( deployment == null ) {
            return null;
        }

        String id = null;
        String name = null;
        String source = null;
        String tenantId = null;

        id = deployment.getId();
        name = deployment.getName();
        source = deployment.getSource();
        tenantId = deployment.getTenantId();

        String deploymentTime = formatDate(deployment.getDeploymentTime());

        DeploymentDto deploymentDto = new DeploymentDto( id, name, deploymentTime, source, tenantId );

        return deploymentDto;
    }

    @Override
    public ProcessDefinitionDto toProcessDefinitionDto(ProcessDefinition def) {
        if ( def == null ) {
            return null;
        }

        String resource = null;
        String diagram = null;
        Boolean startableInTasklist = null;
        String id = null;
        String key = null;
        String category = null;
        String description = null;
        String name = null;
        Integer version = null;
        String deploymentId = null;
        Boolean suspended = null;
        String tenantId = null;
        String versionTag = null;
        Integer historyTimeToLive = null;

        resource = def.getResourceName();
        diagram = def.getDiagramResourceName();
        startableInTasklist = def.isStartableInTasklist();
        id = def.getId();
        key = def.getKey();
        category = def.getCategory();
        description = def.getDescription();
        name = def.getName();
        version = def.getVersion();
        deploymentId = def.getDeploymentId();
        suspended = def.isSuspended();
        tenantId = def.getTenantId();
        versionTag = def.getVersionTag();
        historyTimeToLive = def.getHistoryTimeToLive();

        ProcessDefinitionDto processDefinitionDto = new ProcessDefinitionDto( id, key, category, description, name, version, resource, deploymentId, diagram, suspended, tenantId, versionTag, historyTimeToLive, startableInTasklist );

        return processDefinitionDto;
    }

    @Override
    public ProcessInstanceDto toProcessInstanceDto(ProcessInstance pi) {
        if ( pi == null ) {
            return null;
        }

        String caseInstanceId = null;
        Boolean ended = null;
        Boolean suspended = null;
        String id = null;
        String businessKey = null;
        String tenantId = null;

        caseInstanceId = pi.getCaseInstanceId();
        ended = pi.isEnded();
        suspended = pi.isSuspended();
        id = pi.getId();
        businessKey = pi.getBusinessKey();
        tenantId = pi.getTenantId();

        List<Map<String, String>> links = emptyLinks();
        String definitionId = null;

        ProcessInstanceDto processInstanceDto = new ProcessInstanceDto( id, definitionId, businessKey, caseInstanceId, ended, suspended, tenantId, links );

        return processInstanceDto;
    }

    @Override
    public ExecutionDto toExecutionDto(Execution e) {
        if ( e == null ) {
            return null;
        }

        String id = null;
        String processInstanceId = null;
        String tenantId = null;
        Boolean suspended = null;

        id = e.getId();
        processInstanceId = e.getProcessInstanceId();
        tenantId = e.getTenantId();
        suspended = e.isSuspended();

        String activityId = null;
        String parentId = null;
        String processDefinitionId = null;

        ExecutionDto executionDto = new ExecutionDto( id, processInstanceId, parentId, processDefinitionId, tenantId, suspended, activityId );

        return executionDto;
    }

    @Override
    public TaskDto toTaskDto(Task task) {
        if ( task == null ) {
            return null;
        }

        String id = null;
        String name = null;
        String assignee = null;
        String owner = null;
        Integer priority = null;
        String delegationState = null;
        String processInstanceId = null;
        String executionId = null;
        String processDefinitionId = null;
        String taskDefinitionKey = null;
        String tenantId = null;
        Boolean suspended = null;

        id = task.getId();
        name = task.getName();
        assignee = task.getAssignee();
        owner = task.getOwner();
        priority = task.getPriority();
        if ( task.getDelegationState() != null ) {
            delegationState = task.getDelegationState().name();
        }
        processInstanceId = task.getProcessInstanceId();
        executionId = task.getExecutionId();
        processDefinitionId = task.getProcessDefinitionId();
        taskDefinitionKey = task.getTaskDefinitionKey();
        tenantId = task.getTenantId();
        suspended = task.isSuspended();

        String createTime = formatDate(task.getCreateTime());
        String dueDate = formatDate(task.getDueDate());

        TaskDto taskDto = new TaskDto( id, name, assignee, owner, createTime, dueDate, priority, delegationState, processInstanceId, executionId, processDefinitionId, taskDefinitionKey, tenantId, suspended );

        return taskDto;
    }

    @Override
    public EventSubscriptionDto toEventSubscriptionDto(EventSubscription es) {
        if ( es == null ) {
            return null;
        }

        String id = null;
        String eventType = null;
        String eventName = null;
        String executionId = null;
        String processInstanceId = null;
        String activityId = null;
        String tenantId = null;

        id = es.getId();
        eventType = es.getEventType();
        eventName = es.getEventName();
        executionId = es.getExecutionId();
        processInstanceId = es.getProcessInstanceId();
        activityId = es.getActivityId();
        tenantId = es.getTenantId();

        String configuration = null;
        String created = formatDate(es.getCreated());

        EventSubscriptionDto eventSubscriptionDto = new EventSubscriptionDto( id, eventType, eventName, executionId, processInstanceId, activityId, configuration, created, tenantId );

        return eventSubscriptionDto;
    }
}
