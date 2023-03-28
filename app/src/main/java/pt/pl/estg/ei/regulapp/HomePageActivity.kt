package pt.pl.estg.ei.regulapp

import android.Manifest
import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.MediaStoreSignature
import android.content.Intent
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.FirebaseStorage
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import pt.pl.estg.ei.regulapp.chat.MainChatActivity
import pt.pl.estg.ei.regulapp.forum.ThreadsActivity
import pt.pl.estg.ei.regulapp.R.layout
import android.content.pm.PackageManager
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import de.hdodenhof.circleimageview.CircleImageView
import pt.pl.estg.ei.regulapp.classes.*
import java.lang.NullPointerException

class HomePageActivity constructor() : AppCompatActivity() {
    private val STORAGE_PERMISSION_CODE: Int = 1
    protected var profilePic: CircleImageView? = null
    protected var drawerLayout: DrawerLayout? = null
    protected var navigationView: NavigationView? = null
    protected var toolbar: Toolbar? = null
    protected var session: Session? = null
    protected var child: Crianca? = null
    protected var addCrianca: Boolean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_homepage)

        //permissoes -> não foi necessário
        /*if (ContextCompat.checkSelfPermission(HomePageActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(HomePageActivity.this, "You have already granted this permission!",
                    Toast.LENGTH_SHORT).show();
        } else {
            requestStoragePermission();
        }*/session = (getApplication() as GlobalSettings).session
        if (session == null) {
            logout()
        }
        addCrianca = intent.getBooleanExtra("addCrianca", false)
        val child_name: String? = getIntent().getStringExtra("child")
        if (child_name != null) {
            try {
                child = session?.getCriancaByName(child_name)
                session?.thisSession = child
            } catch (e: NullPointerException) {
                logout()
                FirebaseCrashlytics.getInstance().recordException(e); // log it to Crashalytics despite catching the exception
            }
        } else {
            child = session?.thisSession
        }


        //butões
        val menuOcupacoesButton: Button = findViewById(R.id.btnAtividades)
        menuOcupacoesButton.setOnClickListener { _ -> openMenuAtividades() }
        //Button menuButton = findViewById(R.id.btnHistoricoAtividade);
        //menuButton.setOnClickListener(v -> openMenu());
        val profileButton: CircleImageView = findViewById(R.id.btnProfileHomepage)
        profileButton.setOnClickListener { v: View? -> openMenuProfile() }
        val feedbackDiarioButton: Button = findViewById(R.id.btnFeedbackSemana)

        feedbackDiarioButton.setOnClickListener { _ -> openMenuFeedbackSemanal() }
        val estrategiasPreferidas: Button = findViewById(R.id.btnEstrategiasFav)
        estrategiasPreferidas.setOnClickListener { _ -> openEstrategiasPreferidas() }
        findViewById<View>(R.id.btnChat).setOnClickListener { _ -> openChat() }
        findViewById<View>(R.id.btnForum).setOnClickListener{ _ -> openForum() }
        val forum: Button = findViewById(R.id.btnForum)
        forum.setOnClickListener { _ -> openForum() }


        //layout properties
        val nameField: TextView = findViewById(R.id.txtName)
        nameField.setText(child?.nome)
        profilePic = findViewById(R.id.btnProfileHomepage)
        loadProfilePic()

        //toolbar
        drawerLayout = findViewById(R.id.drawer_layout_homepage)
        navigationView = findViewById(R.id.nav_view_homepage)
        toolbar = findViewById(R.id.toolbarHomepage)
        iniciarToolbar()

        if(savedInstanceState == null) {
            Sidebar.installSidebar(supportFragmentManager,drawerLayout!!,navigationView!!)
        }

    }

    private fun loadProfilePic() {
        if (child?.storageImageRef != null) {
            val profileImage: StorageReference = FirebaseStorage.getInstance().reference.child(child?.storageImageRef!!)
            Glide.with(this).load(profileImage).signature(MediaStoreSignature("",
                    System.currentTimeMillis(), 0)).into(profilePic!!)
            Glide.with(this@HomePageActivity).load(profileImage).signature(MediaStoreSignature("",
                    System.currentTimeMillis(), 0)).error(R.drawable.perfil2).into(profilePic!!)
        } else {
            Toast.makeText(applicationContext, "nao foi possivel ", Toast.LENGTH_LONG)
        }
    }

    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok"
                    ) { _ , _->
                        ActivityCompat.requestPermissions(
                            this@HomePageActivity,
                            arrayOf<String?>(Manifest.permission.READ_EXTERNAL_STORAGE),
                            STORAGE_PERMISSION_CODE
                        )
                    }
                .setNegativeButton("cancel"
                ) { dialog, _ -> dialog.dismiss() }
                .create().show()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf<String?>(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults.get(0) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "Permission GRANTED", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(applicationContext, "Permission DENIED", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadProfilePic()
    }

    private fun 
    nuTesteAtual() {
        //Para testar novas activities
        val intent: Intent = Intent(this, ThreadsActivity::class.java)
        startActivity(intent)
    }

    private fun openForum() {
        val intent: Intent = Intent(this, ThreadsActivity::class.java)
        startActivity(intent)
    }

    private fun openEstrategiasPreferidas() {
        val intent: Intent = Intent(this, EstrategiasPreferidas::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        startActivity(intent)
    }

    private fun openMenuFeedbackSemanal() {
        val intent: Intent = Intent(this, RelatorioSemanalActivity::class.java)
        startActivity(intent)
    }

    private fun openMenuProfile() {
        val intent: Intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun openMenu() {
        val intent: Intent = Intent(this, HistoricoAtividadeActivity::class.java)
        startActivity(intent)
    }

    private fun openMenuAtividades() {
        val intent: Intent = Intent(this, MenuOcupacoes::class.java)
        startActivity(intent)
    }

    fun clickMenuHomepage(view: View?) {
        //abrir janela menu
        openDrawer(drawerLayout!!)
    }

    private fun openDrawer(drawerLayout: DrawerLayout) {
        //abrir o layout da janela
        drawerLayout.openDrawer(GravityCompat.END)
    }

    override fun onBackPressed() {
        if (addCrianca!!) {
            val intent: Intent = Intent(this, MenuEscolherCrianca::class.java)
            startActivity(intent)
        } else {
            super.onBackPressed()
        }
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val intent: Intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun openChat() {
        val intent: Intent = Intent(this, MainChatActivity::class.java)
        startActivity(intent)
    }

    private fun iniciarToolbar() {
        setSupportActionBar(toolbar)
        navigationView?.bringToFront()
        //Auto selecionar home
        navigationView?.setCheckedItem(R.id.nav_home)
    }

    companion object {
        private const val TAG: String = "HomePageActivity"
    }
}