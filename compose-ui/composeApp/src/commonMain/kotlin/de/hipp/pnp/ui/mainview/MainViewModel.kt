package de.hipp.pnp.ui.mainview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel


class MainViewModel : ViewModel() {
    var userIsAuthenticated by mutableStateOf(false)
    var showContent by mutableStateOf(false)
}