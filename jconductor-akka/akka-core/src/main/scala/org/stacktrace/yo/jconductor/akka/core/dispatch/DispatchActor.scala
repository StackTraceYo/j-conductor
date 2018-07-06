package org.stacktrace.yo.jconductor.akka.core.dispatch

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.event.LoggingAdapter
import akka.routing.RoundRobinPool
import org.stacktrace.yo.jconductor.akka.core.actor.WorkActor
import org.stacktrace.yo.jconductor.akka.core.work.JobProtocol.ScheduleJob
import org.stacktrace.yo.jconductor.core.execution.work.Job

class DispatchActor(val myWorkerCount: Int, val myDispatcherName: String) extends Actor with ActorLogging {

  val myWorkers: ActorRef = context.actorOf(RoundRobinPool(myWorkerCount).props(DispatchActor.workerProp()), myDispatcherName)
  val myLogger: LoggingAdapter = log

  override def receive: Receive = {

    case scheduleJob@ScheduleJob(job: Job[Any, Any], id: String) =>
      myLogger.debug("Scheduling")
  }
}

object DispatchActor {

  def workerProp(): Props = {
    Props(new WorkActor())
  }
}
