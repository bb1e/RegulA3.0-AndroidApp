package pt.pl.estg.ei.regulapp.chat

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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.FirebaseRecyclerAdapter
import pt.pl.estg.ei.regulapp.R.layout
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

class RequestsFragment : Fragment() {
    private var view1: View? = null
    private var recyclerView: RecyclerView? = null
    private var chatrequestRef: DatabaseReference? = null
    private var userref: DatabaseReference? = null
    private var contactref: DatabaseReference? = null
    private var teraRef: DatabaseReference? = null
    private var mauth: FirebaseAuth? = null
    private var currentUserId: String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        view1 = inflater.inflate(layout.fragment_requests, container, false)
        recyclerView = view1!!.findViewById(R.id.chat_request_recyclerview)
        recyclerView!!.setLayoutManager(LinearLayoutManager(context))
        userref = FirebaseDatabase.getInstance().reference.child("Users")
        teraRef = FirebaseDatabase.getInstance().reference.child("Terapeutas")
        chatrequestRef = FirebaseDatabase.getInstance().reference.child("Chat Requests")
        contactref = FirebaseDatabase.getInstance().reference.child("Contacts")
        mauth = FirebaseAuth.getInstance()
        currentUserId = (requireActivity().getApplication() as GlobalSettings).session!!.id
        return view1
    }

    override fun onStart() {
        super.onStart()
        val options: FirebaseRecyclerOptions<Contacts?> = FirebaseRecyclerOptions.Builder<Contacts?>().setQuery(chatrequestRef!!.child(currentUserId!!), Contacts::class.java as Class<Contacts?>)
                .build()
        val adapter: FirebaseRecyclerAdapter<Contacts?, RequestViewHolder?> = object : FirebaseRecyclerAdapter<Contacts?, RequestViewHolder?>(options) {
            override fun onBindViewHolder(holder: RequestViewHolder, position: Int, model: Contacts) {
                val aceitar = holder.itemView.findViewById<Button?>(R.id.request_accept_button)
                aceitar.visibility = View.VISIBLE
                aceitar.setOnClickListener { Toast.makeText(context, "aceitei", Toast.LENGTH_LONG) }
                holder.itemView.findViewById<View?>(R.id.request_cancel_button).visibility = View.VISIBLE
                val userId = getRef(position).key
                val getTypeRef = getRef(position).child("request_type").ref
                getTypeRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            val type = dataSnapshot.value.toString()
                            if (type == "received") {
                                userref!!.child(userId!!).get().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        if (task.result!!.value != null) {
                                            userref!!.child(userId).addValueEventListener(object : ValueEventListener {
                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    if (snapshot.hasChild("image")) {
                                                        val requestimage = snapshot.child("image").value.toString()
                                                        Glide.with(this@RequestsFragment).load(requestimage).signature(MediaStoreSignature("",
                                                                System.currentTimeMillis(), 0)).error(R.drawable.default_profile_picture).into(holder.profilepicture!!)
                                                    } else Glide.with(this@RequestsFragment).load(R.drawable.default_profile_picture).into(holder.profilepicture!!)
                                                    val requestusername = snapshot.child("name").value.toString()
                                                    holder.username!!.setText(requestusername)
                                                    holder.itemView.findViewById<View?>(R.id.request_accept_button).setOnClickListener {
                                                        contactref!!.child(currentUserId!!).child(userId).child("Contacts")
                                                                .setValue("Saved")
                                                                .addOnCompleteListener { task ->
                                                                    if (task.isSuccessful) {
                                                                        contactref!!.child(userId).child(currentUserId!!).child("Contacts")
                                                                                .setValue("Saved")
                                                                                .addOnCompleteListener { task ->
                                                                                    if (task.isSuccessful) {
                                                                                        chatrequestRef!!.child(currentUserId!!).child(userId).removeValue().addOnCompleteListener { task ->
                                                                                            if (task.isSuccessful) {
                                                                                                chatrequestRef!!.child(userId).child(currentUserId!!).removeValue().addOnCompleteListener { task ->
                                                                                                    if (task.isSuccessful) {
                                                                                                        Toast.makeText(context, "Novo contacto salvo", Toast.LENGTH_SHORT).show()
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                    }
                                                                }
                                                    }
                                                    holder.itemView.findViewById<View?>(R.id.request_cancel_button).setOnClickListener {
                                                        chatrequestRef!!.child(currentUserId!!).child(userId).removeValue().addOnCompleteListener { task ->
                                                            if (task.isSuccessful) {
                                                                chatrequestRef!!.child(userId).child(currentUserId!!).removeValue().addOnCompleteListener { task ->
                                                                    if (task.isSuccessful) {
                                                                        Toast.makeText(context, "Request Deleted", Toast.LENGTH_SHORT).show()
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    holder.itemView.setOnClickListener {
                                                        val options = arrayOf<CharSequence?>(
                                                                "Aceitar", "Rejeitar"
                                                        )
                                                        val builder = AlertDialog.Builder(context!!)
                                                        builder.setTitle("Pedido de amizade de: $requestusername")
                                                        builder.setItems(options) { dialog, which ->
                                                            if (which == 0) {
                                                                contactref!!.child(currentUserId!!).child(userId).child("Contacts")
                                                                        .setValue("Saved")
                                                                        .addOnCompleteListener { task ->
                                                                            if (task.isSuccessful) {
                                                                                contactref!!.child(userId).child(currentUserId!!).child("Contacts")
                                                                                        .setValue("Saved")
                                                                                        .addOnCompleteListener { task ->
                                                                                            if (task.isSuccessful) {
                                                                                                chatrequestRef!!.child(currentUserId!!).child(userId).removeValue().addOnCompleteListener { task ->
                                                                                                    if (task.isSuccessful) {
                                                                                                        chatrequestRef!!.child(userId).child(currentUserId!!).removeValue().addOnCompleteListener { task ->
                                                                                                            if (task.isSuccessful) {
                                                                                                                Toast.makeText(context, "Novo contacto salvo", Toast.LENGTH_SHORT).show()
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                            }
                                                                        }
                                                            }
                                                            if (which == 1) {
                                                                chatrequestRef!!.child(currentUserId!!).child(userId).removeValue().addOnCompleteListener { task ->
                                                                    if (task.isSuccessful) {
                                                                        chatrequestRef!!.child(userId).child(currentUserId!!).removeValue().addOnCompleteListener { task ->
                                                                            if (task.isSuccessful) {
                                                                                Toast.makeText(context, "Request Deleted", Toast.LENGTH_SHORT).show()
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        builder.show()
                                                    }
                                                }

                                                override fun onCancelled(error: DatabaseError) {}
                                            })
                                        } else {
                                            teraRef!!.child(userId).addValueEventListener(object : ValueEventListener {
                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    if (snapshot.hasChild("image")) {
                                                        val requestimage = snapshot.child("image").value.toString()
                                                        Glide.with(this@RequestsFragment).load(requestimage).signature(MediaStoreSignature("",
                                                                System.currentTimeMillis(), 0)).error(R.drawable.doctor).into(holder.profilepicture!!)
                                                    }
                                                    val requestusername = snapshot.child("name").value.toString()
                                                    holder.username!!.setText(requestusername)
                                                    holder.itemView.findViewById<View?>(R.id.request_accept_button).setOnClickListener {
                                                        contactref!!.child(currentUserId!!).child(userId).child("Contacts")
                                                                .setValue("Saved")
                                                                .addOnCompleteListener { task ->
                                                                    if (task.isSuccessful) {
                                                                        contactref!!.child(userId).child(currentUserId!!).child("Contacts")
                                                                                .setValue("Saved")
                                                                                .addOnCompleteListener { task ->
                                                                                    if (task.isSuccessful) {
                                                                                        chatrequestRef!!.child(currentUserId!!).child(userId).removeValue().addOnCompleteListener { task ->
                                                                                            if (task.isSuccessful) {
                                                                                                chatrequestRef!!.child(userId).child(currentUserId!!).removeValue().addOnCompleteListener { task ->
                                                                                                    if (task.isSuccessful) {
                                                                                                        Toast.makeText(context, "Novo contacto salvo", Toast.LENGTH_SHORT).show()
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                    }
                                                                }
                                                    }
                                                    holder.itemView.findViewById<View?>(R.id.request_cancel_button).setOnClickListener {
                                                        chatrequestRef!!.child(currentUserId!!).child(userId).removeValue().addOnCompleteListener { task ->
                                                            if (task.isSuccessful) {
                                                                chatrequestRef!!.child(userId).child(currentUserId!!).removeValue().addOnCompleteListener { task ->
                                                                    if (task.isSuccessful) {
                                                                        Toast.makeText(context, "Request Deleted", Toast.LENGTH_SHORT).show()
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    holder.itemView.setOnClickListener {
                                                        val options = arrayOf<CharSequence?>(
                                                                "Aceitar", "Rejeitar"
                                                        )
                                                        val builder = AlertDialog.Builder(context!!)
                                                        builder.setTitle("Pedido de amizade de: $requestusername")
                                                        builder.setItems(options) { dialog, which ->
                                                            if (which == 0) {
                                                                contactref!!.child(currentUserId!!).child(userId).child("Contacts")
                                                                        .setValue("Saved")
                                                                        .addOnCompleteListener { task ->
                                                                            if (task.isSuccessful) {
                                                                                contactref!!.child(userId).child(currentUserId!!).child("Contacts")
                                                                                        .setValue("Saved")
                                                                                        .addOnCompleteListener { task ->
                                                                                            if (task.isSuccessful) {
                                                                                                chatrequestRef!!.child(currentUserId!!).child(userId).removeValue().addOnCompleteListener { task ->
                                                                                                    if (task.isSuccessful) {
                                                                                                        chatrequestRef!!.child(userId).child(currentUserId!!).removeValue().addOnCompleteListener { task ->
                                                                                                            if (task.isSuccessful) {
                                                                                                                Toast.makeText(context, "Novo contacto salvo", Toast.LENGTH_SHORT).show()
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                            }
                                                                        }
                                                            }
                                                            if (which == 1) {
                                                                chatrequestRef!!.child(currentUserId!!).child(userId).removeValue().addOnCompleteListener { task ->
                                                                    if (task.isSuccessful) {
                                                                        chatrequestRef!!.child(userId).child(currentUserId!!).removeValue().addOnCompleteListener { task ->
                                                                            if (task.isSuccessful) {
                                                                                Toast.makeText(context, "Request Deleted", Toast.LENGTH_SHORT).show()
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        builder.show()
                                                    }
                                                }

                                                override fun onCancelled(error: DatabaseError) {}
                                            })
                                        }
                                    }
                                }
                                userref!!.child(userId).get().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        if (task.result!!.getValue() != null) userref!!.child(userId).addValueEventListener(object : ValueEventListener {
                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                if (dataSnapshot.hasChild("image")) {
                                                    val requestimage = dataSnapshot.child("image").value.toString()
                                                    Glide.with(context!!).load(requestimage).signature(MediaStoreSignature("",
                                                            System.currentTimeMillis(), 0)).error(R.drawable.default_profile_picture).into(holder.profilepicture!!)
                                                }
                                                val requestusername = dataSnapshot.child("name").value.toString()
                                                holder.username!!.setText(requestusername)
                                                Toast.makeText(context, "Envias te um pedido de amizade a$requestusername", Toast.LENGTH_LONG)
                                                holder.itemView.setOnClickListener {
                                                    val options = arrayOf<CharSequence?>(
                                                            "Cancelar pedido de amizade"
                                                    )
                                                    val builder = AlertDialog.Builder(context!!)
                                                    builder.setTitle("Já envias-te um pedido")
                                                    builder.setItems(options) { dialog, which ->
                                                        if (which == 0) {
                                                            chatrequestRef!!.child(currentUserId!!).child(userId).removeValue().addOnCompleteListener { task ->
                                                                if (task.isSuccessful) {
                                                                    chatrequestRef!!.child(userId).child(currentUserId!!).removeValue().addOnCompleteListener { task ->
                                                                        if (task.isSuccessful) {
                                                                            Toast.makeText(context, "Cancelas te o pedido de amizade", Toast.LENGTH_SHORT).show()
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    builder.show()
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {}
                                        }) else teraRef!!.child(userId).addValueEventListener(object : ValueEventListener {
                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                if (dataSnapshot.hasChild("image")) {
                                                    val requestimage = dataSnapshot.child("image").value.toString()
                                                    Glide.with(context!!).load(requestimage).signature(MediaStoreSignature("",
                                                            System.currentTimeMillis(), 0)).error(R.drawable.default_profile_picture).into(holder.profilepicture!!)
                                                }
                                                val requestusername = dataSnapshot.child("name").value.toString()
                                                holder.username!!.setText(requestusername)
                                                Toast.makeText(context, "Envias te um pedido de amizade a$requestusername", Toast.LENGTH_LONG)
                                                holder.itemView.setOnClickListener {
                                                    val options = arrayOf<CharSequence?>(
                                                            "Cancelar pedido de amizade"
                                                    )
                                                    val builder = AlertDialog.Builder(context!!)
                                                    builder.setTitle("Já envias-te um pedido")
                                                    builder.setItems(options) { dialog, which ->
                                                        if (which == 0) {
                                                            chatrequestRef!!.child(currentUserId!!).child(userId).removeValue().addOnCompleteListener { task ->
                                                                if (task.isSuccessful) {
                                                                    chatrequestRef!!.child(userId).child(currentUserId!!).removeValue().addOnCompleteListener { task ->
                                                                        if (task.isSuccessful) {
                                                                            Toast.makeText(context, "Cancelas te o pedido de amizade", Toast.LENGTH_SHORT).show()
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    builder.show()
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {}
                                        })
                                    }
                                }
                            } else if (type == "sent") {
                                val request_sent_btn = holder.itemView.findViewById<Button?>(R.id.request_accept_button)
                                request_sent_btn.text = "Cancelar"
                                request_sent_btn.setOnClickListener {
                                    chatrequestRef!!.child(currentUserId!!).child(userId!!).removeValue().addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            chatrequestRef!!.child(userId!!).child(currentUserId!!).removeValue().addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    Toast.makeText(context, "Foi cancelado o pedido ", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    }
                                }
                                holder.itemView.findViewById<View?>(R.id.request_cancel_button).visibility = View.INVISIBLE
                                Log.d("TAG", "onDataChange: " + userref!!.child(userId!!).key)
                                userref!!.child(userId!!).get().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        if (task.result!!.getValue() != null) userref!!.child(userId).addValueEventListener(object : ValueEventListener {
                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                if (dataSnapshot.hasChild("image")) {
                                                    val requestimage = dataSnapshot.child("image").value.toString()
                                                    Glide.with(context!!).load(requestimage).signature(MediaStoreSignature("",
                                                            System.currentTimeMillis(), 0)).error(R.drawable.default_profile_picture).into(holder.profilepicture!!)
                                                }
                                                val requestusername = dataSnapshot.child("name").value.toString()
                                                holder.username!!.setText(requestusername)
                                                Toast.makeText(context, "Envias te um pedido de amizade a$requestusername", Toast.LENGTH_LONG)
                                                holder.itemView.setOnClickListener {
                                                    val options = arrayOf<CharSequence?>(
                                                            "Cancelar pedido de amizade"
                                                    )
                                                    val builder = AlertDialog.Builder(context!!)
                                                    builder.setTitle("Já envias-te um pedido")
                                                    builder.setItems(options) { dialog, which ->
                                                        if (which == 0) {
                                                            chatrequestRef!!.child(currentUserId!!).child(userId).removeValue().addOnCompleteListener { task ->
                                                                if (task.isSuccessful) {
                                                                    chatrequestRef!!.child(userId).child(currentUserId!!).removeValue().addOnCompleteListener { task ->
                                                                        if (task.isSuccessful) {
                                                                            Toast.makeText(context, "Cancelas te o pedido de amizade", Toast.LENGTH_SHORT).show()
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    builder.show()
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {}
                                        }) else teraRef!!.child(userId).addValueEventListener(object : ValueEventListener {
                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                if (dataSnapshot.hasChild("image")) {
                                                    val requestimage = dataSnapshot.child("image").value.toString()
                                                    Glide.with(context!!).load(requestimage).signature(MediaStoreSignature("",
                                                            System.currentTimeMillis(), 0)).error(R.drawable.default_profile_picture).into(holder.profilepicture!!)
                                                }
                                                val requestusername = dataSnapshot.child("name").value.toString()
                                                holder.username!!.setText(requestusername)
                                                Toast.makeText(context, "Envias te um pedido de amizade a$requestusername", Toast.LENGTH_LONG)
                                                holder.itemView.setOnClickListener {
                                                    val options = arrayOf<CharSequence?>(
                                                            "Cancelar pedido de amizade"
                                                    )
                                                    val builder = AlertDialog.Builder(context!!)
                                                    builder.setTitle("Já envias-te um pedido")
                                                    builder.setItems(options) { dialog, which ->
                                                        if (which == 0) {
                                                            chatrequestRef!!.child(currentUserId!!).child(userId).removeValue().addOnCompleteListener { task ->
                                                                if (task.isSuccessful) {
                                                                    chatrequestRef!!.child(userId).child(currentUserId!!).removeValue().addOnCompleteListener { task ->
                                                                        if (task.isSuccessful) {
                                                                            Toast.makeText(context, "Cancelas te o pedido de amizade", Toast.LENGTH_SHORT).show()
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    builder.show()
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {}
                                        })
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(layout.users_display_layout, parent, false)
                return RequestViewHolder(view)
            }
        }
        recyclerView!!.setAdapter(adapter)
        adapter.startListening()
    }

    class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var username: TextView?
        var profilepicture: CircularImageView?
        var accept_button: Button?
        var reject_button: Button?

        init {
            username = itemView.findViewById(R.id.users_profile_name)
            profilepicture = itemView.findViewById(R.id.users_profile_image)
            accept_button = itemView.findViewById(R.id.request_accept_button)
            reject_button = itemView.findViewById(R.id.request_cancel_button)


            //imageView=itemView.findViewById(R.id.users_online_status);
        }
    }
}