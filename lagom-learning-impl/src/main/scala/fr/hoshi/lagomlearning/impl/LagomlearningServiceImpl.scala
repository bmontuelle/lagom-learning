package fr.hoshi.lagomlearning.impl

import fr.hoshi.lagomlearning.api
import fr.hoshi.lagomlearning.api.{LagomlearningService}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}

/**
  * Implementation of the LagomlearningService.
  */
class LagomlearningServiceImpl(persistentEntityRegistry: PersistentEntityRegistry) extends LagomlearningService {

  override def hello(id: String) = ServiceCall { _ =>
    // Look up the lagom-learning entity for the given ID.
    val ref = persistentEntityRegistry.refFor[LagomlearningEntity](id)

    // Ask the entity the Hello command.
    ref.ask(Hello(id))
  }

  override def useGreeting(id: String) = ServiceCall { request =>
    // Look up the lagom-learning entity for the given ID.
    val ref = persistentEntityRegistry.refFor[LagomlearningEntity](id)

    // Tell the entity to use the greeting message specified.
    ref.ask(UseGreetingMessage(request.message))
  }


  override def greetingsTopic(): Topic[api.GreetingMessageChanged] =
    TopicProducer.singleStreamWithOffset {
      fromOffset =>
        persistentEntityRegistry.eventStream(LagomlearningEvent.Tag, fromOffset)
          .map(ev => (convertEvent(ev), ev.offset))
    }

  private def convertEvent(helloEvent: EventStreamElement[LagomlearningEvent]): api.GreetingMessageChanged = {
    helloEvent.event match {
      case GreetingMessageChanged(msg) => api.GreetingMessageChanged(helloEvent.entityId, msg)
    }
  }
}
