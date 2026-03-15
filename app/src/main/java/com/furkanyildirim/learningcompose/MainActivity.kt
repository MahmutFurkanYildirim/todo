package com.furkanyildirim.learningcompose

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.furkanyildirim.learningcompose.data.preferences.UserPreferences
import com.furkanyildirim.learningcompose.domain.repository.LocalTodoRepository
import com.furkanyildirim.learningcompose.ui.navigation.TodoNavigation
import com.furkanyildirim.learningcompose.ui.theme.LearningComposeTheme
import com.furkanyildirim.learningcompose.utils.LocaleHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var localTodoRepository: LocalTodoRepository

    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private var signedInEmail: String? by mutableStateOf(null)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _: Boolean ->
        // Permission result can be handled here if needed.
    }

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data ?: return@registerForActivityResult
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        runCatching { task.getResult(ApiException::class.java) }
            .onSuccess { account ->
                val idToken = account.idToken ?: return@onSuccess
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val previousUid = firebaseAuth.currentUser?.uid
                firebaseAuth.signInWithCredential(credential)
                    .addOnSuccessListener {
                        val newUid = firebaseAuth.currentUser?.uid
                        if (!previousUid.isNullOrBlank() && previousUid != newUid) {
                            clearLocalTodos()
                        }
                        signedInEmail = firebaseAuth.currentUser?.email
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            getString(R.string.auth_google_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .onFailure {
                Toast.makeText(
                    this,
                    getString(R.string.auth_google_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("learning_compose_prefs", Context.MODE_PRIVATE)
        val languageCode = prefs.getString("language_code", "tr").orEmpty()
        super.attachBaseContext(LocaleHelper.wrap(newBase, languageCode))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applySavedLanguage()
        enableEdgeToEdge()
        signedInEmail = firebaseAuth.currentUser?.email

        // Request notification permission.
        askNotificationPermission()

        setContent {
            var isDarkTheme by remember { mutableStateOf(userPreferences.isDarkTheme()) }
            var authChoiceCompleted by remember { mutableStateOf(userPreferences.isAuthChoiceCompleted()) }
            var onboardingCompleted by remember { mutableStateOf(userPreferences.isOnboardingCompleted()) }
            var selectedLanguage by remember { mutableStateOf(userPreferences.getLanguageCode()) }

            LearningComposeTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    TodoNavigation(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                        isDarkTheme = isDarkTheme,
                        onThemeChange = {
                            isDarkTheme = it
                            userPreferences.setDarkTheme(it)
                        },
                        authChoiceCompleted = authChoiceCompleted,
                        onCompleteAuthChoice = {
                            authChoiceCompleted = true
                            userPreferences.setAuthChoiceCompleted(true)
                        },
                        onboardingCompleted = onboardingCompleted,
                        onCompleteOnboarding = {
                            onboardingCompleted = true
                            userPreferences.setOnboardingCompleted(true)
                        },
                        onResetOnboarding = {
                            onboardingCompleted = false
                            userPreferences.setOnboardingCompleted(false)
                        },
                        selectedLanguage = selectedLanguage,
                        onLanguageChange = { languageCode ->
                            if (languageCode != selectedLanguage) {
                                selectedLanguage = languageCode
                                userPreferences.setLanguageCode(languageCode)
                                AppCompatDelegate.setApplicationLocales(
                                    LocaleListCompat.forLanguageTags(languageCode)
                                )
                                this@MainActivity.recreate()
                            }
                        },
                        signedInEmail = signedInEmail,
                        onEmailSignIn = { email, password ->
                            signInWithEmail(email, password)
                        },
                        onEmailSignUp = { email, password ->
                            signUpWithEmail(email, password)
                        },
                        onGoogleSignIn = { startGoogleSignIn() },
                        onSignOut = {
                            googleSignInOptionsOrNull()?.let { options ->
                                GoogleSignIn.getClient(this@MainActivity, options).signOut()
                            }
                            firebaseAuth.signOut()
                            signedInEmail = null
                            userPreferences.setAuthChoiceCompleted(false)
                            authChoiceCompleted = false
                            clearLocalTodos()
                        }
                    )
                }
            }
        }
    }

    private fun startGoogleSignIn() {
        val options = googleSignInOptionsOrNull() ?: return
        val signInClient = GoogleSignIn.getClient(this, options)
        googleSignInLauncher.launch(signInClient.signInIntent)
    }

    private fun signInWithEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, getString(R.string.auth_email_required), Toast.LENGTH_SHORT).show()
            return
        }

        val previousUid = firebaseAuth.currentUser?.uid
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val newUid = firebaseAuth.currentUser?.uid
                if (!previousUid.isNullOrBlank() && previousUid != newUid) {
                    clearLocalTodos()
                }
                signedInEmail = firebaseAuth.currentUser?.email
            }
            .addOnFailureListener {
                val code = (it as? FirebaseAuthException)?.errorCode.orEmpty()
                val messageRes = when (code) {
                    "ERROR_INVALID_EMAIL" -> R.string.auth_email_invalid_format
                    "ERROR_INVALID_CREDENTIAL",
                    "ERROR_WRONG_PASSWORD",
                    "ERROR_USER_NOT_FOUND" -> R.string.auth_email_invalid_credentials
                    else -> R.string.auth_email_failed
                }
                Toast.makeText(
                    this,
                    getString(messageRes),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun signUpWithEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, getString(R.string.auth_email_required), Toast.LENGTH_SHORT).show()
            return
        }
        if (password.length < 6) {
            Toast.makeText(this, getString(R.string.auth_email_password_short), Toast.LENGTH_SHORT).show()
            return
        }

        val previousUid = firebaseAuth.currentUser?.uid
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val newUid = firebaseAuth.currentUser?.uid
                if (!previousUid.isNullOrBlank() && previousUid != newUid) {
                    clearLocalTodos()
                }
                signedInEmail = firebaseAuth.currentUser?.email
                Toast.makeText(
                    this,
                    getString(R.string.auth_email_sign_up_success),
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {
                val code = (it as? FirebaseAuthException)?.errorCode.orEmpty()
                val messageRes = when (code) {
                    "ERROR_INVALID_EMAIL" -> R.string.auth_email_invalid_format
                    "ERROR_EMAIL_ALREADY_IN_USE" -> R.string.auth_email_already_in_use
                    "ERROR_WEAK_PASSWORD" -> R.string.auth_email_password_short
                    else -> R.string.auth_email_sign_up_failed
                }
                Toast.makeText(
                    this,
                    getString(messageRes),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun applySavedLanguage() {
        val languageCode = userPreferences.getLanguageCode()
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
    }

    private fun googleSignInOptionsOrNull(): GoogleSignInOptions? {
        val webClientIdResId = resources.getIdentifier(
            "default_web_client_id",
            "string",
            packageName
        )
        if (webClientIdResId == 0) {
            Toast.makeText(this, getString(R.string.auth_google_config_missing), Toast.LENGTH_SHORT).show()
            return null
        }

        val webClientId = getString(webClientIdResId)
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
    }

    private fun clearLocalTodos() {
        lifecycleScope.launch {
            localTodoRepository.clearAllTodos()
        }
    }
}
