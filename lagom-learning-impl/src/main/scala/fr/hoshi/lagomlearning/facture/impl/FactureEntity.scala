package fr.hoshi.lagomlearning.facture.impl

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import fr.hoshi.lagomlearning.facture.api.model.{FactureCree, FactureTravaux}

class FactureEntity extends PersistentEntity {

  override type Command = FacturationCommand[_]
  override type Event = FacturationEvent
  override type State = FacturationState

  /**
    * The initial state. This is used if there is no snapshotted state to be found.
    */
  override def initialState: FacturationState = FacturationState.empty

  /**
    * An entity can define different behaviours for different states, so the behaviour
    * is a function of the current state to a set of actions.
    */
  override def behavior: Behavior = {
    // @formatter:off
    Actions()
      .onCommand[CreerFacturationTravaux, FactureCree] {
        case (CreerFacturationTravaux(content), ctx, state) => {
          if (!state.isEmpty) {
            ctx.invalidCommand(s"La facture N° $entityId a déja été créée")
            ctx.done
          }
          else if (content.chiffrage <= 0) {
            ctx.invalidCommand("Le montant de la facture ne peut etre inferieur à 0")
            ctx.done
          }
          else {
            ctx.thenPersist(TravauxFactures(entityId, content)) { _ =>
              ctx.reply(FactureCree(entityId))
            }
          }
        }
      }
      .onCommand[ModifierFacturationTravaux, Done] {
        case (ModifierFacturationTravaux(content), ctx, state) => {
          if (state.isEmpty) {
            ctx.invalidCommand(s"La facture N° $entityId doit etre créer pour pouvoir etre modifiée")
            ctx.done
          }
          else if (content.chiffrage <= 0) {
            ctx.invalidCommand("Le montant de la facture ne peut etre inferieur à 0")
            ctx.done
          }
          else {
            ctx.thenPersist(FactureTravauxModifiee(entityId, content)) { _ =>
              ctx.reply(Done)
            }
          }
        }
      }
      .onEvent {
        case (TravauxFactures(factureId, creerFactureTravaux), state) => {
          state.copy(content =
            Some(FactureTravaux(
              numero = creerFactureTravaux.numero,
              dateDeFacturation = Some(creerFactureTravaux.dateDeFacturation),
              dateFinPrestation = Some(creerFactureTravaux.dateFinPrestation),
              modalite = creerFactureTravaux.modalite,
              chiffrage = Some(creerFactureTravaux.chiffrage),
              estimation = Some(creerFactureTravaux.estimation)
            )))
        }
        case (FactureTravauxModifiee(factureId, modifierFactureTravaux), state) => {
          state.copy(content =
            Some(state.content.get.copy(
              dateDeFacturation = Some(modifierFactureTravaux.dateDeFacturation),
              dateFinPrestation = Some(modifierFactureTravaux.dateFinPrestation),
              modalite = Some(modifierFactureTravaux.modalite),
              motifModification = Some(modifierFactureTravaux.motifModification),
              motifCommentaire = Some(modifierFactureTravaux.motifCommentaire),
              chiffrage = Some(modifierFactureTravaux.chiffrage),
              estimation = Some(modifierFactureTravaux.estimation)
            )))
        }
      }
      .onReadOnlyCommand[LireFacturation, FactureTravaux] {
        // Command handler for the Hello command
        case (lireFacture: LireFacturation, ctx, state) => {
          // Reply with a message built from the current message, and the name of
          // the person we're meant to say hello to.
          state.content match {
            case Some(factureTravaux) => ctx.reply(factureTravaux)
            case None => ctx.invalidCommand(s"La facture N° ${lireFacture.numeroFacture} n'existe pas ")
          }
        }
      }
    // @formatter:on
  }
}

