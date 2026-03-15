package com.furkanyildirim.learningcompose.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.furkanyildirim.learningcompose.R
import com.furkanyildirim.learningcompose.ui.theme.CyberpunkBackground
import com.furkanyildirim.learningcompose.ui.theme.HotMagenta
import com.furkanyildirim.learningcompose.ui.theme.LearningComposeTheme
import com.furkanyildirim.learningcompose.ui.theme.NeonBarContainer
import com.furkanyildirim.learningcompose.ui.theme.NeonBarPosition
import com.furkanyildirim.learningcompose.ui.theme.NeonCyan

@Composable
fun AuthChoiceScreen(
    onEmailSignIn: (String, String) -> Unit,
    onEmailSignUp: (String, String) -> Unit,
    onGoogleSignIn: () -> Unit,
    onContinueAsGuest: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AuthChoiceContent(
        email = email,
        password = password,
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onEmailSignIn = { onEmailSignIn(email.trim(), password) },
        onEmailSignUp = { onEmailSignUp(email.trim(), password) },
        onGoogleSignIn = onGoogleSignIn,
        onContinueAsGuest = onContinueAsGuest,
        modifier = modifier
    )
}

@Composable
fun AuthChoiceContent(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onEmailSignIn: () -> Unit,
    onEmailSignUp: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onContinueAsGuest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(24.dp)
    val textFieldShape = RoundedCornerShape(14.dp)

    CyberpunkBackground(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            NeonBarContainer(
                position = NeonBarPosition.Top,
                modifier = Modifier.height(10.dp)
            ) {}

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(cardShape)
                        .border(
                            width = 1.2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    NeonCyan.copy(alpha = 0.85f),
                                    HotMagenta.copy(alpha = 0.85f)
                                )
                            ),
                            shape = cardShape
                        ),
                    shape = cardShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.86f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.auth_choice_title),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = stringResource(R.string.auth_choice_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp, bottom = 18.dp)
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = onEmailChange,
                            label = { Text(stringResource(R.string.auth_email_label)) },
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.PersonOutline,
                                    contentDescription = null
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            shape = textFieldShape,
                            colors = cyberpunkTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = onPasswordChange,
                            label = { Text(stringResource(R.string.auth_password_label)) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            shape = textFieldShape,
                            colors = cyberpunkTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        AuthActionButton(
                            text = stringResource(R.string.auth_email_sign_in),
                            onClick = onEmailSignIn,
                            containerColor = NeonCyan,
                            contentColor = Color(0xFF021017),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        AuthActionButton(
                            text = stringResource(R.string.auth_email_sign_up),
                            onClick = onEmailSignUp,
                            containerColor = HotMagenta,
                            contentColor = Color(0xFF1C0017),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        AuthOutlinedButton(
                            text = stringResource(R.string.auth_google_gmail_sign_in),
                            icon = Icons.Default.Email,
                            onClick = onGoogleSignIn,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = onContinueAsGuest,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Text(stringResource(R.string.auth_continue_guest))
                        }
                    }
                }
            }

            NeonBarContainer(
                position = NeonBarPosition.Bottom,
                modifier = Modifier.height(10.dp)
            ) {}
        }
    }
}

@Composable
private fun cyberpunkTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = NeonCyan,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f),
    focusedLabelColor = NeonCyan,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    cursorColor = HotMagenta,
    focusedLeadingIconColor = NeonCyan,
    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.35f)
)

@Composable
private fun AuthActionButton(
    text: String,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
    ) {
        Text(text = text)
    }
}

@Composable
private fun AuthOutlinedButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .border(
                BorderStroke(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        listOf(NeonCyan.copy(alpha = 0.9f), HotMagenta.copy(alpha = 0.9f))
                    )
                ),
                shape = RoundedCornerShape(14.dp)
            ),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.padding(end = 8.dp),
            tint = NeonCyan
        )
        Text(text = text)
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthChoiceScreenPreview() {
    LearningComposeTheme {
        AuthChoiceContent(
            email = "neo@matrix.dev",
            password = "123456",
            onEmailChange = {},
            onPasswordChange = {},
            onEmailSignIn = {},
            onEmailSignUp = {},
            onGoogleSignIn = {},
            onContinueAsGuest = {}
        )
    }
}
