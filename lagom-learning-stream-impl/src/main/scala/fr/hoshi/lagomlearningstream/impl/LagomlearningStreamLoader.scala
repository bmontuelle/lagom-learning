package fr.hoshi.lagomlearningstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import fr.hoshi.lagomlearningstream.api.LagomlearningStreamService
import fr.hoshi.lagomlearning.api.LagomlearningService
import com.softwaremill.macwire._

class LagomlearningStreamLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new LagomlearningStreamApplication(context) {
      override def serviceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new LagomlearningStreamApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[LagomlearningStreamService])
}

abstract class LagomlearningStreamApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[LagomlearningStreamService](wire[LagomlearningStreamServiceImpl])

  // Bind the LagomlearningService client
  lazy val lagomlearningService = serviceClient.implement[LagomlearningService]
}
