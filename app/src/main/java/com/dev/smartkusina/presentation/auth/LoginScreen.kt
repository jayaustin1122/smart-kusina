package com.dev.smartkusina.presentation.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.smartkusina.R
import com.dev.smartkusina.composables.LoadingScreen
import com.dev.smartkusina.presentation.auth.state.AuthAction
import com.dev.smartkusina.presentation.auth.state.AuthState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val authAction by viewModel.authAction.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    val googleSignInClient = remember {
        GoogleSignIn.getClient(context, gso)
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                viewModel.signInWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign-In failed
            }
        }
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            onNavigateToHome()
        }
    }

    if (authState is AuthState.Loading) {
        LoadingScreen()
    }

    var isLoginMode by remember { mutableStateOf(true) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogType by remember { mutableStateOf(DialogType.SUCCESS) }
    var dialogMessage by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    LaunchedEffect(authAction) {
        authAction?.let { action ->
            when (action) {
                is AuthAction.LoginSuccess -> {
                    dialogType = DialogType.SUCCESS
                    dialogMessage = "Login successful! Welcome back."
                    showDialog = true
                }
                is AuthAction.RegisterSuccess -> {
                    dialogType = DialogType.SUCCESS
                    dialogMessage = "Account created successfully! Please login."
                    showDialog = true
                    isLoginMode = true
                    password = ""
                    confirmPassword = ""
                }
                is AuthAction.GoogleSignInSuccess -> {
                    dialogType = DialogType.SUCCESS
                    dialogMessage = "Google sign-in successful!"
                    showDialog = true
                }
                is AuthAction.Error -> {
                    dialogType = DialogType.ERROR
                    dialogMessage = action.message
                    showDialog = true
                }
                AuthAction.LogoutSuccess -> {}
            }
            viewModel.clearAuthAction()
        }
    }

    // Auth Dialog
    if (showDialog) {
        AuthDialog(
            dialogType = dialogType,
            message = dialogMessage,
            onDismiss = { showDialog = false }
        )
    }

    // Loading Dialog
    if (isLoading) {
        LoadingDialog(
            message = when {
                isLoginMode -> "Signing in..."
                else -> "Creating account..."
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE8F5E8),
                        Color(0xFFF0F8F0)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // App Logo
            Card(
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(50.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF28C20))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🍳", fontSize = 40.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Smart Kusina",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF28C20)
            )

            Text(
                text = "Your Smart Kitchen Companion",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF666666)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Mode Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(
                    onClick = { isLoginMode = true },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (isLoginMode) Color(0xFFF28C20) else Color.Gray
                    )
                ) {
                    Text(
                        text = "Sign In",
                        fontWeight = if (isLoginMode) FontWeight.Bold else FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.width(24.dp))

                TextButton(
                    onClick = { isLoginMode = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (!isLoginMode) Color(0xFFF28C20) else Color.Gray
                    )
                ) {
                    Text(
                        text = "Sign Up",
                        fontWeight = if (!isLoginMode) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    if (!isLoginMode) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Full Name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFF28C20),
                                focusedLabelColor = Color(0xFFF28C20)
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFF28C20),
                            focusedLabelColor = Color(0xFFF28C20)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(
                                onClick = { passwordVisible = !passwordVisible },
                                enabled = !isLoading
                            ) {
                                Image(
                                    painter = if (passwordVisible) painterResource(R.drawable.eye)
                                    else painterResource(R.drawable.hide),
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = if (isLoginMode) ImeAction.Done else ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (isLoginMode) {
                                    focusManager.clearFocus()
                                    viewModel.signInWithEmail(email, password)
                                } else {
                                    focusManager.moveFocus(FocusDirection.Down)
                                }
                            },
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFF28C20),
                            focusedLabelColor = Color(0xFFF28C20)
                        )
                    )

                    // Confirm Password field (only for registration)
                    if (!isLoginMode) {
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirm Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            trailingIcon = {
                                IconButton(
                                    onClick = { confirmPasswordVisible = !confirmPasswordVisible },
                                    enabled = !isLoading
                                ) {
                                    Image(
                                        painter = if (confirmPasswordVisible) painterResource(R.drawable.eye)
                                        else painterResource(R.drawable.hide),
                                        contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    if (password == confirmPassword) {
                                        viewModel.signUpWithEmail(email, password, name)
                                    }
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFF28C20),
                                focusedLabelColor = Color(0xFFF28C20)
                            ),
                            isError = confirmPassword.isNotEmpty() && password != confirmPassword
                        )

                        if (confirmPassword.isNotEmpty() && password != confirmPassword) {
                            Text(
                                text = "Passwords don't match",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Email/Password Auth Button
                    Button(
                        onClick = {
                            if (isLoginMode) {
                                viewModel.signInWithEmail(email, password)
                            } else {
                                if (password == confirmPassword) {
                                    viewModel.signUpWithEmail(email, password, name)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !isLoading && email.isNotEmpty() && password.isNotEmpty() &&
                                (isLoginMode || (name.isNotEmpty() && password == confirmPassword)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF28C20)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isLoginMode) "Sign In" else "Create Account",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

enum class DialogType {
    SUCCESS, ERROR
}

@Composable
fun AuthDialog(
    dialogType: DialogType,
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = if (dialogType == DialogType.SUCCESS)
                    Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null,
                tint = if (dialogType == DialogType.SUCCESS)
                    Color(0xFF4CAF50) else Color(0xFFE57373),
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = if (dialogType == DialogType.SUCCESS) "Success" else "Error",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (dialogType == DialogType.SUCCESS)
                        Color(0xFF4CAF50) else Color(0xFFF28C20)
                )
            ) {
                Text("OK", color = Color.White)
            }
        }
    )
}

@Composable
fun LoadingDialog(
    message: String
) {
    AlertDialog(
        onDismissRequest = { },
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                CircularProgressIndicator(
                    color = Color(0xFFF28C20),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = { },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    )
}