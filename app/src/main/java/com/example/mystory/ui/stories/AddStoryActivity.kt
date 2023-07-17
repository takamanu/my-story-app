package com.example.mystory.ui.stories

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.mystory.*
import com.example.mystory.databinding.ActivityAddStoryBinding
import com.example.mystory.data.LoginResponse
import com.example.mystory.utils.Utils
import com.example.mystory.vm.MainViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var sessionPreferences: SharedPreferences
    private var getFile: File? = null
    private var result: Bitmap? = null

    private lateinit var storyViewModel: MainViewModel

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.permission_failed),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        overridePendingTransition(R.anim.fade_in, 0)

//        setToolbar()
        storyViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        getPermission()
        binding.btnCamera.setOnClickListener { startCameraX() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnUpload.setOnClickListener {
            val description = binding.tvDescription.text.toString().trim()
            if (description.isEmpty()) {
                val snackbar = Snackbar.make(binding.root, "Please add a description!", Snackbar.LENGTH_LONG)
                val snackbarView = snackbar.view
                val textView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                textView.setTypeface(textView.typeface, Typeface.BOLD)
                snackbar.setBackgroundTint(resources.getColor(R.color.red))
                snackbar.show()
            } else {
                // Description is not empty, proceed with the upload
                uploadImage()
            }
        }


        binding.backButton.setOnClickListener {
            val mainActivity = Intent(this@AddStoryActivity, MainActivity::class.java)
            startActivity(mainActivity)
        }
        binding.progressBar4.visibility = View.GONE
//        showLoading()
    }

    private fun getPermission() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun startCameraX() {
        launcherIntentCameraX.launch(Intent(this, CameraActivity::class.java))
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isRearCamera = it.data?.getBooleanExtra("isRearCamera", true) as Boolean

            getFile = myFile
            result =
                Utils.rotateImage(
                    BitmapFactory.decodeFile(getFile?.path),
                    isRearCamera
                )
        }
        binding.ivAddStory.setImageBitmap(result)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val selectedImg: Uri = it.data?.data as Uri
            val myFile = Utils.uriToFile(selectedImg, this@AddStoryActivity)
            getFile = myFile
            binding.ivAddStory.setImageURI(selectedImg)
        }
    }

    private fun uploadImage() {
        if (getFile != null) {

            val file = Utils.compressImage(getFile as File)

            val description = binding.tvDescription.text.toString()
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            sessionPreferences = getSharedPreferences("session", Context.MODE_PRIVATE)
            val loginResponseJson = sessionPreferences.getString("loginResponse", null)
            val gson = Gson()

            val user = gson.fromJson(loginResponseJson, LoginResponse::class.java)

            storyViewModel.uploadImage(
                user,
                description,
                imageMultipart,
                object : Utils.ApiCallbackString {
                    override fun onResponse(success: Boolean, message: String) {
//                        showAlertDialog(success, message)
                        Utils.showToast(this@AddStoryActivity, message)
                        if (success){
                            val mainActivity =
                                Intent(this@AddStoryActivity, MainActivity::class.java)
                            mainActivity.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(mainActivity)
                            finish()
                        }

//                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                })


        } else {
            Utils.showToast(this@AddStoryActivity, getString(R.string.no_file))
        }
    }

//    private fun showAlertDialog(param: Boolean, message: String) {
//        if (param) {
//            AlertDialog.Builder(this).apply {
//                setTitle(getString(R.string.information))
//                setMessage(getString(R.string.upload_success))
//                setPositiveButton(getString(R.string.continue_)) { _, _ ->
//
//                }
//                finish()
//                create()
//                show()
//            }
//        } else {
//            AlertDialog.Builder(this).apply {
//                setTitle(getString(R.string.information))
//                setMessage(getString(R.string.upload_failed) + ", $message")
//                setPositiveButton(getString(R.string.continue_)) { _, _ ->
//                    binding.progressBar4.visibility = View.GONE
//                }
//                create()
//                show()
//            }
//        }
//    }

//    private fun showLoading() {
//        storyViewModel.isLoading.observe(this) {
//            binding.apply {
//                if (it) progressBar4.visibility = View.VISIBLE
//                else progressBar4.visibility = View.GONE
//            }
//        }
//    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Apply fade-out animation
        overridePendingTransition(0, R.anim.fade_out)
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    private fun showMessage(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}
