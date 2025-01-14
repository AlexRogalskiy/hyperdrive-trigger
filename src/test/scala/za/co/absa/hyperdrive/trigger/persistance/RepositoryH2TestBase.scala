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

import play.api.libs.json.{JsValue, Json}
import slick.jdbc.JdbcType
import za.co.absa.hyperdrive.trigger.configuration.application.{DatabaseConfig, TestDatabaseConfig}
import za.co.absa.hyperdrive.trigger.models.tables.Profile

import scala.util.Try

trait RepositoryH2TestBase extends RepositoryTestBase {
  val h2Profile = slick.jdbc.H2Profile
  override val profile = h2Profile
  override val dbProvider: DatabaseProvider = new DatabaseProvider(TestDatabaseConfig(Map(
    "connectionPool" -> "disabled",
    "url" -> "jdbc:h2:mem:hyperdriver;INIT=create domain if not exists JSONB as text;MODE=PostgreSQL;DATABASE_TO_UPPER=false",
    "driver" -> "org.h2.Driver",
    "keepAliveConnection" -> "true"
  )))
}

trait H2Profile extends Profile {
  override val api: MyAPI = new MyAPI {
    override implicit val playJsonTypeMapper: JdbcType[JsValue] =
      MappedColumnType.base[JsValue, String](
        payload => payload.toString(),
        payloadString => Try(Json.parse(payloadString)).getOrElse(
          throw new Exception(s"Couldn't parse payload: $payloadString")
        )
      )
  }
}
