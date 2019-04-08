package uk.co.victoriajanedavis.tindercardstack.data

class User(
    val id: Int,
    val name: String,
    val city: String
)

fun generateDummyUsers(): List<User> {
    return listOf(
        User(id = 1, name = "Rick", city = "Toronto"),
        User(id = 2, name = "Mark", city = "Montreal"),
        User(id = 3, name = "Eric", city = "Ottawa"),
        User(id = 4, name = "Chris", city = "Vancouver"),
        User(id = 5, name = "Bob", city = "Edmonton"),
        User(id = 6, name = "Victor", city = "Calgary"),
        User(id = 7, name = "Richard", city = "Winnipeg"),
        User(id = 8, name = "Steve", city = "Halifax"),
        User(id = 9, name = "Michael", city = "St. John's"),
        User(id = 10, name = "Connor", city = "Hamilton"),
        User(id = 11, name = "Thomas", city = "Yellowknife")
    )
}