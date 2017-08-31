package fr.hoshi.lagomlearning.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import fr.hoshi.lagomlearning.api.LagomlearningService
import fr.hoshi.lagomlearning.facture.api.FacturationService
import fr.hoshi.lagomlearning.facture.api.model.{FactureCree, FactureTravaux}
import fr.hoshi.lagomlearning.facture.impl._
import play.api.libs.ws.ahc.AhcWSComponents

import scala.collection.immutable.Seq

class LagomlearningLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new LagomlearningApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new LagomlearningApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[LagomlearningService])
}

abstract class LagomlearningApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with LagomKafkaComponents
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer = LagomServer.forServices(
    bindService[LagomlearningService].to(wire[LagomlearningServiceImpl]),
    bindService[FacturationService].to(wire[FacturationServiceImpl]),
    metricsServiceBinding
  )

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry = LagomlearningSerializerRegistry ++ FacturationSerializerRegistry

  // Register the lagom-learning persistent entity
  persistentEntityRegistry.register(wire[LagomlearningEntity])
  persistentEntityRegistry.register(wire[FactureEntity])
}
