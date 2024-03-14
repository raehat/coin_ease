package com.example.particleapped.ui.particleAppScreen

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.particleapped.ui.particleAppScreen.homescreen.BuyCryptoScreen
import com.example.particleapped.ui.particleAppScreen.homescreen.HomeScreen
import com.example.particleapped.ui.particleAppScreen.homescreen.MyAccountScreen
import com.example.particleapped.ui.particleAppScreen.homescreen.PayByAddressScreen
import com.example.particleapped.ui.particleAppScreen.homescreen.PaymentCompleted
import com.example.particleapped.ui.particleAppScreen.homescreen.SelectDestinationChainScreen
import com.example.particleapped.ui.particleAppScreen.homescreen.SellCryptoScreen
import com.example.particleapped.ui.particleAppScreen.homescreen.SwitchChainScreen
import com.example.particleapped.ui.particleAppScreen.homescreen.ViewSentTransactionsScreen
import com.example.particleapped.ui.particleAppScreen.homescreen.ViewUnclaimedTransactionsScreen
import com.example.particleapped.ui.particleAppScreen.loginscreen.LoginScreen
import com.example.particleapped.ui.particleAppScreen.splashscreen.SplashScreen
import com.example.particleapped.ui.theme.ParticleAppTheme
import com.example.particleapped.utils.ImageUtils
import com.example.particleapped.utils.QRScanner
import com.microblink.blinkid.activity.result.OneSideScanResult
import com.microblink.blinkid.activity.result.ResultStatus
import com.microblink.blinkid.activity.result.contract.OneSideDocumentScan
////import com.microblink.blinkid.activity.result.OneSideScanResult
////import com.microblink.blinkid.activity.result.ResultStatus
////import com.microblink.blinkid.activity.result.contract.OneSideDocumentScan
//import com.yoti.mobile.android.capture.face.ui.FaceCapture
import com.yoti.mobile.android.capture.face.ui.models.camera.CameraState
import com.yoti.mobile.android.capture.face.ui.models.face.FaceCaptureResult

class ParticleApp : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val resultLauncher =
            registerForActivityResult(OneSideDocumentScan()) { oneSideDocumentScan: OneSideScanResult ->
                when (oneSideDocumentScan.resultStatus) {
                    ResultStatus.FINISHED -> {
                        // code after a successful scan
                        // use oneSideDocumentScan.result for fetching results, for example:
                        val firstName = oneSideDocumentScan.result?.firstName?.value()
                        val documentImageBitmap = oneSideDocumentScan.result?.faceImage?.convertToBitmap()

                        val intent = Intent(this, MyCameraActivity::class.java)
                        val byteArray = ImageUtils.bitmapToByteArray(documentImageBitmap)
                        if (byteArray != null)
                            intent.putExtra("imageData", byteArray)
                        startActivity(intent)
                    }
                    ResultStatus.CANCELLED -> {
                        // code after a cancelled scan
                    }
                    ResultStatus.EXCEPTION -> {
                        // code after a failed scan
                    }
                    else -> {}
                }
            }
        val viewModel : ParticleAppViewModel = ViewModelProvider(this)[ParticleAppViewModel::class.java]
        setContent {
            ParticleAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val showToast : (String) -> Unit = {
                        Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                    }
                    val navController = rememberNavController()
                    val data = "true"
                    NavHost(navController = navController, startDestination = Screen.SplashScreen) {
                        composable(route = Screen.SplashScreen) {
                            ColumnScreen { SplashScreen(navController) }
                        }
                        composable(route = Screen.LoginScreen) {
                            ColumnScreen { LoginScreen(navController, viewModel, showToast, resultLauncher) }
                        }
                        composable(route = Screen.HomeScreen) {
                            HomeScreen(navController, viewModel)
                        }
                        composable(route = Screen.MyAccountScreen) {
                            MyAccountScreen(navController, viewModel, showToast)
                        }
                        composable(route = Screen.SwitchChainScreen) {
                            SwitchChainScreen(navController, viewModel, showToast)
                        }
                        composable(route = Screen.PayByAddressScreen) {
                            PayByAddressScreen(navController, viewModel, showToast)
                        }
                        composable(route = Screen.SelectDestinationChainScreen) {
                            SelectDestinationChainScreen(navController, viewModel)
                        }
                        composable(route = Screen.QrScanner) {
                            QRScanner(navController, viewModel)
                        }
                        composable(route = Screen.PaymentCompletedScreen) {
                            PaymentCompleted(navController, viewModel)
                        }
                        composable(route = Screen.ViewSentTxScreen) {
                            ViewSentTransactionsScreen(navController, viewModel, showToast)
                        }
                        composable(route = Screen.ViewUnclaimedTxScreen) {
                            ViewUnclaimedTransactionsScreen(navController, viewModel, showToast)
                        }
                        composable(route = Screen.SellCryptoScreen) {
                            SellCryptoScreen(navController, viewModel, showToast)
                        }
                        composable(route = Screen.BuyCryptoScreen) {
                            BuyCryptoScreen(navController, viewModel, showToast, LocalContext.current)
                        }
                    }
                }
            }
        }
    }
}