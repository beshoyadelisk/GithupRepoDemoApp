package com.beshoyisk.copticorphanstask.presentation.log_in

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.beshoyisk.copticorphanstask.R
import com.beshoyisk.copticorphanstask.components.CommonButton
import com.beshoyisk.copticorphanstask.components.CommonTextField
import com.beshoyisk.copticorphanstask.ui.theme.CopticOrphansPrimary

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
    state: SignInState,
    onPasswordChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onSingUpCLicked: () -> Unit,
    onEmailPwSignInClicked: () -> Unit,
    onGoogleSignInClicked: () -> Unit
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val isMailError by remember { mutableStateOf(state.isEmailError) }
    val isPasswordError by remember { mutableStateOf(state.isPasswordError) }

    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(0.4f)
                .background(color = CopticOrphansPrimary)
                .padding(horizontal = 50.dp),
            painter = painterResource(id = R.drawable.coptic_orphans_logo),
            contentDescription = "logo",
            alignment = Alignment.Center,
            contentScale = ContentScale.FillWidth
        )

        CommonTextField(
            modifier = Modifier.padding(top = 16.dp),
            value = state.email,
            placeholder = stringResource(id = R.string.email),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Email,
                    contentDescription = "Email"
                )
            },
            keyboardType = KeyboardType.Email,
            onValueChange = onEmailChanged,
            isError = isMailError,
            errorMessage = stringResource(id = R.string.mail_cant_be_empty)
        )

        CommonTextField(modifier = Modifier.padding(top = 8.dp),
            value = state.password,
            placeholder = stringResource(id = R.string.password),
            keyboardType = KeyboardType.Password,
            imeActions = ImeAction.Done,
            onValueChange = onPasswordChanged,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = "Email"
                )
            },
            isError = isPasswordError,
            errorMessage = stringResource(id = R.string.password_cant_be_empty),
            onAction = KeyboardActions {
                keyboardController?.hide()
            })

        if (state.isLoading) {
            CircularProgressIndicator()
        }
        AnimatedVisibility(visible = state.isLoading.not()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CommonButton(
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 20.dp)
                        .fillMaxWidth(0.5f)
                    ,
                    text = stringResource(id = R.string.sign_in),
                    cornerRadius = 15.dp,
                    onClick = {
                        keyboardController?.hide()
                        onEmailPwSignInClicked()
                    },
                    enabled = state.password.isNotEmpty() && state.email.trim().isNotEmpty()
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = stringResource(id = R.string.or_connect_using),
                    style = MaterialTheme.typography.caption,
                    color = Color.Gray
                )
                Button(
                    onClick = onGoogleSignInClicked,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Red,
                        contentColor = Color.White
                    )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "",
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.google),
                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 16.dp)
                    )
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Don't have account?",
                            style = MaterialTheme.typography.body1,
                            color = Color.Gray
                        )
                        Text(
                            text = "Sign Up",
                            style = MaterialTheme.typography.body1,
                            color = Color.Blue,
                            modifier = Modifier.clickable { onSingUpCLicked() }
                        )
                    }
                }
            }

        }

    }

}