package org.stacktrace.yo.jconductor.akka.core.actor

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill}
import akka.event.LoggingAdapter
import org.stacktrace.yo.jconductor.akka.core.work.JobProtocol._
import org.stacktrace.yo.jconductor.core.execution.stage.StageListener
import org.stacktrace.yo.jconductor.core.execution.work.Job

class WorkerActor(node: ActorRef, workParams: WorkParams[Any, Any], id: String) extends Actor with ActorLogging {

  val myJob: Job[Any, Any] = workParams._1
  val myParams: Any = workParams._2
  val myListener: Option[StageListener[Any]] = workParams._3
  val myLogger: LoggingAdapter = log


  override def receive: Receive = {
    case Start() =>
      myLogger.debug("Starting {}", id)
      node ! JobStarted(id)
      myJob.init(myParams)
      myListener.map(listener => listener.onStart())
      try {
        myLogger.debug("Working {}", id)
        val result = myJob.doWork(myParams)
        myListener.map(listener => listener.onComplete())
        myLogger.debug("{} Finished", id)
        node ! JobComplete(id, result)
      }
      catch {
        case exec: Throwable =>
          myListener.map(listener => listener.onError())
          myLogger.debug("{} Errored", id)
          node ! JobErrored(id, exec)
      }
    case Close() =>
      myLogger.debug("Closing {}", id)
      myJob.postRun()
      self ! PoisonPill
  }

}


