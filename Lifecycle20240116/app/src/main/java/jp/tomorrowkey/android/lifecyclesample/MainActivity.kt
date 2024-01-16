package jp.tomorrowkey.android.lifecyclesample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.currentStateAsState
import jp.tomorrowkey.android.lifecyclesample.ui.theme.LifecycleSampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Old way
            ObserveLifecycleEvent { event ->
                Log.d("ObserveLifecycleEvent", "event: $event")
            }

            // New way
            val state by LocalLifecycleOwner.current.lifecycle.currentStateAsState()
            Log.d("currentStateAsState", "event: $state")

            LifecycleEventEffect(event = Lifecycle.Event.ON_CREATE) {
                Log.d("LifecycleEventEffect", "event: onCreate")
            }
            LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
                Log.d("LifecycleEventEffect", "event: onResume")
            }

            LifecycleResumeEffect(Unit) {
                Log.d("LifecycleResumeEffect", "event: onResume")

                onPauseOrDispose {
                    Log.d("LifecycleResumeEffect", "event: onPauseOrDispose")
                }
            }
            LifecycleStartEffect(Unit) {
                Log.d("LifecycleResumeEffect", "onStart")

                onStopOrDispose {
                    Log.d("LifecycleResumeEffect", "event: onStopOrDispose")
                }
            }

            LifecycleSampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun ObserveLifecycleEvent(onEvent: (Lifecycle.Event) -> Unit = {}) {
    val currentLifecycleEvent by rememberUpdatedState(onEvent)
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                currentLifecycleEvent(event)
            }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun Greeting(
    name: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "Hello $name!",
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LifecycleSampleTheme {
        Greeting("Android")
    }
}
