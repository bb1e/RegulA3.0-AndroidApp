package pt.pl.estg.ei.regulapp.chat

import android.widget.TextView
import com.mikhaellopez.circularimageview.CircularImageView
import android.widget.ImageButton
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.StorageTask
import android.app.ProgressDialog
import android.os.Bundle
import pt.pl.estg.ei.regulapp.R
import pt.pl.estg.ei.regulapp.GlobalSettings
import com.google.firebase.database.FirebaseDatabase
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.MediaStoreSignature
import android.content.Intent
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.storage.FirebaseStorage
import android.widget.Toast
import com.google.firebase.database.ValueEventListener
import android.text.TextUtils
import android.app.AlertDialog
import pt.pl.estg.ei.regulapp.R.layout
import android.net.Uri
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.*
import com.google.firebase.storage.UploadTask
import pt.pl.estg.ei.regulapp.classes.Analytics
import pt.pl.estg.ei.regulapp.classes.AnalyticsEvent
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ChatActivity : AppCompatActivity() {
    private var messageRecieverId: String? = null
    private var getMessageRecievername: String? = null
    private var messagereceiverimage: String? = null
    private var messageSenderId: String? = null
    private var username: TextView? = null
    private var userlastseen: TextView? = null
    private var userprofile: CircularImageView? = null
    private var chattoolbar: Toolbar? = null
    private var sendMessageButton: ImageButton? = null
    private var sendFileButton: ImageButton? = null
    private var messagesentinput: EditText? = null
    private var mauth: FirebaseAuth? = null
    private var RootRef: DatabaseReference? = null
    private val messagesList: MutableList<Messages?>? = ArrayList()
    private var linearLayoutManager: LinearLayoutManager? = null
    private var messageAdapter: MessageAdapter? = null
    private var usermessagerecyclerview: RecyclerView? = null
    private var savecurrentTime: String? = null
    private var savecurrentDate: String? = null
    private var checker: String? = ""
    private var myUrl: String? = ""
    private var uploadTask: StorageTask<*>? = null
    private var fileuri: Uri? = null
    private var loadingBar: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_chat)
        loadingBar = ProgressDialog(this)
        mauth = FirebaseAuth.getInstance()
        messageSenderId = (application as GlobalSettings).session?.id
        RootRef = FirebaseDatabase.getInstance().reference
        messageRecieverId = intent.extras?.get("visit_user_id").toString()
        getMessageRecievername = intent.extras?.get("visit_user_name").toString()
        messagereceiverimage = intent.extras?.get("visit_image").toString()
        chattoolbar = findViewById(R.id.chat_toolbar)
        setSupportActionBar(chattoolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowCustomEnabled(true)
            val layoutInflater = this.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val actionbarview = layoutInflater.inflate(layout.custom_chat_bar, null)
            actionBar.customView = actionbarview
        }
        username = findViewById(R.id.custom_profile_name)
        userlastseen = findViewById(R.id.custom_user_last_seen)
        userprofile = findViewById(R.id.custom_profile_image)
        sendMessageButton = findViewById(R.id.send_message_btn)
        sendFileButton = findViewById(R.id.send_files_btn)
        userlastseen?.setVisibility(View.GONE)
        messagesentinput = findViewById(R.id.input_messages)
        messageAdapter = MessageAdapter(messagesList, messageSenderId)
        usermessagerecyclerview = findViewById(R.id.private_message_list_of_users)
        linearLayoutManager = LinearLayoutManager(this)
        usermessagerecyclerview?.setLayoutManager(linearLayoutManager)
        usermessagerecyclerview?.setAdapter(messageAdapter)
        val calendar = Calendar.getInstance()
        val currentDate = SimpleDateFormat("dd/MM/yyyy")
        savecurrentDate = currentDate.format(calendar.time)
        val currentTime = SimpleDateFormat("hh:mm a")
        savecurrentTime = currentTime.format(calendar.time)
        username?.setText(getMessageRecievername)
        Glide.with(this@ChatActivity).load(messagereceiverimage).signature(MediaStoreSignature("",
                System.currentTimeMillis(), 0)).error(R.drawable.doctor).into(userprofile!!)
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.vermelho_chat)
        Displaylastseen()
        sendMessageButton?.setOnClickListener(View.OnClickListener { SendMessage() })
        sendFileButton?.setOnClickListener(View.OnClickListener {
            val options = arrayOf<CharSequence?>(
                    "Imagens", "Ficheiros PDF", "Ficheiros Word"
            )
            val builder = AlertDialog.Builder(this@ChatActivity)
            builder.setTitle("Escolher ficheiro")
            builder.setItems(options) { dialog, which ->
                if (which == 0) {
                    checker = "image"
                    val intent = Intent()
                    intent.action = Intent.ACTION_GET_CONTENT
                    intent.type = "image/*"
                    startActivityForResult(Intent.createChooser(intent, "Escolher imagem"), 555)
                } else if (which == 1) {
                    checker = "pdf"
                    val intent = Intent()
                    intent.action = Intent.ACTION_GET_CONTENT
                    intent.type = "application/pdf"
                    startActivityForResult(Intent.createChooser(intent, "Escolher ficheiro PDF"), 555)
                } else if (which == 2) {
                    checker = "docx"
                    val intent = Intent()
                    intent.action = Intent.ACTION_GET_CONTENT
                    intent.type = "application/msword"
                    startActivityForResult(Intent.createChooser(intent, "Escolher ficheiro Word"), 555)
                }
            }
            builder.show()
        })
        RootRef?.child("Messages")?.child(messageSenderId!!)?.child(messageRecieverId!!)?.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val messages = dataSnapshot.getValue(Messages::class.java)
                messagesList?.add(messages)
                messageAdapter?.notifyDataSetChanged()
                usermessagerecyclerview?.smoothScrollToPosition(usermessagerecyclerview?.getAdapter()?.getItemCount()!!)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 555 && resultCode == RESULT_OK && data != null && data.data != null) {
            loadingBar?.setTitle("Sending File")
            loadingBar?.setMessage("please wait, we are sending that file...")
            loadingBar?.setCanceledOnTouchOutside(false)
            loadingBar?.show()
            fileuri = data.data
            if (checker != "image") {
                val storageReference = FirebaseStorage.getInstance().reference.child("Document Files")
                val messageSenderRef = "Messages/$messageSenderId/$messageRecieverId"
                val messageReceiverRef = "Messages/$messageRecieverId/$messageSenderId"
                val Usermessagekeyref = RootRef?.child("Messages")?.child(messageSenderId!!)?.child(messageRecieverId!!)?.push()!!
                val messagePushID = Usermessagekeyref.key
                val filepath = storageReference.child("$messagePushID.$checker")
                filepath.putFile(fileuri!!).addOnSuccessListener {
                    filepath.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        val messageDocsBody: MutableMap<String, String> = HashMap<String, String>()
                        messageDocsBody["message"] = downloadUrl
                        messageDocsBody["name"] = fileuri?.getLastPathSegment()!!
                        messageDocsBody["type"] = checker!!
                        messageDocsBody["from"] = messageSenderId!!
                        messageDocsBody["to"] = messageRecieverId!!
                        messageDocsBody["messageID"] = messagePushID!!
                        messageDocsBody["time"] = savecurrentTime!!
                        messageDocsBody["date"] = savecurrentDate!!
                        val messageBodyDDetail: MutableMap<String,MutableMap<String,String>> = HashMap<String, MutableMap<String,String>>()
                        messageBodyDDetail["${messageSenderRef}/${messagePushID}"] = messageDocsBody
                        messageBodyDDetail["$messageReceiverRef/$messagePushID"] = messageDocsBody
                        RootRef?.updateChildren(messageBodyDDetail as Map<String, Any>)
                        loadingBar?.dismiss()
                    }.addOnFailureListener { e ->
                        loadingBar?.dismiss()
                        Toast.makeText(this@ChatActivity, e.message, Toast.LENGTH_SHORT).show()
                    }
                }.addOnProgressListener { taskSnapshot ->
                    val p = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                    loadingBar?.setMessage((p as Int).toString() + " % Uploading...")
                }
            } else if (checker == "image") {
                val storageReference = FirebaseStorage.getInstance().reference.child("Image Files")
                val messageSenderRef = "Messages/$messageSenderId/$messageRecieverId"
                val messageReceiverRef = "Messages/$messageRecieverId/$messageSenderId"
                val Usermessagekeyref = RootRef?.child("Messages")?.child(messageSenderId!!)?.child(messageRecieverId!!)?.push()
                val messagePushID = Usermessagekeyref?.key
                val filepath = storageReference.child("$messagePushID.jpg")
                uploadTask = filepath.putFile(fileuri!!)
                (uploadTask as UploadTask).continueWithTask { continuation -> if(!continuation.isSuccessful){
                    throw continuation.exception!!
                } else {
                    filepath.downloadUrl
                }
                } .addOnCompleteListener(OnCompleteListener<Uri?> { task ->
                    if (task.isSuccessful) {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()
                        val messageTextBody: MutableMap<String,String > = HashMap<String,String>()
                        messageTextBody["message"] = myUrl!!
                        messageTextBody["name"] = fileuri?.getLastPathSegment()!!
                        messageTextBody["type"] = checker!!
                        messageTextBody["from"] = messageSenderId!!
                        messageTextBody["to"] = messageRecieverId!!
                        messageTextBody["messageID"] = messagePushID!!
                        messageTextBody["time"] = savecurrentTime!!
                        messageTextBody["date"] = savecurrentDate!!
                        val messageBodyDetails: MutableMap<String,MutableMap<String,String>> = HashMap<String,MutableMap<String,String>>()
                        messageBodyDetails["$messageSenderRef/$messagePushID"] = messageTextBody
                        messageBodyDetails["$messageReceiverRef/$messagePushID"] = messageTextBody
                        RootRef?.updateChildren(messageBodyDetails as Map<String, Any>)?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                loadingBar?.dismiss()
                                //Toast.makeText(ChatActivity.this,"Message sent Successfully...",Toast.LENGTH_SHORT).show();
                            } else {
                                loadingBar?.dismiss()
                                Toast.makeText(this@ChatActivity, "Error:", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            messagesentinput?.setText("")
                        }
                    }
                })
            } else {
                loadingBar?.dismiss()
                Toast.makeText(this, "please select file", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun Displaylastseen() {
        RootRef?.child("Users")?.child(messageRecieverId!!)?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child("userState").hasChild("state")) {
                    val state = dataSnapshot.child("userState").child("state").value.toString()
                    //String date=dataSnapshot.child("userState").child("date").getValue().toString();
                    //String time=dataSnapshot.child("userState").child("time").getValue().toString();

                    /*if(state.equals("online"))
                    {
                        userlastseen.setText("online");
                    }
                    else if(state.equals("offline"))
                    {
                        userlastseen.setText("offline");
                    }*/
                } else {
//                    userlastseen.setText("offline");
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun SendMessage() {
        val messagetext = messagesentinput?.getText().toString()
        if (TextUtils.isEmpty(messagetext)) {
            Toast.makeText(this, "Please enter message first..", Toast.LENGTH_SHORT).show()
        } else {
            val messageSenderRef = "Messages/$messageSenderId/$messageRecieverId"
            val messageReceiverRef = "Messages/$messageRecieverId/$messageSenderId"
            val Usermessagekeyref = RootRef?.child("Messages")?.child(messageSenderId!!)?.child(messageRecieverId!!)?.push()
            val messagePushID = Usermessagekeyref?.key
            val messageTextBody: MutableMap<String,String> = HashMap<String,String>()
            messageTextBody["message"] = messagetext
            messageTextBody["type"] = "text"
            messageTextBody["from"] = messageSenderId!!
            messageTextBody["to"] = messageRecieverId!!
            messageTextBody["messageID"] = messagePushID!!
            messageTextBody["time"] = savecurrentTime!!
            messageTextBody["date"] = savecurrentDate!!
            val messageBodyDetails: MutableMap<String, Map<String,String>> = HashMap<String,Map<String,String>>()
            messageBodyDetails["$messageSenderRef/$messagePushID"] = messageTextBody
            messageBodyDetails["$messageReceiverRef/$messagePushID"] = messageTextBody
            RootRef?.updateChildren(messageBodyDetails as Map<String, Any>)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Toast.makeText(ChatActivity.this,"Message sent Successfully...",Toast.LENGTH_SHORT).show();
                    Analytics.fireEvent(AnalyticsEvent.CHAT_MESSAGE_SUCCESS)
                } else {
                    Toast.makeText(this@ChatActivity, "Error:", Toast.LENGTH_SHORT).show()
                    Analytics.fireEvent(AnalyticsEvent.CHAT_MESSAGE_FAILED(task.exception.toString()))
                }
                messagesentinput?.setText("")
            }
        }
    }
}