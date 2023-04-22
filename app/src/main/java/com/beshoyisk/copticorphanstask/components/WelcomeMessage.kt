package com.beshoyisk.copticorphanstask.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar
import com.beshoyisk.copticorphanstask.R


@Composable
fun WelcomeMessage(modifier: Modifier = Modifier, username: String = "") {
    val c = Calendar.getInstance()
    val message = when (c.get(Calendar.HOUR_OF_DAY)) {
        in 0..11 -> stringResource(id = R.string.good_morning)
        in 12..15 -> stringResource(id = R.string.good_afternoon)
        in 16..20 -> stringResource(id = R.string.good_evening)
        in 21..23 -> stringResource(id = R.string.good_night)
        else -> stringResource(id = R.string.hello)
    }.plus("!")
    Column(
        modifier = modifier.padding(vertical = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(fontSize = 18.sp)
                ) {
                    append(message + "\n")
                }
                withStyle(
                    style = SpanStyle(
                        color = Color.Gray,
                        fontWeight = FontWeight.Thin,
                        fontSize = 16.sp
                    )
                ) {
                    append(username)
                }
            },
            style = MaterialTheme.typography.subtitle2,
            fontFamily = FontFamily.Serif,
        )
    }


}