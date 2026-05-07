package uk.gov.govuk.sar.data

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uk.gov.govuk.data.user.model.User
import java.io.File

class SubjectAccessRequestFile(
    val context: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) {
    companion object {
        const val FILENAME = "subject-access-request.json"
    }

    suspend fun writeUserData(user: User) {
        withContext(dispatcher) {
            try {
                val file = File(context.filesDir, FILENAME)
                file.writeText(Gson().toJson(user))
                println("Saved: ${file.absolutePath}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun readUserData(): String {
        var fileContent = ""

        try {
            val file = File(context.filesDir, FILENAME)

            if (file.exists()) {
                val jsonString = file.readText()
                val user: User = Gson().fromJson(jsonString, User::class.java)

                withContext(dispatcher) {
                    fileContent = "ConsentStatus: ${user.notifications.consentStatus} Push ID: ${user.notifications.pushId}"
                }
            } else {
                withContext(dispatcher) {
                    fileContent = "File does not exist yet!"
                }
            }
        } catch (e: Exception) {
            fileContent = e.message ?: e.toString()
        }

        return fileContent
    }
}
