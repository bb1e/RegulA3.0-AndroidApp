package pt.pl.estg.ei.regulapp

import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import android.content.Intent
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import android.text.TextWatcher
import android.text.Editable
import android.os.Build
import android.text.Html
import pt.pl.estg.ei.regulapp.R.layout
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.google.firebase.firestore.*
import com.hsalf.smileyrating.SmileyRating
import com.hsalf.smileyrating.SmileyRating.OnSmileySelectedListener
import pt.pl.estg.ei.regulapp.classes.*
import java.util.*

class FeedbackEstrategiaActivity : AppCompatActivity(), OnItemSelectedListener {
    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    private var toolbar: Toolbar? = null
    private var nomeEstrategia: TextView? = null
    private var enviarFeedback: AppCompatButton? = null
    private var rating: Int = 0
    private var avaliacao: SmileyRating? = null
    private var tituloFeedback: TextView? = null
    private var radioGroup: RadioGroup? = null
    private var buttonSim: RadioButton? = null
    private var buttonNao: RadioButton? = null
    private var toolbarTitle: TextView? = null
    private var comentario: EditText? = null
    private var tituloComentario: TextView? = null
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_feedback_estrategia)

        //Params passados na criacao
        val tituloEstrategia: String = intent.getSerializableExtra("titulo") as String
        val idEstrategia: Long = intent.getSerializableExtra("idEstrategia") as Long
        val idCrianca: String = intent.getSerializableExtra("idCrianca") as String
        nomeEstrategia = findViewById(R.id.textViewFBNomeEstrategia)
        enviarFeedback = findViewById(R.id.btnEnviarFeedbackEstrategia)
        avaliacao = findViewById(R.id.smile_rating_fb_estrategia)
        tituloFeedback = findViewById(R.id.textViewTituloFBEstrategia)
        tituloComentario = findViewById(R.id.textViewFAComentarios)
        radioGroup = findViewById(R.id.radioGroupPerguntaRealizou)
        buttonSim = findViewById(R.id.radioButtonSim)
        buttonNao = findViewById(R.id.radioButtonNao)
        drawerLayout = findViewById(R.id.drawer_layout_feedback)
        navigationView = findViewById(R.id.nav_view_feedback)
        toolbarTitle = findViewById(R.id.toolbar_title)
        toolbar = findViewById(R.id.toolbarFeedbackEstrategia)
        comentario = findViewById(R.id.editTextFAComentario)
        enviarFeedback?.isEnabled = false

        //nomeEstrategia.setText(tituloEstrategia);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            nomeEstrategia?.text = Html.fromHtml(tituloEstrategia, Html.FROM_HTML_MODE_COMPACT)
        } else {
            nomeEstrategia?.text = Html.fromHtml(tituloEstrategia)
        }

        //Mudar a barra de avalicao - carinhas
        mudarRatingStyle(avaliacao)

        //Mudar visibilidade
        tituloFeedback?.visibility = View.GONE
        avaliacao?.visibility = View.GONE
        comentario?.setOnKeyListener(object : View.OnKeyListener {
            /**
             * This listens for the user to press the enter button on
             * the keyboard and then hides the virtual keyboard
             */
            override fun onKey(arg0: View?, arg1: Int, event: KeyEvent?): Boolean {
                // If the event is a key-down event on the "enter" button
                if ((event?.action == KeyEvent.ACTION_DOWN) &&
                        (arg1 == KeyEvent.KEYCODE_ENTER)) {
                    val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(comentario?.windowToken, 0)
                    return true
                }
                return false
            }
        })
        comentario?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (buttonNao?.isChecked!!) {
                    enviarFeedback?.isEnabled =
                        comentario?.text.toString().trim { it <= ' ' }.isNotEmpty()
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (buttonNao?.isChecked!!) {
                    enviarFeedback?.isEnabled =
                        comentario?.text.toString().trim { it <= ' ' }.isNotEmpty()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (buttonNao?.isChecked!!) {
                    if (comentario?.text.toString().trim { it <= ' ' }.isEmpty()) {
                        enviarFeedback?.isEnabled = false
                    }
                }
            }
        })
        radioGroup?.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                enviarFeedback?.isEnabled = false
                if (buttonSim?.isChecked!!) {
                    tituloComentario?.text = "Comentários Adicionais" //não é obrigatório
                    if (rating > 0) {
                        enviarFeedback?.isEnabled = true
                    }
                    tituloFeedback?.visibility = View.VISIBLE
                    avaliacao?.visibility = View.VISIBLE
                    avaliacao?.setSmileySelectedListener(object : OnSmileySelectedListener {
                        override fun onSmileySelected(type: SmileyRating.Type?) {
                            rating = type?.rating!!
                            enviarFeedback?.isEnabled = (rating == SmileyRating.Type.BAD.rating) || (rating == SmileyRating.Type.OKAY.rating) || (rating == SmileyRating.Type.GREAT.rating)
                            Log.d("TESTE Feedbackbar", "Avaliação: " + avaliacao?.selectedSmiley)
                        }
                    })
                } else {
                    enviarFeedback?.isEnabled = false
                    tituloComentario?.text = "Comentários Adicionais (*)"
                    if (comentario?.text.toString().trim({ it <= ' ' }).isNotEmpty()) {
                        enviarFeedback?.isEnabled = true
                    }
                    tituloFeedback?.visibility = View.GONE
                    avaliacao?.visibility = View.GONE
                }
            }
        })
        enviarFeedback?.setOnClickListener(View.OnClickListener { v: View? ->
            Analytics.fireEvent(AnalyticsEvent.ESTRATEGIA_FEEDBACKENVIADO)
            enviarFeedback(
                idCrianca,
                idEstrategia.toString() + "",
                buttonSim?.isChecked!!,
                rating,
                comentario?.text.toString().trim { it <= ' ' }
            )
        })


        //menu
        toolbarTitle?.text = "Feedback da Estratégia"
        toolbarTitle?.setTextSize(TypedValue.COMPLEX_UNIT_PT, 13f)

        //toolbar
        iniciarToolbar()
        mudarStatusBarColor()
        if(savedInstanceState == null) {
            Sidebar.installSidebar(supportFragmentManager,drawerLayout!!,navigationView!!)
        }
    }

    private fun mudarStatusBarColor() {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.amarelo_estrategias)
    }

    private fun mudarRatingStyle(feedbackbar: SmileyRating?) {
        //Mudar de 5 para 3 e mudar os titulos
        //TERRIBLE, BAD, OKAY, GOOD, GREAT, NONE
        feedbackbar?.setCustomSmiley()
        feedbackbar?.setTitle(SmileyRating.Type.TERRIBLE, "Mau")
        feedbackbar?.setTitle(SmileyRating.Type.BAD, "Indiferente")
        feedbackbar?.setTitle(SmileyRating.Type.OKAY, "Bom")
        feedbackbar?.setFaceBackgroundColor(SmileyRating.Type.TERRIBLE, resources.getColor(R.color.vermelho_smiley))
        /*
        feedbackbar.setFaceBackgroundColor(SmileyRating.Type.TERRIBLE, getResources().getColor(R.color.vermelho_smiley));
        feedbackbar.setFaceBackgroundColor(SmileyRating.Type.BAD, getResources().getColor(R.color.amarelo_smiley));
        feedbackbar.setFaceBackgroundColor(SmileyRating.Type.OKAY, getResources().getColor(R.color.verde_smiley));

         */
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}
    override fun onNothingSelected(parent: AdapterView<*>?) {}
    private fun iniciarToolbar() {
        setSupportActionBar(toolbar)
        navigationView?.bringToFront()
        val titulo: TextView = findViewById(R.id.toolbar_title)
        titulo.setTextColor(resources.getColor(R.color.toolbar_text))
        titulo.setTextSize(TypedValue.COMPLEX_UNIT_PT, 13f)
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

    private fun updateFeedbackList(id: String, feedbackList: ArrayList<FeedbackEstrategia>) {
        val docRef: DocumentReference = db
                .collection("estrategias")
                .document(id)
        val map: MutableMap<String, Any> = HashMap()
        map.put("feedback", feedbackList)
        docRef.update(map)
            .addOnSuccessListener { Log.d("FEA", "onSuccess: updated the doc") }
            .addOnFailureListener { e -> Log.e("FEA-UpdateDB", "onFailure: ", e) }
    }

    private fun getAndUpdateFeedbackList(id: String, feedbackNovo: FeedbackEstrategia) {
        db
                .collection("estrategias")
                .document(id)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    Log.d("FEA-GetFromDB", "onSuccess: " + documentSnapshot.data)
                    Log.d(
                        "FEA-GetFromDB",
                        "onSuccess: " + documentSnapshot.get("feedback") as ArrayList<FeedbackEstrategia>
                    )
                    val avaliacoesOriginais: ArrayList<FeedbackEstrategia> =
                        documentSnapshot.get("feedback") as ArrayList<FeedbackEstrategia>
                    avaliacoesOriginais.add(feedbackNovo)
                    updateFeedbackList(id, avaliacoesOriginais)
                }
            .addOnFailureListener { e -> Log.e("FEA-GetFromDB", "onFailure: ", e) }
    }

    private fun enviarFeedback(idCrianca: String, idEstrategia: String, realizou: Boolean, cara: Int, comentario: String) {
        val feedback: FeedbackEstrategia
        val currente: Calendar = Calendar.getInstance()
        val dia: Int = currente.get(Calendar.DAY_OF_MONTH)
        val mes: Int = currente.get(Calendar.MONTH) + 1
        val ano: Int = currente.get(Calendar.YEAR)
        val horas: Int = currente.get(Calendar.HOUR_OF_DAY)
        val minutos: Int = currente.get(Calendar.MINUTE)
        val data: Data = Data(dia, mes, ano, horas, minutos)
        feedback = FeedbackEstrategia(idCrianca, idEstrategia, realizou, cara, comentario, data)

        //getAndUpdateFeedbackList(idEstrategia.toString(), feedback);
        Toast.makeText(applicationContext, "Feedback enviado", Toast.LENGTH_SHORT).show()
        super.onBackPressed()
    }
}