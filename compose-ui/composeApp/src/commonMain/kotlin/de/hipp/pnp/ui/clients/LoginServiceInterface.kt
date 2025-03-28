package de.hipp.pnp.ui.clients

interface LoginServiceInterface {
    fun login(onSuccess: () -> Unit = {}, onError: (Error) -> Unit = {})
    fun logout(onSuccess: () -> Unit = {}, onError: (Error) -> Unit = {})
}