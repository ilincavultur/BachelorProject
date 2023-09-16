package com.example.digitaldiaryba

import android.Manifest
import android.content.ContentValues.TAG
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.digitaldiaryba.ui.navigation.Navigation
import com.example.digitaldiaryba.ui.navigation.Screen


import com.example.digitaldiaryba.ui.components.bottomnavigation.CustomBottomNavigation
import com.example.digitaldiaryba.ui.theme.DigitalDiaryBATheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private var token: String =""

    // START https://firebase.google.com/docs/auth/android/manage-users
    private fun login() {
        auth.signInWithEmailAndPassword("ilinca.vultur17@gmail.com", "123456")
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.e("TAG", "signupWithEmail:success");
                    val user = auth.currentUser

                    user?.getIdToken(true)?.addOnCompleteListener {
                        Log.d(TAG, "doLogin: token " + it.result.token)
                        token = it.result.token.toString()
                    }
                } else {
                    Log.e("TAG", "signupWithEmail:failure", task.getException());

                }
            }
    }
    // END https://firebase.google.com/docs/auth/android/manage-users

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {}

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestPermission() {
        permissionLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_MEDIA_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        ))
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

        setContent {
            DigitalDiaryBATheme {

                requestPermission()

                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val snackbarHost = rememberScaffoldState()
                    CustomBottomNavigation(
                        navController = navController,
                        title = when(navBackStackEntry?.destination?.route) {
                            Screen.AlbumFeed.route -> stringResource(id = R.string.album_feed_screen)
                            Screen.PresentationFeed.route -> stringResource(id = R.string.saved_presentation_screen)
                            Screen.NearbyLandmarkScreen.route -> stringResource(id = R.string.nearby_landmarks)
                            Screen.AlbumDetailScreen.route -> navBackStackEntry?.destination?.route
                            else -> {null}
                        },
                        showBottomBar = navBackStackEntry?.destination?.route !in listOf(
                            Screen.AlbumDetailScreen.route,
                            Screen.FileDetailScreen.route,
                            Screen.ImagePreviewScreen.route,
                            Screen.VideoPreviewScreen.route,
                            Screen.ImageCaptureScreen.route,
                            Screen.VideoCaptureScreen.route,
                        ),
                        showBackArrow = navBackStackEntry?.destination?.route !in listOf(
                            Screen.AlbumFeed.route,
                            Screen.NearbyLandmarkScreen.route,
                            Screen.AlbumDetailScreen.route,
                            Screen.PresentationFeed.route,
                            Screen.EditDescriptionDialog.route,
                            Screen.ChooseAlbumNameDialog.route,
                            Screen.ChoosePresentationNameDialog.route,
                        ),
                        modifier = Modifier.fillMaxSize(),
                        content = {
                            Navigation(
                                navController = navController,
                                startDestination = Screen.AlbumFeed.route,
                                modifier = Modifier.fillMaxSize(),
                                snackbarHostState = snackbarHost.snackbarHostState
                            )
                        },
                        backgroundColor = MaterialTheme.colorScheme.onSurface,
                        showTopbar = !navBackStackEntry?.destination?.route.toString().contains(Screen.TabLayout.route),
                        snackbarHostState = snackbarHost.snackbarHostState
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        login()
    }

}

