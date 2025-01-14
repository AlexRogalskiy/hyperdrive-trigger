/*
 * Copyright 2018 ABSA Group Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { api } from '../../constants/api.constants';
import { WorkflowService } from './workflow.service';
import { ProjectModelFactory } from '../../models/project.model';
import { WorkflowModel, WorkflowModelFactory } from '../../models/workflow.model';
import { WorkflowJoinedModelFactory } from '../../models/workflowJoined.model';
import { TableSearchResponseModel } from '../../models/search/tableSearchResponse.model';
import { TableSearchRequestModelFactory } from '../../models/search/tableSearchRequest.model';

describe('WorkflowService', () => {
  let underTest: WorkflowService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [WorkflowService],
      imports: [HttpClientTestingModule],
    });
    underTest = TestBed.inject(WorkflowService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(underTest).toBeTruthy();
  });

  it('getProjects() should return projects', () => {
    const projects = [
      ProjectModelFactory.create('projectName1', [
        WorkflowModelFactory.create('workflowName1', true, 'projectName1', new Date(Date.now()), new Date(Date.now()), 0),
      ]),
    ];

    underTest.getProjects().subscribe(
      (data) => expect(data).toEqual(projects),
      (error) => fail(error),
    );

    const req = httpTestingController.expectOne(api.GET_PROJECTS);
    expect(req.request.method).toEqual('GET');
    req.flush([...projects]);
  });

  it('getWorkflows() should return workflows', () => {
    const workflows = [WorkflowModelFactory.create('workflowName1', true, 'projectName1', new Date(Date.now()), new Date(Date.now()), 0)];

    underTest.getWorkflows().subscribe(
      (data) => expect(data).toEqual(workflows),
      (error) => fail(error),
    );

    const req = httpTestingController.expectOne(api.GET_WORKFLOWS);
    expect(req.request.method).toEqual('GET');
    req.flush([...workflows]);
  });

  it('searchWorkflows() should return workflows search response', () => {
    const workflows = [WorkflowModelFactory.create('workflowName1', true, 'projectName1', new Date(Date.now()), new Date(Date.now()), 0)];
    const searchResponseModel = new TableSearchResponseModel<WorkflowModel>(workflows, 1);
    const request = TableSearchRequestModelFactory.create(0, 100);

    underTest.searchWorkflows(request).subscribe(
      (data) => expect(data).toEqual(searchResponseModel),
      (error) => fail(error),
    );

    const req = httpTestingController.expectOne(api.SEARCH_WORKFLOWS);
    expect(req.request.method).toEqual('POST');
    req.flush(searchResponseModel);
  });

  it('getWorkflow() should return workflow data', () => {
    const workflow = WorkflowJoinedModelFactory.create('name', true, 'project', undefined, undefined, undefined, 0);

    underTest.getWorkflow(workflow.id).subscribe(
      (data) => expect(data).toEqual(workflow),
      (error) => fail(error),
    );

    const req = httpTestingController.expectOne(api.GET_WORKFLOW + `?id=${workflow.id}`);
    expect(req.request.method).toEqual('GET');
    req.flush(workflow);
  });

  it('deleteWorkflow() should delete workflow', () => {
    const id = 1;
    const response = true;
    underTest.deleteWorkflow(id).subscribe(
      (data) => expect(data).toEqual(response),
      (error) => fail(error),
    );

    const req = httpTestingController.expectOne(api.DELETE_WORKFLOW + `?id=${id}`);
    expect(req.request.method).toEqual('DELETE');
    req.flush(new Boolean(true));
  });

  it('switchWorkflowActiveState() should switch workflow active state', () => {
    const id = 1;
    const response = true;
    underTest.switchWorkflowActiveState(id).subscribe(
      (data) => expect(data).toEqual(response),
      (error) => fail(error),
    );

    const req = httpTestingController.expectOne(api.SWITCH_WORKFLOW_ACTIVE_STATE.replace('{id}', id.toString()));
    expect(req.request.method).toEqual('POST');
    req.flush(new Boolean(true));
  });

  it('updateWorkflowsIsActive() should update workflows isActive', () => {
    const ids = [1, 2];
    const isActiveNewValue = true;
    const response = true;
    underTest.updateWorkflowsIsActive(ids, isActiveNewValue).subscribe(
      (data) => expect(data).toEqual(response),
      (error) => fail(error),
    );

    const req = httpTestingController.expectOne(api.UPDATE_WORKFLOWS_IS_ACTIVE + `?isActiveNewValue=${isActiveNewValue}`);
    expect(req.request.method).toEqual('POST');
    req.flush(new Boolean(true));
  });

  it('exportWorkflow() should return workflow blob', () => {
    const content = '{"workflowId":"1"}';
    const blob = new Blob([content], { type: 'application/json' });
    const filename = 'filename.json';
    const ids = [1, 2];
    underTest.exportWorkflows(ids).subscribe(
      (data) => {
        expect(data.fileName).toEqual(filename);
        expect(data.blob).toEqual(blob);
      },
      (error) => fail(error),
    );
    const req = httpTestingController.expectOne(api.EXPORT_WORKFLOWS + `?jobIds=${ids.join(',')}`);
    expect(req.request.method).toEqual('GET');
    req.flush(blob, {
      headers: { 'Content-Disposition': `attachment; filename=${filename}` },
    });
  });

  it('importWorkflow() should return imported workflow', () => {
    const workflow = WorkflowJoinedModelFactory.create('name', true, 'project', undefined, undefined, undefined, 0);
    const file: File = new File(['content'], 'filename.json');

    underTest.importWorkflow(file).subscribe(
      (data) => expect(data).toEqual(workflow),
      (error) => fail(error),
    );

    const req = httpTestingController.expectOne(api.IMPORT_WORKFLOW);
    expect(req.request.method).toEqual('POST');
    req.flush(workflow);
  });

  it('importWorkflows() should return inserted workflows', () => {
    const workflow = WorkflowModelFactory.create('workflowName', true, 'projectName', new Date(Date.now()), new Date(Date.now()), 0);
    const file: File = new File(['content'], 'workflows.zip');

    underTest.importWorkflows(file).subscribe(
      (data) => expect(data).toEqual([workflow]),
      (error) => fail(error),
    );

    const req = httpTestingController.expectOne(api.IMPORT_WORKFLOWS);
    expect(req.request.method).toEqual('POST');
    req.flush([workflow]);
  });

  it('createWorkflow() should return created workflow', () => {
    const workflow = WorkflowJoinedModelFactory.create('name', true, 'project', undefined, undefined, undefined, 0);

    underTest.createWorkflow(workflow).subscribe(
      (data) => expect(data).toEqual(workflow),
      (error) => fail(error),
    );

    const req = httpTestingController.expectOne(api.CREATE_WORKFLOW);
    expect(req.request.method).toEqual('PUT');
    req.flush(workflow);
  });

  it('updateWorkflow() should return updated workflow', () => {
    const workflow = WorkflowJoinedModelFactory.create('name', true, 'project', undefined, undefined, undefined, 0);

    underTest.updateWorkflow(workflow).subscribe(
      (data) => expect(data).toEqual(workflow),
      (error) => fail(error),
    );

    const req = httpTestingController.expectOne(api.UPDATE_WORKFLOW);
    expect(req.request.method).toEqual('POST');
    req.flush(workflow);
  });

  it('runWorkflowJobs() should run selected jobs', () => {
    const workflowId = 5;
    const jobIds = [1, 2, 3];
    const response = true;

    underTest.runWorkflowJobs(workflowId, jobIds).subscribe(
      (data) => expect(data).toEqual(response),
      (error) => fail(error),
    );

    const req = httpTestingController.expectOne(api.RUN_WORKFLOWS_JOBS + `?workflowId=${workflowId}`);
    expect(req.request.method).toEqual('PUT');
    req.flush(new Boolean(response));
  });

  it('runWorkflows() should run selected workflows', () => {
    const workflowIds = [1, 2, 3];
    const response = true;

    underTest.runWorkflows(workflowIds).subscribe(
      (data) => expect(data).toEqual(response),
      (error) => fail(error),
    );

    const req = httpTestingController.expectOne(api.RUN_WORKFLOWS);
    expect(req.request.method).toEqual('PUT');
    req.flush(new Boolean(response));
  });
});
