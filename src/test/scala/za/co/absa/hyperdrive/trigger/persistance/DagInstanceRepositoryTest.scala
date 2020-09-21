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

package za.co.absa.hyperdrive.trigger.persistance

import org.scalatest.FlatSpec
import org.scalatest._
import za.co.absa.hyperdrive.trigger.models.enums.DagInstanceStatuses

import scala.concurrent.ExecutionContext.Implicits.global

class DagInstanceRepositoryTest extends FlatSpec with Matchers with BeforeAndAfterAll with BeforeAndAfterEach with RepositoryTestBase {

  val dagInstanceRepository: DagInstanceRepository = new DagInstanceRepositoryImpl { override val profile = h2Profile }

  override def beforeAll: Unit = {
    h2SchemaSetup()
  }

  override def afterAll: Unit = {
    h2SchemaDrop()
  }

  override def afterEach: Unit = {
    clearData()
  }

  "dagInstanceRepository.getDagsToRun" should "return zero dag instances when db is empty" in {
    val runningIds = Seq.empty[Long]
    val size = 10
    val result = await(dagInstanceRepository.getDagsToRun(runningIds, size))
    result.isEmpty shouldBe true
  }

  "dagInstanceRepository.getDagsToRun" should "return one running dag for each workflow" in {
    createTestData()
    val runningIds = Seq.empty[Long]
    val size = 10
    val result = await(dagInstanceRepository.getDagsToRun(runningIds, size))
    result.size shouldBe 2
  }

  "dagInstanceRepository.getDagsToRun" should "filter out all dags" in {
    createTestData()
    val runningIds = TestData.dagInstances.map(_.id)
    val size = 10
    val result = await(dagInstanceRepository.getDagsToRun(runningIds, size))
    result.size shouldBe 0
  }

  "dagInstanceRepository.getDagsToRun" should "filter out running dags" in {
    createTestData()
    val runningIds = TestData.runningDagInstances.map(_.id)
    val size = 10
    val result = await(dagInstanceRepository.getDagsToRun(runningIds, size))
    result.foreach(_.status shouldBe DagInstanceStatuses.InQueue)
  }

  "dagInstanceRepository.getDagsToRun" should "return one running dag for each workflow with limited size" in {
    createTestData()
    val runningIds = Seq.empty[Long]
    val size = 1
    val result = await(dagInstanceRepository.getDagsToRun(runningIds, size))
    result.size shouldBe 1
  }

  "dagInstanceRepository.hasRunningDagInstance" should "return true when workflow with running instance exists" in {
    createTestData()
    val result = await(dagInstanceRepository.hasRunningDagInstance(TestData.w1.id))
    result shouldBe true
  }

  "dagInstanceRepository.hasRunningDagInstance" should "return false when workflow with running instance does not exist" in {
    createTestData()
    val result = await(dagInstanceRepository.hasRunningDagInstance(TestData.w3.id))
    result shouldBe false
  }

  "dagInstanceRepository.hasInQueueDagInstance" should "return true when workflow with inQueue instance exists" in {
    createTestData()
    val result = await(dagInstanceRepository.hasInQueueDagInstance(TestData.w2.id))
    result shouldBe true
  }

  it should "return false when workflow with inQueue instance does not exist" in {
    createTestData()
    val result = await(dagInstanceRepository.hasInQueueDagInstance(TestData.w3.id))
    result shouldBe false
  }

}
