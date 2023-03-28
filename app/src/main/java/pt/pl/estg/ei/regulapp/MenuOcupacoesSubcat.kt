package pt.pl.estg.ei.regulapp

import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import android.content.Intent
import com.google.android.material.navigation.NavigationView
import androidx.viewpager.widget.ViewPager
import androidx.drawerlayout.widget.DrawerLayout
import pt.pl.estg.ei.regulapp.R.layout
import pt.pl.estg.ei.regulapp.enums.AreasOcupacao
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat

class MenuOcupacoesSubcat constructor() : AppCompatActivity() {
    private val viewPager: ViewPager? = null
    var drawerLayout: DrawerLayout? = null
    var navigationView: NavigationView? = null
    var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //Card estrategia = (Card) getIntent().getSerializableExtra("estrategia");
        val areasOcupacao: AreasOcupacao = intent.getSerializableExtra("opcao") as AreasOcupacao
        if (areasOcupacao == AreasOcupacao.AVD) {
            setContentView(layout.activity_menu_ocupacao_avd)
            val b1: Button? = findViewById(R.id.btnAVD1)
            val b2: Button? = findViewById(R.id.btnAVD2)
            val b3: Button? = findViewById(R.id.btnAVD3)
            val b9: Button? = findViewById(R.id.btnAVD9)
            val b4: Button? = findViewById(R.id.btnAVD4)
            val b5: Button? = findViewById(R.id.btnAVD5)
            val b6: Button? = findViewById(R.id.btnAVD6)
            val b7: Button? = findViewById(R.id.btnAVD7)
            val b8: Button? = findViewById(R.id.btnAVD8)
            b1?.setOnClickListener{ _ -> openAtividade("Vestir/despir") }
            b2?.setOnClickListener{ _ -> openAtividade("Alimentação e horas da refeição") }
            b3?.setOnClickListener{ _ -> openAtividade("Tomar banho") }
            b9?.setOnClickListener{ _ -> openAtividade("Higiene sanitária") }
            b4?.setOnClickListener{ _ -> openAtividade("Higiene pessoal – lavar os dentes") }
            b5?.setOnClickListener{ _ -> openAtividade("Higiene Pessoal – cortar as unhas") }
            b6?.setOnClickListener{ _ -> openAtividade("Higiene Pessoal – lavar a mãos") }
            b7?.setOnClickListener{ _ -> openAtividade("Higiene Pessoal – cortar o cabelo") }
            b8?.setOnClickListener{ _ -> openAtividade("Higiene Pessoal - colocar creme") }
        }
        if (areasOcupacao == AreasOcupacao.DESCANSO) {
            setContentView(layout.activity_menu_ocupacao_descanso)
            val b1: Button = findViewById(R.id.btnAVD1)
            val b2: Button = findViewById(R.id.btnAVD2)
            b1.setOnClickListener { _ -> openAtividade("Preparação do sono") }
            b2.setOnClickListener { _ -> openAtividade("Participação do sono") }
        } else {
            Log.d(TAG, "onCreate: Areas Ocupacao " + areasOcupacao)
        }

        //toolbar
        val toolbarMenu: ImageView = findViewById(R.id.toolbar_menu)
        val toolbarTitle: TextView = findViewById(R.id.toolbar_title)
        val toolbarSubTitle: TextView = findViewById(R.id.toolbar_subTitle)
        val toolbarTitleWithSubtitle: TextView = findViewById(R.id.toolbar_title_with_subtitle)


        //Alterações da toolbar
        if (areasOcupacao == AreasOcupacao.AVD) {
            toolbarTitleWithSubtitle.setText("Estratégias")
            toolbarTitleWithSubtitle.setVisibility(View.VISIBLE)
            toolbarTitle.setVisibility(View.GONE)
            toolbarSubTitle.setText("Atividades da Vida Diária")
            toolbarSubTitle.setVisibility(View.VISIBLE)
            toolbarTitleWithSubtitle.setTextColor(getResources().getColor(R.color.cinza_titulos))


            //toolbarBackgroundColor.setBackgroundResource(R.color.verde_toolbar_avd);
            //toolbar
            drawerLayout = findViewById(R.id.drawer_layout_avd)
            navigationView = findViewById(R.id.nav_view_avd)
            toolbar = findViewById(R.id.toolbarAVD)
            toolbar?.setBackgroundResource(R.color.amarelo_estrategias)
            iniciarToolbar()
            mudarStatusBarColor()
        }
        if (areasOcupacao == AreasOcupacao.DESCANSO) {
            toolbarTitleWithSubtitle.setText("Estratégias")
            toolbarTitleWithSubtitle.setVisibility(View.VISIBLE)
            toolbarTitle.setVisibility(View.GONE)
            toolbarSubTitle.setText("Descanso / Sono")
            toolbarSubTitle.setVisibility(View.VISIBLE)
            toolbarTitleWithSubtitle.setTextColor(resources.getColor(R.color.cinza_titulos))
            //toolbarBackgroundColor.setBackgroundResource(R.color.verde_toolbar_avd);
            //toolbar
            drawerLayout = findViewById(R.id.drawer_layout_descanso)
            navigationView = findViewById(R.id.nav_view_descanso)
            toolbar = findViewById(R.id.toolbarDescanso)
            toolbar?.setBackgroundResource(R.color.amarelo_estrategias)
            iniciarToolbar()
            mudarStatusBarColor()
        }

        if(savedInstanceState == null) {
            Sidebar.installSidebar(supportFragmentManager,drawerLayout!!,navigationView!!);
        }
    }

    private fun mudarStatusBarColor() {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setStatusBarColor(getResources().getColor(R.color.amarelo_estrategias))
    }

    fun openAtividade(opcao: String?) {
        val intent: Intent
        intent = Intent(this, ListaEstrategias::class.java)
        intent.putExtra("opcao", opcao)
        startActivity(intent)
    }

    private fun alterarStyleSubCat(cor: Int) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setStatusBarColor(getResources().getColor(cor))
        toolbar?.setBackgroundResource(cor)
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
        openDrawer(drawerLayout!!)
    }

    private fun openDrawer(drawerLayout: DrawerLayout) {
        //abrir o layout da janela
        drawerLayout.openDrawer(GravityCompat.END)
    }

    public override fun onBackPressed() {
        if (drawerLayout?.isDrawerOpen(GravityCompat.END)!!) {
            drawerLayout?.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }
    companion object {
        private val TAG: String? = "MenuOcupacoesSubcat "
    }
}