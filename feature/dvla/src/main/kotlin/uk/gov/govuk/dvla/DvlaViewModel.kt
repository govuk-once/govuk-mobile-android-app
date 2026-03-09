package uk.gov.govuk.dvla

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uk.gov.govuk.dvla.data.DvlaRepo
import javax.inject.Inject

@HiltViewModel
internal class DvlaViewModel @Inject constructor(
    private val dvlaRepo: DvlaRepo
) : ViewModel() {

    fun linkAccount(id: String) {
        viewModelScope.launch {

//            val tempId = java.util.UUID.randomUUID().toString()

            dvlaRepo.linkAccount(id)
            delay(3000)

        }
    }
}