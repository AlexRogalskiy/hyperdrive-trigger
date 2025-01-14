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

package za.co.absa.hyperdrive.trigger.models.enums

import play.api.libs.json.{Format, JsPath, JsString, Writes}

object JobTypes {

  sealed abstract class JobType(val name: String) {
    override def toString: String = name
  }

  case object Spark extends JobType("Spark")
  case object Hyperdrive extends JobType("Hyperdrive")
  case object Shell extends JobType("Shell")

  val jobTypes: Set[JobType] = Set(Spark, Hyperdrive, Shell)

  def convertJobTypeNameToJobType(jobType: String): JobType = {
    JobTypes.jobTypes.find(_.name == jobType).getOrElse(
      throw new Exception(s"Couldn't find JobType: $jobType")
    )
  }

  implicit val jobTypesFormat: Format[JobType] = Format[JobType](
    JsPath.read[String].map(convertJobTypeNameToJobType),
    Writes[JobType] { jobType => JsString(jobType.name) }
  )
}