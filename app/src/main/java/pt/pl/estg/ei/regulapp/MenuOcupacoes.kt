package pt.pl.estg.ei.regulapp

import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import android.content.Intent
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import pt.pl.estg.ei.regulapp.R.layout
import pt.pl.estg.ei.regulapp.enums.AreasOcupacao
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import pt.pl.estg.ei.regulapp.classes.Analytics
import pt.pl.estg.ei.regulapp.classes.AnalyticsEvent

class MenuOcupacoes constructor() : AppCompatActivity() {
    protected var drawerLayout: DrawerLayout? = null
    protected var navigationView: NavigationView? = null
    protected var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_menu_estrategias)
        //butões
        val buttonER: Button = findViewById(R.id.btnAVD)
        val buttonAVD: Button = findViewById(R.id.btnAVDverdadeiro)
        val buttonBrincar: Button = findViewById(R.id.btnAVD3)
        val buttonDescanso: Button = findViewById(R.id.btnAVD2)
        buttonER.setOnClickListener { openMenuAtividades(AreasOcupacao.ER) }
        buttonAVD.setOnClickListener { openMenuAtividades(AreasOcupacao.AVD) }
        buttonBrincar.setOnClickListener { openMenuAtividades(AreasOcupacao.BRINCAR) }
        buttonDescanso.setOnClickListener { openMenuAtividades(AreasOcupacao.DESCANSO) }

        //menu
        val toolbarTitle: TextView = findViewById(R.id.toolbar_title)
        toolbar = findViewById(R.id.toolbarEstrategias)
        toolbarTitle.setText("Estratégias")
        toolbar?.setBackgroundResource(R.color.amarelo_estrategias)
        toolbarTitle.setTextColor(getResources().getColor(R.color.cinza_titulos))


        //toolbar
        drawerLayout = findViewById(R.id.drawer_layout_estrategias)
        navigationView = findViewById(R.id.nav_view_estrategia)
        toolbar = findViewById(R.id.toolbarEstrategias)
        iniciarToolbar()
        mudarStatusBarColor()
        if(savedInstanceState == null) {
            Sidebar.installSidebar(supportFragmentManager,drawerLayout!!,navigationView!!);
        }
    }

    override fun onResume() {
        super.onResume()
        Analytics.fireEvent(AnalyticsEvent.OCUPACOES_OPENMENU)
    }

    private fun mudarStatusBarColor() {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setStatusBarColor(getResources().getColor(R.color.amarelo_estrategias))
    }

    fun openMenuAtividades(opcao: AreasOcupacao?) {
        val intent: Intent
        if (opcao == AreasOcupacao.BRINCAR) {
            intent = Intent(this, ListaEstrategias::class.java)
            intent.putExtra("opcao", "Brincar/Jogar")
        } else if (opcao == AreasOcupacao.ER) {
            intent = Intent(this, ListaEstrategias::class.java)
            intent.putExtra("opcao", "Estratégias Regulatórias")
        } else {
            intent = Intent(this, MenuOcupacoesSubcat::class.java)
            intent.putExtra("opcao", opcao)
        }
        startActivity(intent)
    }

    private fun iniciarToolbar() {
        setSupportActionBar(toolbar)
        navigationView?.bringToFront()
        //Auto selecionar Estrategias
        navigationView?.setCheckedItem(R.id.nav_estrategias)
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
            super.onBackPressed()
        }
    }
}