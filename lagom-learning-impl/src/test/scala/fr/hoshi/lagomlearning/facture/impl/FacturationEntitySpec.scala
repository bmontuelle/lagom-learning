package fr.hoshi.lagomlearning.facture.impl

import java.time.LocalDate

import akka.Done
import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.lightbend.lagom.scaladsl.testkit.PersistentEntityTestDriver
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import fr.hoshi.lagomlearning.facture.api.model.{FactureCree, FactureTravaux, FactureTravauxCreation, FactureTravauxModification}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

class FacturationEntitySpec extends WordSpec with Matchers with BeforeAndAfterAll {

  private val system = ActorSystem("FacturationEntitySpec",
    JsonSerializerRegistry.actorSystemSetupFor(FacturationSerializerRegistry))

  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  private val entityId = "facturation-1"
  private def withTestDriver(block: PersistentEntityTestDriver[FacturationCommand[_], FacturationEvent, FacturationState] => Unit): Unit = {
    val driver = new PersistentEntityTestDriver(system, new FactureEntity, entityId)
    block(driver)
    driver.getAllIssues should have size 0
  }

  "Facturation entity" should {
    "create a new facture, update it and read it back" in withTestDriver { driver =>
      val creation = FactureTravauxCreation("FV12345678", LocalDate.now, LocalDate.now, None, 230, 230)
      val outcome = driver.run(CreerFacturationTravaux(creation))
      outcome.events should ===(Vector(TravauxFactures(entityId, creation)))
      outcome.replies should ===(Vector(FactureCree(entityId)))

      val date1 = LocalDate.now
      val date2 = LocalDate.now

      val modification = FactureTravauxModification(
        date1, date2, "Modalité 2", "Modification reason", "Modification comment", 240, 230
      )
      val outcome1 = driver.run(ModifierFacturationTravaux(modification))
      outcome1.events should ===(Vector(FactureTravauxModifiee(entityId, modification)))
      outcome1.replies should ===(Vector(Done))

      val outcome2 = driver.run(LireFacturation("FV12345678"))
      outcome2.replies should ===(Vector(FactureTravaux("FV12345678",
        Some(date1), Some(date2), Some("Modalité 2"), Some("Modification reason"), Some("Modification comment"), Some(240), Some(230))))
    }

  }
}
