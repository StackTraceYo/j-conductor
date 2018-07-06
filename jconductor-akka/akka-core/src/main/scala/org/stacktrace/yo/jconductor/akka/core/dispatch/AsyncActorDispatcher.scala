package org.stacktrace.yo.jconductor.akka.core.dispatch

import org.stacktrace.yo.jconductor.core.dispatch.dispatcher.Dispatcher
import org.stacktrace.yo.jconductor.core.execution.stage.StageListener
import org.stacktrace.yo.jconductor.core.execution.work.Job

import scala.concurrent.Future

trait AsyncActorDispatcher extends Dispatcher {

  def scheduleAsync[T, V](job: Job[T, V], params: T): Future[V]

  def scheduleAsync[T, V](job: Job[T, V], params: T, listener: StageListener[V]): Future[V]

}
