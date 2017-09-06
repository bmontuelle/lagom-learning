package fr.hoshi.lagomlearning.facture.api.model

import java.time.LocalDate

import play.api.libs.json._


final case class FactureTravaux(
                                 numero: String,
                                 dateDeFacturation: Option[LocalDate] = None,
                                 dateFinPrestation: Option[LocalDate] = None,
                                 modalite: Option[String] = None,
                                 motifModification: Option[String] = None,
                                 motifCommentaire: Option[String] = None,
                                 chiffrage: Option[Double] = None,
                                 estimation: Option[Double] = None
                               )

object FactureTravaux {
  implicit val format = Json.format[FactureTravaux]
}

final case class FactureTravauxCreation(numero: String,
                                        dateDeFacturation: LocalDate,
                                        dateFinPrestation: LocalDate,
                                        modalite: Option[String],
                                        chiffrage: Double,
                                        estimation: Double)

object FactureTravauxCreation {
  implicit val format = Json.format[FactureTravauxCreation]
}


final case class FactureTravauxModification(
                                             dateDeFacturation: LocalDate,
                                             dateFinPrestation: LocalDate,
                                             modalite: String,
                                             motifModification: String,
                                             motifCommentaire: String,
                                             chiffrage: Double,
                                             estimation: Double)

object FactureTravauxModification {
  implicit val format = Json.format[FactureTravauxModification]
}



sealed trait FactureEvent {
  def numero: String
}

object FactureEvent {
  implicit val reads: Reads[FactureEvent] = {
    (__ \ "event_type").read[String].flatMap {
      case "postCreated" => implicitly[Reads[FactureCree]].map(identity)
      case "postPublished" => implicitly[Reads[FactureModifiee]].map(identity)
      case other => Reads(_ => JsError(s"Unknown event type $other"))
    }
  }
  implicit val writes: Writes[FactureEvent] = Writes { event =>
    val (jsValue, eventType) = event match {
      case m: FactureCree => (Json.toJson(m)(FactureCree.format), "created")
      case m: FactureModifiee => (Json.toJson(m)(FactureModifiee.format), "updated")
    }
    jsValue.transform(__.json.update((__ \ 'event_type).json.put(JsString(eventType)))).get
  }
}

final case class FactureCree(numero: String) extends FactureEvent

object FactureCree {
  implicit val format = Json.format[FactureCree]
}

final case class FactureModifiee(numero: String) extends FactureEvent

object FactureModifiee {
  implicit val format = Json.format[FactureModifiee]
}
