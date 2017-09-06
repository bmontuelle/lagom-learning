package fr.hoshi.lagomlearning.facture.impl

import java.time.LocalDate

import akka.stream.testkit.scaladsl.TestSink
import com.lightbend.lagom.scaladsl.api.transport.TransportException
import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.{ServiceTest, TestTopicComponents}
import fr.hoshi.lagomlearning.facture.api.FacturationService
import fr.hoshi.lagomlearning.facture.api.model._
import fr.hoshi.lagomlearning.impl.LagomlearningApplication
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration._

class FacturationServiceSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

  private val server = ServiceTest.startServer(
    ServiceTest.defaultSetup
      .withCassandra(true)
  ) { ctx =>
    new LagomlearningApplication(ctx) with LocalServiceLocator
  }

  val client = server.serviceClient.implement[FacturationService]

  override protected def afterAll() = server.stop()

  "Facturation service.create" should {
    "Creer une nouvelle facture" in {
      client.create.invoke(FactureTravauxCreation("FV12345678", LocalDate.now, LocalDate.now, None, 230, 230)).map { answer =>
        answer should ===(FactureCree("FV12345678"))
      }
    }

    "Rejeter une facture négative" in {
      recoverToSucceededIf[TransportException] {
        client.create.invoke(FactureTravauxCreation("FV12345678", LocalDate.now, LocalDate.now, None, -25, -25))
      }
    }
  }

  "Facturation service.update" should {
    "Modifier une nouvelle facture" in {
      val numeroFacture = "FV12345679"
      client.create.invoke(FactureTravauxCreation(numeroFacture, LocalDate.now, LocalDate.now, None, 230, 230)).flatMap { _ =>
        client.update(numeroFacture).invoke(FactureTravauxModification(LocalDate.now, LocalDate.now, "Mise a jour", "Correction chiffrage", "", 240, 230)).map { answer =>
          answer should ===(FactureModifiee(numeroFacture))
        }
      }
    }

    "Rejeter la modification d'une facture inconnue" in {
      recoverToSucceededIf[TransportException] {
        client.update("FV12345680").invoke(FactureTravauxModification(LocalDate.now, LocalDate.now, "Mise a jour", "Correction chiffrage", "", 240, 230))
      }
    }
  }

  "Facturation" should {
    "publish events on the kafka topic" in {
      implicit val system = server.actorSystem
      implicit val mat = server.materializer
      //subscribe to the topic
      val source = client.invoicesTopic().subscribe.atMostOnceSource

      //push a new invoice that should trigger a publication on the topic
      val numeroFacture = "ZAAAAAAA"
      Await.result(client.create.invoke(FactureTravauxCreation(numeroFacture, LocalDate.now, LocalDate.now, None, 230, 230)), 10 seconds)

      source
        .initialTimeout(3 minutes)
        .completionTimeout(3 minutes)
        .runWith(TestSink.probe[FactureEvent])
        .request(1)
        .expectNext(2 minutes) should ===(FactureCree(numeroFacture))

    }
  }
}
