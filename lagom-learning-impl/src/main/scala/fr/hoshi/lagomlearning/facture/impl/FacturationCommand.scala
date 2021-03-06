package fr.hoshi.lagomlearning.facture.impl

import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import fr.hoshi.lagomlearning.facture.api.model._
import play.api.libs.json.Json


/**
  * This interface defines all the commands that the HelloWorld entity supports.
  */
sealed trait FacturationCommand[R] extends ReplyType[R]

case class LireFacturation(numeroFacture: String) extends FacturationCommand[FactureTravaux]

object LireFacturation {
  implicit val format = Json.format[LireFacturation]
}




case class CreerFacturationTravaux(content: FactureTravauxCreation
                              ) extends FacturationCommand[FactureCree]

object CreerFacturationTravaux {
  implicit val format = Json.format[CreerFacturationTravaux]
}


case class ModifierFacturationTravaux(content: FactureTravauxModification) extends FacturationCommand[FactureModifiee]

object ModifierFacturationTravaux {
  implicit val format = Json.format[ModifierFacturationTravaux]
}

