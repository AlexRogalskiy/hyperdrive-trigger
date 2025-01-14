
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

package za.co.absa.hyperdrive.trigger.api.rest.services

import java.time.LocalDateTime
import java.util.UUID
import org.apache.commons.lang3.RandomStringUtils
import za.co.absa.hyperdrive.trigger.api.rest.services.JobTemplateFixture.{GenericShellJobTemplate, GenericSparkJobTemplate}
import za.co.absa.hyperdrive.trigger.models._
import za.co.absa.hyperdrive.trigger.models.enums.JobTypes
import za.co.absa.hyperdrive.trigger.models.enums.JobTypes.JobType

object WorkflowFixture {
  val Random = new scala.util.Random(42)
  val MinLengthRandomString = 3
  val MaxLengthRandomString = 45
  val MinRandomDate = LocalDateTime.of(2020, 1, 1, 0, 0, 0)
  val CronExpressions = Seq(
    "0 0/10 * ? * * *",
    "0 1 * ? * * *",
    "0 31 * ? * * *",
    "0 0 18 ? * * *",
    "0 0 6 ? * * *",
    "0 43 12 22 4 ? *"
  )

  def createWorkflowJoined(sparkJobTemplateId: Long = GenericSparkJobTemplate.id, shellJobTemplateId: Long = GenericShellJobTemplate.id) = {
    WorkflowJoined(
      id = 10,
      name = "testWorkflow",
      isActive = true,
      created = LocalDateTime.of(2020, 2, 29, 10, 59, 34),
      updated = None,
      project = "testProject",
      sensor = Sensor(
        workflowId = 10,
        properties = KafkaSensorProperties(
          topic = "testTopic",
          servers =  List("http://localhost:9093", "http://localhost:9092"),
          matchProperties = Map("ingestionToken" -> "abcdef-123456")
        )
      ),
      dagDefinitionJoined = DagDefinitionJoined(
        workflowId = 10,
        jobDefinitions = Seq(
          JobDefinition(
            dagDefinitionId = 0,
            jobTemplateId = Some(sparkJobTemplateId),
            name = "TestJob1",
            jobParameters = SparkDefinitionParameters(
              jobType = JobTypes.Spark,
              jobJar = Option("/dir/driver.jar"),
              mainClass = Option("aaa.bbb.TestClass")
            ),
            order = 1,
            id = 1
          ),
          JobDefinition(
            dagDefinitionId = 0,
            jobTemplateId = Some(shellJobTemplateId),
            name = "TestJob2",
            jobParameters = ShellDefinitionParameters(
              scriptLocation = Option("/dir/script.sh")
            ),
            order = 2,
            id = 2
          )
        )
      )
    )
  }

  def createKafkaOffloadingWorkflow(projectName: String) = {
    WorkflowJoined(
      name = randomString(),
      isActive = randomBoolean(),
      created = randomDate(),
      updated = if (randomBoolean()) Some(randomDate()) else None,
      project = projectName,
      sensor = Sensor(
        workflowId = 0,
        properties = AbsaKafkaSensorProperties(
          topic = randomString(),
          servers = List(
              s"http://${randomString()}:${randomInt(0, 65535)}",
              s"https://${randomString()}:${randomInt(0, 65535)}",
              s"https://${randomString()}:${randomInt(0, 65535)}"
          ),
          ingestionToken = randomUuid()
        )
      ),
      dagDefinitionJoined = DagDefinitionJoined(
        workflowId = 0,
        jobDefinitions = Seq(
          JobDefinition(
            dagDefinitionId = 0,
            jobTemplateId = Some(GenericSparkJobTemplate.id),
            name = s"${randomString()} Driver",
            jobParameters = SparkDefinitionParameters(
              jobType = JobTypes.Spark,
              jobJar = Option(s"${randomString()}/driver.jar"),
              mainClass = Option("za.co.absa.hyperdrive.driver.drivers.CommandLineIngestionDriver"),
              appArguments = List(
                s"component.decoder=za.co.absa.hyperdrive.ingestor.implementation.decoder.avro.confluent.ConfluentAvroKafkaStreamDecoder",
                s"component.ingestor=Spark",
                s"component.manager=za.co.absa.hyperdrive.ingestor.implementation.manager.checkpoint.CheckpointOffsetManager",
                s"component.reader=za.co.absa.hyperdrive.ingestor.implementation.reader.kafka.KafkaStreamReader",
                s"component.transformer=za.co.absa.hyperdrive.ingestor.implementation.transformer.column.selection.ColumnSelectorStreamTransformer",
                s"component.writer=za.co.absa.hyperdrive.ingestor.implementation.writer.parquet.ParquetPartitioningStreamWriter",
                s"decoder.avro.schema.registry.url=${randomString()}",
                s"decoder.avro.schema.retention.policy=${randomString()}",
                s"decoder.avro.value.schema.id=${randomString()}",
                s"decoder.avro.value.schema.naming.strategy=${randomString()}",
                s"decoder.avro.value.schema.record.name=${randomString()}",
                s"decoder.avro.value.schema.record.namespace=${randomString()}",
                s"ingestor.spark.app.name=${randomString()}",
                s"manager.checkpoint.base.location=${randomString()}",
                s"reader.kafka.brokers=${randomString()}",
                s"reader.kafka.topic=${randomString()}",
                s"reader.option.failOnDataLoss=${randomString()}",
                s"reader.option.kafka.sasl.jaas.config=${randomString()}",
                s"reader.option.kafka.sasl.kerberos.service.name=${randomString()}",
                s"reader.option.kafka.sasl.mechanism=${randomString()}",
                s"reader.option.kafka.security.protocol=${randomString()}",
                s"reader.option.kafka.ssl.key.password=${randomString()}",
                s"reader.option.kafka.ssl.keystore.location=${randomString()}",
                s"reader.option.kafka.ssl.keystore.password=${randomString()}",
                s"reader.option.kafka.ssl.truststore.location=${randomString()}",
                s"reader.option.kafka.ssl.truststore.password=${randomString()}",
                s"transformer.columns.to.select=${randomString()}",
                s"writer.parquet.destination.directory=${randomString()}"
              )
            ),
            order = 0
          ),
          JobDefinition(
            dagDefinitionId = 0,
            jobTemplateId = Some(GenericSparkJobTemplate.id),
            name = s"${randomString()} Publisher",
            jobParameters = SparkDefinitionParameters(
              jobType = JobTypes.Spark,
              jobJar = Option(s"${randomString()}/publisher.jar"),
              mainClass = Option("za.co.absa.hyperdrive.publisher.HyperdrivePublisher"),
              appArguments = List(
                s"--buffer-directory=${randomString()}",
                s"--raw-directory=${randomString()}",
                s"--publish-directory=${randomString()}"
              )
            ),
            order = 1
          )
        )
      )
    )
  }

  def createTimeBasedShellScriptWorkflow(projectName: String) = {
    WorkflowJoined(
      name = s"Time ${randomString(maxLength = MaxLengthRandomString - 5)}",
      isActive = randomBoolean(),
      created = randomDate(),
      updated = if (randomBoolean()) Some(randomDate()) else None,
      project = projectName,
      sensor = Sensor(
        workflowId = 0,
        properties = TimeSensorProperties(
          cronExpression = randomCron()
        )
      ),
      dagDefinitionJoined = DagDefinitionJoined(
        workflowId = 0,
        jobDefinitions = Seq(
          JobDefinition(
            dagDefinitionId = 0,
            jobTemplateId = Some(GenericShellJobTemplate.id),
            name = s"${randomString()}",
            jobParameters = ShellDefinitionParameters(
              scriptLocation = Option(s"${randomString()}/script.sh")
            ),
            order = 0
          )
        )
      )
    )
  }

  def createShellScriptJobWithoutTemplateAndScript(): WorkflowJoined = {
    WorkflowJoined(
      name = s"Time ${randomString(maxLength = MaxLengthRandomString - 5)}",
      isActive = randomBoolean(),
      created = randomDate(),
      updated = if (randomBoolean()) Some(randomDate()) else None,
      project = "project",
      sensor = Sensor(
        workflowId = 0,
        properties = TimeSensorProperties(
          cronExpression = randomCron()
        )
      ),
      dagDefinitionJoined = DagDefinitionJoined(
        workflowId = 0,
        jobDefinitions = Seq(
          JobDefinition(
            dagDefinitionId = 0,
            jobTemplateId = None,
            name = s"${randomString()}",
            jobParameters = ShellDefinitionParameters(
              scriptLocation = None
            ),
            order = 0
          )
        )
      )
    )
  }

  def createSparkJobWithoutTemplateAndJar(): WorkflowJoined = {
    WorkflowJoined(
      name = s"Time ${randomString(maxLength = MaxLengthRandomString - 5)}",
      isActive = randomBoolean(),
      created = randomDate(),
      updated = if (randomBoolean()) Some(randomDate()) else None,
      project = "project",
      sensor = Sensor(
        workflowId = 0,
        properties = TimeSensorProperties(
          cronExpression = randomCron()
        )
      ),
      dagDefinitionJoined = DagDefinitionJoined(
        workflowId = 0,
        jobDefinitions = Seq(
          JobDefinition(
            dagDefinitionId = 0,
            jobTemplateId = None,
            name = s"${randomString()}",
            jobParameters = SparkDefinitionParameters(
              jobType = JobTypes.Spark,
              jobJar = None,
              mainClass = None
            ),
            order = 0
          )
        )
      )
    )
  }

  private def randomString(minLength: Int = MinLengthRandomString, maxLength: Int = MaxLengthRandomString) = {
    val length = randomInt(minLength, maxLength)
    RandomStringUtils.randomAlphanumeric(length)
  }

  private def randomUuid() = {
    UUID.randomUUID().toString
  }

  private def randomDate() = {
    val currentYear = LocalDateTime.now().getYear
    val endOfYear = LocalDateTime.of(currentYear, 12, 31, 23, 59, 59)
    val days = java.time.temporal.ChronoUnit.DAYS.between(MinRandomDate, endOfYear).toInt
    MinRandomDate.plusDays(Random.nextInt(days)).plusSeconds(Random.nextInt(86400))
  }

  private def randomBoolean() = {
    Random.nextBoolean()
  }

  private def randomInt(min: Int, max: Int) = min + Random.nextInt(max - min)

  private def randomCron() = CronExpressions(Random.nextInt(CronExpressions.size))
}
