package fr.hoshi.lagomlearning.facture.impl

import fr.hoshi.lagomlearning.facture.api.model.FactureTravaux
import play.api.libs.json.Json

object FacturationState {
  val empty = FacturationState(None)
  implicit val format = Json.format[FacturationState]
}

final case class FacturationState(content: Option[FactureTravaux]) {
  def isEmpty: Boolean = content.isEmpty
}