package fr.hoshi.lagomlearning.impl

import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import fr.hoshi.lagomlearning.api.GreetingMessage

object LagomlearningSerializerRegistry extends JsonSerializerRegistry {
  override val serializers = Vector(
    JsonSerializer[GreetingMessage],
    JsonSerializer[GreetingMessageChanged],
    JsonSerializer[Hello],
    JsonSerializer[UseGreetingMessage],
    JsonSerializer[GreetingMessageChanged],
    JsonSerializer[LagomlearningState]
  )
}
