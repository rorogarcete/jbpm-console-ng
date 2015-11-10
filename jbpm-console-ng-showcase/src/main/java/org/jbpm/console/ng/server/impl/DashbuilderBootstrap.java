/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.server.impl;

import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.jbpm.console.ng.es.client.editors.requestlist.RequestListViewImpl;
import org.jbpm.console.ng.ht.client.editors.taskslist.grid.dash.DataSetTasksListGridViewImpl;
import org.jbpm.console.ng.pr.client.editors.instance.list.dash.DataSetProcessInstanceListViewImpl;
import org.uberfire.commons.services.cdi.Startup;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Startup
@ApplicationScoped
public class DashbuilderBootstrap {
        public static final String JBPM_DATASOURCE = "java:jboss/datasources/ExampleDS";
        public static final String HUMAN_TASKS_DATASET = "jbpmHumanTasks";
        public static final String HUMAN_TASKS_TABLE = "AuditTaskImpl";

        public static final String PROCESS_INSTANCE_DATASET = "jbpmProcessInstances";
        public static final String PROCESS_INSTANCE_TABLE = "ProcessInstanceLog";

        public static final String HUMAN_TASKS_WITH_USER_DATASET = "jbpmHumanTasksWithUser";
        public static final String HUMAN_TASKS_WITH_ADMIN_DATASET = "jbpmHumanTasksWithAdmin";

        public static final String REQUEST_LIST_DATASET = "jbpmRequestList";
        public static final String REQUEST_LIST_TABLE = "RequestInfo";
        @Inject
        protected DataSetDefRegistry dataSetDefRegistry;

        @PostConstruct
        protected void init() {
                registerDataSetDefinitions();
        }

        protected void registerDataSetDefinitions() {
                dataSetDefRegistry.registerDataSetDef(
                        DataSetFactory.newSQLDataSetDef()
                                .uuid( HUMAN_TASKS_DATASET )
                                .name( "Human tasks" )
                                .dataSource( JBPM_DATASOURCE )
                                .dbTable( HUMAN_TASKS_TABLE, false )
                                .date( DataSetTasksListGridViewImpl.COLUMN_ACTIVATIONTIME )
                                .label( DataSetTasksListGridViewImpl.COLUMN_ACTUALOWNER )
                                .label( DataSetTasksListGridViewImpl.COLUMN_CREATEDBY )
                                .date( DataSetTasksListGridViewImpl.COLUMN_CREATEDON )
                                .label( DataSetTasksListGridViewImpl.COLUMN_DEPLOYMENTID )
                                .text( DataSetTasksListGridViewImpl.COLUMN_DESCRIPTION )
                                .date( DataSetTasksListGridViewImpl.COLUMN_DUEDATE )
                                .label( DataSetTasksListGridViewImpl.COLUMN_NAME )
                                .label( DataSetTasksListGridViewImpl.COLUMN_PARENTID )
                                .label( DataSetTasksListGridViewImpl.COLUMN_PRIORITY )
                                .label( DataSetTasksListGridViewImpl.COLUMN_PROCESSID )
                                .label( DataSetTasksListGridViewImpl.COLUMN_PROCESSINSTANCEID )
                                .label( DataSetTasksListGridViewImpl.COLUMN_PROCESSSESSIONID )
                                .label( DataSetTasksListGridViewImpl.COLUMN_STATUS )
                                .label( DataSetTasksListGridViewImpl.COLUMN_TASKID )
                                .label( DataSetTasksListGridViewImpl.COLUMN_WORKITEMID )
                                .buildDef() );

                dataSetDefRegistry.registerDataSetDef(
                        DataSetFactory.newSQLDataSetDef()
                                .uuid( PROCESS_INSTANCE_DATASET)
                                .name( "Process Instances" )
                                .dataSource( JBPM_DATASOURCE )
                                .dbTable( PROCESS_INSTANCE_TABLE, false )
                                .label( DataSetProcessInstanceListViewImpl.COLUMN_PROCESSINSTANCEID )
                                .label( DataSetProcessInstanceListViewImpl.COLUMN_PROCESSID )
                                .date( DataSetProcessInstanceListViewImpl.COLUMN_START )
                                .date( DataSetProcessInstanceListViewImpl.COLUMN_END )
                                .label( DataSetProcessInstanceListViewImpl.COLUMN_STATUS )
                                .label( DataSetProcessInstanceListViewImpl.COLUMN_PARENTPROCESSINSTANCEID )
                                .label( DataSetProcessInstanceListViewImpl.COLUMN_OUTCOME )
                                .label( DataSetProcessInstanceListViewImpl.COLUMN_DURATION )
                                .label( DataSetProcessInstanceListViewImpl.COLUMN_IDENTITY )
                                .label( DataSetProcessInstanceListViewImpl.COLUMN_PROCESSVERSION )
                                .label( DataSetProcessInstanceListViewImpl.COLUMN_PROCESSNAME )
                                .label( DataSetProcessInstanceListViewImpl.COLUMN_CORRELATIONKEY )
                                .label( DataSetProcessInstanceListViewImpl.COLUMN_EXTERNALID )
                                .label( DataSetProcessInstanceListViewImpl.COLUMN_PROCESSINSTANCEDESCRIPTION )
                                .buildDef() );

                dataSetDefRegistry.registerDataSetDef(
                        DataSetFactory.newSQLDataSetDef()
                                .uuid( HUMAN_TASKS_WITH_USER_DATASET )
                                .name( "Human tasks and users" )
                                .dataSource( JBPM_DATASOURCE )
                                .dbSQL("select  t.activationtime, t.actualowner, t.createdby, "
                                        + "t.createdon, t.deploymentid, t.description, t.duedate, "
                                        + "t.name, t.parentid, t.priority, t.processid, t.processinstanceid, "
                                        + "t.processsessionid, t.status, t.taskid, t.workitemid, oe.id oeid "
                                        + "from AuditTaskImpl t, "
                                        + "peopleassignments_potowners po, "
                                        + "organizationalentity oe "
                                        + "where t.id = po.task_id and po.entity_id = oe.id", false)
                                .date( DataSetTasksListGridViewImpl.COLUMN_ACTIVATIONTIME )
                                .label( DataSetTasksListGridViewImpl.COLUMN_ACTUALOWNER )
                                .label( DataSetTasksListGridViewImpl.COLUMN_CREATEDBY )
                                .date( DataSetTasksListGridViewImpl.COLUMN_CREATEDON )
                                .label( DataSetTasksListGridViewImpl.COLUMN_DEPLOYMENTID )
                                .text( DataSetTasksListGridViewImpl.COLUMN_DESCRIPTION )
                                .date( DataSetTasksListGridViewImpl.COLUMN_DUEDATE )
                                .label( DataSetTasksListGridViewImpl.COLUMN_NAME )
                                .label( DataSetTasksListGridViewImpl.COLUMN_PARENTID )
                                .label( DataSetTasksListGridViewImpl.COLUMN_PRIORITY )
                                .label( DataSetTasksListGridViewImpl.COLUMN_PROCESSID )
                                .label( DataSetTasksListGridViewImpl.COLUMN_PROCESSINSTANCEID )
                                .label( DataSetTasksListGridViewImpl.COLUMN_PROCESSSESSIONID )
                                .label( DataSetTasksListGridViewImpl.COLUMN_STATUS )
                                .label( DataSetTasksListGridViewImpl.COLUMN_TASKID )
                                .label( DataSetTasksListGridViewImpl.COLUMN_WORKITEMID )
                                .label( DataSetTasksListGridViewImpl.COLUMN_ORGANIZATIONAL_ENTITY )
                                .buildDef() );

                dataSetDefRegistry.registerDataSetDef(
                        DataSetFactory.newSQLDataSetDef()
                                .uuid( HUMAN_TASKS_WITH_ADMIN_DATASET )
                                .name( "Human tasks and admins" )
                                .dataSource( JBPM_DATASOURCE )
                                .dbSQL( "select t.activationtime, t.actualowner, t.createdby, "
                                        + "t.createdon, t.deploymentid, t.description, t.duedate, "
                                        + "t.name, t.parentid, t.priority, t.processid, t.processinstanceid, "
                                        + "t.processsessionid, t.status, t.taskid, t.workitemid, oe.id oeid "
                                        + "from AuditTaskImpl t, "
                                        + "peopleassignments_bas bas, "
                                        + "organizationalentity oe "
                                        + "where t.id = bas.task_id and bas.entity_id = oe.id", false )
                                .date( DataSetTasksListGridViewImpl.COLUMN_ACTIVATIONTIME )
                                .label( DataSetTasksListGridViewImpl.COLUMN_ACTUALOWNER )
                                .label( DataSetTasksListGridViewImpl.COLUMN_CREATEDBY )
                                .date( DataSetTasksListGridViewImpl.COLUMN_CREATEDON )
                                .label( DataSetTasksListGridViewImpl.COLUMN_DEPLOYMENTID )
                                .text( DataSetTasksListGridViewImpl.COLUMN_DESCRIPTION )
                                .date( DataSetTasksListGridViewImpl.COLUMN_DUEDATE )
                                .label( DataSetTasksListGridViewImpl.COLUMN_NAME )
                                .label( DataSetTasksListGridViewImpl.COLUMN_PARENTID )
                                .label( DataSetTasksListGridViewImpl.COLUMN_PRIORITY )
                                .label( DataSetTasksListGridViewImpl.COLUMN_PROCESSID )
                                .label( DataSetTasksListGridViewImpl.COLUMN_PROCESSINSTANCEID )
                                .label( DataSetTasksListGridViewImpl.COLUMN_PROCESSSESSIONID )
                                .label( DataSetTasksListGridViewImpl.COLUMN_STATUS )
                                .label( DataSetTasksListGridViewImpl.COLUMN_TASKID )
                                .label( DataSetTasksListGridViewImpl.COLUMN_WORKITEMID )
                                .label( DataSetTasksListGridViewImpl.COLUMN_ORGANIZATIONAL_ENTITY )
                                .buildDef() );

                dataSetDefRegistry.registerDataSetDef(
                        DataSetFactory.newSQLDataSetDef()
                                .uuid( REQUEST_LIST_DATASET )
                                .name( "Request List" )
                                .dataSource( JBPM_DATASOURCE )
                                .dbTable( REQUEST_LIST_TABLE, false )
                                .label( RequestListViewImpl.COLUMN_ID )
                                .date( RequestListViewImpl.COLUMN_TIMESTAMP )
                                .label( RequestListViewImpl.COLUMN_STATUS )
                                .label( RequestListViewImpl.COLUMN_COMMANDNAME )
                                .label( RequestListViewImpl.COLUMN_MESSAGE )
                                .label( RequestListViewImpl.COLUMN_BUSINESSKEY )
                                .buildDef() );
        }
}
