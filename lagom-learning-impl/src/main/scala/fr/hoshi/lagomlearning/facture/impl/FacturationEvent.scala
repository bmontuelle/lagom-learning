package fr.hoshi.lagomlearning.facture.impl

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag}
import fr.hoshi.lagomlearning.facture.api.model.{FactureTravauxModification, FactureTravauxCreation}
import play.api.libs.json.Json

/**
  * This interface defines all the events that the LagomlearningEntity supports.
  */
sealed trait FacturationEvent extends AggregateEvent[FacturationEvent] {
  def aggregateTag = FacturationEvent.Tag
}

object FacturationEvent {
  val Tag = AggregateEventTag[FacturationEvent]
}

case class TravauxFactures(
                            numeroFacture: String,
                            content: FactureTravauxCreation
                          ) extends FacturationEvent

object TravauxFactures {
  implicit val format = Json.format[TravauxFactures]
}


case class FactureTravauxModifiee(
                                   numeroFacture: String,
                                   content: FactureTravauxModification
                                 ) extends FacturationEvent

object FactureTravauxModifiee {
  implicit val format = Json.format[FactureTravauxModifiee]
}
