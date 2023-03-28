package pt.pl.estg.ei.regulapp.chat

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import android.os.Bundle
import pt.pl.estg.ei.regulapp.R
import pt.pl.estg.ei.regulapp.GlobalSettings
import com.google.firebase.database.FirebaseDatabase
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.MediaStoreSignature
import android.content.Intent
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.database.ValueEventListener
import android.text.TextUtils
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import pt.pl.estg.ei.regulapp.R.layout
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import de.hdodenhof.circleimageview.CircleImageView
import pt.pl.estg.ei.regulapp.classes.*
import java.util.HashMap

class SettingsActivity : AppCompatActivity() {
    private var UpdateAccountSettings: Button? = null
    private var username: EditText? = null
    private var userprofileimage: CircleImageView? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var Refdatabase: DatabaseReference? = null
    private var currentUserrID: String? = null
    private val photoUri: String? = null
    private val toolbar: Toolbar? = null
    private var session: Session? = null
    private var userprofilestoragereference: StorageReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_settings)
        firebaseAuth = FirebaseAuth.getInstance()
        session = (this.application as GlobalSettings).session
        currentUserrID = (this.application as GlobalSettings).session!!.id
        if (currentUserrID == null) {
            firebaseAuth!!.signOut()
        }
        Refdatabase = FirebaseDatabase.getInstance().reference
        userprofilestoragereference = FirebaseStorage.getInstance().reference.child("storageImageRef")
        UpdateAccountSettings = findViewById(R.id.update_settings_button)
        username = findViewById(R.id.set_user_name)
        userprofileimage = findViewById(R.id.set_profile_image)
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.azul_claro)
        UpdateAccountSettings!!.setOnClickListener(View.OnClickListener { UpdateSettings() })
        RetrieveUserInfo()
        userprofileimage!!.setOnClickListener(View.OnClickListener {
            val galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, 1)
        })
    }

    override fun onResume() {
        super.onResume()
        Analytics.fireEvent(AnalyticsEvent.CHAT_SIGNUP_OPENMENU)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            val uriimage = data.data
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this)
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri = result.uri
                val filepath = userprofilestoragereference!!.child("$currentUserrID.jpg")
                filepath.putFile(resultUri).addOnSuccessListener { taskSnapshot ->
                    val firebaseUri = taskSnapshot.storage.downloadUrl
                    firebaseUri.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        Refdatabase!!.child("Users").child(currentUserrID!!).child("image")
                                .setValue(downloadUrl).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(this@SettingsActivity, "Image saved in database successfuly", Toast.LENGTH_SHORT).show()
                                    } else {
                                        val message = task.exception.toString()
                                        Toast.makeText(this@SettingsActivity, "Error: $message", Toast.LENGTH_SHORT).show()
                                    }
                                }
                    }
                }
            }
        }
    }

    private fun UpdateSettings() {
        val setusername = username!!.getText().toString()
        if (TextUtils.isEmpty(setusername)) {
            Toast.makeText(this@SettingsActivity, "Please write your user name first...", Toast.LENGTH_SHORT).show()
        } else {
            val profileMap = HashMap<String?, Any?>()
            profileMap["uid"] = currentUserrID
            profileMap["name"] = setusername
            profileMap["image"] = photoUri
            Refdatabase!!.child("Users").child(currentUserrID!!).updateChildren(profileMap).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Analytics.fireEvent(AnalyticsEvent.CHAT_SIGNUP_SUCCESS)
                    Toast.makeText(this@SettingsActivity, "Your profile has been updated...", Toast.LENGTH_SHORT).show()
                    sendUserToMainActivity()
                } else {
                    Analytics.fireEvent(AnalyticsEvent.CHAT_SIGNUP_FAILED)
                    val errormessage = task.exception.toString()
                    Toast.makeText(this@SettingsActivity, "Error :$errormessage", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun sendUserToMainActivity() {
        val mainIntent = Intent(this@SettingsActivity, MainChatActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(mainIntent)
        finish()
    }

    private fun RetrieveUserInfo() {
        Refdatabase!!.child("Users").child(currentUserrID!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("name") && dataSnapshot.hasChild("storageImageRef")) {
                    val retrieveusername = dataSnapshot.child("name").value.toString()
                    val retrieveuserstatus = dataSnapshot.child("status").value.toString()
                    val retrieveuserimage = dataSnapshot.child("storageImageRef").value.toString()
                    Log.d("1", retrieveuserimage)
                    Picasso.get().load(retrieveuserimage).into(userprofileimage)
                    Log.d("2", userprofileimage.toString())
                } else if (dataSnapshot.exists() && dataSnapshot.hasChild("name")) {
                    val retrieveusername = dataSnapshot.child("name").value.toString()
                    username!!.setText(retrieveusername)
                } else {
                    username!!.setText(session!!.nome!!)
                    val hashMap = HashMap<String?, Any?>()
                    hashMap["name"] = session!!.nome!!
                    hashMap["image"] = photoUri
                    hashMap["uid"] = session!!.id
                    Refdatabase!!.child("Users").child(currentUserrID!!).updateChildren(hashMap)
                    Glide.with(this@SettingsActivity).load(photoUri).signature(MediaStoreSignature("",
                            System.currentTimeMillis(), 0)).error(R.drawable.default_profile_picture).into(userprofileimage!!)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}