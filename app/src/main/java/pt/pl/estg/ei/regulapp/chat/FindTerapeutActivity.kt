package pt.pl.estg.ei.regulapp.chat

import android.widget.TextView
import com.mikhaellopez.circularimageview.CircularImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import pt.pl.estg.ei.regulapp.R
import com.google.firebase.database.FirebaseDatabase
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.MediaStoreSignature
import android.content.Intent
import com.google.firebase.database.DataSnapshot
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.FirebaseStorage
import com.google.android.gms.tasks.OnSuccessListener
import android.widget.Toast
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import pt.pl.estg.ei.regulapp.LoginActivity
import pt.pl.estg.ei.regulapp.RelatorioSemanalActivity
import pt.pl.estg.ei.regulapp.EstrategiasPreferidas
import pt.pl.estg.ei.regulapp.MenuEscolherCrianca
import pt.pl.estg.ei.regulapp.MenuOcupacoes
import pt.pl.estg.ei.regulapp.HomePageActivity
import pt.pl.estg.ei.regulapp.classes.Terapeuta
import pt.pl.estg.ei.regulapp.R.layout
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.google.firebase.Timestamp
import java.util.*

class FindTerapeutActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private var Userref: DatabaseReference? = null
    private var drawerLayout: DrawerLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_find_friends)

        Userref = FirebaseDatabase.getInstance().getReference().child("Terapeutas")
        recyclerView = findViewById(R.id.find_friends_recyclerlist)
        recyclerView?.setLayoutManager(LinearLayoutManager(this))

        drawerLayout = findViewById(R.id.drawerLayoutChat)
        val navigationView: NavigationView? = findViewById(R.id.nav_chat_v2)
        val toolbar: Toolbar? = findViewById(R.id.toolbarChat)
        val textView: TextView? = toolbar?.findViewById(R.id.toolbar_title)
        textView?.setText("Procurar Terapeutas")
        setSupportActionBar(toolbar)
        navigationView?.bringToFront()
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setStatusBarColor(getResources().getColor(R.color.vermelho_chat))

        if(savedInstanceState == null) {
            Sidebar.installSidebar(supportFragmentManager,drawerLayout!!,navigationView!!);
        }

        //createRandomTerapeutTest();
    }

    fun clickMenu(view: View) {
        //abrir janela menu
        drawerLayout?.openDrawer(GravityCompat.END)
    }

    override fun onStart() {
        super.onStart()
        val options: FirebaseRecyclerOptions<Terapeuta?>? = FirebaseRecyclerOptions.Builder<Terapeuta?>().setQuery(Userref!!, Terapeuta::class.java as Class<Terapeuta?>).build()

        val adapter: FirebaseRecyclerAdapter<Terapeuta?, FindFriendsViewHolder?>? = object : FirebaseRecyclerAdapter<Terapeuta?, FindFriendsViewHolder?>(options!!) {
            override fun onBindViewHolder(holder: FindFriendsViewHolder, position: Int, model: Terapeuta) {}
            public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindFriendsViewHolder {
                val view: View? = LayoutInflater.from(parent.getContext()).inflate(layout.users_display_layout, parent, false)
                val viewHolder: FindFriendsViewHolder? = FindFriendsViewHolder(view!!)
                return viewHolder!!
            }
        }
        val contactsList: MutableList<Terapeuta?> = LinkedList()
        Userref?.get()?.addOnSuccessListener(object : OnSuccessListener<DataSnapshot?> {
            public override fun onSuccess(dataSnapshot: DataSnapshot?) {
                for (child: DataSnapshot? in dataSnapshot?.children!!) {
                    val contact: Terapeuta = child?.getValue(Terapeuta::class.java) as Terapeuta
                    contactsList.add(contact)
                }
                val adapterRecycler: RecyclerView.Adapter<*> = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindFriendsViewHolder {
                        val view: View? = LayoutInflater.from(this@FindTerapeutActivity).inflate(layout.users_display_layout, parent, false)
                        val viewHolder: FindFriendsViewHolder = FindFriendsViewHolder(view!!)
                        return viewHolder
                    }

                    public override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                        val c: Terapeuta? = contactsList.get(position)
                        val viewHolder: FindFriendsViewHolder? = holder as FindFriendsViewHolder?
                        viewHolder?.username?.setText(c?.name)
                        viewHolder?.userstatus?.setText(c?.description)
                        if (c?.image != null && !c.image?.isEmpty()!!) {
                            val profileImage: StorageReference? = FirebaseStorage.getInstance().reference.child(c.image!!)
                            Glide.with(this@FindTerapeutActivity).load(profileImage).signature(MediaStoreSignature("",
                                    System.currentTimeMillis(), 0)).error(R.drawable.default_profile_picture).into(viewHolder?.profile!!)
                        }

                        //Picasso.get().load(profileImage).placeholder(R.drawable.profile_image).into(holder.profile);
                        holder.itemView.setOnClickListener {
                            val visit_user_id: String = c?.uid.toString()
                            val profileintent: Intent =
                                Intent(this@FindTerapeutActivity, ChatProfileActivity::class.java)
                            profileintent.putExtra("type_of_user", "Terapeutas")
                            profileintent.putExtra("visit_user_id", visit_user_id)
                            startActivity(profileintent)
                        }
                    }

                    public override fun getItemCount(): Int {
                        return contactsList.size
                    }
                }
                recyclerView?.setLayoutManager(LinearLayoutManager(this@FindTerapeutActivity))
                recyclerView?.setAdapter(adapterRecycler)
            }
        })
    }

    private fun createRandomTerapeutTest() {
        val profileMap: HashMap<String?, Any?>? = HashMap()
        profileMap?.put("uid", "currentUserrID")
        profileMap?.put("name", "Dr. Francisco Silva")
        profileMap?.put("description", "Doutor a 21 anos com 30 anos de experiencia nesta área")
        profileMap?.put("image", "profilePictures/Lwsi3ljvNYTjweStNWmIYXZvxpH2.png")
        FirebaseDatabase.getInstance().getReference("Terapeutas").child(Timestamp.now().seconds.toString() + "").setValue(profileMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    this@FindTerapeutActivity,
                    "Your profile has been updated...",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
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