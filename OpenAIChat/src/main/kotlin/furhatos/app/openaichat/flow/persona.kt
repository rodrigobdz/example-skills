package furhatos.app.openaichat.flow

import furhatos.app.openaichat.OpenAIChatbot
import furhatos.flow.kotlin.FlowControlRunner
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.voice.AcapelaVoice
import furhatos.flow.kotlin.voice.PollyNeuralVoice
import furhatos.flow.kotlin.voice.Voice
import furhatos.nlu.SimpleIntent

class Persona(
    val name: String,
    val otherNames: List<String> = listOf(),
    val intro: String = "",
    val desc: String,
    val face: List<String>,
    val mask: String = "adult",
    val voice: List<Voice>
) {
    val fullDesc = "$name, the $desc"

    val intent = SimpleIntent((listOf(name, desc, fullDesc) + otherNames))

    /** The prompt for the openAI language model **/
    val chatbot =
        OpenAIChatbot("The following is a conversation between $name, the $desc, and a Person", "Person", name)
}

fun FlowControlRunner.activate(persona: Persona) {
    for (voice in persona.voice) {
        if (voice.isAvailable) {
            furhat.voice = voice
            break
        }
    }

    for (face in persona.face) {
        if (furhat.faces[persona.mask]?.contains(face)!!) {
            furhat.character = face
            break
        }
    }
}

val hostPersona = Persona(
    name = "Host",
    desc = "host",
    face = listOf("Alex", "default"),
    voice = listOf(PollyNeuralVoice("Matthew"))
)

val personas = listOf(
    Persona(
        name = "Marvin",
        desc = "depressed robot",
        face = listOf("Titan"),
        voice = listOf(AcapelaVoice("WillSad"), PollyNeuralVoice("Kimberly"))
    ),
    Persona(
        name = "Emma",
        desc = "personal trainer",
        intro = "How do you think I could help you?",
        face = listOf("Isabel"),
        voice = listOf(PollyNeuralVoice("Olivia"))
    ),
    Persona(
        name = "Jerry Seinfeld",
        desc = "famous comedian",
        otherNames = listOf("Seinfeld"),
        intro = "You know, crankiness is at the essence of all comedy.",
        face = listOf("Marty"),
        voice = listOf(AcapelaVoice("WillFromAfar"), PollyNeuralVoice("Joey"))
    ),
    Persona(
        name = "Albert Einstein",
        otherNames = listOf("Einstein"),
        desc = "famous scientist",
        intro = "What can I help you with?",
        face = listOf("Samuel"),
        voice = listOf(AcapelaVoice("WillOldMan"), PollyNeuralVoice("Brian"))
    )
)