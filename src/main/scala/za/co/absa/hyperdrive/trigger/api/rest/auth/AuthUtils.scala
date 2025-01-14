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

package za.co.absa.hyperdrive.trigger.api.rest.auth

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.{Authentication, GrantedAuthority}
import org.springframework.stereotype.Component
import za.co.absa.hyperdrive.trigger.configuration.application.AuthConfig

@Component("authUtils")
class AuthUtils @Autowired()(authConfig: AuthConfig) {
  private val adminRole: Option[String] = authConfig.adminRole

  def hasAdminRole(auth: Authentication): Boolean = {
    adminRole.forall(auth.getAuthorities.toArray(Array[GrantedAuthority]()).map(auth => auth.getAuthority).contains(_))
  }
}
