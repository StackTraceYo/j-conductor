package org.stacktrace.yo.jconductor.akka.core.actor

import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger}

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.event.LoggingAdapter
import org.stacktrace.yo.jconductor.akka.core.work.JobProtocol._

class WorkNodeActor(myDispatcher: ActorRef, maxChildren: Integer = 1) extends Actor with ActorLogging {

  val working = new AtomicBoolean(false)
  val myChildrenCount = new AtomicInteger(0)
  val myLogger: LoggingAdapter = log

  override def receive: Receive = {

    case scheduleJob@ScheduleJob(jobParams: WorkParams[Any, Any], id: String) =>
      toggleToWorking()
      sender() ! Accepted(id)
      myDispatcher ! Accepted(id)
      val worker = context.actorOf(WorkNodeActor.workerProp(self, jobParams, id))
      myChildrenCount.getAndIncrement()
      worker ! Start()
  }

  def receiveWorking: Receive = {
    case scheduleJob@ScheduleJob(jobParams, id) =>
      if (myChildrenCount.get() >= maxChildren) {
        myLogger.warning("Max Concurrent Children Reached - Rejecting {}", id)
        sender() ! Rejected(jobParams, id)
      }
      else {
        val worker = context.actorOf(WorkNodeActor.workerProp(self, jobParams, id))
        myChildrenCount.getAndIncrement()
        worker ! Start()
      }
    case msg@JobStarted(id) =>
      myDispatcher ! msg
    case msg@JobComplete(id, result) =>
      myLogger.debug("Job {} Completed", id)
      notifyDispatcher(id, msg)
      sender() ! Close()
      if (myChildrenCount.decrementAndGet() < maxChildren) {
        toggleToIdle()
      }
    case msg@JobErrored(id, error) =>
      myLogger.warning("Job {} Errored: {}", id, error)
      notifyDispatcher(id, msg)
      sender() ! Close()
      if (myChildrenCount.decrementAndGet() < maxChildren) {
        toggleToIdle()
      }
  }

  def toggleToWorking(): Unit = {
    myLogger.debug("Switching To Working")
    context.become(receiveWorking)
    working.set(true)
  }

  def toggleToIdle(): Unit = {
    myLogger.debug("Switching To Idle")
    context.become(receive)
    working.set(false)
  }

  def notifyDispatcher(id: String, command: JobCommand): Unit = {
    myDispatcher ! command
  }

}

object WorkNodeActor {

  def workerProp(node: ActorRef, job: WorkParams[Any, Any], id: String): Props = {
    Props(new WorkerActor(node, job, id))
  }
}


