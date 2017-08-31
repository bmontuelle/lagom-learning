package fr.hoshi.lagomlearning.facture.impl

import akka.Done
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import fr.hoshi.lagomlearning.facture.api.FacturationService
import fr.hoshi.lagomlearning.facture.api.model.{FactureCree, FactureTravauxCreation}

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
}
