package com.coffeehub.pos.presentation.auth

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.brewpoint.pos.R
import com.coffeehub.pos.presentation.theme.CardSurface
import com.coffeehub.pos.presentation.theme.CreamWhite
import com.coffeehub.pos.presentation.theme.DarkRoast
import com.coffeehub.pos.presentation.theme.EspressoBrown
import com.coffeehub.pos.presentation.theme.LatteCaramel
import com.coffeehub.pos.presentation.theme.RichBrownSurface
import com.coffeehub.pos.presentation.theme.WarmGray

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.loggedInUser) {
        if (uiState.loggedInUser != null) onLoginSuccess()
    }

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkRoast, RichBrownSurface, CardSurface)))) {
        Box(modifier = Modifier.size(300.dp).offset(x = (-80).dp, y = (-80).dp).clip(CircleShape).background(EspressoBrown.copy(alpha = 0.15f)))
        Box(modifier = Modifier.size(200.dp).align(Alignment.BottomEnd).offset(x = 60.dp, y = 60.dp).clip(CircleShape).background(LatteCaramel.copy(alpha = 0.1f)))

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(modifier = Modifier.size(100.dp).clip(MaterialTheme.shapes.extraLarge).background(Brush.linearGradient(listOf(EspressoBrown, LatteCaramel))), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.LocalCafe, contentDescription = null, modifier = Modifier.size(60.dp), tint = CreamWhite)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "BrewPoint", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = CreamWhite)
            Text(text = stringResource(R.string.app_subtitle), style = MaterialTheme.typography.titleMedium, color = WarmGray)
            Spacer(modifier = Modifier.height(48.dp))

            Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(28.dp)) {
                    Text(text = stringResource(R.string.sign_in), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(text = stringResource(R.string.enter_credentials), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = uiState.username, onValueChange = viewModel::onUsernameChange,
                        label = { Text(stringResource(R.string.username)) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        singleLine = true, shape = MaterialTheme.shapes.medium
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = uiState.password, onValueChange = viewModel::onPasswordChange,
                        label = { Text(stringResource(R.string.password)) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = stringResource(R.string.toggle_password))
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(); viewModel.login() }),
                        singleLine = true, shape = MaterialTheme.shapes.medium
                    )

                    AnimatedVisibility(visible = uiState.error != null) {
                        uiState.error?.let { error ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.small)
                                .background(MaterialTheme.colorScheme.errorContainer).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(error, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { viewModel.login() }, modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = !uiState.isLoading, shape = MaterialTheme.shapes.medium) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Login, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.sign_in), style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = stringResource(R.string.demo_hint), style = MaterialTheme.typography.bodySmall, color = WarmGray)
        }
    }
}
