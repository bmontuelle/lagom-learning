package fr.hoshi.lagomlearning.facture.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import fr.hoshi.lagomlearning.facture.api.FacturationService
import fr.hoshi.lagomlearning.facture.api.model.{FactureCree, FactureEvent, FactureModifiee, FactureTravauxCreation}

class FacturationServiceImpl (persistentEntityRegistry: PersistentEntityRegistry) extends FacturationService {

  override def get(numero: String) = ServiceCall { _ =>
    // Look up the lagom-learning entity for the given ID.
    val ref = persistentEntityRegistry.refFor[FactureEntity](numero)

    // Ask the entity the LireFacture command.
    ref.ask(LireFacturation(numero))
  }

  override def create: ServiceCall[FactureTravauxCreation, FactureCree] = ServiceCall { (req: FactureTravauxCreation)  =>
    // Look up the lagom-learning entity for the given ID.
    val ref = persistentEntityRegistry.refFor[FactureEntity](req.numero)

    ref.ask(CreerFacturationTravaux(req))
  }

  override def update(numero: String) = ServiceCall { req =>
    // Look up the lagom-learning entity for the given ID.
    val ref = persistentEntityRegistry.refFor[FactureEntity](numero)

    // Ask the entity the LireFacture command.
    ref.ask(ModifierFacturationTravaux(req))
  }

  //Streams processors to transform entity event stream to a kafka Topic
  override def invoicesTopic(): Topic[FactureEvent] = {
    TopicProducer.singleStreamWithOffset { fromOffset =>
      persistentEntityRegistry.eventStream(FacturationEvent.Tag, fromOffset).map { ev =>
        ev.event match {
          case TravauxFactures(numero, content) => (FactureCree(numero), ev.offset)
          case FactureTravauxModifiee(numero, content) => (FactureModifiee(numero), ev.offset)
        }
      }
    }
  }

}
