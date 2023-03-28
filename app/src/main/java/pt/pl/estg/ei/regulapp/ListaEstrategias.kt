package pt.pl.estg.ei.regulapp

import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import android.content.Intent
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import pt.pl.estg.ei.regulapp.enums.BD
import lib.kingja.switchbutton.SwitchMultiButton
import pt.pl.estg.ei.regulapp.adapters.MyAdapter
import pt.pl.estg.ei.regulapp.R.layout
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.google.firebase.firestore.*
import pt.pl.estg.ei.regulapp.classes.*
import java.util.ArrayList

class ListaEstrategias: AppCompatActivity() {
    private var listView: ListView? = null
    private var listViewRecomendadas: ListView? = null
    private var TAG: String? = null
    private var estrategias: ArrayList<Estrategia?>? = null
    private var estrategiasPreferidas: ArrayList<Estrategia?>? = null
    private var estrategiasRecomendadas: ArrayList<Estrategia?>? = null
    private var idsEstrategiasRecomendadas: ArrayList<String?>? = null
    private var criancaDaBd: Crianca? = null
    private var idCrianca: String? = null
    private var recomendadas: Boolean = false
    private var selecionar: SwitchMultiButton? = null

    //toolbar e menu hamburger
    var drawerLayout: DrawerLayout? = null
    var navigationView: NavigationView? = null
    var toolbar: Toolbar? = null
    var toolbarTitle: TextView? = null

    //Layout
    var imagemFundo: ImageView? = null
    var toolbarTitleWithSubtitle: TextView? = null
    var toolbarSubtitle: TextView? = null
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    lateinit var param : String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TAG = "LE"
        estrategias = ArrayList()
        estrategiasRecomendadas = ArrayList()
        idsEstrategiasRecomendadas = ArrayList()
        setContentView(layout.activity_lista_estrategias)
        param = intent.getStringExtra("opcao") as String
        selecionar = findViewById(R.id.btnSelecionarTipoEstrategias)
        listView = findViewById(R.id.listView)
        listViewRecomendadas = findViewById(R.id.listViewRecomendadas)
        idCrianca = GlobalSettings.Companion.myAppInstance?.session?.thisSession?.id
        listView?.setVisibility(View.GONE)
        listViewRecomendadas?.setVisibility(View.GONE)
        val session: Session = (application as GlobalSettings).session!!
        estrategiasPreferidas = session.thisSession?.estrategiasFavoritas
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        if (param.compareTo("Estratégias Regulatórias") == 0) {
            db.collection(BD.estrategias_demo.toString()).whereEqualTo("titulo", param)
                    .get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document: QueryDocumentSnapshot in task.result!!) {
                            val estrategia: Estrategia? = document.toObject(Estrategia::class.java)
                            estrategias?.add(estrategia)
                            listar(estrategias as ArrayList<Estrategia?>)
                        }
                        val docRef: DocumentReference = db.collection(BD.criancas_test.toString()).document(idCrianca!!)

                        docRef.get()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val document: DocumentSnapshot = task.result!!
                                    if (document.exists()) {
                                        criancaDaBd = document.toObject(Crianca::class.java)
                                        val estrategiasRecomendadas: ArrayList<String?>? =
                                            criancaDaBd?.estrategiasRecomendadas
                                        recomendadas = estrategiasRecomendadas != null && estrategiasRecomendadas.size > 0
                                        setRecomendadas(
                                            recomendadas,
                                            estrategiasRecomendadas,
                                            estrategias,
                                            param
                                        )
                                    } else {
                                        Log.d(TAG, "No such document")
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.exception)
                                }
                            }
                    } else {
                        Log.d("ListaEstrategias", "Error getting documents: ", task.exception)
                    }
                }
        } else {
            db.collection(BD.estrategias_demo.toString()).whereEqualTo("titulo", param) //.whereEqualTo("tipoAlvo",session.getThisSession().getTipoAutismo().toString())
                    .get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document: QueryDocumentSnapshot? in task.result!!) {
                            val estrategia: Estrategia = document?.toObject(Estrategia::class.java)!!
                            estrategias?.add(estrategia)
                            listar(estrategias as ArrayList<Estrategia?>)
                        }
                        val docRef: DocumentReference =
                            db.collection(BD.criancas_test.toString()).document(idCrianca!!)
                        docRef.get()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val document: DocumentSnapshot = task.result!!
                                    if (document.exists()) {
                                        criancaDaBd = document.toObject(Crianca::class.java)
                                        val estrategiasRecomendadas: ArrayList<String?>? =
                                            criancaDaBd?.estrategiasRecomendadas
                                        recomendadas = estrategiasRecomendadas != null && estrategiasRecomendadas.size > 0
                                        setRecomendadas(
                                            recomendadas,
                                            estrategiasRecomendadas,
                                            estrategias,
                                            param
                                        )
                                    } else {
                                        Log.d(TAG, "No such document")
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException())
                                }
                            }
                    } else {
                        Log.d("ListaEstrategias", "Error getting documents: ", task.getException())
                    }
                }
        }


        //TOOLBAR E MENU HAMBURGER
        toolbarTitle = findViewById(R.id.toolbar_title)
        toolbar = findViewById(R.id.toolbarListaEstrategias)
        drawerLayout = findViewById(R.id.drawer_layout_lista_estrategias)
        navigationView = findViewById(R.id.nav_view_lista_estrategias)
        imagemFundo = findViewById(R.id.imageViewImagemFundo)
        toolbarSubtitle = findViewById(R.id.toolbar_subTitle)
        toolbarTitleWithSubtitle = findViewById(R.id.toolbar_title_with_subtitle)
        toolbarTitle?.setTextColor(getResources().getColor(R.color.cinza_titulos))
        mudarStatusBarColor()
        iniciarToolbar()
        /* teste */toolbarTitle?.setText(param)
        mudarLayout(param)

        if(savedInstanceState == null) {
            Sidebar.installSidebar(supportFragmentManager,drawerLayout!!,navigationView!!)
        }
    }

    override fun onResume() {
        super.onResume()
        Analytics.fireEvent(AnalyticsEvent.LISTAESTRATEGIAS_OPENMENU(param))
    }

    protected fun setRecomendadas(haRecomendadas: Boolean, idStratsRecomendadas: ArrayList<String?>?, strats: ArrayList<Estrategia?>?, param: String?) {
        val stratsRecomendadas: ArrayList<Estrategia?> = ArrayList()
        var haRecomendadasDoTipo: Boolean = false
        if (haRecomendadas) {
            strats?.indices?.forEach { i ->
                val idStrat: String = strats[i]?.id.toString() + ""
                for (j in idStratsRecomendadas?.indices!!) {
                    if ((idStrat == idStratsRecomendadas[j]) && (strats[i]?.titulo == param)
                    ) {
                        stratsRecomendadas.add(strats[i]!!)
                        continue
                    }
                }
            }
            if (stratsRecomendadas.size > 0) {
                haRecomendadasDoTipo = true
            }
        }
        if (haRecomendadas && haRecomendadasDoTipo) {
            listarRecomendadas(stratsRecomendadas)
            selecionar?.visibility = View.VISIBLE
            listView?.visibility = View.GONE
            listViewRecomendadas?.visibility = View.VISIBLE
            selecionar?.setText("Recomendadas", "Todas")
            selecionar?.setOnSwitchListener { position, tabText ->
                if (selecionar?.selectedTab == 0) {
                    //Recomendadas
                    listarRecomendadas(stratsRecomendadas)
                    listViewRecomendadas?.setVisibility(View.VISIBLE)
                    listView?.setVisibility(View.GONE)
                }
                if (selecionar?.selectedTab == 1) {
                    //Todas
                    listar(estrategias as ArrayList<Estrategia?>)
                    listViewRecomendadas?.setVisibility(View.GONE)
                    listView?.setVisibility(View.VISIBLE)
                }
            }
        } else {
            selecionar?.setVisibility(View.GONE)
            listViewRecomendadas?.setVisibility(View.GONE)
            listView?.setVisibility(View.VISIBLE)
        }
    }

    private fun mudarStatusBarColor() {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.amarelo_estrategias)
    }

    protected fun listar(estrategias: ArrayList<Estrategia?>) {
        val session: Session = (application as GlobalSettings).session!!
        estrategiasPreferidas = session.thisSession?.estrategiasFavoritas
        val adapter: MyAdapter = MyAdapter(this, estrategias, estrategiasPreferidas!!)
        listView?.adapter = adapter
    }

    protected fun listarRecomendadas(estrategiasRec: ArrayList<Estrategia?>) {
        val session: Session? = (application as GlobalSettings).session
        estrategiasPreferidas = session?.thisSession?.estrategiasFavoritas
        val adapterRecomendadas: MyAdapter = MyAdapter(this, estrategiasRec, estrategiasPreferidas!!)
        listViewRecomendadas?.adapter = adapterRecomendadas
    }

    protected fun mudarLayout(ocupacao: String?) {
        when (ocupacao) {
            "Tomar banho" -> {
                imagemFundo?.setImageResource(R.drawable.lista_estrategia_banho_img)
                imagemFundo?.scaleType = ImageView.ScaleType.CENTER_CROP
                mudarTituloToolbar("Tomar banho")
                alterarStyleSubCat(R.color.amarelo_claro)
            }
            "Vestir/despir" -> {
                imagemFundo?.setImageResource(R.drawable.lista_estrategia_vestir_img)
                mudarTituloToolbar("Vestir e Despir")
                alterarStyleSubCat(R.color.rosa_claro)
            }
            "Alimentação e horas da refeição" -> {
                imagemFundo?.setImageResource(R.drawable.lista_estrategia_alimentacao)
                imagemFundo?.scaleType = ImageView.ScaleType.CENTER_CROP
                mudarTituloToolbar("Alimentação")
                alterarStyleSubCat(R.color.cinza)
            }
            "Higiene sanitária" -> {
                imagemFundo?.setImageResource(R.drawable.lista_estrategia_higiene_img3)
                imagemFundo?.scaleType = ImageView.ScaleType.CENTER_CROP
                mudarTituloToolbar("Higiene sanitária")
                alterarStyleSubCat(R.color.amarelo_claro)
                imagemFundo?.setPadding(100, 20, 0, 0)
            }
            "Higiene pessoal – lavar os dentes" -> {
                imagemFundo?.setImageResource(R.drawable.lista_estrategia_banho_img)
                imagemFundo?.scaleType = ImageView.ScaleType.CENTER_CROP
                mudarTituloToolbar("Lavar os dentes")
                alterarStyleSubCat(R.color.azul_claro)
            }
            "Higiene Pessoal – cortar as unhas" -> {
                imagemFundo?.scaleType = ImageView.ScaleType.CENTER_CROP
                imagemFundo?.setImageResource(R.drawable.lista_estrategia_banho_img)
                mudarTituloToolbar("Cortar as unhas")
                alterarStyleSubCat(R.color.rosa_claro)
            }
            "Higiene Pessoal – cortar o cabelo" -> {
                imagemFundo?.setImageResource(R.drawable.lista_estrategia_banho_img)
                imagemFundo?.scaleType = ImageView.ScaleType.CENTER_CROP
                mudarTituloToolbar("Cortar o cabelo")
                alterarStyleSubCat(R.color.rosa_claro)
            }
            "Higiene Pessoal – lavar a mãos" -> {
                imagemFundo?.setImageResource(R.drawable.lista_estrategia_banho_img)
                imagemFundo?.scaleType = ImageView.ScaleType.CENTER_CROP
                mudarTituloToolbar("Lavar as mãos")
                alterarStyleSubCat(R.color.amarelo_claro)
            }
            "Higiene Pessoal - colocar creme" -> {
                imagemFundo?.setImageResource(R.drawable.lista_estrategia_banho_img)
                imagemFundo?.setScaleType(ImageView.ScaleType.CENTER_CROP)
                mudarTituloToolbar("Colocar creme")
                alterarStyleSubCat(R.color.cinza)
            }
            "Brincar/Jogar" -> {
                imagemFundo?.setImageResource(R.drawable.lista_estrategia_brincar_img)
                imagemFundo?.setScaleType(ImageView.ScaleType.CENTER_CROP)
                mudarTituloToolbar("Brincar e Jogar")
            }
            "Preparação do sono" -> {
                imagemFundo?.setImageResource(R.drawable.lista_estrategia_dormir_img)
                imagemFundo?.setScaleType(ImageView.ScaleType.CENTER_CROP)
                mudarTituloToolbar("Preparação do sono")
            }
            "Participação do sono" -> {
                imagemFundo?.setImageResource(R.drawable.lista_estrategia_dormir_img)
                imagemFundo?.scaleType = ImageView.ScaleType.CENTER_CROP
                mudarTituloToolbar("Participação do sono")
            }
            "Estratégias Regulatórias" -> {
                imagemFundo?.setImageResource(R.drawable.lista_estrategia_regulatorias)
                imagemFundo?.scaleType = ImageView.ScaleType.CENTER_CROP
                mudarTituloToolbar("Estratégias Regulatórias")
            }
        }
    }

    protected fun mudarTituloToolbar(subtitulo: String?) {
        toolbarTitle?.visibility = View.GONE
        toolbarTitleWithSubtitle?.visibility = View.VISIBLE
        toolbarTitleWithSubtitle?.text = "Lista de Estratégias"
        toolbarSubtitle?.visibility = View.VISIBLE
        toolbarSubtitle?.text = subtitulo
        toolbarTitleWithSubtitle?.setTextColor(resources.getColor(R.color.cinza_titulos))
    }

    private fun alterarStyleSubCat(cor: Int) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(cor)
        toolbar?.setBackgroundResource(cor)
        drawerLayout?.setBackgroundResource(cor)
    }

    //TOOLBAR E MENU HAMBURGER
    private fun iniciarToolbar() {
        setSupportActionBar(toolbar)
        navigationView?.bringToFront()
        //navigationView?.setNavigationItemSelectedListener(this)
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

    override fun onBackPressed() {
        if (drawerLayout?.isDrawerOpen(GravityCompat.END)!!) {
            drawerLayout?.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }
}