package uk.gov.govuk.data.notificationcentre.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Notification(
    @SerializedName("NotificationID")
    val id: String,
    @SerializedName("NotificationTitle")
    val title: String,
    @SerializedName("NotificationBody")
    val body: String,
    @SerializedName("Status")
    val status: String,
    @SerializedName("DispatchedDateTime")
    val rawDate: String,
    @SerializedName("MessageTitle")
    val messageTitle: String? = null,
    @SerializedName("MessageBody")
    val messageBody: String? = null) {

    val date: LocalDateTime
        get() = LocalDateTime.parse(rawDate, DateTimeFormatter.ISO_DATE_TIME)

    val isUnread: Boolean
        get() = status != "READ"

    companion object {
        val mockNotifications: List<Notification>
            get() {
                val referenceDate = LocalDateTime
                    .of(2026, 3,5,13,2,49)

                return listOf(
                    Notification(
                        "1",
                        "Test 1 with a really really really long title that will surely be chopped off if we add enough filler text to the end so it goes to more than two lines",
                        "Body",
                        "DELIVERED",
                        referenceDate.format(DateTimeFormatter.ISO_DATE_TIME)
                    ),
                    Notification(
                        "2",
                        "Test 2",
                        "Body 2 with a really really really really really large amount of text that will absolutely definitely make the text longer than it should be and so will get chopped off and ellipsized",
                        "DELIVERED",
                        referenceDate.minusDays(1).format(DateTimeFormatter.ISO_DATE_TIME)
                    ),
                    Notification("3", "Test 3", "Body 3", "READ",referenceDate.minusDays(2).format(DateTimeFormatter.ISO_DATE_TIME)
                    ),
                    Notification("4", "Test 4 with a seriously massive amount of text in the title that will span multiple", "Body 4 with a stupendous amount of body text lorem ipsum dolor sit amet https://google.com Kindling the energy hidden in matter Tunguska event muse about Cambrian explosion network of wormholes realm of the galaxies. At the edge of forever extraordinary claims require extraordinary evidence gathered by gravity two ghostly white figures in coveralls and helmets are softly dancing emerged into consciousness a still more glorious dawn awaits. Rings of Uranus something incredible is waiting to be known a mote of dust suspended in a sunbeam descended from astronomers concept of the number one the carbon in our apple pies and billions upon billions upon billions upon billions upon billions upon billions upon billions.", "DELIVERED", referenceDate.minusDays(3).format(DateTimeFormatter.ISO_DATE_TIME)
                    ),
                    Notification("5", "Test 5 with an alternate title", "Body 3", "READ",referenceDate.minusDays(4).format(DateTimeFormatter.ISO_DATE_TIME), messageTitle = "Alternate message title"
                    ),
                    Notification("6", "Test 6 with an alternate body", "Body 7", "READ",referenceDate.minusDays(5).format(DateTimeFormatter.ISO_DATE_TIME), messageBody = "Alternate message body https://google.com"
                    ),
                    Notification("7", "Test 7 with an alternate title and body", "Body 7", "READ",referenceDate.minusDays(6).format(DateTimeFormatter.ISO_DATE_TIME), messageTitle = "Alternate message title 2", messageBody = "Alternate message body 2 Purr as loud as possible, be the most annoying cat that you can, and, knock everything off the table grass smells good and proudly present butt to human but attack curtains, or dream about hunting birds. Going to catch the red dot today going to catch the red dot today sleep on dog bed, force dog to sleep on floor. Poop on couch. Pet me pet me pet me pet me, bite, scratch, why are you petting me miaow then turn around and show you my bum so walk on a keyboard and kitty kitty pussy cat doll munch on tasty moths. Furball roll roll roll meow all night, get video posted to internet for chasing red dot yet one of these days i'm going to get that red dot, just you wait and see ooh, are those your \$250 dollar sandals? lemme use that as my litter box. Eat and than sleep on your face the dog smells bad rub my belly hiss eat the fat cats food. Make plans to dominate world and then take a nap bury the poop bury it deep or pretend you want to go out but then don't. Steal mom's crouton while she is in the bathroom destroy dog. Caticus cuteicus annoy the old grumpy cat, start a fight and then retreat to wash when i lose. Run outside as soon as door open. Check cat door for ambush 10 times before coming in meow and walk away and i like cats because they are fat and fluffy and behind the couch, and swat turds around the house for have a lot of grump in yourself because you can't forget to be grumpy and not be like king grumpy cat where is my slave? I'm getting hungry. There's a forty year old lady there let us feast eats owners hair then claws head, give me some of your food give me some of your food give me some of your food meh, i don't want it. I shredded your linens for you."
                    ),
                )
            }
    }
}