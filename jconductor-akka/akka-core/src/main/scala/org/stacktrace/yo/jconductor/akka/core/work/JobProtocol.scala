package org.stacktrace.yo.jconductor.akka.core.work

import org.stacktrace.yo.jconductor.core.execution.stage.StageListener
import org.stacktrace.yo.jconductor.core.execution.work.Job

object JobProtocol {


  sealed trait JobCommand

  sealed trait ScheduleJobCommand extends JobCommand

  case class ScheduleJob[Param, Result](work: WorkParams[Param, Result], id: String) extends ScheduleJobCommand

  case class Rejected[Param, Result](work: WorkParams[Param, Result], id: String) extends ScheduleJobCommand

  case class Accepted(id: String) extends ScheduleJobCommand

  case class JobStarted(id: String) extends JobCommand

  case class JobComplete[Result](id: String, jobResult: Result) extends JobCommand

  case class JobErrored(id: String, error: Throwable) extends JobCommand

  case class Start() extends JobCommand

  case class Close() extends JobCommand

  case class Status() extends JobCommand

  case class DispatcherStatus(running: Int, pending: Int)

  type WorkParams[Param, Result] = (Job[Param, Result], Param, Option[StageListener[Result]])
}
