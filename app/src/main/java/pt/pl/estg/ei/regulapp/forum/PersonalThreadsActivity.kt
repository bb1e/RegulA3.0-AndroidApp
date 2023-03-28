package pt.pl.estg.ei.regulapp.forum

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import android.os.Bundle
import pt.pl.estg.ei.regulapp.R
import pt.pl.estg.ei.regulapp.GlobalSettings
import com.google.firebase.database.FirebaseDatabase
import android.content.Intent
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.auth.FirebaseUser
import pt.pl.estg.ei.regulapp.LoginActivity
import pt.pl.estg.ei.regulapp.RelatorioSemanalActivity
import pt.pl.estg.ei.regulapp.EstrategiasPreferidas
import pt.pl.estg.ei.regulapp.MenuEscolherCrianca
import pt.pl.estg.ei.regulapp.MenuOcupacoes
import pt.pl.estg.ei.regulapp.HomePageActivity
import pt.pl.estg.ei.regulapp.R.layout
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import pt.pl.estg.ei.regulapp.classes.*
import java.util.ArrayList

class PersonalThreadsActivity constructor() : AppCompatActivity(), PersonalThreadsAdapter.DataUpdateAfterDelete {
    private val TAG: String = "demoThreads"
    var threadsListView: ListView? = null
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    private var user: FirebaseUser? = null
    var personalThreadsAdapter: PersonalThreadsAdapter? = null
    var threadsList: MutableList<MessageThread?> = ArrayList()

    //toolbar+menu
    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    private var toolbar: Toolbar? = null
    private var toolbarTitle: TextView? = null
    private var toolbarTitleWSubtitle: TextView? = null
    private var toolbarSubtitle: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_personal_threads)
        threadsListView = findViewById(R.id.threadsListView)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        user = mAuth?.currentUser
        val session: Session? = (application as GlobalSettings).session


        /* toolbar + menu */toolbarTitle = findViewById(R.id.toolbar_title)
        toolbarSubtitle = findViewById(R.id.toolbar_subTitle)
        toolbarTitleWSubtitle = findViewById(R.id.toolbar_title_with_subtitle)
        toolbar = findViewById(R.id.toolbarPersonalThreads)
        drawerLayout = findViewById(R.id.drawer_layout_personal_threads)
        navigationView = findViewById(R.id.nav_view_personal_threads)
        iniciarToolbar()
        if (user != null) {
            //userNameTV.setText(user.getDisplayName());
            getThreads(session?.id)
        } else {
            Toast.makeText(this, "No user currently logged in", Toast.LENGTH_SHORT).show()
        }
        threadsListView?.setOnItemClickListener { adapterView, view, i, l ->
            val intent: Intent = Intent(this@PersonalThreadsActivity, ThreadActivity::class.java)
            intent.putExtra("messageThreadDetails", threadsList.get(i))
            startActivity(intent)
            finish()
        }

        if(savedInstanceState == null) {
            Sidebar.installSidebar(supportFragmentManager,drawerLayout!!,navigationView!!);
        }
    }

    override fun onResume() {
        super.onResume()
        Analytics.fireEvent(AnalyticsEvent.FORUM_MEUSPOSTS_OPENMENU)
    }

    override fun deleteThread(thread_id: String?) {
        Analytics.fireEvent(AnalyticsEvent.FORUM_DELETEPOST);
        mDatabase?.child("message_threads")?.child(thread_id!!)?.removeValue()
    }

    fun getThreads(id: String?) {
        mDatabase?.child("message_threads")?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                threadsList.clear()
                for (messageThreadSnapshot: DataSnapshot? in dataSnapshot.children) {
                    val messageThread: MessageThread? = messageThreadSnapshot?.getValue(MessageThread::class.java)
                    if (messageThread != null) {
                        messageThread.thread_id = messageThreadSnapshot.key
                        Log.d(TAG, "onDataChange: " + messageThread.toString())
                    }
                    if ((messageThread?.user_id == id)) {
                        threadsList.add(messageThread)
                    }
                }
                personalThreadsAdapter = PersonalThreadsAdapter(this@PersonalThreadsActivity, layout.threads_listview, threadsList, this@PersonalThreadsActivity)
                threadsListView?.setAdapter(personalThreadsAdapter)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PersonalThreadsActivity, "ThreadsActivity: " + error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun iniciarToolbar() {
        setSupportActionBar(toolbar)
        navigationView?.bringToFront()
        //Auto selecionar Estrategias
        navigationView?.setCheckedItem(R.id.nav_forum)
        toolbarTitle?.setVisibility(View.GONE)
        toolbarTitleWSubtitle?.setVisibility(View.VISIBLE)
        toolbarTitleWSubtitle?.setText("Fórum")
        toolbarSubtitle?.setVisibility(View.VISIBLE)
        toolbarSubtitle?.setText("As minhas publicações")
        personalizarTema()
    }

    fun clickMenu(view: View?) {
        //abrir janela menu
        openDrawer(drawerLayout)
    }

    private fun openDrawer(drawerLayout: DrawerLayout?) {
        //abrir o layout da janela
        drawerLayout?.openDrawer(GravityCompat.END)
    }

    public override fun onBackPressed() {
        if (drawerLayout?.isDrawerOpen(GravityCompat.END)!!) {
            drawerLayout?.closeDrawer(GravityCompat.END)
        } else {
            //super.onBackPressed();
            openForum()
        }
    }

    private fun personalizarTema() {
        val window: Window? = getWindow()
        window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window?.setStatusBarColor(getResources().getColor(R.color.azul_forum))
    }

    private fun openForum() {
        val intent: Intent = Intent(this, ThreadsActivity::class.java)
        startActivity(intent)
    }

}