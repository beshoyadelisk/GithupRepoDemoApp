package com.beshoyisk.copticorphanstask.presentation.sign_up


import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
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
fun SignUpScreen(
    state: SignUpState,
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSingUpCLicked: () -> Unit
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val isNameError by remember(state) { mutableStateOf(state.isNameError) }
    val isMailError by remember(state) { mutableStateOf(state.isEmailError) }
    val isPasswordError by remember(state) { mutableStateOf(state.isPasswordError) }

    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth(),
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
            value = state.name,
            placeholder = stringResource(id = R.string.name),
            keyboardType = KeyboardType.Text,
            imeActions = ImeAction.Next,
            onValueChange = onNameChanged,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Email"
                )
            },
            isError = isNameError,
            errorMessage = stringResource(id = R.string.required)
        )
        CommonTextField(
            modifier = Modifier.padding(top = 8.dp),
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
            errorMessage = stringResource(id = R.string.required)
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
            errorMessage = stringResource(id = R.string.password_invalid),
            onAction = KeyboardActions {
                keyboardController?.hide()
            })

        if (state.isLoading) {
            CircularProgressIndicator()
        }
        AnimatedVisibility(visible = state.isLoading.not()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CommonButton(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    text = stringResource(id = R.string.sign_up),
                    cornerRadius = 15.dp,
                    onClick = {
                        keyboardController?.hide()
                        onSingUpCLicked()
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

        }

    }

}