package fr.hoshi.lagomlearning.facture.impl

import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import fr.hoshi.lagomlearning.facture.api.model._

object FacturationSerializerRegistry extends JsonSerializerRegistry {
  override val serializers = Vector(
    JsonSerializer[ModifierFacturationTravaux],
    JsonSerializer[CreerFacturationTravaux],
    JsonSerializer[LireFacturation],
    JsonSerializer[FactureCree],
    JsonSerializer[FactureModifiee],
    JsonSerializer[FactureTravauxModifiee],
    JsonSerializer[FactureTravaux],
    JsonSerializer[FacturationState],
    JsonSerializer[TravauxFactures]
  )
}
