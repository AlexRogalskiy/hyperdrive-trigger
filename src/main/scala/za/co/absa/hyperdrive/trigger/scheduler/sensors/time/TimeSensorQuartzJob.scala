
/*
 * Copyright 2018-2019 ABSA Group Limited
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

package za.co.absa.hyperdrive.trigger.scheduler.sensors.time

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import org.quartz.{Job, JobExecutionContext}
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import play.api.libs.json.JsObject
import za.co.absa.hyperdrive.trigger.models.{Event, Properties}

import scala.concurrent.Future

class TimeSensorQuartzJob extends Job {
  private val logger = LoggerFactory.getLogger(this.getClass)
  private val eventDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")

  override def execute(jobExecutionContext: JobExecutionContext): Unit = {
    logger.info("Starting Time Sensor Quartz Job")
    val jobDataMap = jobExecutionContext.getJobDetail.getJobDataMap
    val push = jobDataMap.get(TimeSensor.PUSH_FUNCTION_JOB_DATA_MAP_KEY).asInstanceOf[Seq[Event] => Future[Unit]]
    val properties = jobDataMap.get(TimeSensor.PROPERTIES_JOB_DATA_MAP_KEY).asInstanceOf[Properties]
    val sourceEventId = s"sid=${properties.sensorId};t=${eventDateFormatter.format(LocalDateTime.now())}"
    val event = Event(sourceEventId, properties.sensorId, JsObject.empty)
    push(Seq(event))
  }
}
