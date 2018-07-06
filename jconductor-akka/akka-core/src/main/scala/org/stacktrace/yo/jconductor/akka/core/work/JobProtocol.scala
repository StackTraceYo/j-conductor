package org.stacktrace.yo.jconductor.akka.core.work

import org.stacktrace.yo.jconductor.core.execution.work.Job

object JobProtocol {

  case class ScheduleJob[Param, Result](job: Job[Param, Result], id: String)

  case class JobScheduled(id: String)

  case class JobStarted(id: String)

  case class JobComplete[Result](id: String, jobResult: Result)

  case class JobErrored(id: String, error: Throwable)

}
