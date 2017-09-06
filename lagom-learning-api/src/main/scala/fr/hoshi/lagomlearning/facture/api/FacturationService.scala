package fr.hoshi.lagomlearning.facture.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
import com.lightbend.lagom.scaladsl.api.transport.Method._
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import fr.hoshi.lagomlearning.facture.api.model._


object FacturationService {
  val NEW_BILL_TOPIC_NAME = "facturation_crees"
  val UPDATED_BILL_TOPIC_NAME = "facturation_modifiees"
}


trait FacturationService extends Service {

  def get(numero: String): ServiceCall[NotUsed, FactureTravaux]

  def create: ServiceCall[FactureTravauxCreation, FactureCree]

  def update(numero: String): ServiceCall[FactureTravauxModification, FactureModifiee]

  /**
    * This gets published to Kafka.
    */
  def newBillsTopic(): Topic[FactureCree]
  def updatedBillsTopic(): Topic[FactureModifiee]

  override final def descriptor = {
    import Service._
    // @formatter:off
    named("lagom-learning-factures")
      .withCalls(
        restCall(GET, "/api/facture/:id", get _),
        restCall(POST, "/api/facture", create _),
        restCall(PUT, "/api/facture/:id", update _)
      )
      .withTopics(
        topic(FacturationService.NEW_BILL_TOPIC_NAME, newBillsTopic _)
          // Kafka partitions messages, messages within the same partition will
          // be delivered in order, to ensure that all messages for the same user
          // go to the same partition (and hence are delivered in order with respect
          // to that user), we configure a partition key strategy that extracts the
          // name as the partition key.
          .addProperty(KafkaProperties.partitionKeyStrategy, PartitionKeyStrategy[FactureCree](_.numero)),
        topic(FacturationService.UPDATED_BILL_TOPIC_NAME, updatedBillsTopic _)
          .addProperty(KafkaProperties.partitionKeyStrategy, PartitionKeyStrategy[FactureModifiee](_.numero))
      )
      .withAutoAcl(true)
    // @formatter:on
  }
}
