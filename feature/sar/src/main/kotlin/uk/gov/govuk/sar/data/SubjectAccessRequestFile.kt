package uk.gov.govuk.sar.data

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uk.gov.govuk.data.user.model.ConsentStatus
import uk.gov.govuk.data.user.model.Notifications
import uk.gov.govuk.data.user.model.User
import java.io.File

class SubjectAccessRequestFile(
    val context: Context
) {
    companion object {
        const val FILENAME = "subject-access-request.json"
    }

    suspend fun writeFile() {
        val gson = Gson()
        val user = User(Notifications(consentStatus = ConsentStatus.ACCEPTED, pushId = "12345")) // TODO: get this from getUserInfo()
        val jsonString: String = gson.toJson(user)

        try {
            val file = File(context.filesDir, FILENAME)
            file.writeText(jsonString)

            withContext(Dispatchers.Main) {
                println("Saved: ${file.absolutePath}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun readFile(): String {
        val gson = Gson()
        var fileContent = ""

        try {
            val file = File(context.filesDir, FILENAME)

            if (file.exists()) {
                val jsonString = file.readText()
                val user: User = gson.fromJson(jsonString, User::class.java)

                withContext(Dispatchers.Main) {
                    fileContent = "ConsentStatus: ${user.notifications.consentStatus} Push ID: ${user.notifications.pushId}"
                }
            } else {
                withContext(Dispatchers.Main) {
                    fileContent = "File does not exist yet!"
                }
            }
        } catch (e: Exception) {
            fileContent = e.message ?: e.toString()
        }

        return fileContent
    }
}
