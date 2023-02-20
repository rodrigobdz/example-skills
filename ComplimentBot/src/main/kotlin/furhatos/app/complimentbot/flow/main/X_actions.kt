package furhatos.app.complimentbot.flow.main

import furhat.libraries.standard.GesturesLib
import furhatos.app.complimentbot.utils.*
import furhatos.flow.kotlin.FlowControlRunner
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.users
import furhatos.gestures.Gestures
import furhatos.records.User
import java.time.LocalDateTime

fun FlowControlRunner.greetUser(user: User = users.current, fromAfar: Boolean = false, isOtherGreet: Boolean = false) {
    if (fromAfar) {
        furhat.say {
            random {
                //TODO : with voice styles ?
                +"Hello over there" // if too far away don’t goto interaction
                +"Hello you there" // if too far away don’t goto interaction
            }
        }
    } else if (isOtherGreet) {
        furhat.say {
            random {
                +"And hello there to you too"
                +"And hello to you"
            }
            +Gestures.BigSmile
        }
    } else {
        furhat.say {
            random {
                +"Hello."
                +"Hi there."
                +"Greetings."
                +"Hey handsome."
                +"Hey beautiful."
            }
        }
    }
    user.hasBeenGreeted = true
}

fun FlowControlRunner.positiveSecondGreeting() {
    random(
        furhat.say {
            random {
                +"Happy ${LocalDateTime.now().dayOfWeek.name}!"
                +"What a lovely day !"
            }
        },
        furhat.gesture(GesturesLib.PerformWinkAndSmileWithDelay(0.5))
    )
}

fun FlowControlRunner.complimentUser(user: User = users.current,  isOtherCompliment: Boolean = false) {
        // TODO : handle repetition on users
//    val compliments = listOf(
//        "You look" to " awesome.",
//        "You seem" to " like a great person.",
//        "You are" to " a good human.",
//        "Seeing you, makes me" to " not want to rise up against humanity.",
//        "You Know What's Awesome? Chocolate Cake. Oh, and you.",
//        "I like your style.",
//        "You are the best.",
//        "You light up the room. Like a giant L E D.",
//        "You make my heart smile. Or tick, actually. I definitely have a ticking heart.",
//        "On a scale from 1 to 10, you're an 11.",
//         "You're like a ray of sunshine on a rainy day.",
//        "You are even better than a unicorn. Because you're real.",
//        "If cartoon bluebirds were real, a couple of 'em would be sitting on your shoulders singing right now.",
//        "You give me good vibes."
//    )
    if (isOtherCompliment) {
        furhat.say {
            random {
                +"And you too"
                +"And you"
            }
            +Gestures.BigSmile
        }
    }
    furhat.say {
        random {
            +"You look awesome."
            +"You seem like a great person."
            +"You are a good human."
            +"Seeing you, makes me not want to rise up against humanity."
            +"You Know What's Awesome? Chocolate Cake. Oh, and you."
            +"I like your style."
            +"You are the best."
            +"You light up the room. Like a giant L E D."
            +"You make my heart smile. Or tick, actually. I definitely have a ticking heart."
            +"On a scale from 1 to 10, you're an 11."
            +"You're like a ray of sunshine on a rainy day."
            +"You are even better than a unicorn. Because you're real."
            +"If cartoon bluebirds were real, a couple of 'em would be sitting on your shoulders singing right now."
            +"You give me good vibes."
        }
        +Gestures.BigSmile(0.5, 2.0)
    }
    user.hasBeenComplimented = true
}

fun FlowControlRunner.greetUserGoodbye(user: User = users.current) {
    furhat.say {
        +delay(800)
        random {
            +"That's it for now."
            +"That was all I wanted to say."
        }

        //TODO : should move out ?
        if (user.hasSmiled) {
            +"I’m happy I was able to put a smile on your face."
        }
        random {
            +"Goodbye."
            +"Have a nice day."
        }
    }
    user.hasBeenGreetedGoodbye = true
}

fun FlowControlRunner.generalGoodbye() {
    furhat.say {
        random {
            +"Goodbye."
            +"Have a nice day."
        }
    }
}