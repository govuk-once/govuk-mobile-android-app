package uk.gov.govuk.sar.data

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uk.gov.govuk.data.user.model.ConsentStatus
import uk.gov.govuk.data.user.model.Notifications
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
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun readUserData(): User {
        var user = User(
            Notifications(
                consentStatus = ConsentStatus.UNKNOWN,
                pushId = "0000"
            )
        )

        try {
            val file = File(context.filesDir, FILENAME)

            if (file.exists()) {
                val jsonString = file.readText()
                user = Gson().fromJson(jsonString, User::class.java)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return user
    }
}
