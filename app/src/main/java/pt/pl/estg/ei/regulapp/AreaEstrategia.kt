package pt.pl.estg.ei.regulapp

import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import android.content.Intent
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import android.annotation.SuppressLint
import com.google.firebase.firestore.FirebaseFirestore
import pt.pl.estg.ei.regulapp.enums.BD
import android.os.Build
import android.text.Html
import com.google.firebase.firestore.QuerySnapshot
import pt.pl.estg.ei.regulapp.R.layout
import com.google.firebase.firestore.QueryDocumentSnapshot
import pt.pl.estg.ei.regulapp.adapters.AreasEstrategiasAdapter
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.google.android.gms.tasks.Task
import pt.pl.estg.ei.regulapp.classes.*
import java.util.ArrayList

class AreaEstrategia constructor() : AppCompatActivity() {
    private var tituloEstrategia: String? = null
    private var idEstrategia: String? = null
    private var idCrianca: String? = null
    private var tipoAlvo: String? = null
    private var titulo: String? = null
    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    private var toolbar: Toolbar? = null
    private var toolbarSubtittle: TextView? = null
    private var toolbarTitleWithSubtitle: TextView? = null
    private var toolbarTitle: TextView? = null
    private var nomeEstrategia: TextView? = null
    private var reatividadeAlvo: TextView? = null
    private var areaOcupacao: TextView? = null
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_area_estrategia)

        //Params passados na criacao
        tituloEstrategia = getIntent().getSerializableExtra("titulo") as String?
        idEstrategia = getIntent().getSerializableExtra("idEstrategia") as String?
        idCrianca = getIntent().getSerializableExtra("idCrianca") as String?
        tipoAlvo = getIntent().getSerializableExtra("tipoAlvo") as String?
        titulo = getIntent().getSerializableExtra("ao") as String?
        toolbarTitle = findViewById(R.id.toolbar_title)
        toolbarTitleWithSubtitle = findViewById(R.id.toolbar_title_with_subtitle)
        toolbarSubtittle = findViewById(R.id.toolbar_subTitle)
        toolbar = findViewById(R.id.toolbarAreaEstrategia)
        drawerLayout = findViewById(R.id.drawer_layout_area_estrategia)
        navigationView = findViewById(R.id.nav_view_area_estrategia)
        nomeEstrategia = findViewById(R.id.textViewAreaFBTitulo)
        reatividadeAlvo = findViewById(R.id.tipoAlvo)
        areaOcupacao = findViewById(R.id.tituloAO)
        fillTextviews()
        iniciarToolbar()
        alterarEstiloPagina()
        preencher(idEstrategia.toString(), idCrianca)

        if(savedInstanceState == null) {
            Sidebar.installSidebar(supportFragmentManager,drawerLayout!!,navigationView!!)
        }
    }

    override fun onResume() {
        super.onResume()
        Analytics.fireEvent(AnalyticsEvent.ESTRATEGIA_VIEWED)
    }

    @SuppressLint("SetTextI18n")
    private fun fillTextviews() {
        when (titulo) {
            "Alimentação e horas da refeição" -> areaOcupacao?.setText("Alimentação")
            "Brincar/Jogar" -> areaOcupacao?.setText("Brincar e jogar")
            "Higiene Pessoal - colocar creme" -> areaOcupacao?.setText("Colocar creme")
            "Higiene Pessoal – cortar as unhas" -> areaOcupacao?.setText("Cortar as unhas")
            "Higiene Pessoal – cortar o cabelo" -> areaOcupacao?.setText("Cortar o cabelo")
            "Higiene Pessoal – lavar a mãos" -> areaOcupacao?.setText("Lavar as mãos")
            "Higiene pessoal – lavar os dentes" -> areaOcupacao?.setText("Lavar os dentes")
            "Vestir/despir" -> areaOcupacao?.setText("Vestir e despir")
            else -> areaOcupacao?.setText(titulo)
        }
        when (tipoAlvo) {
            "Hiper_reativo" -> reatividadeAlvo?.setText("Hiper-reativo")
            "Hipo_reativo" -> reatividadeAlvo?.setText("Hipo-reativo")
            else -> reatividadeAlvo?.setText(tipoAlvo)
        }
    }

    private fun preencher(idEstrategia: String?, idCrianca: String?) {
        //Aceder ao documento da estratégia
        db
                .collection(BD.feedbackEstrategias_demo.toString())
                .whereEqualTo("idCrianca", idCrianca)
                .whereEqualTo("idEstrategia", idEstrategia)
                .get().addOnCompleteListener(object : OnCompleteListener<QuerySnapshot?> {
                    public override fun onComplete(task: Task<QuerySnapshot?>) {
                        Log.d("AreaEstrategia", "Success getting documents: ")
                        val feedbacks: ArrayList<FeedbackEstrategia> = ArrayList()
                        if (task.isSuccessful()) {
                            for (document: QueryDocumentSnapshot in task.getResult()!!) {
                                //Todos os feedbacks da estrategia e da crianca
                                val feedback: FeedbackEstrategia = document.toObject(FeedbackEstrategia::class.java)
                                feedbacks.add(feedback)
                                Log.d("AreaEstrategia", "Success getting documents: " + feedback.comentario)
                            }
                            val qtdFeedback: TextView = findViewById(R.id.textViewQtdAvaliacoes)
                            val mediaFeedback: ImageView = findViewById(R.id.imageViewMediaFeedback)
                            val utilizouUltimoFeedback: TextView = findViewById(R.id.textViewUltimaAvaliacoesRealizou)
                            val ultimoFeedback: ImageView = findViewById(R.id.imageViewUltimoFeedback)
                            val comentarioUltimoFeedback: TextView = findViewById(R.id.textViewUltimaAvaliacoesComentario)
                            val dataUltimoFeedback: TextView = findViewById(R.id.textViewUltimaAvaliacoesData)
                            val ultimoFeedbackLayout: LinearLayout = findViewById(R.id.linearLayoutFB)
                            val mediaFeedbackLayout: LinearLayout = findViewById(R.id.linearLayoutMediaAvaliacoes)
                            val tituloComentarios: TextView = findViewById(R.id.textViewHistoricoComentariosTitulo)
                            val comentarios: ListView = findViewById(R.id.listViewHistoricoComentarios)
                            val numeroFeedbacks: Int = feedbacks.size
                            qtdFeedback.setText("" + numeroFeedbacks)
                            var total: Long = java.lang.Long.valueOf(0)
                            var media: Long = java.lang.Long.valueOf(0)
                            val lastFeedback: FeedbackEstrategia
                            if (feedbacks.size > 0) {
                                lastFeedback = feedbacks.get(feedbacks.size - 1)
                                val untimaAvaliacao: Int = lastFeedback.avaliacao
                                for (i in 0 until numeroFeedbacks) {
                                    total += feedbacks[i].avaliacao
                                }
                                if (total != 0L) {
                                    media = total / feedbacks.size
                                }
                                Log.d("debug", "Media: $media")
                                if (media == 1L || media == 2L) {
                                    mediaFeedback.setImageResource(R.drawable.smiley_sad)
                                } else if (media == 3L) {
                                    mediaFeedback.setImageResource(R.drawable.smiley_meh)
                                } else if (media == 4L || media == 5L) {
                                    mediaFeedback.setImageResource(R.drawable.smiley_smile)
                                }
                                if (lastFeedback.comentario?.isEmpty()!!) {
                                    comentarioUltimoFeedback.setVisibility(View.GONE)
                                }
                                if (lastFeedback.realizou) {
                                    utilizouUltimoFeedback.setText("Realizada")
                                } else {
                                    utilizouUltimoFeedback.setText("Não Realizada")
                                    ultimoFeedback.setVisibility(View.GONE)
                                }
                                if ((untimaAvaliacao == 1) || (untimaAvaliacao == 0) || (untimaAvaliacao == 2)) {
                                    ultimoFeedback.setImageResource(R.drawable.smiley_sad)
                                } else if (untimaAvaliacao == 3) {
                                    ultimoFeedback.setImageResource(R.drawable.smiley_meh)
                                } else if (untimaAvaliacao == 5) {
                                    ultimoFeedback.setImageResource(R.drawable.smiley_smile)
                                }
                                dataUltimoFeedback.setText(lastFeedback.data.toString())
                                comentarioUltimoFeedback.setText(lastFeedback.comentario)

                                //====================================
                                //  Lista de comentários
                                //====================================
                                val comentariosDaDB: ArrayList<String?> = ArrayList()
                                val datasDaDB: ArrayList<String?> = ArrayList()
                                if (feedbacks.size > 0) {
                                    for (i in feedbacks.indices.reversed()) {
                                        val com: String = feedbacks.get(i).comentario!!
                                        val dataComentario: Data? = feedbacks.get(i).data
                                        if (com.isEmpty()) {
                                            continue
                                        }
                                        comentariosDaDB.add(com)
                                        datasDaDB.add(dataComentario.toString())
                                    }
                                }
                                val areaEstrategiaAdapter: AreasEstrategiasAdapter? = AreasEstrategiasAdapter(this@AreaEstrategia, comentariosDaDB, datasDaDB)
                                comentarios.adapter = areaEstrategiaAdapter
                            } else {
                                ultimoFeedbackLayout.setVisibility(View.GONE)
                                mediaFeedbackLayout.setVisibility(View.GONE)
                                tituloComentarios.setVisibility(View.GONE)
                            }
                        } else {
                            Log.d("AreaEstrategia", "Error getting documents: ", task.getException())
                        }
                    }
                })
    }

    private fun alterarEstiloPagina() {
        toolbarTitle?.text = "Área da Estratégia"
        toolbarTitle?.setTextColor(getResources().getColor(R.color.cinza_titulos))
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setStatusBarColor(getResources().getColor(R.color.amarelo_estrategias))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            nomeEstrategia?.setText(Html.fromHtml(tituloEstrategia, Html.FROM_HTML_MODE_COMPACT))
        } else {
            nomeEstrategia?.setText(Html.fromHtml(tituloEstrategia))
        }
    }

    private fun iniciarToolbar() {
        setSupportActionBar(toolbar)
        navigationView?.bringToFront()
        //navigationView?.setNavigationItemSelectedListener(this)
        //Auto selecionar Relatorio Semanal
        navigationView?.setCheckedItem(R.id.nav_estrategias)
    }

    fun clickMenu(view: View) {
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
            super.onBackPressed()
        }
    }
}