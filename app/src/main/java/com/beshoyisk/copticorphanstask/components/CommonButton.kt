package com.beshoyisk.copticorphanstask.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.beshoyisk.copticorphanstask.ui.theme.CopticOrphansTaskTheme

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun CommonButtonPrev() {
CopticOrphansTaskTheme {
    CommonButton()
}
}
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun CommonButton(
    modifier: Modifier = Modifier,
    text: String = "button",
    icon: Painter? = null,
    iconOnEnd: Boolean = true,
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    cornerRadius: Dp = 10.dp
) {
    Button(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        shape = RoundedCornerShape(cornerRadius)
    ) {
        if (iconOnEnd.not() && icon != null) {
            Icon(
                painter = icon,
                contentDescription = "button icon",
                modifier = Modifier.padding(end = 16.dp)
            )
        }

        Text(text = text)

        if (iconOnEnd && icon != null)
            Icon(
                painter = icon,
                contentDescription = "button icon",
                modifier = Modifier.padding(start = 16.dp)
            )

    }
}

@Composable
fun CommonTextButton(
    modifier: Modifier = Modifier,
    text: String = "button",
    icon: Painter? = null,
    onClick: () -> Unit = {},
    enabled: Boolean = true
) {
    TextButton(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
        enabled = enabled,
        onClick = onClick
    ) {

        Text(text = text)

        if (icon != null)
            Icon(
                painter = icon,
                contentDescription = "button icon",
                modifier = Modifier.padding(start = 16.dp)
            )

    }
}