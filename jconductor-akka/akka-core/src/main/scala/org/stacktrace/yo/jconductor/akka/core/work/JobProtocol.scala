package org.stacktrace.yo.jconductor.akka.core.work

import org.stacktrace.yo.jconductor.core.execution.stage.StageListener
import org.stacktrace.yo.jconductor.core.execution.work.Job

object JobProtocol {

  sealed trait JobCommand

  sealed trait ScheduleJobCommand

  case class ScheduleJob[Param, Result](job: Job[Param, Result], params: Param, id: String) extends JobCommand with ScheduleJobCommand

  case class ScheduleJobWithListener[Param, Result](job: Job[Param, Result], params: Param, listener: StageListener[Result], id: String) extends JobCommand with ScheduleJobCommand

  case class JobScheduled(id: String) extends JobCommand

  case class JobStarted(id: String) extends JobCommand

  case class JobComplete[Result](id: String, jobResult: Result) extends JobCommand

  case class JobErrored(id: String, error: Throwable) extends JobCommand

}
