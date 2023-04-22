package com.beshoyisk.copticorphanstask.components


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.beshoyisk.copticorphanstask.R

@Composable
@Preview(showBackground = true)
fun CommonTextField(
    modifier: Modifier = Modifier,
    value: String = "",
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    imeActions: ImeAction = ImeAction.Next,
    onAction: KeyboardActions = KeyboardActions.Default,
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    isSingleLine: Boolean = true,
    errorMessage: String? = null,
    onValueChange: (String) -> Unit = {}
) {
    val isPasswordField = keyboardType == KeyboardType.Password

    var passwordVisibility: Boolean by remember {
        mutableStateOf(isPasswordField)
    }
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(text = placeholder) },
            isError = isError,
            maxLines = 1,
            singleLine = isSingleLine,
            leadingIcon = leadingIcon,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeActions),
            keyboardActions = onAction,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colors.primary,
                cursorColor = MaterialTheme.colors.secondary
            ),
            trailingIcon =
            if (isPasswordField && value.isNotEmpty()) {
                {
                    IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                        val painter = if (passwordVisibility) {
                            painterResource(R.drawable.ic_eye)
                        } else {
                            painterResource(R.drawable.ic_no_eye)
                        }
                        Icon(
                            painter = painter,
                            contentDescription = "Password Icon"
                        )
                    }
                }
            } else {
                trailingIcon
            },
            shape = RoundedCornerShape(15.dp),
            modifier = modifier,
            visualTransformation = if (passwordVisibility.not()) VisualTransformation.None else PasswordVisualTransformation()
        )
        ErrorText(
            text = if (isError && errorMessage != null)
                errorMessage
            else "",
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun ErrorText(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color = MaterialTheme.colors.error,
    textStyle: TextStyle = MaterialTheme.typography.body2
) {
    Text(
        text = text,
        color = textColor,
        style = textStyle,
        modifier = modifier,
        textAlign = TextAlign.Center
    )
}