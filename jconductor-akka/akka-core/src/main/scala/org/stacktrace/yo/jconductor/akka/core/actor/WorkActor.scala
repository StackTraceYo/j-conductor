package org.stacktrace.yo.jconductor.akka.core.actor

import akka.actor.{Actor, ActorLogging}

class WorkActor extends Actor with ActorLogging {

  override def receive: Receive = {
    case any: Any =>
  }

}


