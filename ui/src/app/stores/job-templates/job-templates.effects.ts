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

import { Injectable } from '@angular/core';
import { Actions, Effect, ofType } from '@ngrx/effects';
import * as JobTemplatesActions from './job-templates.actions';
import { catchError, mergeMap, switchMap } from 'rxjs/operators';
import { TableSearchResponseModel } from '../../models/search/tableSearchResponse.model';
import { JobTemplateService } from '../../services/job-template/job-template.service';
import { JobTemplateModel } from '../../models/jobTemplate.model';

@Injectable()
export class JobTemplatesEffects {
  constructor(private actions: Actions, private jobTemplateService: JobTemplateService) {}

  @Effect({ dispatch: true })
  jobTemplatesSearch = this.actions.pipe(
    ofType(JobTemplatesActions.SEARCH_JOB_TEMPLATES),
    switchMap((action: JobTemplatesActions.SearchJobTemplates) => {
      return this.jobTemplateService.searchJobTemplates(action.payload).pipe(
        mergeMap((searchResult: TableSearchResponseModel<JobTemplateModel>) => {
          return [
            {
              type: JobTemplatesActions.SEARCH_JOB_TEMPLATES_SUCCESS,
              payload: { jobTemplatesSearchResponse: searchResult },
            },
          ];
        }),
        catchError(() => {
          return [
            {
              type: JobTemplatesActions.SEARCH_JOB_TEMPLATES_FAILURE,
            },
          ];
        }),
      );
    }),
  );
}
