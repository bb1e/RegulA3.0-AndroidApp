package pt.pl.estg.ei.regulapp

import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import android.content.Intent
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.firestore.FirebaseFirestore
import pt.pl.estg.ei.regulapp.classes.FeedbackEstrategia
import android.os.Build
import android.text.Html
import pt.pl.estg.ei.regulapp.R.layout
import com.google.firebase.firestore.DocumentSnapshot
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat

class AreaFeedbackEstrategiaActivity : AppCompatActivity() {
    private var tituloEstrategia: String? = null
    private val idEstrategia: String? = null
    private var idCrianca: String? = null
    private var idFeedbackEstrategia: String? = null
    private var txtTituloEstrategia: TextView? = null
    private var utilizou: TextView? = null
    private var data: TextView? = null
    private var avaliacao: ImageView? = null
    private var comentario: TextView? = null
    private var comentarioTitulo: TextView? = null
    private var feedbackTitulo: TextView? = null
    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    private var toolbar: Toolbar? = null
    private var toolbarTitle: TextView? = null
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_area_feedback_estrategia)
        tituloEstrategia = intent.getSerializableExtra("titulo") as String
        idCrianca = intent.getSerializableExtra("idCrianca") as String
        idFeedbackEstrategia = intent.getSerializableExtra("idFeedback") as String
        toolbarTitle = findViewById(R.id.toolbar_title)
        toolbar = findViewById(R.id.toolbarAreaFeedback)
        drawerLayout = findViewById(R.id.drawer_layout_area_feedback)
        navigationView = findViewById(R.id.nav_view_area_feedback)
        utilizou = findViewById(R.id.textViewAvaliacoesRealizou)
        data = findViewById(R.id.textViewAvaliacoesData)
        avaliacao = findViewById(R.id.imageViewFeedback)
        comentario = findViewById(R.id.textViewAvaliacoesComentario)
        comentarioTitulo = findViewById(R.id.tvAvaliacoesComentarioTitulo)
        feedbackTitulo = findViewById(R.id.tvAvaliacoesAvaliacaoTitulo)
        txtTituloEstrategia = findViewById(R.id.textViewAreaFBTitulo)
        iniciarToolbar()
        alterarEstiloPagina()
        preencherFeedback(idCrianca!!, idFeedbackEstrategia!!)

        if(savedInstanceState == null) {
            Sidebar.installSidebar(supportFragmentManager,drawerLayout!!,navigationView!!)
        }
    }

    private fun preencherFeedback(idCrianca: String, idFeedbackEstrategia: String) {
        db
                .collection("feedbackEstrategias_demo")
                .document(idFeedbackEstrategia)
                .get()
                .addOnCompleteListener { task ->
                    Log.d("AreaFeedback", "Success getting documents")
                    var feedback: FeedbackEstrategia = FeedbackEstrategia()
                    if (task.isSuccessful) {
                        val document: DocumentSnapshot = task.result!!
                        if (document.exists()) {
                            feedback = document.toObject(FeedbackEstrategia::class.java)!!
                            data?.text = feedback.data.toString()
                            when (feedback.avaliacao) {
                                in 1..2 -> avaliacao?.setImageResource(R.drawable.smiley_sad)
                                3 -> avaliacao?.setImageResource(R.drawable.smiley_meh)
                                in 4..5 -> avaliacao?.setImageResource(R.drawable.smiley_smile)
                            }
                            comentario?.text = feedback.comentario
                            if (feedback.realizou) {
                                utilizou?.text = "Realizada"
                                if ((feedback.comentario == "")) {
                                    comentario?.visibility = View.GONE
                                    comentarioTitulo?.visibility = View.GONE
                                }
                            } else {
                                utilizou?.setText("Não realizada")
                                avaliacao?.setVisibility(View.GONE)
                                feedbackTitulo?.setVisibility(View.GONE)
                            }
                        }
                    } else {
                        Log.d("AreaFeedback", "Error getting documents: ", task.exception)
                    }
                }
    }

    private fun alterarEstiloPagina() {
        toolbarTitle?.setText("Feedback")
        toolbarTitle?.setTextColor(resources.getColor(R.color.cinza_titulos))
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setStatusBarColor(resources.getColor(R.color.amarelo_estrategias))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            txtTituloEstrategia?.setText(Html.fromHtml(tituloEstrategia, Html.FROM_HTML_MODE_COMPACT))
        } else {
            txtTituloEstrategia?.setText(Html.fromHtml(tituloEstrategia))
        }
    }

    private fun iniciarToolbar() {
        setSupportActionBar(toolbar)
        navigationView?.bringToFront()
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