package org.stacktrace.yo.jconductor.akka.core.dispatch

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{Matchers, WordSpecLike}

class ActorDispatcherSpec(system: ActorSystem = ActorSystem()) extends TestKit(system) with ImplicitSender with WordSpecLike with Matchers {

  "An ActorDispatcherTest" must {

    "Be Created" in {
      val dispatcher = new ActorDispatcher(1, "TestDispatcher", system)

    }
  }

}