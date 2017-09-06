package fr.hoshi.lagomlearning.facture.api.model

import java.time.LocalDate

import play.api.libs.json.Json


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


final case class FactureCree(numero: String)

object FactureCree {
  implicit val format = Json.format[FactureCree]
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


final case class FactureModifiee(numero: String)

object FactureModifiee {
  implicit val format = Json.format[FactureModifiee]
}
