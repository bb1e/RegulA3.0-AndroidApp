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
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment

/**
 * A simple [Fragment] subclass.
 */
class ContactsFragment : Fragment() {
    private var view1: View? = null
    private var myrecyclerview: RecyclerView? = null
    private var contactsRef: DatabaseReference? = null
    private var userRef: DatabaseReference? = null
    private var mauth: FirebaseAuth? = null
    private var currentUserId: String? = null
    private var terapeutasRef: DatabaseReference? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        view1 = inflater.inflate(layout.fragment_contacts, container, false)
        myrecyclerview = view?.findViewById(R.id.contacts_recyclerview_list)
        myrecyclerview?.setLayoutManager(LinearLayoutManager(context))
        mauth = FirebaseAuth.getInstance()
        currentUserId = (activity?.getApplication() as GlobalSettings).session?.id
        contactsRef = FirebaseDatabase.getInstance().reference.child("Contacts").child(currentUserId!!)
        userRef = FirebaseDatabase.getInstance().reference.child("Users")
        terapeutasRef = FirebaseDatabase.getInstance().reference.child("Terapeutas")
        return view1
    }

    override fun onStart() {
        super.onStart()
        val options: FirebaseRecyclerOptions<Contacts?> = FirebaseRecyclerOptions.Builder<Contacts?>()
                .setQuery(contactsRef!!, Contacts::class.java as Class<Contacts?>)
                .build()
        val adapter: FirebaseRecyclerAdapter<Contacts?, ContactsViewHolder?> = object : FirebaseRecyclerAdapter<Contacts?, ContactsViewHolder?>(options) {
            override fun onBindViewHolder(holder: ContactsViewHolder, position: Int, model: Contacts) {
                val userId = getRef(position).key
                userRef?.child(userId!!)?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.child("userState").hasChild("state")) {
                                val state = dataSnapshot.child("userState").child("state").value.toString()
                                val date = dataSnapshot.child("userState").child("date").value.toString()
                                val time = dataSnapshot.child("userState").child("time").value.toString()
                            } else {
                                holder.onlineIcon?.setVisibility(View.INVISIBLE)
                            }
                            if (dataSnapshot.hasChild("image")) {
                                val image = dataSnapshot.child("image").value.toString()
                                val name = dataSnapshot.child("name").value.toString()
                                holder.username?.setText(name)
                                Glide.with(this@ContactsFragment).load(image).signature(MediaStoreSignature("",
                                        System.currentTimeMillis(), 0)).error(R.drawable.default_profile_picture).into(holder.profilepicture!!)
                            } else {
                                val name = dataSnapshot.child("name").value.toString()
                                holder.username?.setText(name)
                            }
                        } else {
                            terapeutasRef?.child(userId)?.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        if (dataSnapshot.child("userState").hasChild("state")) {
                                            val state = dataSnapshot.child("userState").child("state").value.toString()
                                            val date = dataSnapshot.child("userState").child("date").value.toString()
                                            val time = dataSnapshot.child("userState").child("time").value.toString()
                                        }
                                        if (dataSnapshot.hasChild("image")) {
                                            val image = dataSnapshot.child("image").value.toString()
                                            val name = dataSnapshot.child("name").value.toString()
                                            holder.username?.setText(name)
                                            Glide.with(this@ContactsFragment).load(image).signature(MediaStoreSignature("",
                                                    System.currentTimeMillis(), 0)).error(R.drawable.default_profile_picture).into(holder.profilepicture!!)
                                        } else {
                                            val name = dataSnapshot.child("name").value.toString()
                                            holder.username?.setText(name)
                                        }
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {}
                            })
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(layout.users_display_layout, parent, false)
                return ContactsViewHolder(view)
            }
        }
        myrecyclerview?.setAdapter(adapter)
        adapter.startListening()
    }

    class ContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var username: TextView?
        var profilepicture: CircularImageView?
        var onlineIcon: ImageView? = null

        init {
            username = itemView.findViewById(R.id.users_profile_name)
            profilepicture = itemView.findViewById(R.id.users_profile_image)
            //onlineIcon=itemView.findViewById(R.id.users_online_status);
        }
    }
}