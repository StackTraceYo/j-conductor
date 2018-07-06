package org.stacktrace.yo.jconductor.akka.core.dispatch

import akka.actor.{ActorRef, ActorSystem, Props}
import org.stacktrace.yo.jconductor.akka.core.work.JobProtocol.ScheduleJob
import org.stacktrace.yo.jconductor.core.execution.work.Job

class ActorDispatcher(val myWorkerCount: Int, val myDispatcherName: String = "ActorDispatcher", implicit val as: ActorSystem) {

  val myDispatcher: ActorRef = as.actorOf(ActorDispatcher.dispatchProp(myWorkerCount, myDispatcherName))

  def test(): Unit = {
    myDispatcher ! ScheduleJob(null, null)
  }

  //
  //  def scheduleAsync[T, V](job: Job[T, V], params: T): Future[V] = ???
  //
  //  def scheduleAsync[T, V](job: Job[T, V], params: T, listener: StageListener[V]): Future[V] = ???
  //
//    def schedule[T, V](job: Job[T, V], params: T): String = ???
  //
  //  def schedule[T, V](job: Job[T, V], params: T, listener: StageListener[V]): String = ???
  //
  //  def shutdown(): Boolean = ???
}

object ActorDispatcher {

  def dispatchProp(numWorkers: Int, name: String): Props = {
    Props(new DispatchActor(numWorkers, name))
  }
}
