package za.co.absa.hyperdrive.trigger.persistance

import za.co.absa.hyperdrive.trigger.models.JobDefinition
import za.co.absa.hyperdrive.trigger.models.tables.JDBCProfile.profile._
import scala.concurrent.{ExecutionContext, Future}

trait JobDefinitionsRepository extends Repository {
  def getJobDefinition(eventTriggerId: Long)(implicit ec: ExecutionContext): Future[Option[JobDefinition]]
}

class JobDefinitionsRepositoryImpl extends JobDefinitionsRepository {

  override def getJobDefinition(eventTriggerId: Long)(implicit ec: ExecutionContext): Future[Option[JobDefinition]] = db.run{(
    for {
      e <- eventTriggerTable if e.id === eventTriggerId
      w <- workflowTable if w.id === e.workflowId
      jd <- jobDefinitionTable if w.id === jd.workflowId
    } yield {
      jd
    }).result.headOption
  }

}