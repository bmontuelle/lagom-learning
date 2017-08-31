package fr.hoshi.lagomlearningstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import fr.hoshi.lagomlearningstream.api.LagomlearningStreamService
import fr.hoshi.lagomlearning.api.LagomlearningService

import scala.concurrent.Future

/**
  * Implementation of the LagomlearningStreamService.
  */
class LagomlearningStreamServiceImpl(lagomlearningService: LagomlearningService) extends LagomlearningStreamService {
  def stream = ServiceCall { hellos =>
    Future.successful(hellos.mapAsync(8)(lagomlearningService.hello(_).invoke()))
  }
}
