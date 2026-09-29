package com.microbond.app.examplecode
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {Column(){
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
    Divider()

    Button(onClick = {
        print("hello josh")
    }) {
        Text("press me")
    }
}
}