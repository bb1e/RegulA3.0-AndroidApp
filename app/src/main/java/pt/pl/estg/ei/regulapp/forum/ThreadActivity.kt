package pt.pl.estg.ei.regulapp.forum

import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import pt.pl.estg.ei.regulapp.R
import pt.pl.estg.ei.regulapp.GlobalSettings
import android.content.Intent
import org.ocpsoft.prettytime.PrettyTime
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.auth.FirebaseUser
import pt.pl.estg.ei.regulapp.LoginActivity
import pt.pl.estg.ei.regulapp.RelatorioSemanalActivity
import pt.pl.estg.ei.regulapp.EstrategiasPreferidas
import pt.pl.estg.ei.regulapp.MenuEscolherCrianca
import pt.pl.estg.ei.regulapp.MenuOcupacoes
import pt.pl.estg.ei.regulapp.HomePageActivity
import pt.pl.estg.ei.regulapp.forum.MessagesAdapter.DataUpdateAfterMessageDelete
import pt.pl.estg.ei.regulapp.R.layout
import android.graphics.Color
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.*
import pt.pl.estg.ei.regulapp.classes.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ThreadActivity constructor() : AppCompatActivity(), DataUpdateAfterMessageDelete {
    private val TAG: String = "demoChat"
    var threadNameTV: TextView? = null
    var threadDescription: TextView? = null
    var threadAutor: TextView? = null
    var threadAge: TextView? = null
    var newMessageET: EditText? = null
    var sendButton: ImageView? = null
    var messagesLV: ListView? = null

    //Idade da thread
    private val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US)
    private val p: PrettyTime = PrettyTime(Locale("pt"))
    private var convertedDate: Date? = null
    var messageThread: MessageThread? = null
    private var messagesAdapter: MessagesAdapter? = null
    var messagesList: MutableList<Message?> = ArrayList()
    private var user: FirebaseUser? = null
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null

    //toolbar+menu
    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    private var toolbar: Toolbar? = null
    private var toolbarTitle: TextView? = null
    private var toolbarTitleWSubtitle: TextView? = null
    private var toolbarSubtitle: TextView? = null
    private var verifiedUser: ImageView? = null
    private var session: Session? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_forum)
        setTitle("Chatroom")
        session = (application as GlobalSettings).session
        threadNameTV = findViewById(R.id.threadNameTV)
        newMessageET = findViewById(R.id.newMessageET)
        //homeButton = findViewById(R.id.homeButton);
        sendButton = findViewById(R.id.sendButton)
        messagesLV = findViewById(R.id.messagesLV)
        threadDescription = findViewById(R.id.threadDescription)
        threadAutor = findViewById(R.id.forumAutor)
        threadAge = findViewById(R.id.forumThreadAge)
        verifiedUser = findViewById(R.id.verifiedUser)

        /* toolbar + menu */toolbarTitle = findViewById(R.id.toolbar_title)
        toolbarSubtitle = findViewById(R.id.toolbar_subTitle)
        toolbarTitleWSubtitle = findViewById(R.id.toolbar_title_with_subtitle)
        toolbar = findViewById(R.id.toolbarAreaPublicacao)
        drawerLayout = findViewById(R.id.drawer_layout_area_publicacao)
        navigationView = findViewById(R.id.nav_view_area_publicacao)
        iniciarToolbar()
        mDatabase = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()
        user = mAuth?.currentUser
        threadNameTV?.setTextColor(Color.parseColor("#000000"))
        if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getExtras()?.containsKey("messageThreadDetails")!!) {
                messageThread = getIntent().getSerializableExtra("messageThreadDetails") as MessageThread?
                threadNameTV?.setText(messageThread?.title)
                threadDescription?.setText(messageThread?.description)
                threadAutor?.setText(messageThread?.user_name)
                getMessages(messageThread?.thread_id)
                if (!messageThread?.profissional?.isEmpty()!!) {
                    verifiedUser?.setVisibility(View.VISIBLE)
                }
                try {
                    convertedDate = dateFormat.parse(messageThread?.created_time)
                    p.setReference(Date())
                } catch (e: ParseException) {
                    e.printStackTrace()
                    FirebaseCrashlytics.getInstance().recordException(e); // log it to Crashalytics despite catching the exception
                }
                threadAge?.setText(p.format(convertedDate))
            }

            if(savedInstanceState == null) {
                Sidebar.installSidebar(supportFragmentManager,drawerLayout!!,navigationView!!);
            }
        } else {
            Toast.makeText(this, "No data received", Toast.LENGTH_SHORT).show()
        }

        /*
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    user=null;
                    mDatabase=null;
                    mAuth=null;
                    Intent intent = new Intent(ChatActivity.this, ThreadsActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    user=null;
                    mDatabase=null;
                    mAuth=null;
                    Toast.makeText(ChatActivity.this, "You need to login", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ChatActivity.this, LoginActivityChat.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

         */sendButton?.setOnClickListener {
            val message: String = newMessageET?.text.toString()
            val user_name: String? = session?.nome
            if (message.isEmpty()) {
                Toast.makeText(this@ThreadActivity, "Enter Message", Toast.LENGTH_SHORT).show()
            } else {
                addMessage(message, user_name!!, messageThread?.thread_id!!)
                updateCount(
                    mDatabase?.child("message_threads")?.child(messageThread?.thread_id!!)?.child("qtdComentarios")!!, true
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Analytics.fireEvent(AnalyticsEvent.FORUM_VIEWPOST(messageThread));
    }

    fun addMessage(message: String, user_name: String, thread_id: String) {
        if (user != null) {
            mDatabase?.child("message_threads")?.child(thread_id)?.child("messages")?.push()?.setValue(Message(message, user?.uid, user_name, SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())))
            newMessageET?.setText("")
            Analytics.fireEvent(AnalyticsEvent.FORUM_SENDCOMMENT);
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateCount(database: DatabaseReference, isSoma: Boolean) {
        database.runTransaction(object : Transaction.Handler {
            public override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val value: Long? = mutableData.getValue(Long::class.java)
                if (value == null) {
                    mutableData.setValue(0)
                } else {
                    if (isSoma) {
                        mutableData.setValue(value + 1)
                    } else {
                        mutableData.setValue(value - 1)
                    }
                }
                return Transaction.success(mutableData)
            }

            public override fun onComplete(databaseError: DatabaseError?, b: Boolean,
                                           dataSnapshot: DataSnapshot?) {
                Log.d(TAG, "transaction:onComplete:" + databaseError)
            }
        })
    }

    fun getMessages(thread_id: String?) {
        mDatabase?.child("message_threads")?.child(thread_id!!)?.child("messages")?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                messagesList.clear()
                for (messageSnapshot: DataSnapshot? in dataSnapshot.getChildren()) {
                    val message: Message? = messageSnapshot?.getValue(Message::class.java)
                    if (message != null) {
                        message.message_id = messageSnapshot.getKey()
                        Log.d(TAG, "onDataChange: " + message.toString())
                    }
                    messagesList.add(message)
                }
                messagesAdapter = MessagesAdapter(this@ThreadActivity, layout.threads_listview, messagesList, this@ThreadActivity)
                messagesLV?.setAdapter(messagesAdapter)
            }

            public override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@ThreadActivity, "ChatActivity: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    public override fun deleteMessage(message_id: String?) {
        mDatabase?.child("message_threads")?.child(messageThread?.thread_id!!)?.child("messages")?.child(message_id!!)?.removeValue()
        updateCount(mDatabase?.child("message_threads")?.child(messageThread?.thread_id!!)?.child("qtdComentarios")!!, false)
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
        toolbarSubtitle?.setText("Publicação")
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
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setStatusBarColor(getResources().getColor(R.color.azul_forum))
    }

    private fun openMenuEscolherCrianca() {
        val intent: Intent = Intent(this, MenuEscolherCrianca::class.java)
        startActivity(intent)
    }

    private fun openEstrategiasPreferidas() {
        val intent: Intent = Intent(this, EstrategiasPreferidas::class.java)
        startActivity(intent)
    }

    private fun openMenuFeedbackSemanal() {
        val intent: Intent = Intent(this, RelatorioSemanalActivity::class.java)
        startActivity(intent)
    }

    private fun openHomepage() {
        val intent: Intent = Intent(this, HomePageActivity::class.java)
        intent.putExtra("ArrivedByToolbar", true)
        startActivity(intent)
    }

    private fun openForum() {
        val intent: Intent = Intent(this, ThreadsActivity::class.java)
        startActivity(intent)
    }
}