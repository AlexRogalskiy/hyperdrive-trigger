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

package za.co.absa.hyperdrive.trigger.models.tables

import java.time.LocalDateTime

import slick.lifted.{ColumnOrdered, ProvenShape}
import za.co.absa.hyperdrive.trigger.models.dagRuns.{DagRun, Sort}

trait DagRunTable {
  this: Profile with JdbcTypeMapper =>
  import profile.api._

  final class DagRunTable(tag: Tag) extends Table[DagRun](tag, _tableName = "dag_run_view") {
    def workflowName: Rep[String] = column[String]("workflow_name")
    def projectName: Rep[String] = column[String]("project_name")
    def jobCount: Rep[Int] = column[Int]("job_count")
    def started: Rep[LocalDateTime] = column[LocalDateTime]("started")
    def finished: Rep[Option[LocalDateTime]] = column[Option[LocalDateTime]]("finished")
    def status: Rep[String] = column[String]("status")
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc, O.SqlType("BIGSERIAL"))
    override def * : ProvenShape[DagRun] = (workflowName, projectName, jobCount, started, finished, status, id).mapTo[DagRun]

    private val sortFields = Map(
      "workflowName" -> this.workflowName,
      "projectName" -> this.projectName,
      "jobCount" -> this.jobCount,
      "started" -> this.started,
      "finished" -> this.finished,
      "status" -> this.status,
      "id" -> this.id
    )

    def sortFields(sort: Option[Sort]): ColumnOrdered[_] = {
      val definedSort = sort.getOrElse(Sort("id", 1))
      val ordering: slick.ast.Ordering.Direction = if (definedSort.order == -1) slick.ast.Ordering.Desc else slick.ast.Ordering.Asc
      ColumnOrdered(this.sortFields(definedSort.by), slick.ast.Ordering(ordering))
    }

  }

  lazy val dagRunTable: TableQuery[DagRunTable] = TableQuery[DagRunTable]
}
