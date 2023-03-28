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
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.FirebaseStorage
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import pt.pl.estg.ei.regulapp.LoginActivity
import pt.pl.estg.ei.regulapp.RelatorioSemanalActivity
import pt.pl.estg.ei.regulapp.EstrategiasPreferidas
import pt.pl.estg.ei.regulapp.MenuOcupacoes
import pt.pl.estg.ei.regulapp.HomePageActivity
import pt.pl.estg.ei.regulapp.R.layout
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import java.util.*

class FindFriendsActivity : AppCompatActivity() {
    private val mtoolbar: Toolbar? = null
    private var recyclerView: RecyclerView? = null
    private var Userref: DatabaseReference? = null
    private var UserContactd: DatabaseReference? = null
    private var drawerLayout: DrawerLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_find_friends)
        val idCrianca: String? = (getApplication() as GlobalSettings?)?.session?.id
        Userref = FirebaseDatabase.getInstance().getReference().child("Users")
        UserContactd = FirebaseDatabase.getInstance().getReference().child("Contacts").child(idCrianca!!)
        recyclerView = findViewById(R.id.find_friends_recyclerlist)
        drawerLayout = findViewById(R.id.drawerLayoutChat)

        val navigationView: NavigationView? = findViewById(R.id.nav_chat_v2)
        val toolbar: Toolbar? = findViewById(R.id.toolbarChat)
        val textView: TextView? = toolbar?.findViewById(R.id.toolbar_title)
        textView?.setText("Procurar Amigos")
        setSupportActionBar(toolbar)
        navigationView?.bringToFront()
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setStatusBarColor(getResources().getColor(R.color.vermelho_chat))
        if(savedInstanceState == null) {
            Sidebar.installSidebar(supportFragmentManager,drawerLayout!!,navigationView!!);
        }
    }

    fun clickMenu(view: View?) {
        //abrir janela menu
        drawerLayout?.openDrawer(GravityCompat.END)
    }

    override fun onStart() {
        super.onStart()
        val contactsList: MutableList<Contacts?> = LinkedList()
        Userref?.get()?.addOnSuccessListener(object : OnSuccessListener<DataSnapshot?> {
            override fun onSuccess(dataSnapshot: DataSnapshot?) {
                for (child: DataSnapshot? in dataSnapshot?.children!!) {
                    val contact: Contacts = child?.getValue(Contacts::class.java) as Contacts
                    if ((contact.uid == FirebaseAuth.getInstance().uid)) {
                        continue
                    }
                    contactsList.add(contact)
                }
                val adapterRecycler: RecyclerView.Adapter<*> = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindFriendsViewHolder {
                        val view: View = LayoutInflater.from(this@FindFriendsActivity).inflate(layout.users_display_layout, parent, false)
                        val viewHolder: FindFriendsViewHolder = FindFriendsViewHolder(view)
                        return viewHolder
                    }

                    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                        val c: Contacts? = contactsList.get(position)
                        val viewHolder: FindFriendsViewHolder? = holder as FindFriendsViewHolder?
                        viewHolder?.username?.setText(c?.name)
                        FirebaseAuth.getInstance().getUid()
                        Log.d("TAG", "onBindViewHolder: ")
                        c?.uid
                        if (c?.image != null) {
                            val profileImage: StorageReference = FirebaseStorage.getInstance().reference.child(c.image!!)
                            Glide.with(this@FindFriendsActivity).load(profileImage).signature(MediaStoreSignature("",
                                    System.currentTimeMillis(), 0)).error(R.drawable.default_profile_picture).into(viewHolder?.profile!!)
                        }
                        holder.itemView.setOnClickListener {
                            val visit_user_id: String = c?.uid!!
                            val profileintent: Intent =
                                Intent(this@FindFriendsActivity, ChatProfileActivity::class.java)
                            profileintent.putExtra("visit_user_id", visit_user_id)
                            profileintent.putExtra("type_of_user", "Users")
                            startActivity(profileintent)
                        }
                    }

                    override fun getItemCount(): Int {
                        return contactsList.size
                    }
                }
                recyclerView?.setLayoutManager(LinearLayoutManager(this@FindFriendsActivity))
                recyclerView?.setAdapter(adapterRecycler)
            }
        })
    }

    override fun onBackPressed() {
        if (drawerLayout?.isDrawerOpen(GravityCompat.END)!!) {
            drawerLayout?.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }

    class FindFriendsViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var username: TextView?
        var userstatus: TextView?
        var profile: CircularImageView?

        init {
            username = itemView.findViewById(R.id.users_profile_name)
            userstatus = itemView.findViewById(R.id.status)
            profile = itemView.findViewById(R.id.users_profile_image)
        }
    }
}