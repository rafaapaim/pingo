package com.pingo.alertacrypto.presentation.alerts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pingo.alertacrypto.domain.alert.Alert
import com.pingo.alertacrypto.domain.alert.AlertDirection
import com.pingo.alertacrypto.domain.alert.AlertUseCases
import com.pingo.alertacrypto.domain.alert.RepeatMode
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AlertListUiState(
    val alerts: List<Alert> = emptyList(),
    val errorMessage: String? = null,
)

@HiltViewModel
class AlertListViewModel @Inject constructor(
    private val useCases: AlertUseCases,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(AlertListUiState())
    val uiState = mutableUiState.asStateFlow()

    init {
        viewModelScope.launch {
            useCases.observeAlerts().collect { alerts ->
                mutableUiState.value = mutableUiState.value.copy(alerts = alerts)
            }
        }
    }

    fun save(
        existing: Alert?,
        symbol: String,
        targetPriceBRL: BigDecimal,
        direction: AlertDirection,
        repeatMode: RepeatMode,
    ) = perform("save") {
        if (existing == null) {
            useCases.create(symbol, targetPriceBRL, direction, repeatMode)
        } else {
            useCases.update(
                existing.copy(
                    symbol = symbol,
                    targetPriceBRL = targetPriceBRL,
                    direction = direction,
                    repeatMode = repeatMode,
                    updatedAt = Instant.now(),
                ),
            )
        }
    }

    fun setEnabled(id: UUID, enabled: Boolean) = perform("toggle") {
        useCases.setEnabled(id, enabled)
    }

    fun delete(id: UUID) = perform("delete") {
        useCases.delete(id)
    }

    private fun perform(operation: String, action: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                action()
                mutableUiState.value = mutableUiState.value.copy(errorMessage = null)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.e("AlertListViewModel", "Failed to $operation alert", error)
                mutableUiState.value = mutableUiState.value.copy(
                    errorMessage = error.localizedMessage ?: "Não foi possível concluir a operação.",
                )
            }
        }
    }
}
