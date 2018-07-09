package org.stacktrace.yo.jconductor.akka.core.dispatch

import java.util.UUID

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import org.stacktrace.yo.jconductor.akka.core.actor.DispatchActor
import org.stacktrace.yo.jconductor.akka.core.work.JobProtocol.{JobCommand, JobScheduled, ScheduleJob}
import org.stacktrace.yo.jconductor.core.execution.stage.StageListener
import org.stacktrace.yo.jconductor.core.execution.work.Job

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.language.postfixOps

class ActorDispatcher(val myWorkerCount: Int, val myDispatcherName: String = "ActorDispatcher", implicit val as: ActorSystem) {
  private implicit val timeout: Timeout = Timeout(3 seconds)

  val myDispatcher: ActorRef = as.actorOf(ActorDispatcher.dispatchProp(myWorkerCount, myDispatcherName))
  implicit val ex: ExecutionContextExecutor = as.dispatcher

  //
  //  def scheduleAsync[T, V](job: Job[T, V], params: T): Future[V] = ???
  //
  //  def scheduleAsync[T, V](job: Job[T, V], params: T, listener: StageListener[V]): Future[V] = ???
  //

  def schedule[T, V](job: Job[T, V], params: T): String = {
    val id = createId()
    Await.result(
      askCommand(ScheduleJob(job, params, id))
        .mapTo[JobScheduled]
        .map(js => js.id),
      1 seconds
    )
  }

  def schedule[T, V](job: Job[T, V], params: T, listener: StageListener[V]): String = {
    val id = createId()
    Await.result(
      askCommand(ScheduleJob(job, params, id))
        .mapTo[JobScheduled]
        .map(js => js.id),
      1 seconds
    )
  }

  private def askCommand(jobCommand: JobCommand) = {
    myDispatcher ? jobCommand
  }

  private def createId(): String = {
    UUID.randomUUID().toString
  }
}

object ActorDispatcher {

  def dispatchProp(numWorkers: Int, name: String): Props = {
    Props(new DispatchActor(numWorkers, name))
  }
}
