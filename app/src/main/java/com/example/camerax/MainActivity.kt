package com.example.camerax

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.camerax.ui.theme.CameraXTheme
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.camerax.ui.theme.CameraPreview
import com.example.camerax.ui.theme.PhotoBottomSheetContent
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if(!hasRequiredPermissions()) {
            ActivityCompat.requestPermissions(
                this,CAMERAX_PERMISSIONS,0
            )
        }
        setContent {
            CameraXTheme {
                val scope = rememberCoroutineScope()
                val scaffoldState = rememberBottomSheetScaffoldState()
                val controller = remember {
                    LifecycleCameraController(applicationContext).apply{
                        setEnabledUseCases(
                            CameraController.IMAGE_CAPTURE or
                                    CameraController.VIDEO_CAPTURE
                        )
                    }
                }
                val viewModel = viewModel<MainViewModel>()
                val bitmaps = viewModel.bitmaps.collectAsState()
                BottomSheetScaffold(
                    scaffoldState = scaffoldState,
                    sheetPeekHeight = 0.dp,
                    sheetContent = {
                        PhotoBottomSheetContent(
                            bitmaps = bitmaps.value,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                ) {padding->
                    Box(modifier = Modifier.fillMaxSize()
                        .padding(padding)){
                        CameraPreview(
                            controller = controller,
                            modifier = Modifier.fillMaxSize()
                        )
                        IconButton(onClick = {
                            if(controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                                controller.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                            else
                                controller.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                        },modifier =  Modifier.offset(16.dp,16.dp)) {
                            Icon(imageVector = Icons.Default.Cameraswitch,
                                contentDescription = "Switch Camera")
                        }
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(35.dp)
                            .align(Alignment.BottomCenter),
                            horizontalArrangement = Arrangement.SpaceAround){
                            IconButton(onClick = {
                                scope.launch{
                                    scaffoldState.bottomSheetState.expand()
                                }
                            },
                                modifier = Modifier.size(30.dp)){
                                Icon(imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = "Photo Library")
                            }
                            IconButton(onClick = {
                                takePhoto(
                                    controller = controller,
                                    onPhotoTaken = {bitmap->
                                        viewModel.onTakePhoto(bitmap)

                                    }
                                )
                            },
                                modifier = Modifier.size(30.dp)){
                                Icon(imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = "Photo Camera")
                            }
                            IconButton(onClick = {},
                                modifier = Modifier.size(30.dp)){
                                Icon(imageVector = Icons.Default.Videocam,
                                    contentDescription = "Video Camera")
                            }

                        }

                    }

                }
            }
        }
    }

    private fun takePhoto(
        controller: LifecycleCameraController,
        onPhotoTaken : (Bitmap)-> Unit
    ){
        controller.takePicture(
            ContextCompat.getMainExecutor(applicationContext),
            object: OnImageCapturedCallback(){
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    val matrix = Matrix().apply {
                        postRotate(image.imageInfo.rotationDegrees.toFloat())

                    }
                    val rotatedBitmap = Bitmap.createBitmap(
                        image.toBitmap(),
                        0,
                        0,
                        image.width,
                        image.height,
                        matrix,
                        true
                    )
                    onPhotoTaken(rotatedBitmap)
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.e("Camera","Couldn't take Photo",exception)
                }
            }
        )


    }
    private fun hasRequiredPermissions(): Boolean {
        return CAMERAX_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
    companion object{
        private val CAMERAX_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    }
}
