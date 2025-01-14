
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

package za.co.absa.hyperdrive.trigger.configuration.application

import org.springframework.boot.context.properties.bind.{DefaultValue, Name}
import org.springframework.boot.context.properties.{ConfigurationProperties, ConstructorBinding}
import org.springframework.validation.annotation.Validated

import javax.validation.constraints.NotNull

@ConfigurationProperties("health")
@ConstructorBinding
@Validated
class HealthConfig (
  @DefaultValue(Array("120000"))
  @Name("databaseConnection.timeoutMillis")
  val databaseConnectionTimeoutMillis: Int = 120000,
  @NotNull
  @Name("yarnConnection.testEndpoint")
  val yarnConnectionTestEndpoint: String,
  @Name("yarnConnection.timeoutMillis")
  yarnConnectionTimeoutMillisInternal: Integer
) {
  val yarnConnectionTimeoutMillis: Option[Int] = Option[Integer](yarnConnectionTimeoutMillisInternal).map(Integer2int)
}
