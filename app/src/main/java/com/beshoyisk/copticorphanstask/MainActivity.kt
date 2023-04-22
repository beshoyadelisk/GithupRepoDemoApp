package com.beshoyisk.copticorphanstask

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.beshoyisk.copticorphanstask.data.remote.auth.SignInResult
import com.beshoyisk.copticorphanstask.domain.model.UserData
import com.beshoyisk.copticorphanstask.navigation.Screen
import com.beshoyisk.copticorphanstask.presentation.home.HomeScreen
import com.beshoyisk.copticorphanstask.presentation.home.RepoViewModel
import com.beshoyisk.copticorphanstask.presentation.log_in.LoginScreen
import com.beshoyisk.copticorphanstask.presentation.log_in.LoginViewModel
import com.beshoyisk.copticorphanstask.presentation.sign_up.SignUpScreen
import com.beshoyisk.copticorphanstask.presentation.sign_up.SignUpViewModel
import com.beshoyisk.copticorphanstask.ui.theme.CopticOrphansTaskTheme
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CopticOrphansTaskTheme {

                // A surface container using the 'background' color from the theme
                Scaffold {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it),
                        color = MaterialTheme.colors.background
                    ) {
                        val navController = rememberNavController()
                        val loginViewModel: LoginViewModel = hiltViewModel()
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Login.route
                        ) {
                            composable(Screen.Login.route) {
                                val state by loginViewModel.state.collectAsStateWithLifecycle()
                                LaunchedEffect(key1 = Unit) {
                                    navigateToHome(
                                        navController = navController,
                                        userData = loginViewModel.getSignedInUser()
                                    )
                                }
                                val callbackManager = remember { CallbackManager.Factory.create() }
                                val loginManager = LoginManager.getInstance()
                                val fbAuthLauncher = rememberLauncherForActivityResult(
                                    contract =
                                    loginManager.createLogInActivityResultContract(
                                        callbackManager,
                                        null
                                    )
                                ) {}
                                DisposableEffect(key1 = Unit) {
                                    loginManager.registerCallback(callbackManager,
                                        object : FacebookCallback<LoginResult> {
                                            override fun onCancel() {
                                                val signInResult = SignInResult(
                                                    data = null,
                                                    errorMessage = "Cancelled"
                                                )
                                                loginViewModel.onSignInResult(signInResult)
                                            }

                                            override fun onError(error: FacebookException) {
                                                val signInResult = SignInResult(
                                                    data = null,
                                                    errorMessage = error.message
                                                        ?: "Failed to login"
                                                )
                                                loginViewModel.onSignInResult(signInResult)
                                            }

                                            override fun onSuccess(result: LoginResult) {
                                                loginViewModel.signInByFacebook(result.accessToken)
                                            }

                                        })
                                    onDispose {
                                        loginManager.unregisterCallback(callbackManager)
                                    }
                                }
                                val launcher = rememberLauncherForActivityResult(
                                    contract = ActivityResultContracts.StartIntentSenderForResult(),
                                    onResult = { result ->
                                        if (result.resultCode == RESULT_OK) {
                                            loginViewModel.signInByIntent(
                                                result.data
                                                    ?: return@rememberLauncherForActivityResult
                                            )
                                        }
                                    }
                                )
                                LaunchedEffect(key1 = state.isSignInSuccessful) {
                                    if (state.isSignInSuccessful) {
                                        Toast.makeText(
                                            applicationContext,
                                            "Sign in success",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navigateToHome(
                                            navController = navController,
                                            userData = loginViewModel.getSignedInUser()
                                        )
                                        loginViewModel.resetState()
                                    }
                                }
                                if (state.isSignInSuccessful.not())
                                    LoginScreen(
                                        state = state,
                                        onEmailChanged = loginViewModel::updateEmail,
                                        onPasswordChanged = loginViewModel::updatePassword,
                                        onEmailPwSignInClicked = loginViewModel::signByEmailAndPw,
                                        onSingUpCLicked = {
                                            navController.navigate(Screen.SignUp.route)
                                            loginViewModel.resetState()
                                        },
                                        onFacebookSignInCLicked = {
                                            fbAuthLauncher.launch(listOf("email"))
                                        },
                                        onGoogleSignInClicked = {
                                            lifecycleScope.launch {
                                                val signInIntentSender =
                                                    loginViewModel.googleSignIn()
                                                launcher.launch(
                                                    IntentSenderRequest.Builder(
                                                        signInIntentSender ?: return@launch
                                                    ).build()
                                                )
                                            }
                                        }
                                    )
                            }
                            composable(Screen.SignUp.route) {
                                val signUpViewModel: SignUpViewModel = hiltViewModel()
                                val state by signUpViewModel.state.collectAsStateWithLifecycle()
                                LaunchedEffect(key1 = state.isSignUpSuccessful) {
                                    if (state.isSignUpSuccessful) {
                                        Toast.makeText(
                                            applicationContext,
                                            "Sign up success",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navigateToHome(
                                            navController = navController,
                                            userData = loginViewModel.getSignedInUser()
                                        )
                                        signUpViewModel.resetState()
                                        loginViewModel.resetState()
                                    }
                                }
                                SignUpScreen(
                                    state = state,
                                    onNameChanged = signUpViewModel::updateName,
                                    onEmailChanged = signUpViewModel::updateEmail,
                                    onPasswordChanged = signUpViewModel::updatePassword,
                                    onSingUpCLicked = signUpViewModel::signUpByEmailAndPw,
                                )
                            }
                            composable(Screen.Home.route) { backStackEntry ->
                                val arguments = backStackEntry.arguments
                                val username = arguments?.getString("username")
                                val profilePicture = arguments?.getString("profilePicture")
                                requireNotNull(username) { "Username not provided!" }
                                val repoViewModel: RepoViewModel = hiltViewModel()
                                val repos = repoViewModel.githubRepList.collectAsLazyPagingItems()
                                val searchQuery by repoViewModel.searchQuery.collectAsStateWithLifecycle()
                                HomeScreen(
                                    username = username,
                                    profilePicture = profilePicture ?: "",
                                    searchQuery = searchQuery,
                                    onSearchQueryChange = repoViewModel::searchQueryChanged,
                                    onSearchClicked = repoViewModel::search,
                                    loadList = repoViewModel::fetchList,
                                    repos = repos
                                ) {
                                    lifecycleScope.launch {
                                        loginViewModel.signOut()
                                        Toast.makeText(
                                            applicationContext,
                                            "Signed out",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.navigate(Screen.Login.route) {
                                            popUpTo(navController.graph.id) {
                                                inclusive = true
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    private fun navigateToHome(navController: NavController, userData: UserData?) {
        if (userData == null) return
        navController.navigate(
            Screen.Home.createRoute(
                username = userData.username ?: userData.email ?: "",
                profilePicture = userData.profilePictureUrl
            )
        ) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
        }
    }
}

