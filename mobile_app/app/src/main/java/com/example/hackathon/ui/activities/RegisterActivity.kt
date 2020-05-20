package com.example.hackathon.ui.activities

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.hackathon.R
import com.example.hackathon.data.register.FaceID.APIService
import com.example.hackathon.data.register.FaceID.Client
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_register.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class RegisterActivity : AppCompatActivity() {

    private lateinit var storageReference: StorageReference
    private lateinit var databaseReference: DatabaseReference

    private var imageFaceUrl = "null"
    private var imageCMNDUrl = "null"

    private var uri_face: Uri? = null
    private var uri_cmnd: Uri? = null
    lateinit var apiService: APIService
    private val API_LINK = "http://192.168.1.24:8000/"
    private var ID_user = createIDUser()

    companion object {
        const val IMAGE_FACE_REQUEST = 1
        const val IMAGE_CMND_REQUEST = 2
        private lateinit var fileReference_img: StorageReference
        private lateinit var fileeReference_cmnd: StorageReference
    }

    override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(
                this@RegisterActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            == PackageManager.PERMISSION_DENIED
        ) {

            // Requesting the permission
            ActivityCompat.requestPermissions(
                this@RegisterActivity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            );
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_register)
        apiService = Client.getClient(API_LINK)!!.create(
            APIService::class.java
        )
        storageReference = FirebaseStorage.getInstance().getReference("uploads_face_id")
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(ID_user)
        // set adapter for function of register
        val items = listOf("Recruiter", "Job Seeker")
        val adapter = ArrayAdapter(this, R.layout.item_function_register, items)
        (function_register as AutoCompleteTextView).setAdapter(adapter)

        // set not first start is false
        var prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        var editPrefs = prefs.edit()
        editPrefs.putBoolean("firstStart", false)
        editPrefs.apply()

        // Choose image face
        face_image.setOnClickListener {
            var intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(intent, IMAGE_FACE_REQUEST)
        }

        // Choose image cmnd
        cmnd_image.setOnClickListener {
            var intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(intent, IMAGE_CMND_REQUEST)
        }

        // Save value to server is reference Users
        register_button.setOnClickListener {
            if (uri_cmnd != null && uri_face != null) {
                var fullname = full_name_register.text.toString().trim()
                var email = email_register.text.toString().trim()
                var password = password_register.text.toString().trim()
                var function = function_register.text.toString().trim()
                if (fullname.equals("") || email.equals("") || password.equals("")) {
                    Toast.makeText(this, "Can't empty value", Toast.LENGTH_SHORT).show()
                } else {
                    //upload here
                    var hashMap: HashMap<String, Any> = HashMap()
                    uploadImage()
                    var prefs: SharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
                    var editPrefs = prefs.edit()
                    editPrefs.putString("IDUser", ID_user)
                    editPrefs.apply()
                    // should update to push up firebase
                    hashMap.put("fullname", fullname)
                    hashMap.put("email", email)
                    hashMap.put("password", password)
                    hashMap.put("avatar", "default")
                    hashMap.put("rating", "0")
                    hashMap.put("function", function)
                    hashMap.put("imageFaceURL", imageFaceUrl)
                    hashMap.put("imageCMNDUrl", imageCMNDUrl)
                    databaseReference.setValue(hashMap)
                    var intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }
            } else {
                Toast.makeText(this, "Can't empty image", Toast.LENGTH_LONG).show()
            }

            // Put url to Khôi
            /*databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    var imageFaceIDModel = UserModel(p0)
                    apiService.postImageURL(imageFaceIDModel.imageCMNDURL, imageFaceIDModel.imageFaceURL).enqueue(object : Callback<UserModel>{
                        override fun onFailure(call: Call<UserModel>, t: Throwable) {
                        }

                        override fun onResponse(
                            call: Call<UserModel>,
                            response: Response<UserModel>
                        ) {
                            Log.e("Null", response.body()?.approve.toString())
                        }
                    })
                }
            })*/
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_FACE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            uri_face = data.data!!
            Glide.with(applicationContext)
                .load(FileUtils.getPathFromUri(applicationContext, uri_face)).centerCrop()
                .fitCenter().into(face_image)

            Log.e("URL", uri_face.toString())
        }

        if (requestCode == IMAGE_CMND_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            uri_cmnd = data.data!!
            Glide.with(applicationContext)
                .load(FileUtils.getPathFromUri(applicationContext, uri_cmnd)).centerCrop()
                .fitCenter().into(cmnd_image)
            Log.e("URL", uri_cmnd.toString())
        }
    }

    private fun uploadImage() {
        var progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading")
        progressDialog.show()

        // image of face
        if (uri_face != null) {
            fileReference_img = storageReference.child(
                System.currentTimeMillis().toString() + "." + getFileExtension(uri_face!!)
            )

            fileReference_img.putFile(uri_face!!).continueWithTask(object :
                Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
                override fun then(p0: Task<UploadTask.TaskSnapshot>): Task<Uri> {
                    if (!p0.isSuccessful) {
                        throw p0.exception!!
                    }
                    return fileReference_img.downloadUrl
                }
            }).addOnCompleteListener {
                // get image URL for user database
                if (it.isSuccessful) {
                    var downloadUri: Uri? = it.getResult()
                    var mUri: String = downloadUri.toString()
                    databaseReference =
                        FirebaseDatabase.getInstance().getReference("Users").child(ID_user)
                    var hashMap: HashMap<String, Any> = HashMap()
                    hashMap.put("imageFaceURL", mUri)
                    databaseReference.updateChildren(hashMap)
                    imageFaceUrl = mUri

                    progressDialog.dismiss()
                }
            }
        } else {
            Toast.makeText(applicationContext, "No face image selected", Toast.LENGTH_SHORT).show()
        }

        // image of cmnd
        if (uri_cmnd != null) {
            fileeReference_cmnd = storageReference.child(
                System.currentTimeMillis().toString() + "." + getFileExtension(uri_cmnd!!)
            )

            fileeReference_cmnd.putFile(uri_cmnd!!).continueWithTask(object :
                Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
                override fun then(p0: Task<UploadTask.TaskSnapshot>): Task<Uri> {
                    if (!p0.isSuccessful) {
                        throw p0.exception!!
                    }
                    return fileeReference_cmnd.downloadUrl
                }
            }).addOnCompleteListener {
                // get image URL for user database
                if (it.isSuccessful) {
                    var downloadUri: Uri? = it.getResult()
                    var mUri: String = downloadUri.toString()

                    databaseReference =
                        FirebaseDatabase.getInstance().getReference("Users").child(ID_user)
                    var hashMap: HashMap<String, Any> = HashMap()
                    hashMap.put("imageCMNDUrl", mUri)
                    databaseReference.updateChildren(hashMap)

                    imageCMNDUrl = mUri
                    progressDialog.dismiss()
                }
            }
        } else {
            Toast.makeText(applicationContext, "No face image selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        var contentResolver: ContentResolver = contentResolver
        var mimeType: MimeTypeMap = MimeTypeMap.getSingleton()
        return mimeType.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun createIDUser(): String {
        var ID = ""
        var simpleDateFormat = SimpleDateFormat("yyyyMMddkkmmss")
        var date = Calendar.getInstance().time
        ID = simpleDateFormat.format(date)
        return "us" + ID
    }
}
