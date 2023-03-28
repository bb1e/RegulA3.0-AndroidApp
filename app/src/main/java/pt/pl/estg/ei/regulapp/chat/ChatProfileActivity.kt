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
import com.google.firebase.storage.FirebaseStorage
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.ValueEventListener
import androidx.drawerlayout.widget.DrawerLayout
import android.provider.MediaStore
import com.google.firebase.firestore.FirebaseFirestore
import pt.pl.estg.ei.regulapp.enums.BD
import pt.pl.estg.ei.regulapp.R.layout
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.NullPointerException
import java.util.HashMap

class ChatProfileActivity : AppCompatActivity() {
    private var reciever_id: String? = null
    private var sender_user_id: String? = null
    private var current_state: String? = null
    private val oldName: String? = null
    private var visit_profile_image: CircleImageView? = null
    private var visit_name: TextView? = null
    private var visit_age: TextView? = null
    private var visit_status: TextView? = null
    private var request_button: Button? = null
    private var decline_button: Button? = null
    private var mauth: FirebaseAuth? = null
    var ref: DatabaseReference? = null
    var chatrequestref: DatabaseReference? = null
    var contactsRef: DatabaseReference? = null
    var NotificationRef: DatabaseReference? = null
    var rootRef: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_profile_chat)
        mauth = FirebaseAuth.getInstance()
        sender_user_id = (this.application as GlobalSettings).session?.id
        val tipoUtilizador = intent.extras?.get("type_of_user").toString()
        reciever_id = try {
            intent.extras?.get("visit_user_id").toString()
        } catch (exception: NullPointerException) {
            sender_user_id
        }
        visit_profile_image = findViewById(R.id.visit_profile_image)
        visit_name = findViewById(R.id.visit_user_name)
        visit_age = findViewById(R.id.visit_age)
        visit_status = findViewById(R.id.descriptionTV)
        request_button = findViewById(R.id.send_message_request_button)
        decline_button = findViewById(R.id.decline_message_request_button)
        current_state = "new"
        ref = FirebaseDatabase.getInstance().reference
        rootRef = ref?.child(tipoUtilizador)?.child(reciever_id!!)
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.vermelho_chat)
        val layout = findViewById<DrawerLayout?>(R.id.drawer_layout_profile)
        val hamburger = layout.findViewById<ImageView?>(R.id.toolbar_menu)
        hamburger.visibility = View.INVISIBLE
        val textView = layout.findViewById<TextView?>(R.id.toolbar_title)
        textView.text = "Perfil"
        ref = FirebaseDatabase.getInstance().reference
        chatrequestref = FirebaseDatabase.getInstance().reference.child("Chat Requests")
        contactsRef = FirebaseDatabase.getInstance().reference.child("Contacts")
        NotificationRef = FirebaseDatabase.getInstance().reference.child("Notifications")
        val name = findViewById<EditText?>(R.id.profileUserName)
        rootRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) return
                if (dataSnapshot.hasChild("image")) {
                    val retrieveuserimage = dataSnapshot.child("image").value.toString()
                    if (!retrieveuserimage.isEmpty()) {
                        Glide.with(applicationContext).load(FirebaseStorage.getInstance().getReference(retrieveuserimage)).signature(MediaStoreSignature("",
                                System.currentTimeMillis(), 0)).error(R.drawable.default_profile_picture).into(visit_profile_image!!)
                    } // Picasso.get().load(retrieveuserimage).into(visit_profile);
                }
                if (dataSnapshot.hasChild("name")) {
                    val retrieveusername = dataSnapshot.child("name").value.toString()
                    if (sender_user_id == reciever_id) {
                        name.setText(retrieveusername)
                    } else visit_name?.setText(retrieveusername)
                }
                if (dataSnapshot.hasChild("description")) {
                    val retrieveuserstatus = dataSnapshot.child("description").value.toString()
                    visit_status?.setText(retrieveuserstatus)
                    visit_status?.setVisibility(View.VISIBLE)
                }
                if (dataSnapshot.hasChild("age")) {
                    val retrieveuserstatus = dataSnapshot.child("age").value.toString()
                    visit_age?.setText("$retrieveuserstatus anos")
                    visit_age?.setVisibility(View.VISIBLE)
                }
                ManageChatRequest()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        if (sender_user_id == reciever_id) {
            request_button?.setVisibility(View.INVISIBLE)
            findViewById<View?>(R.id.visit_user_name).visibility = View.INVISIBLE
            val salvar = findViewById<Button?>(R.id.save)
            salvar.visibility = View.VISIBLE
            salvar.setOnClickListener { v: View? ->
                val nomeString: String = name.text.toString()
                if (name.text.toString() === oldName && nomeString.isEmpty()) Toast.makeText(applicationContext, "Insira um nome que seja alteravel", Toast.LENGTH_SHORT).show() else rootRef?.child("name")?.setValue(nomeString)?.addOnSuccessListener(OnSuccessListener { Log.d("ChatProfile-> ", "atualizado campo name") })
            }
            findViewById<View?>(R.id.profileUserName).visibility = View.VISIBLE
            visit_profile_image?.setOnClickListener(View.OnClickListener { v: View? ->
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 1000)
            })
        } else request_button?.setVisibility(View.VISIBLE)
    }

    private fun ManageChatRequest() {
        chatrequestref?.child(sender_user_id!!)?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild(reciever_id!!)) {
                    val reuest_type = dataSnapshot.child(reciever_id!!).child("request_type").value.toString()
                    if (reuest_type == "sent") {
                        current_state = "request_sent"
                        request_button?.setText("  Cancel Chat Request  ")
                    } else if (reuest_type == "received") {
                        current_state = "request_received"
                        request_button?.setText("  Accept Chat Request  ")
                        decline_button?.setVisibility(View.VISIBLE)
                        decline_button?.setEnabled(true)
                        decline_button?.setOnClickListener(View.OnClickListener { CancelChatRequest() })
                    }
                } else {
                    contactsRef?.child(sender_user_id!!)?.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.hasChild(reciever_id!!)) {
                                current_state = "friends"
                                request_button?.setText(" Remove this contact ")
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        request_button?.setOnClickListener(View.OnClickListener {
            request_button?.setEnabled(false)
            if (current_state == "new") {
                SendChatRequest()
            }
            if (current_state == "request_sent") {
                CancelChatRequest()
            }
            if (current_state == "request_received") {
                AcceptChatRequest()
            }
            if (current_state == "friends") {
                RemoveSpecificChatRequest()
            }
        })
    }

    private fun RemoveSpecificChatRequest() {
        contactsRef?.child(sender_user_id!!)?.child(reciever_id!!)?.removeValue()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        contactsRef?.child(reciever_id!!)?.child(sender_user_id!!)?.removeValue()!!
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        request_button?.setEnabled(true)
                                        request_button?.setText("  Send Request  ")
                                        current_state = "new"
                                        decline_button?.setVisibility(View.INVISIBLE)
                                        decline_button?.setEnabled(false)
                                    }
                                }
                    }
                }
    }

    private fun AcceptChatRequest() {
        contactsRef?.child(sender_user_id!!)?.child(reciever_id!!)?.child("Contacts")?.setValue("Saved")?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        contactsRef?.child(reciever_id!!)?.child(sender_user_id!!)?.child("Contacts")?.setValue("Saved")?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        chatrequestref?.child(sender_user_id!!)?.child(reciever_id!!)?.removeValue()?.addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        chatrequestref?.child(reciever_id!!)?.child(sender_user_id!!)?.removeValue()?.addOnCompleteListener {
                                                                    request_button?.setEnabled(true)
                                                                    current_state = "friends"
                                                                    request_button?.setText(" Remove this contact ")
                                                                    decline_button?.setVisibility(View.INVISIBLE)
                                                                    decline_button?.setEnabled(false)
                                                                }
                                                    }
                                                }
                                    }
                                }
                    }
                }
    }

    private fun CancelChatRequest() {
        chatrequestref?.child(sender_user_id!!)?.child(reciever_id!!)?.removeValue()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        chatrequestref?.child(reciever_id!!)!!.child(sender_user_id!!).removeValue()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        request_button?.setEnabled(true)
                                        request_button?.setText("  Send Request  ")
                                        current_state = "new"
                                        decline_button?.setVisibility(View.INVISIBLE)
                                        decline_button?.setEnabled(false)
                                    }
                                }
                    }
                }
    }

    private fun SendChatRequest() {
        chatrequestref?.child(sender_user_id!!)?.child(reciever_id!!)?.child("request_type")?.setValue("sent")?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        chatrequestref?.child(reciever_id!!)?.child(sender_user_id!!)!!.child("request_type").setValue("received")
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val chatnotificationMap = HashMap<String?, String?>()
                                        chatnotificationMap["from"] = sender_user_id
                                        chatnotificationMap["type"] = "request"
                                        NotificationRef!!.child(reciever_id!!).push().setValue(chatnotificationMap)
                                                .addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        request_button?.setEnabled(true)
                                                        current_state = "request_sent"
                                                        request_button?.setText("  Cancel Chat Request  ")
                                                    }
                                                }
                                    }
                                }
                    }
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {
                val imageUri = data?.getData()
                visit_profile_image?.setImageURI(imageUri)
                val query = "profilePictures/$reciever_id.png"
                val mountainImagesRef = FirebaseStorage.getInstance().getReference(query)
                mountainImagesRef.putFile(imageUri!!)
                val session = (this.application as GlobalSettings).session
                val db = FirebaseFirestore.getInstance()
                val dr = db.collection(BD.sessions_test.toString()).document(mauth?.getUid()!!)
                dr.update("storageImageRef", query).addOnSuccessListener { Log.d("ChatProfile-> ", "atualizado campo storageImageRef") }
                val map: HashMap<String, String> = HashMap<String,String>()
                map["image"] = query
                rootRef!!.updateChildren(map as Map<String, Any>).addOnSuccessListener { Log.d("ChatProfile-> ", "atualizado campo image rootref:" + rootRef?.child("image").toString()) }
            }
        }
    }
}