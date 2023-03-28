package pt.pl.estg.ei.regulapp.forum

import android.widget.TextView
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import android.os.Bundle
import pt.pl.estg.ei.regulapp.R
import pt.pl.estg.ei.regulapp.GlobalSettings
import com.google.firebase.database.FirebaseDatabase
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
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
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import pt.pl.estg.ei.regulapp.classes.*
import java.text.SimpleDateFormat
import java.util.*

class NewThreadActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    private var user: FirebaseUser? = null
    private var titleThread: EditText? = null
    private var descriptionThread: EditText? = null
    private var publishThread: AppCompatButton? = null

    //toolbar+menu
    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    private var toolbar: Toolbar? = null
    private var toolbarTitle: TextView? = null
    private var toolbarTitleWSubtitle: TextView? = null
    private var toolbarSubtitle: TextView? = null
    protected var session: Session? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_new_thread)
        session = (application as GlobalSettings).session
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        user = mAuth?.currentUser
        titleThread = findViewById(R.id.forumNewTitulo)
        descriptionThread = findViewById(R.id.forumNewDescricao)
        publishThread = findViewById(R.id.forumPublicarBtn)

        /* toolbar + menu */toolbarTitle = findViewById(R.id.toolbar_title)
        toolbarSubtitle = findViewById(R.id.toolbar_subTitle)
        toolbarTitleWSubtitle = findViewById(R.id.toolbar_title_with_subtitle)
        toolbar = findViewById(R.id.toolbarAddThread)
        drawerLayout = findViewById(R.id.drawer_layout_add_thread)
        navigationView = findViewById(R.id.nav_view_add_thread)
        iniciarToolbar()
        publishThread?.setOnClickListener {
            val title: String = titleThread?.text.toString()
            val description: String = descriptionThread?.text.toString()
            val user_name: String = session?.nome!!
            val dataPublicacao: Data = getData()
            if (!title?.isEmpty()!!) {
                addThread(title, description, user_name)
            } else {
                Analytics.fireEvent(AnalyticsEvent.FORUM_NEWPOST_VALIDATIONERROR);
                Toast.makeText(this@NewThreadActivity, "Enter Thread Title", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        if(savedInstanceState == null) {
            Sidebar.installSidebar(supportFragmentManager,drawerLayout!!,navigationView!!);
        }
    }

    private fun getData(): Data {
        val calendar: Calendar = Calendar.getInstance()
        val dia: Int = calendar.get(Calendar.DAY_OF_MONTH)
        val mes: Int = calendar.get(Calendar.MONTH) + 1
        val ano: Int = calendar.get(Calendar.YEAR)
        val horas: Int = calendar.get(Calendar.HOUR_OF_DAY)
        val minutos: Int = calendar.get(Calendar.MINUTE)
        val dataPublicacao: Data = Data(dia, mes, ano, horas, minutos)
        return dataPublicacao
    }

    fun addThread(title: String, description: String, user_name: String) {
        if (user != null) {
            mDatabase?.child("message_threads")?.push()?.setValue(MessageThread(title, description, user?.uid, user_name, SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())))?.addOnCompleteListener(
                OnCompleteListener<Void?> { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "here", Toast.LENGTH_LONG).show()
                        onBackPressed()
                    }
                })
            Analytics.fireEvent(AnalyticsEvent.FORUM_NEWPOST_SUCCESS);
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun iniciarToolbar() {
        setSupportActionBar(toolbar)
        navigationView?.bringToFront()
        //Auto selecionar Estrategias
        navigationView?.setCheckedItem(R.id.nav_forum)
        toolbarTitle?.visibility = View.GONE
        toolbarTitleWSubtitle?.visibility = View.VISIBLE
        toolbarTitleWSubtitle?.text = "Fórum"
        toolbarSubtitle?.visibility = View.VISIBLE
        toolbarSubtitle?.text = "Nova publicação"
        personalizarTema()
    }

    fun clickMenu(view: View?) {
        //abrir janela menu
        openDrawer(drawerLayout!!)
    }

    private fun openDrawer(drawerLayout: DrawerLayout) {
        //abrir o layout da janela
        drawerLayout.openDrawer(GravityCompat.END)
    }

    override fun onBackPressed() {
        if (drawerLayout?.isDrawerOpen(GravityCompat.END)!!) {
            drawerLayout?.closeDrawer(GravityCompat.END)
        } else {
            //super.onBackPressed();
            openForum()
        }
    }

    private fun personalizarTema() {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.azul_forum)
    }

    private fun openForum() {
        val intent: Intent = Intent(this, ThreadsActivity::class.java)
        startActivity(intent)
    }
}