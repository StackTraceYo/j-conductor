package org.stacktrace.yo.jconductor.akka.core.actor

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.event.LoggingAdapter
import akka.routing.RoundRobinPool
import org.stacktrace.yo.jconductor.akka.core.work.JobProtocol._

class DispatchActor(val myWorkerCount: Int, val myDispatcherName: String) extends Actor with ActorLogging {

  val myWorkers: ActorRef = context.actorOf(RoundRobinPool(myWorkerCount).props(DispatchActor.workerProp(self)), myDispatcherName)
  val myPendingCount = new AtomicInteger(0)
  val myRunningCount = new AtomicInteger(0)
  val myJobs = new ConcurrentHashMap[String, String]()
  val myLogger: LoggingAdapter = log

  override def receive: Receive = {

    case scheduleJob@ScheduleJob(work: WorkParams[Any, Any], id: String) =>
      sender() ! Accepted(id)
      schedule(scheduleJob, id)
    case Rejected(work, id) =>
      schedule(ScheduleJob((work._1, work._2, work._3), id), id)
    case Accepted(id) =>
      accept(id)
    case JobStarted(id) =>
      started(id)
    case JobComplete(id, result) =>
      finish(id, result)
    case JobErrored(id, throwable) =>
      finish(id, throwable)
    case Status() =>
      sender() ! DispatcherStatus(myRunningCount.get(), myPendingCount.get())
  }

  private def schedule(job: ScheduleJobCommand, id: String): Unit = {
    myLogger.debug("Scheduling {}", id)
    myWorkers ! job
  }

  private def finish(id: String, result: Any): Unit = {
    myRunningCount.getAndDecrement()
    result match {
      case e: Throwable => myJobs.replace(id, "Errored")
      case _: Any => myJobs.replace(id, "Complete")
    }
  }

  private def accept(id: String): Unit = {
    myPendingCount.getAndIncrement()
    myJobs.put(id, "Pending")
  }

  private def started(id: String): Unit = {
    myRunningCount.getAndIncrement()
    myPendingCount.getAndDecrement()
    myJobs.put(id, "Running")
  }

}

object DispatchActor {

  def workerProp(dispatcher: ActorRef): Props = {
    Props(new WorkNodeActor(dispatcher))
  }
}
