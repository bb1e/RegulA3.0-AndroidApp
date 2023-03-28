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
import pt.pl.estg.ei.regulapp.classes.Crianca
import lib.kingja.switchbutton.SwitchMultiButton
import android.widget.AdapterView.OnItemClickListener
import android.text.TextWatcher
import android.text.Editable
import pt.pl.estg.ei.regulapp.R.layout
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import org.apache.commons.lang3.StringUtils
import pt.pl.estg.ei.regulapp.classes.Analytics
import pt.pl.estg.ei.regulapp.classes.AnalyticsEvent
import java.util.ArrayList

class ThreadsActivity constructor() : AppCompatActivity(), ThreadsAdapter.DataUpdateAfterDelete {
    private val TAG: String? = "demoThreads"
    var userNameTV: TextView? = null
    var currentThreadsTV: TextView? = null
    var searchThreads: EditText? = null
    var logOutButton: ImageButton? = null
    var addThreadButton: ImageButton? = null
    var threadsListView: ListView? = null
    var threadsListViewTop: ListView? = null
    var addThreadBtn: ImageView? = null
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    private var user: FirebaseUser? = null
    private val spinnerOrdenar: Spinner? = null
    private var personal: ImageView? = null
    private var crianca: Crianca? = null
    private var threadsRecentesAdapter: ThreadsAdapter? = null
    private var threadsPopularesAdapter: ThreadsAdapter? = null
    var threadsList: MutableList<MessageThread?> = ArrayList()
    var threadsListRecentes: MutableList<MessageThread?> = ArrayList()
    var threadListPopulares: MutableList<MessageThread?> = ArrayList()
    var messagesList: MutableList<Message?> = ArrayList()
    var qtdMensagens: ArrayList<Int?> = ArrayList()

    //toolbar+menu
    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    private var toolbar: Toolbar? = null
    private var toolbarTitle: TextView? = null
    private var toolbarTitleWSubtitle: TextView? = null
    private var toolbarSubtitle: TextView? = null
    private var switchSearch: SwitchMultiButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_threads)
        setTitle("Message Threads")
        crianca = (getApplication() as GlobalSettings).session?.thisSession

        //userNameTV = findViewById(R.id.userNameTV);
        //currentThreadsTV=findViewById(R.id.currentThreadsTV);
        //addThreadET=findViewById(R.id.addThreadET);
        //logOutButton = findViewById(R.id.logOutButton);
        //addThreadButton=findViewById(R.id.addThreadButton);
        threadsListView = findViewById(R.id.threadsListView)
        threadsListViewTop = findViewById(R.id.threadsListViewTop)
        addThreadBtn = findViewById(R.id.addThreadButton)
        personal = findViewById(R.id.forumPersonal)
        searchThreads = findViewById(R.id.searchThreads)

        /* toolbar + menu */toolbarTitle = findViewById(R.id.toolbar_title)
        toolbarSubtitle = findViewById(R.id.toolbar_subTitle)
        toolbarTitleWSubtitle = findViewById(R.id.toolbar_title_with_subtitle)
        toolbar = findViewById(R.id.toolbarForum)
        drawerLayout = findViewById(R.id.drawer_layout_forum)
        navigationView = findViewById(R.id.nav_view_forum)
        iniciarToolbar()
        switchSearch = findViewById(R.id.btnSelecionarProcura)
        switchSearch?.setText("Recentes", "Populares")
        switchSearch?.setOnSwitchListener { position, tabText ->
            Analytics.fireEvent(AnalyticsEvent.FORUM_SWITCHFILTER(if (position == 0) "top" else "recente"))
            if (position == 0) {
                threadsListViewTop?.setVisibility(View.GONE)
                threadsListView?.setVisibility(View.VISIBLE)
            }
            if (position == 1) {
                threadsListView?.setVisibility(View.GONE)
                threadsListViewTop?.setVisibility(View.VISIBLE)
            }
        }
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().getReference()
        user = mAuth?.getCurrentUser()

        //userNameTV.setTextColor(Color.parseColor("#000000"));
        //currentThreadsTV.setTextColor(Color.parseColor("#000000"));

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter: ArrayAdapter<CharSequence?> = ArrayAdapter.createFromResource(this,
                R.array.popularidade_tempo, android.R.layout.simple_spinner_item)
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        if (user != null) {
            //userNameTV.setText(user.getDisplayName());
            getThreads()
        } else {
            Toast.makeText(this, "No user currently logged in", Toast.LENGTH_SHORT).show()
        }

        /*
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                user=null;
                mDatabase=null;
                mAuth=null;
                Intent intent = new Intent(ThreadsActivity.this, LoginActivityChat.class);
                startActivity(intent);
                finish();
            }
        });



        addThreadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title=addThreadET.getText().toString();
                String user_name=crianca.getParentName();
                if(!title.isEmpty()) {
                    addThread(title,user_name);
                }else{
                    Toast.makeText(ThreadsActivity.this, "Enter Thread Title", Toast.LENGTH_SHORT).show();
                }
            }
        });

         */threadsListView?.setOnItemClickListener { adapterView, view, i, l ->
            val intent: Intent = Intent(this@ThreadsActivity, ThreadActivity::class.java)
            intent.putExtra("messageThreadDetails", threadsListRecentes.get(i))
            startActivity(intent)
            finish()
        }
        threadsListViewTop?.setOnItemClickListener(OnItemClickListener { adapterView, view, i, l ->
            val intent: Intent = Intent(this@ThreadsActivity, ThreadActivity::class.java)
            intent.putExtra("messageThreadDetails", threadListPopulares.get(i))
            startActivity(intent)
            finish()
        })
        addThreadBtn?.setOnClickListener {
            val intent: Intent = Intent(this@ThreadsActivity, NewThreadActivity::class.java)
            //intent.putExtra("", )
            startActivity(intent)
            finish()
        }
        personal?.setOnClickListener {
            val intent: Intent = Intent(this@ThreadsActivity, PersonalThreadsActivity::class.java)
            //intent.putExtra("", )
            startActivity(intent)
            finish()
        }


        //Procura
        searchThreads?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //(ThreadsActivity.this).threadsRecentesAdapter.getFilter().filter(s);
                //(ThreadsActivity.this).threadsPopularesAdapter.getFilter().filter(s);
                var aFiltrar: MutableList<MessageThread?> = ArrayList()
                if (switchSearch?.getSelectedTab() == 0) { //RECENTES
                    aFiltrar = threadsListRecentes
                } else { // Populares
                    aFiltrar = threadListPopulares
                }
                val threadsPesquisadas: MutableList<MessageThread?>? = ArrayList()
                for (thread: MessageThread? in aFiltrar) {
                    if ((thread?.title?.toLowerCase()?.contains(s.toString().toLowerCase())!! ||
                                    thread?.user_name?.toLowerCase()?.contains(s.toString().toLowerCase())!! ||
                                    StringUtils.stripAccents(thread?.title).toLowerCase().contains(s.toString().toLowerCase()) ||
                                    StringUtils.stripAccents(thread?.user_name).toLowerCase().contains(s.toString().toLowerCase()))) {
                        //Verifica os titulos e os autores, compara com o texto da pesquisa
                        threadsPesquisadas?.add(thread)
                    }
                }
                if (switchSearch?.getSelectedTab() == 0) { //RECENTES
                    threadsRecentesAdapter = ThreadsAdapter(this@ThreadsActivity, layout.threads_listview, threadsPesquisadas!!, this@ThreadsActivity)
                    threadsListView?.setAdapter(threadsRecentesAdapter)
                } else { // Populares
                    threadsPopularesAdapter = ThreadsAdapter(this@ThreadsActivity, layout.threads_listview, threadsPesquisadas!!, this@ThreadsActivity)
                    threadsListViewTop?.setAdapter(threadsPopularesAdapter)
                }
            }

            public override fun afterTextChanged(s: Editable?) {}
        })

        if(savedInstanceState == null) {
            Sidebar.installSidebar(supportFragmentManager,drawerLayout!!,navigationView!!);
        }
    }

    override fun onResume() {
        super.onResume()
        Analytics.fireEvent(AnalyticsEvent.FORUM_OPENMENU)
    }

    /*
    public void addThread(String title,String user_name){
        if(user!=null) {
            mDatabase.child("message_threads").push().setValue(new MessageThread(title, user.getUid(),user_name)).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"here",Toast.LENGTH_LONG).show();
                    }
                }
            });
            addThreadET.setText("");
        }else{
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
        }
    }

     */
    public override fun deleteThread(thread_id: String?) {
        mDatabase?.child("message_threads")?.child(thread_id!!)?.removeValue()
    }

    fun getThreads() {
        mDatabase?.child("message_threads")?.addValueEventListener(object : ValueEventListener {
            public override fun onDataChange(dataSnapshot: DataSnapshot) {
                threadsList.clear()
                for (messageThreadSnapshot: DataSnapshot? in dataSnapshot.children) {
                    val messageThread: MessageThread? = messageThreadSnapshot?.getValue(MessageThread::class.java)
                    if (messageThread != null) {
                        messageThread.thread_id = messageThreadSnapshot.getKey()
                        Log.d(TAG, "onDataChange: " + messageThread.toString())
                    }
                    threadsList.add(messageThread)
                }
                if (threadsList.size > 1) {
                    for (i in threadsList.indices.reversed()) {
                        threadsListRecentes.add(threadsList.get(i))
                    }
                } else {
                    threadsListRecentes = threadsList
                }

                //Populares
                threadListPopulares = threadsList
                if (threadListPopulares.size > 1) {
                    for (i in 0 until threadListPopulares.size - 1) {
                        var guardaMax: Int = threadListPopulares.get(i)?.qtdComentarios!!
                        var guardaInd: Int = i
                        for (j in i + 1 until threadListPopulares.size) {
                            if (threadListPopulares.get(j)?.qtdComentarios!! > guardaMax) {
                                guardaMax = threadListPopulares.get(j)?.qtdComentarios!!
                                guardaInd = j
                            }
                        }
                        if (guardaInd != i) {
                            val aux: MessageThread? = threadListPopulares.get(i)
                            threadListPopulares.set(i, threadListPopulares.get(guardaInd))
                            threadListPopulares.set(guardaInd, aux)
                        }
                    }
                } else {
                    threadListPopulares = threadsList
                }
                threadsRecentesAdapter = ThreadsAdapter(this@ThreadsActivity, layout.threads_listview, threadsListRecentes, this@ThreadsActivity)
                threadsPopularesAdapter = ThreadsAdapter(this@ThreadsActivity, layout.threads_listview, threadListPopulares, this@ThreadsActivity)
                threadsListView?.setAdapter(threadsRecentesAdapter)
                threadsListViewTop?.setAdapter(threadsPopularesAdapter)
            }

            public override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@ThreadsActivity, "ThreadsActivity: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun iniciarToolbar() {
        setSupportActionBar(toolbar)
        navigationView?.bringToFront()
        //Auto selecionar Estrategias
        navigationView?.setCheckedItem(R.id.nav_forum)
        toolbarTitle?.setText("Fórum")
        personalizarTema()
    }

    private fun personalizarTema() {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setStatusBarColor(getResources().getColor(R.color.azul_forum))
    }

    fun clickMenu(view: View?) {
        //abrir janela menu
        openDrawer(drawerLayout!!)
    }

    private fun openDrawer(drawerLayout: DrawerLayout) {
        //abrir o layout da janela
        drawerLayout?.openDrawer(GravityCompat.END)
    }

    public override fun onBackPressed() {
        if (drawerLayout?.isDrawerOpen(GravityCompat.END)!!) {
            drawerLayout?.closeDrawer(GravityCompat.END)
        } else {
            //super.onBackPressed();
            openHomepage()
        }
    }

    private fun openHomepage() {
        val intent: Intent = Intent(this, HomePageActivity::class.java)
        intent.putExtra("ArrivedByToolbar", true)
        startActivity(intent)
    }
}