package com.example.in2000_team11weatherapp.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.in2000_team11weatherapp.model.state.TextUiState
import java.util.*

class TextToSpeechViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = mutableStateOf(TextUiState())
    val state: State<TextUiState> = _state
    private var textToSpeech: TextToSpeech? = null

    init {
        textToSpeech = TextToSpeech(
            getApplication<Application>().applicationContext
        ) {
            if (it == TextToSpeech.SUCCESS) {
                textToSpeech?.let { txtToSpeech ->
                    txtToSpeech.language = Locale.US
                    txtToSpeech.setSpeechRate(1.0f)
                }
            }
        }
    }

    fun onTextFieldValueChange(text: String) {
        _state.value = state.value.copy(
            text = text
        )
    }

    fun textToSpeech() {
        _state.value = state.value.copy(
            isButtonEnabled = false
        )
        textToSpeech?.stop()
        textToSpeech?.speak(
            _state.value.text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            null
        )
        _state.value = state.value.copy(
            isButtonEnabled = true
        )
    }

    override fun onCleared() {
        textToSpeech?.shutdown()
        super.onCleared()
    }
}