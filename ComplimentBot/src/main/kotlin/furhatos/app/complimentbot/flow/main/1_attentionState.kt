package furhatos.app.complimentbot.flow.main

import furhatos.app.complimentbot.delayWhenUsersAreGone
import furhatos.app.complimentbot.flow.InteractionParent
import furhatos.app.complimentbot.flow.LeaderGoneForAWhile
import furhatos.app.complimentbot.flow.LeaderGoneForAWhileInstant
import furhatos.app.complimentbot.utils.*
import furhatos.event.Event
import furhatos.flow.kotlin.*
import furhatos.nlu.common.Greeting
import furhatos.records.User
import java.util.*
import kotlin.concurrent.schedule

var userQueue: LinkedList<User> = LinkedList()
class UserEnteredEvent(val user: User, val zone: Zone): Event()
class UserLeftEvent(val user: User): Event()

val attentionState: State = state(InteractionParent) {

    include(attentionGeneralIntents)

    onEntry { reentry() }
    onReentry {
        when (users.current.zone) {
            Zone.ZONE1 -> goto(complimentNextGroup(users.current))
            Zone.ZONE2 -> {
                if (isAttendingFurhatAvg(users.current) && !users.current.hasBeenGreeted) {
                    users.current.isBeingEngaged = true
                    greetUser(fromAfar = true)
                }
                furhat.listen()
            }
            else -> furhat.attendC(users.current)
        }
    }

    onResponse<Greeting> {
        if (users.current.zone == Zone.ZONE1) {
            goto(complimentNextGroup(users.current))
        }
        // Rest happens only for far away users
        else {
            if (!users.current.hasBeenGreeted) {
                greetUser()
                furhat.say("Can you maybe step closer?")
            } else {
                positiveSecondGreeting()
            }
            users.current.isBeingEngaged = false
            handleNext()
        }
    }
    onResponse {
        if (users.current.zone.isCloser(Zone.ZONE3)) {
            //goto interaction here seems risky
            furhat.say {
                random {
                    +"Hello ?"
                    +"You can come closer if you want"
                }
            }
        }
        handleNext()
    }
    onNoResponse {
        users.current.isBeingEngaged = false
        handleNext()
    }

    onUserEnter(instant = true) {
        if (it != users.current && users.current.isBeingEngaged) {
            // Don't interrupt if interacting with current user, but keep in mind that the user has to be engaged.
            // If a user enter the zone 1 he becomes the next target
            if (it.zone == Zone.ZONE1) {
                userQueue.push(it)
            } else {
                userQueue.add(it)
            }
        } else {
            // We have to separate instant and non-instant behavior here
            send(UserEnteredEvent(it, it.zone))
        }}
    onEvent<UserEnteredEvent> {
        if (it.user == users.current) {
            if (it.zone == Zone.ZONE1) {
                goto(complimentNextGroup(users.current))
            } else if (it.zone == Zone.ZONE2) {
                reentry() //Entering from zone3
            }
        } else if (!users.current.isBeingEngaged) {
            furhat.attendC(it.user)
            if (it.zone == Zone.ZONE1) {
                goto(complimentNextGroup(users.current))
            } else { //Entering zone 2 or 3
                reentry()
            }
        }
    }

    onUserLeave(instant = true) {
        // Only care about the main user in the attention state
        if (it.isBeingEngaged && it == users.current) {
            // If the user is being engaged we don't want to interrupt a listen
            Timer().schedule(delay = delayWhenUsersAreGone) {
                send(LeaderGoneForAWhileInstant(it))
            }
        } else if (it == users.current) {
            send(UserLeftEvent(it))
        }}
    onEvent<UserLeftEvent> {
        if (users.hasAny() && it.user == users.current) {
            furhat.attendC(users.other)
        } else {
            furhat.attendNobodyC()
            goto(ActiveIdle)
        }
    }
    onEvent<LeaderGoneForAWhileInstant>(instant = true) {
        if (!users.list.contains(it.user)) {
            raise(LeaderGoneForAWhile(it.user))
        }}
    onEvent<LeaderGoneForAWhile> {
        it.user.isBeingEngaged = false
        handleNext()
    }
}

/**
 * Flow function to handle users entering zones while furhat is trying to get zone 2 users' attention
 */
fun FlowControlRunner.handleNext() {
    try {
        val next = userQueue.remove()
        if (next.zone <= Zone.ZONE2) { // Treat only if the user is active
            furhat.attendC(next)
            reentry()
        }
    } catch (_: NoSuchElementException) {
        // If no next user we either continue to attend the current one or go back to Idle
        if (users.list.none { it.zone >= Zone.ZONE3 }) {
            furhat.attendNobodyC()
            goto(ActiveIdle)
        } else {
            furhat.listen()
        }
    }
}