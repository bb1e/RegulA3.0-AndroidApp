package pt.pl.estg.ei.regulapp

import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import android.content.Intent
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.firestore.FirebaseFirestore
import pt.pl.estg.ei.regulapp.enums.BD
import pt.pl.estg.ei.regulapp.classes.Crianca
import pt.pl.estg.ei.regulapp.classes.Estrategia
import pt.pl.estg.ei.regulapp.adapters.EstrategiasFavoritasAdapter
import pt.pl.estg.ei.regulapp.R.layout
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import pt.pl.estg.ei.regulapp.classes.Analytics
import pt.pl.estg.ei.regulapp.classes.AnalyticsEvent
import java.util.ArrayList

class EstrategiasPreferidas constructor() : AppCompatActivity() {
    //toolbar e menu hamburger
    var drawerLayout: DrawerLayout? = null
    var navigationView: NavigationView? = null
    var toolbar: Toolbar? = null
    var toolbarTitle: TextView? = null
    var imagemFundo: ImageView? = null
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_lista_estrategias)
        val listView: ListView = findViewById(R.id.listView)
        val idCrianca: String = GlobalSettings.Companion.myAppInstance?.session?.thisSession?.id!!

        /*
        ArrayList<Estrategia> estrategiasPref = ((GlobalSettings) this.getApplication()).getSession().getThisSession().getEstrategiasFavoritas();
        for (Estrategia estrategia:estrategiasPref) {
            nomes.add(estrategia.getDescricao());
        }

         */db
                .collection(BD.criancas_test.toString())
                .whereEqualTo("id", idCrianca)
                .get().addOnCompleteListener { task ->
                Log.d("FAV", "Success getting crianca document: ")
                if (task.isSuccessful) {
                    val criancas: MutableList<Crianca> =
                        task.result!!.toObjects(Crianca::class.java)
                    val crianca: Crianca = criancas[0]
                    Log.d("FAV", "Success getting crianca document: " + crianca.nome)
                    val estrategiasPref: ArrayList<Estrategia?>? = crianca.estrategiasFavoritas
                    val adapter: EstrategiasFavoritasAdapter? = EstrategiasFavoritasAdapter(
                        this@EstrategiasPreferidas,
                        estrategiasPref!!,
                        null
                    )
                    listView.setAdapter(adapter)
                    adapter?.notifyDataSetChanged()
                }
            }


        //TOOLBAR E MENU HAMBURGER
        toolbarTitle = findViewById(R.id.toolbar_title)
        toolbar = findViewById(R.id.toolbarListaEstrategias)
        drawerLayout = findViewById(R.id.drawer_layout_lista_estrategias)
        navigationView = findViewById(R.id.nav_view_lista_estrategias)
        imagemFundo = findViewById(R.id.imageViewImagemFundo)
        mudarStatusBarColor()
        toolbarTitle?.setTextColor(resources.getColor(R.color.cinza_titulos))
        iniciarToolbar()
        toolbarTitle?.text = "Estratégias Favoritas"
        toolbarTitle?.setTextSize(TypedValue.COMPLEX_UNIT_PT, 13f)
        imagemFundo?.setImageResource(R.drawable.lista_estrategia_favorita_img) //imagem de fundo de baixo
        imagemFundo?.scaleType = ImageView.ScaleType.CENTER_CROP

        if(savedInstanceState == null) {
            Sidebar.installSidebar(supportFragmentManager,drawerLayout!!,navigationView!!)
        }
    }

    override fun onResume() {
        super.onResume()
        Analytics.fireEvent(AnalyticsEvent.LISTAESTRATEGIASFAVORITAS_OPENMENU);
    }

    private fun mudarStatusBarColor() {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = getResources().getColor(R.color.amarelo_estrategias)
    }

    //TOOLBAR E MENU HAMBURGER
    private fun iniciarToolbar() {
        setSupportActionBar(toolbar)
        navigationView?.bringToFront()
        //Auto selecionar Relatorio Semanal
        navigationView?.setCheckedItem(R.id.nav_estrategias_fav)
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
}