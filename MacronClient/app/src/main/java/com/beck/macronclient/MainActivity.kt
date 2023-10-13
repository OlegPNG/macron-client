package com.beck.macronclient

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.beck.macronclient.ui.screens.HomeScreen
import com.beck.macronclient.ui.screens.LoginScreen
import com.beck.macronclient.ui.screens.RegisterScreen
import com.beck.macronclient.ui.theme.MacronClientTheme
import com.beck.macronclient.viewmodel.MacronViewModel
import com.beck.macronclient.viewmodel.MacronViewModelImpl
import kotlinx.coroutines.flow.map
import okhttp3.WebSocketListener

private const val USER_CONFIG_NAME = "user_config"

val Context.dataStore by preferencesDataStore(
    name = USER_CONFIG_NAME
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val authKey = stringPreferencesKey("user_config")
        val foo = applicationContext.dataStore.data.map { preferences ->
            preferences[authKey]
        }
        setContent {
            MacronClientTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewModel = MacronViewModelImpl()
                    NavHost(navController, startDestination = "login") {
                        composable("register") {
                            RegisterScreen(
                                viewModel = viewModel,
                                onRegister = {
                                    viewModel.getReceivers()
                                    navController.navigate("home")
                                },
                                onNavToLogin = {
                                    viewModel.getReceivers()
                                    navController.navigate("login")
                                }
                            )
                        }
                        composable("login") {
                            LoginScreen(
                                viewModel = viewModel,
                                onLogin = {
                                    viewModel.loginWithPassword()
                                    //viewModel.getReceivers()
                                    navController.navigate("home")
                                }
                            )
                        }
                        composable("home") {
                            HomeScreen(
                                viewModel = viewModel,
                                onRetryClick = {
                                    navController.navigate("login")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MacronTopBar() {

}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MacronClientTheme {
        Greeting("Android")
    }
}