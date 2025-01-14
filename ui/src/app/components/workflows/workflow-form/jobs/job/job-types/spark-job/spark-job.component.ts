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

import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { SparkDefinitionParametersModel } from '../../../../../../../models/jobDefinitionParameters.model';
import { Subscription } from 'rxjs';
import { JobTemplateChangeEventModel } from '../../../../../../../models/jobTemplateChangeEvent';

@Component({
  selector: 'app-spark-job',
  templateUrl: './spark-job.component.html',
  styleUrls: ['./spark-job.component.scss'],
})
export class SparkJobComponent implements OnInit, OnDestroy {
  @Input() isShow: boolean;
  @Input() jobParameters: SparkDefinitionParametersModel;
  @Output() jobParametersChange = new EventEmitter();
  @Input() isJobTemplateSelected: boolean;
  @Input() jobTemplateChanges: EventEmitter<JobTemplateChangeEventModel>;

  jobTemplateChangesSubscription: Subscription;

  constructor() {
    // do nothing
  }

  ngOnInit(): void {
    this.jobTemplateChangesSubscription = this.jobTemplateChanges.subscribe((value: JobTemplateChangeEventModel) => {
      if (value.jobTemplateId) {
        this.jobParametersChange.emit({ ...this.jobParameters, jobJar: undefined, mainClass: undefined });
      }
    });
  }

  jobJarChange(jobJar: string) {
    this.jobParametersChange.emit({ ...this.jobParameters, jobJar: jobJar });
  }

  mainClassChange(mainClass: string) {
    this.jobParametersChange.emit({ ...this.jobParameters, mainClass: mainClass });
  }

  additionalJarsChange(additionalJars: string[]) {
    this.jobParametersChange.emit({ ...this.jobParameters, additionalJars: additionalJars });
  }

  additionalFilesChange(additionalFiles: string[]) {
    this.jobParametersChange.emit({ ...this.jobParameters, additionalFiles: additionalFiles });
  }

  appArgumentsChange(appArguments: string[]) {
    this.jobParametersChange.emit({ ...this.jobParameters, appArguments: appArguments });
  }

  additionalSparkConfigChange(additionalSparkConfig: Map<string, string>) {
    this.jobParametersChange.emit({ ...this.jobParameters, additionalSparkConfig: additionalSparkConfig });
  }

  ngOnDestroy(): void {
    !!this.jobTemplateChangesSubscription && this.jobTemplateChangesSubscription.unsubscribe();
  }
}
