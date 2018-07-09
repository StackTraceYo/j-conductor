package org.stacktrace.yo.jconductor.akka.core.actor

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.event.LoggingAdapter
import akka.routing.RoundRobinPool
import org.stacktrace.yo.jconductor.akka.core.work.JobProtocol._
import org.stacktrace.yo.jconductor.core.execution.stage.StageListener
import org.stacktrace.yo.jconductor.core.execution.work.Job

class DispatchActor(val myWorkerCount: Int, val myDispatcherName: String) extends Actor with ActorLogging {

  val myWorkers: ActorRef = context.actorOf(RoundRobinPool(myWorkerCount).props(DispatchActor.workerProp(self)), myDispatcherName)
  val myPendingCount = new AtomicInteger(0)
  val myRunningCount = new AtomicInteger(0)
  val myJobs = new ConcurrentHashMap[String, String]()
  val myLogger: LoggingAdapter = log

  override def receive: Receive = {

    case scheduleJob@ScheduleJob(job: Job[Any, Any], params: Any, id: String) =>
      schedule(scheduleJob, id)
      sender() ! JobScheduled(id)
    case scheduleJob@ScheduleJobWithListener(job: Job[Any, Any], params: Any, listener: StageListener[Any], id: String) =>
      schedule(scheduleJob, id)
      sender() ! JobScheduled(id)
  }

  def schedule(job: ScheduleJobCommand, id: String): Unit = {
    myLogger.debug("Scheduling {}", id)
    myPendingCount.getAndIncrement()
    myWorkers ! job
    myJobs.put(id, "Pending")
  }

}

object DispatchActor {

  def workerProp(dispatcher: ActorRef): Props = {
    Props(new WorkNodeActor(dispatcher))
  }
}
