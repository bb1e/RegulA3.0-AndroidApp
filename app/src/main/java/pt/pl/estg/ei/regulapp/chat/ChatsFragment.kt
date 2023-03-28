package pt.pl.estg.ei.regulapp.chat

import android.widget.TextView
import com.mikhaellopez.circularimageview.CircularImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import pt.pl.estg.ei.regulapp.R
import pt.pl.estg.ei.regulapp.GlobalSettings
import com.google.firebase.database.FirebaseDatabase
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.MediaStoreSignature
import android.content.Intent
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import org.ocpsoft.prettytime.PrettyTime
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.FirebaseRecyclerAdapter
import pt.pl.estg.ei.regulapp.R.layout
import android.view.*
import androidx.fragment.app.Fragment
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ChatsFragment : Fragment() {
    private var view1: View? = null
    private var recyclerView: RecyclerView? = null
    private var chatRef: DatabaseReference? = null
    private var userRef: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null
    private var userID: String? = null
    private val p: PrettyTime = PrettyTime(Locale("pt"))
    private var terapeutasRef: DatabaseReference? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        view1 = inflater.inflate(layout.fragment_chats, container, false)
        mAuth = FirebaseAuth.getInstance()
        userID = (activity?.application as GlobalSettings).session?.id
        chatRef = FirebaseDatabase.getInstance().reference.child("Contacts").child(userID!!)
        userRef = FirebaseDatabase.getInstance().reference.child("Users")
        terapeutasRef = FirebaseDatabase.getInstance().reference.child("Terapeutas")
        recyclerView = view1?.findViewById(R.id.chats_list)
        recyclerView?.setLayoutManager(LinearLayoutManager(context))
        return view1
    }

    override fun onStart() {
        super.onStart()
        val options: FirebaseRecyclerOptions<Contacts?> = FirebaseRecyclerOptions.Builder<Contacts?>().setQuery(chatRef!!, Contacts::class.java as Class<Contacts?>).build()
        val adapter: FirebaseRecyclerAdapter<Contacts?, ChatViewHolder?> = object : FirebaseRecyclerAdapter<Contacts?, ChatViewHolder?>(options) {
            override fun onBindViewHolder(holder: ChatViewHolder, position: Int, model: Contacts) {
                val userid = getRef(position).key
                val image = arrayOf<String?>("default_image")
                userRef?.child(userid!!)?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild("image")) {
                                val string = dataSnapshot.child("image").value.toString()
                                Glide.with(context!!).load(string).signature(MediaStoreSignature("",
                                        System.currentTimeMillis(), 0)).error(R.drawable.perfil3).into(holder.profile_image!!)
                            }
                            val name = dataSnapshot.child("name").value.toString()
                            holder.username?.setText(name)
                            holder.userstatus?.setText("""
    Last seen: 
    Date  Time
    """.trimIndent())
                            if (dataSnapshot.child("userState").hasChild("state")) {
                                val state = dataSnapshot.child("userState").child("state").value.toString()
                                val date = dataSnapshot.child("userState").child("date").value.toString()
                                val time = dataSnapshot.child("userState").child("time").value.toString()
                                val patr = SimpleDateFormat("dd/MM/yyyyhh:mm a")
                                var dateLastSeen: Date? = null
                                try {
                                    dateLastSeen = patr.parse(date + time)
                                } catch (e: ParseException) {
                                    e.printStackTrace()
                                    FirebaseCrashlytics.getInstance().recordException(e); // log it to Crashalytics despite catching the exception
                                }
                                p?.setReference(Date())
                                if (state == "online") {
                                    holder.userstatus?.setText("")
                                } else if (state == "offline") {
                                    holder.userstatus?.setText(p?.format(dateLastSeen))
                                    holder.userstatus?.setTextSize(8f)
                                }
                            } else {
                                holder.userstatus?.setText("offline")
                            }
                            holder.itemView.setOnClickListener {
                                val chatIntent = Intent(context, ChatActivity::class.java)
                                chatIntent.putExtra("visit_user_id", userid)
                                chatIntent.putExtra("visit_user_name", name)
                                chatIntent.putExtra("visit_image", image[0])
                                startActivity(chatIntent)
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
                terapeutasRef?.child(userid!!)?.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild("image")) {
                                val string = dataSnapshot.child("image").value.toString()
                                Glide.with(context!!).load(string).signature(MediaStoreSignature("",
                                        System.currentTimeMillis(), 0)).error(R.drawable.doctor).into(holder.profile_image!!)
                            }
                            if (dataSnapshot.hasChild("name")) {
                                holder.username?.setText("name")
                            }
                            val name = dataSnapshot.child("name").value.toString()
                            holder.username?.setText(name)
                            holder.userstatus?.setText("""
    Last seen: 
    Date  Time
    """.trimIndent())
                            if (dataSnapshot.child("userState").hasChild("state")) {
                                val state = dataSnapshot.child("userState").child("state").value.toString()
                                val date = dataSnapshot.child("userState").child("date").value.toString()
                                val time = dataSnapshot.child("userState").child("time").value.toString()
                                val patr = SimpleDateFormat("dd/MM/yyyyhh:mm a")
                                var dateLastSeen: Date? = null
                                try {
                                    dateLastSeen = patr.parse(date + time)
                                } catch (e: ParseException) {
                                    e.printStackTrace()
                                    FirebaseCrashlytics.getInstance().recordException(e); // log it to Crashalytics despite catching the exception
                                }
                                p?.setReference(Date())
                                if (state == "online") {
                                    holder.userstatus?.setText("")
                                } else if (state == "offline") {
                                    holder.userstatus?.setText(p?.format(dateLastSeen))
                                    holder.userstatus?.setTextSize(8f)
                                }
                            } else {
                                holder.userstatus?.setText("offline")
                            }
                            holder.itemView.setOnClickListener {
                                val chatIntent = Intent(context, ChatActivity::class.java)
                                chatIntent.putExtra("visit_user_id", userid)
                                chatIntent.putExtra("visit_user_name", name)
                                chatIntent.putExtra("visit_image", image[0])
                                startActivity(chatIntent)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
                val view = LayoutInflater.from(context).inflate(layout.users_display_layout, parent, false)
                return ChatViewHolder(view)
            }
        }
        recyclerView?.setAdapter(adapter)
        adapter.startListening()
    }

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profile_image: CircularImageView?
        var username: TextView?
        var userstatus: TextView?

        init {
            profile_image = itemView.findViewById(R.id.users_profile_image)
            username = itemView.findViewById(R.id.users_profile_name)
            userstatus = itemView.findViewById(R.id.status)
        }
    }
}