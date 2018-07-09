package org.stacktrace.yo.jconductor.akka.core.actor

import akka.actor.{Actor, ActorLogging, ActorRef}

class WorkNodeActor(myDispatcher: ActorRef) extends Actor with ActorLogging {

  override def receive: Receive = {
    case any: Any =>
  }

}


