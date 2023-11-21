package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.example.myapplication.shared.BaseScreenComponent

@Composable
internal fun <State : Any> BaseScreenContent(
    component: BaseScreenComponent<State>,
    content: @Composable (state: State) -> Unit,
) {
    val state by component.state.subscribeAsState()

    content(state)
}
