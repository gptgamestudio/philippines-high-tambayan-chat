package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HighSchoolStrand

private val DarkNavyBg = Color(0xFF0B1120)
private val CardNavyBg = Color(0xFF1E293B)
private val AccentBlue = Color(0xFF2563EB)
private val LightBlue = Color(0xFF38BDF8)
private val BorderNavy = Color(0xFF334155)
private val AmberNotice = Color(0xFFF59E0B)
private val ErrorRed = Color(0xFFEF4444)

enum class AuthTab { SIGN_IN, SIGN_UP }

@Composable
fun AuthScreen(
    onSignInWithEmail: (email: String, pass: String, onResult: (Boolean, String?) -> Unit) -> Unit,
    onSignUpWithEmail: (email: String, pass: String, alias: String, strand: HighSchoolStrand, onResult: (Boolean, String?) -> Unit) -> Unit,
    onGoogleSignInClick: () -> Unit,
    isAuthLoading: Boolean = false,
    authErrorMessage: String? = null,
    onNavigateToCreateUsername: () -> Unit = {},
    onBackClick: (() -> Unit)? = null
) {
    var selectedTab by remember { mutableStateOf(AuthTab.SIGN_IN) }
    var aliasInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    var selectedStrand by remember { mutableStateOf(HighSchoolStrand.SHS_STEM) }
    var strandMenuExpanded by remember { mutableStateOf(false) }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var aliasError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var localError by remember { mutableStateOf<String?>(null) }
    var localSubmitting by remember { mutableStateOf(false) }

    var showTermsModal by remember { mutableStateOf(false) }
    var showAuthInfoModal by remember { mutableStateOf(false) }

    val effectiveLoading = isAuthLoading || localSubmitting

    fun validateAndSubmit() {
        localError = null
        val cleanEmail = emailInput.trim()
        val cleanAlias = aliasInput.trim()

        var hasError = false

        if (selectedTab == AuthTab.SIGN_UP) {
            if (cleanAlias.isBlank() || cleanAlias.length < 3) {
                aliasError = "Maglagay ng alias (hindi bababa sa 3 characters)"
                hasError = true
            } else {
                aliasError = null
            }
        }

        if (cleanEmail.isBlank()) {
            emailError = "Ipasok ang iyong email address"
            hasError = true
        } else if (!cleanEmail.contains("@") || !cleanEmail.contains(".")) {
            emailError = "Maling format ng email (hal. juan@school.edu.ph)"
            hasError = true
        } else {
            emailError = null
        }

        if (passwordInput.length < 6) {
            passwordError = "Ang password ay dapat hindi bababa sa 6 characters"
            hasError = true
        } else {
            passwordError = null
        }

        if (selectedTab == AuthTab.SIGN_UP) {
            if (confirmPasswordInput != passwordInput) {
                confirmPasswordError = "Hindi magkatugma ang password"
                hasError = true
            } else {
                confirmPasswordError = null
            }
        }

        if (hasError) return

        localSubmitting = true
        if (selectedTab == AuthTab.SIGN_IN) {
            onSignInWithEmail(cleanEmail, passwordInput) { success, err ->
                localSubmitting = false
                if (!success) {
                    localError = err ?: "Hindi nagtagumpay ang pag-sign in. Pakisuri ang email at password."
                }
            }
        } else {
            onSignUpWithEmail(cleanEmail, passwordInput, cleanAlias, selectedStrand) { success, err ->
                localSubmitting = false
                if (!success) {
                    localError = err ?: "Hindi nagtagumpay ang paglikha ng account."
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .testTag("auth_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (onBackClick != null) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("auth_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Bumalik",
                            tint = Color.White
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }

                Text(
                    text = "Philippine High Tambayan",
                    color = LightBlue,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = { showAuthInfoModal = true },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Impormasyon sa Auth",
                        tint = AmberNotice
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Brand Header Logo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(AccentBlue, Color(0xFF1E3A8A))
                        )
                    )
                    .border(2.dp, LightBlue.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🇵🇭", fontSize = 40.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Philippine High Tambayan Chat",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Ligtas, tunay na Firebase Authentication, at pambansang hangout ng mga estudyante.",
                color = Color(0xFF94A3B8),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Display error banner if error exists
            val activeError = localError ?: authErrorMessage
            if (activeError != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Error",
                            tint = ErrorRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = activeError,
                            color = Color(0xFFFCA5A5),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Segmented Tabs: Sign In / Sign Up
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                // Sign In Tab Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(9.dp))
                        .background(if (selectedTab == AuthTab.SIGN_IN) AccentBlue else Color.Transparent)
                        .clickable {
                            selectedTab = AuthTab.SIGN_IN
                            emailError = null
                            passwordError = null
                            aliasError = null
                            confirmPasswordError = null
                            localError = null
                        }
                        .padding(vertical = 10.dp)
                        .testTag("tab_sign_in"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Sign In",
                            tint = if (selectedTab == AuthTab.SIGN_IN) Color.White else Color(0xFF94A3B8),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Sign In",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (selectedTab == AuthTab.SIGN_IN) Color.White else Color(0xFF94A3B8)
                        )
                    }
                }

                // Sign Up Tab Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(9.dp))
                        .background(if (selectedTab == AuthTab.SIGN_UP) AccentBlue else Color.Transparent)
                        .clickable {
                            selectedTab = AuthTab.SIGN_UP
                            emailError = null
                            passwordError = null
                            aliasError = null
                            confirmPasswordError = null
                            localError = null
                        }
                        .padding(vertical = 10.dp)
                        .testTag("tab_sign_up"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Sign Up",
                            tint = if (selectedTab == AuthTab.SIGN_UP) Color.White else Color(0xFF94A3B8),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Sign Up",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (selectedTab == AuthTab.SIGN_UP) Color.White else Color(0xFF94A3B8)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Student Alias Field (Only in Sign Up Mode)
            if (selectedTab == AuthTab.SIGN_UP) {
                OutlinedTextField(
                    value = aliasInput,
                    onValueChange = {
                        aliasInput = it
                        if (aliasError != null) aliasError = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_alias_input"),
                    label = { Text("Student Alias / Palayaw", color = Color(0xFF94A3B8)) },
                    placeholder = { Text("halimbawa: benjie_stem12", color = Color(0xFF475569)) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Alias", tint = LightBlue)
                    },
                    isError = aliasError != null,
                    supportingText = {
                        if (aliasError != null) {
                            Text(text = aliasError!!, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = BorderNavy,
                        focusedContainerColor = CardNavyBg,
                        unfocusedContainerColor = CardNavyBg
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Strand Selector
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = "${selectedStrand.badge} - ${selectedStrand.displayName}",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { strandMenuExpanded = true },
                        label = { Text("High School Strand / Baitang", color = Color(0xFF94A3B8)) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.School, contentDescription = "Strand", tint = LightBlue)
                        },
                        trailingIcon = {
                            Text(
                                text = "▼",
                                color = LightBlue,
                                modifier = Modifier
                                    .clickable { strandMenuExpanded = true }
                                    .padding(8.dp)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = BorderNavy,
                            focusedContainerColor = CardNavyBg,
                            unfocusedContainerColor = CardNavyBg
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    DropdownMenu(
                        expanded = strandMenuExpanded,
                        onDismissRequest = { strandMenuExpanded = false },
                        modifier = Modifier.background(CardNavyBg)
                    ) {
                        HighSchoolStrand.values().forEach { strand ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = strand.badge, color = LightBlue, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = strand.displayName, color = Color.White, fontSize = 13.sp)
                                    }
                                },
                                onClick = {
                                    selectedStrand = strand
                                    strandMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Email Form Field
            OutlinedTextField(
                value = emailInput,
                onValueChange = {
                    emailInput = it
                    if (emailError != null) emailError = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_email_input"),
                label = { Text("School / Personal Email", color = Color(0xFF94A3B8)) },
                placeholder = { Text("halimbawa: benjie@school.edu.ph", color = Color(0xFF475569)) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = "Email", tint = LightBlue)
                },
                isError = emailError != null,
                supportingText = {
                    if (emailError != null) {
                        Text(text = emailError!!, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = BorderNavy,
                    focusedContainerColor = CardNavyBg,
                    unfocusedContainerColor = CardNavyBg
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Password Form Field with Show/Hide Eye Icon
            OutlinedTextField(
                value = passwordInput,
                onValueChange = {
                    passwordInput = it
                    if (passwordError != null) passwordError = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_password_input"),
                label = { Text("Password", color = Color(0xFF94A3B8)) },
                placeholder = { Text("Hindi bababa sa 6 characters", color = Color(0xFF475569)) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Password", tint = LightBlue)
                },
                trailingIcon = {
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible },
                        modifier = Modifier.testTag("btn_toggle_password")
                    ) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Itago ang password" else "Ipakita ang password",
                            tint = if (passwordVisible) AccentBlue else Color(0xFF94A3B8)
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = passwordError != null,
                supportingText = {
                    if (passwordError != null) {
                        Text(text = passwordError!!, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = if (selectedTab == AuthTab.SIGN_UP) ImeAction.Next else ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (selectedTab == AuthTab.SIGN_IN) validateAndSubmit()
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = BorderNavy,
                    focusedContainerColor = CardNavyBg,
                    unfocusedContainerColor = CardNavyBg
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Confirm Password Field (Only in Sign Up Mode)
            if (selectedTab == AuthTab.SIGN_UP) {
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = confirmPasswordInput,
                    onValueChange = {
                        confirmPasswordInput = it
                        if (confirmPasswordError != null) confirmPasswordError = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_confirm_password_input"),
                    label = { Text("Kumpirmahin ang Password", color = Color(0xFF94A3B8)) },
                    placeholder = { Text("I-type muli ang password", color = Color(0xFF475569)) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = "Confirm Password", tint = LightBlue)
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { confirmPasswordVisible = !confirmPasswordVisible },
                            modifier = Modifier.testTag("btn_toggle_confirm_password")
                        ) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (confirmPasswordVisible) "Itago ang password" else "Ipakita ang password",
                                tint = if (confirmPasswordVisible) AccentBlue else Color(0xFF94A3B8)
                            )
                        }
                    },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = confirmPasswordError != null,
                    supportingText = {
                        if (confirmPasswordError != null) {
                            Text(text = confirmPasswordError!!, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { validateAndSubmit() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = BorderNavy,
                        focusedContainerColor = CardNavyBg,
                        unfocusedContainerColor = CardNavyBg
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Primary Submit Button (Sign In / Sign Up)
            Button(
                onClick = { validateAndSubmit() },
                enabled = !effectiveLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("auth_submit_button"),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (effectiveLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (selectedTab == AuthTab.SIGN_IN) "Mag-Sign In sa Tambayan" else "Gumawa ng Account (Sign Up)",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Toggle Between Sign In and Sign Up Text Prompt
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedTab == AuthTab.SIGN_IN) "Wala ka pang account? " else "May account ka na? ",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
                Text(
                    text = if (selectedTab == AuthTab.SIGN_IN) "Mag-Sign Up dito" else "Mag-Sign In dito",
                    color = LightBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable {
                            selectedTab = if (selectedTab == AuthTab.SIGN_IN) AuthTab.SIGN_UP else AuthTab.SIGN_IN
                        }
                        .padding(4.dp)
                        .testTag("btn_switch_auth_mode")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Divider OR
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f).height(1.dp).background(BorderNavy))
                Text(
                    text = " O PUMILI NG IBANG OPSYON ",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Box(modifier = Modifier.weight(1f).height(1.dp).background(BorderNavy))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Google Sign-In Button Option
            OutlinedButton(
                onClick = onGoogleSignInClick,
                enabled = !effectiveLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("auth_google_button"),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF475569)),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = CardNavyBg)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🌐", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Mag-sign in gamit ang Google",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Auto-Fill for Owner/Admin Login Convenience
            Card(
                colors = CardDefaults.cardColors(containerColor = CardNavyBg.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderNavy),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Admin Quick Fill (Owner Benjie):",
                            color = AmberNotice,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "insularleann@gmail.com",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                    Button(
                        onClick = {
                            selectedTab = AuthTab.SIGN_IN
                            emailInput = "insularleann@gmail.com"
                            passwordInput = "100877benjie"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Gamitin", color = LightBlue, fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Terms & Privacy link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sa pagpapatuloy, sumasang-ayon ka sa ",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp
                )
                Text(
                    text = "Community Policy",
                    color = LightBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { showTermsModal = true }
                )
            }
        }
    }

    // Community Policy Modal Dialog
    if (showTermsModal) {
        AlertDialog(
            onDismissRequest = { showTermsModal = false },
            title = {
                Text(
                    text = "Philippine High Tambayan Policy",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "1. Republic Act 10627 (Anti-Bullying Act):\nMahigpit na ipinagbabawal ang anumang panunukso, cyberbullying, o paninira sa kapwa estudyante.\n\n" +
                                "2. Republic Act 10173 (Data Privacy Act):\nHindi nangongolekta ng totoong contact info ang app. Huwag mamigay ng address o phone number.\n\n" +
                                "3. Moderation & Safety:\nLahat ng mensahe ay dumadaan sa filtering at maaaring i-report ng mga mag-aaral.",
                        color = Color(0xFFCBD5E1),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showTermsModal = false },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                ) {
                    Text("Naiintindihan Ko", color = Color.White)
                }
            },
            containerColor = CardNavyBg
        )
    }

    // Auth Info Modal Dialog
    if (showAuthInfoModal) {
        AlertDialog(
            onDismissRequest = { showAuthInfoModal = false },
            title = {
                Text(
                    text = "Tala Ukol sa Firebase Authentication",
                    fontWeight = FontWeight.Bold,
                    color = LightBlue
                )
            },
            text = {
                Text(
                    text = "Ang app na ito ay gumagamit ng tunay na Firebase Authentication (Email/Password at Google Sign-In gamit ang Credential Manager). Lahat ng user data, private chats, at barkada friends ay ligtas na nakatali sa iyong authenticated Firebase UID.",
                    color = Color(0xFFCBD5E1),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showAuthInfoModal = false },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                ) {
                    Text("Sige, Salamat", color = Color.White)
                }
            },
            containerColor = CardNavyBg
        )
    }
}
