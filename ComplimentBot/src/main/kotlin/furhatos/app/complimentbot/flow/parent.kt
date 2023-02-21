package furhatos.app.complimentbot.flow

import furhatos.app.complimentbot.doubleUserEventDelay
import furhatos.app.complimentbot.flow.main.attentionState
import furhatos.app.complimentbot.utils.*
import furhatos.event.Event
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.onUserEnter
import furhatos.flow.kotlin.onUserLeave
import furhatos.flow.kotlin.state
import furhatos.records.User
import java.util.*
import kotlin.concurrent.schedule

val userEnterLocks = mutableMapOf<String, Boolean>()
val userLeaveLocks = mutableMapOf<String, Boolean>()
class LeaderGoneForAWhileInstant(val user: User): Event()
class LeaderGoneForAWhile(val user: User): Event()

val UniversalParent = state {
    onEntry(inherit = true, priority = true) {
        logger.debug("${thisState.name} entered")
        propagate()
    }
    onReentry (inherit = true, priority = true) {
        logger.debug("${thisState.name} re-entered")
        propagate()
    }
    onExit (inherit = true, priority = true) {
        logger.debug("${thisState.name} exited")
        propagate()
    }

    onUserEnter(instant = true, priority = true) {user ->
        if (userEnterLocks[user.id] != true) { // Is null if lock not yet attributed
            userEnterLocks[user.id] = true
            Timer().schedule(delay = doubleUserEventDelay) {
                userEnterLocks[user.id] = false
            }
            logger.info("${user.id} entered ${user.zone.name}")
            handleUserGroupEntry(user)
            propagate()
        } else {
            println("userEnter event sensed twice")
        }
    }
    onUserLeave(instant = true, priority = true) {user ->
        if (userLeaveLocks[user.id] != true) { // Is null if lock not yet attributed
            userLeaveLocks[user.id] = true
            Timer().schedule(delay = doubleUserEventDelay) {
                userLeaveLocks[user.id] = false
            }
            logger.info("${user.id} left towards ${user.zone.name}")
            propagate()
        } else {
            println("userLeave event sensed twice")
        }
    }
}

val IdleParent = state(UniversalParent) {
    onUserEnter {
        furhat.attendC(it)
        goto(attentionState)
    }
}