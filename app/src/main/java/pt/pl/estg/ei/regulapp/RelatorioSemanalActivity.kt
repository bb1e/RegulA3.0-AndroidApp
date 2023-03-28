package pt.pl.estg.ei.regulapp

import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import android.content.Intent
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.firestore.FirebaseFirestore
import pt.pl.estg.ei.regulapp.R.layout
import com.google.firebase.firestore.QueryDocumentSnapshot
import android.text.format.DateFormat
import android.util.Log
import android.view.*
import android.widget.*
import com.xw.repo.BubbleSeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import pt.pl.estg.ei.regulapp.classes.*
import java.util.*

class RelatorioSemanalActivity constructor() : AppCompatActivity() {
    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    private var toolbar: Toolbar? = null
    private var toolbarSubtittle: TextView? = null
    private var toolbarTitleWithSubtitle: TextView? = null
    private var toolbarTitle: TextView? = null
    private val nomeEstrategia: TextView? = null
    private var enviarRelatorio: AppCompatButton? = null
    private var av1: BubbleSeekBar? = null
    private var av2: BubbleSeekBar? = null
    private var av3: BubbleSeekBar? = null
    private var av4: BubbleSeekBar? = null
    private var av5: BubbleSeekBar? = null
    private var av6: BubbleSeekBar? = null
    private var av7: BubbleSeekBar? = null
    private var comentario: EditText? = null
    private var toolbarAnterior: ImageView? = null
    private var toolbarSeguinte: ImageView? = null
    private val semana: String? = null
    private var idCrianca: String? = null
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_relatorio_semanal)
        toolbarTitle = findViewById(R.id.toolbar_title)
        toolbarTitleWithSubtitle = findViewById(R.id.toolbar_title_with_subtitle)
        toolbarSubtittle = findViewById(R.id.toolbar_subTitle)
        toolbar = findViewById(R.id.toolbarRelatorioSemanal)
        av1 = findViewById(R.id.bubbleSeekBarAv1)
        av2 = findViewById(R.id.bubbleSeekBarAv2)
        av3 = findViewById(R.id.bubbleSeekBarAv3)
        av4 = findViewById(R.id.bubbleSeekBarAv4)
        av5 = findViewById(R.id.bubbleSeekBarAv5)
        av6 = findViewById(R.id.bubbleSeekBarAv6)
        av7 = findViewById(R.id.bubbleSeekBarAv7)
        comentario = findViewById(R.id.editTextRSComentario)
        toolbarAnterior = findViewById(R.id.toolbar_btnAnterior)
        toolbarSeguinte = findViewById(R.id.toolbar_btnSeguinte)
        enviarRelatorio = findViewById(R.id.btnEnviarRelatorioSemana)
        toolbarSeguinte?.setVisibility(View.INVISIBLE)
        toolbarAnterior?.setVisibility(View.VISIBLE)
        mudarStatusBarColor()
        idCrianca = GlobalSettings.Companion.myAppInstance?.session?.thisSession?.id
        db
                .collection("relatorioSemanal")
                .whereEqualTo("idCrianca", idCrianca)
                .get().addOnCompleteListener { task ->
                Log.d("FeedbackSemanaActivity", "Success getting documents")
                val relatorios: ArrayList<RelatorioSemanal?> = ArrayList()
                if (task.isSuccessful) {
                    for (document: QueryDocumentSnapshot? in task.result!!) {
                        val relatorio: RelatorioSemanal =
                            document?.toObject(RelatorioSemanal::class.java)!!
                        relatorios.add(relatorio)
                        Log.d(
                            "RelatorioSemanal",
                            "Success getting Relatorios Semanais: " + relatorio.semana
                        )
                    }
                    val relatorioSemanaAtual: RelatorioSemanal? = findRelatorioAtualNaDB(relatorios)
                    val relatorioSemanaPassada: RelatorioSemanal? = findRelatorioPassadoNaDB(relatorios)
                    if (relatorioSemanaAtual == null) {
                        av1?.setEnabled(true)
                        av2?.setEnabled(true)
                        av3?.setEnabled(true)
                        av4?.setEnabled(true)
                        av5?.setEnabled(true)
                        av6?.setEnabled(true)
                        av7?.setEnabled(true)
                        av1?.setProgress(1f)
                        av2?.setProgress(1f)
                        av3?.setProgress(1f)
                        av4?.setProgress(1f)
                        av5?.setProgress(1f)
                        av6?.setProgress(1f)
                        av7?.setProgress(1f)
                        enviarRelatorio?.setEnabled(true)
                        comentario?.setEnabled(true)
                        toolbarSubtittle?.setText(getSemana(false))
                    } else {
                        av1?.setEnabled(false)
                        av2?.setEnabled(false)
                        av3?.setEnabled(false)
                        av4?.setEnabled(false)
                        av5?.setEnabled(false)
                        av6?.setEnabled(false)
                        av7?.setEnabled(false)
                        enviarRelatorio?.setEnabled(false)
                        comentario?.setEnabled(false)
                        toolbarSubtittle?.setText(relatorioSemanaAtual.semana)
                        av1?.setProgress(relatorioSemanaAtual.avaliacao1.toFloat())
                        av2?.setProgress(relatorioSemanaAtual.avaliacao2.toFloat())
                        av3?.setProgress(relatorioSemanaAtual.avaliacao3.toFloat())
                        av4?.setProgress(relatorioSemanaAtual.avaliacao4.toFloat())
                        av5?.setProgress(relatorioSemanaAtual.avaliacao5.toFloat())
                        av6?.setProgress(relatorioSemanaAtual.avaliacao6.toFloat())
                        av7?.setProgress(relatorioSemanaAtual.avaliacao7.toFloat())
                        comentario?.setText(relatorioSemanaAtual.comentario)
                    }
                    toolbarAnterior?.setOnClickListener {
                        if (toolbarAnterior?.getVisibility() == View.VISIBLE) {
                            toolbarAnterior?.setVisibility(View.INVISIBLE)
                            toolbarSeguinte?.setVisibility(View.VISIBLE)
                            if (relatorioSemanaPassada == null) {
                                av1?.setEnabled(true)
                                av2?.setEnabled(true)
                                av3?.setEnabled(true)
                                av4?.setEnabled(true)
                                av5?.setEnabled(true)
                                av6?.setEnabled(true)
                                av7?.setEnabled(true)
                                av1?.setProgress(1f)
                                av2?.setProgress(1f)
                                av3?.setProgress(1f)
                                av4?.setProgress(1f)
                                av5?.setProgress(1f)
                                av6?.setProgress(1f)
                                av7?.setProgress(1f)
                                enviarRelatorio?.setEnabled(true)
                                comentario?.setEnabled(true)
                                comentario?.setText("")
                                toolbarSubtittle?.setText(getSemana(true))
                            } else {
                                av1?.setEnabled(false)
                                av2?.setEnabled(false)
                                av3?.setEnabled(false)
                                av4?.setEnabled(false)
                                av5?.setEnabled(false)
                                av6?.setEnabled(false)
                                av7?.setEnabled(false)
                                enviarRelatorio?.setEnabled(false)
                                comentario?.setEnabled(false)
                                toolbarSubtittle?.setText(relatorioSemanaPassada.semana)
                                av1?.setProgress(
                                    relatorioSemanaPassada.avaliacao1.toFloat()
                                )
                                av2?.setProgress(
                                    relatorioSemanaPassada.avaliacao2.toFloat()
                                )
                                av3?.setProgress(
                                    relatorioSemanaPassada.avaliacao3.toFloat()
                                )
                                av4?.setProgress(
                                    relatorioSemanaPassada.avaliacao4.toFloat()
                                )
                                av5?.setProgress(
                                    relatorioSemanaPassada.avaliacao5.toFloat()
                                )
                                av6?.setProgress(
                                    relatorioSemanaPassada.avaliacao6.toFloat()
                                )
                                av7?.setProgress(
                                    relatorioSemanaPassada.avaliacao7.toFloat()
                                )
                                comentario?.setText(relatorioSemanaPassada.comentario)
                            }
                        }
                    }
                    toolbarSeguinte?.setOnClickListener {
                        if (toolbarSeguinte?.getVisibility() == View.VISIBLE) {
                            toolbarAnterior?.setVisibility(View.VISIBLE)
                            toolbarSeguinte?.setVisibility(View.INVISIBLE)
                            if (relatorioSemanaAtual == null) {
                                av1?.setEnabled(true)
                                av2?.setEnabled(true)
                                av3?.setEnabled(true)
                                av4?.setEnabled(true)
                                av5?.setEnabled(true)
                                av6?.setEnabled(true)
                                av7?.setEnabled(true)
                                av1?.setProgress(1f)
                                av2?.setProgress(1f)
                                av3?.setProgress(1f)
                                av4?.setProgress(1f)
                                av5?.setProgress(1f)
                                av6?.setProgress(1f)
                                av7?.setProgress(1f)
                                enviarRelatorio?.setEnabled(true)
                                comentario?.setEnabled(true)
                                comentario?.setText("")
                                toolbarSubtittle?.setText(getSemana(false))
                            } else {
                                av1?.setEnabled(false)
                                av2?.setEnabled(false)
                                av3?.setEnabled(false)
                                av4?.setEnabled(false)
                                av5?.setEnabled(false)
                                av6?.setEnabled(false)
                                av7?.setEnabled(false)
                                enviarRelatorio?.setEnabled(false)
                                comentario?.setEnabled(false)
                                toolbarSubtittle?.setText(relatorioSemanaAtual.semana)
                                av1?.setProgress(relatorioSemanaAtual.avaliacao1.toFloat())
                                av2?.setProgress(relatorioSemanaAtual.avaliacao2.toFloat())
                                av3?.setProgress(relatorioSemanaAtual.avaliacao3.toFloat())
                                av4?.setProgress(relatorioSemanaAtual.avaliacao4.toFloat())
                                av5?.setProgress(relatorioSemanaAtual.avaliacao5.toFloat())
                                av6?.setProgress(relatorioSemanaAtual.avaliacao6.toFloat())
                                av7?.setProgress(relatorioSemanaAtual.avaliacao7.toFloat())
                                comentario?.setText(relatorioSemanaAtual.comentario)
                            }
                        }
                    }
                }
            }
        enviarRelatorio?.setOnClickListener(View.OnClickListener { v: View? ->
            enviarRelatorio(
                idCrianca,
                av1?.getProgress()!!,
                av2?.getProgress()!!,
                av3?.getProgress()!!,
                av4?.getProgress()!!,
                av5?.getProgress()!!,
                av6?.getProgress()!!,
                av7?.getProgress()!!,
                comentario?.getText().toString().trim({ it <= ' ' }),
                toolbarSubtittle?.getText().toString().trim({ it <= ' ' })
            )
        })
        alterarTitulosToolbar()
        drawerLayout = findViewById(R.id.drawer_layout_relatorio_semanal)
        navigationView = findViewById(R.id.nav_view_relatorio_semanal)
        iniciarToolbar()

        if(savedInstanceState == null) {
            Sidebar.installSidebar(supportFragmentManager,drawerLayout!!,navigationView!!);
        }
    }

    override fun onResume() {
        super.onResume()
        Analytics.fireEvent(AnalyticsEvent.RELATORIOSEMANAL_OPENMENU)
    }

    private fun mudarStatusBarColor() {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setStatusBarColor(getResources().getColor(R.color.laranja_relatorio))
    }

    private fun enviarRelatorio(idCrianca: String?, av1: Int, av2: Int, av3: Int, av4: Int, av5: Int, av6: Int, av7: Int, comentario: String?, semana: String?) {
        var inicioFimSemana: ArrayList<Data?>? = ArrayList()
        if ((semana == getSemana(true))) {
            inicioFimSemana = getSemanaDataFormat(true)
        }
        if ((semana == getSemana(false))) {
            inicioFimSemana = getSemanaDataFormat(false)
        }
        if (inicioFimSemana?.size == 2) {
            val relatorio: RelatorioSemanal
            val currente: Calendar? = Calendar.getInstance()
            val data: Data = Data(
                    currente?.get(Calendar.DAY_OF_MONTH)!!,
                    currente.get(Calendar.MONTH) + 1,
                    currente.get(Calendar.YEAR),
                    currente.get(Calendar.HOUR_OF_DAY),
                    currente.get(Calendar.MINUTE))
            relatorio = RelatorioSemanal(idCrianca, data, semana, inicioFimSemana.get(0), inicioFimSemana.get(1), av1, av2, av3, av4, av5, av6, av7, comentario)
            Toast.makeText(this, "Relatório semanal enviado", Toast.LENGTH_SHORT).show()
            Analytics.fireEvent(AnalyticsEvent.RELATORIOSEMANAL_SENT(relatorio))
            super.onBackPressed()
        }
    }

    private fun alterarTitulosToolbar() {
        toolbarTitle?.setVisibility(View.GONE)
        toolbarTitleWithSubtitle?.setVisibility(View.VISIBLE)
        toolbarSubtittle?.setVisibility(View.VISIBLE)
        toolbarTitleWithSubtitle?.setText("Registo Semanal")
        toolbarTitleWithSubtitle?.setTextColor(getResources().getColor(R.color.toolbar_text))
        toolbarSubtittle?.setText("Semana de dd/mm a dd/mm")
        toolbarSubtittle?.setText(getSemana(false))
    }

    private fun iniciarToolbar() {
        setSupportActionBar(toolbar)
        navigationView?.bringToFront()
        //Auto selecionar Relatorio Semanal
        navigationView?.setCheckedItem(R.id.nav_registo_semanal)
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

    private fun getSemana(isLastWeek: Boolean): String? {
        //devolder primeiro dia da semana
        val calendarioUltimoDia: Calendar = Calendar.getInstance()
        val calendarioPrimeiroDia: Calendar = Calendar.getInstance()
        val primeiroDia: Int = calendarioUltimoDia.get(Calendar.DAY_OF_WEEK) //primeiroDia = dia de hoje
        var str: String? = null
        when (primeiroDia) {
            Calendar.SATURDAY -> if (!isLastWeek) {
                calendarioPrimeiroDia.add(Calendar.DAY_OF_YEAR, -5)
                calendarioUltimoDia.add(Calendar.DAY_OF_YEAR, 1)
            } else {
                //TODO
                calendarioPrimeiroDia.add(Calendar.DAY_OF_YEAR, -12)
                calendarioUltimoDia.add(Calendar.DAY_OF_YEAR, -6)
            }
            Calendar.SUNDAY -> if (!isLastWeek) {
                calendarioPrimeiroDia.add(Calendar.DAY_OF_YEAR, -6)
            } else {
                calendarioPrimeiroDia.add(Calendar.DAY_OF_YEAR, -13)
                calendarioUltimoDia.add(Calendar.DAY_OF_YEAR, -7)
            }
            Calendar.MONDAY -> if (!isLastWeek) {
                calendarioPrimeiroDia.add(Calendar.DAY_OF_YEAR, -7)
                calendarioUltimoDia.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                calendarioPrimeiroDia.add(Calendar.DAY_OF_YEAR, -14)
                calendarioUltimoDia.add(Calendar.DAY_OF_YEAR, -8)
            }
            Calendar.TUESDAY -> if (!isLastWeek) {
                calendarioPrimeiroDia.add(Calendar.DAY_OF_YEAR, -8)
                calendarioUltimoDia.add(Calendar.DAY_OF_YEAR, -2)
            } else {
                calendarioPrimeiroDia.add(Calendar.DAY_OF_YEAR, -15)
                calendarioUltimoDia.add(Calendar.DAY_OF_YEAR, -9)
            }
            Calendar.WEDNESDAY -> if (!isLastWeek) {
                calendarioPrimeiroDia.add(Calendar.DAY_OF_YEAR, -9)
                calendarioUltimoDia.add(Calendar.DAY_OF_YEAR, -3)
            } else {
                calendarioPrimeiroDia.add(Calendar.DAY_OF_YEAR, -16)
                calendarioUltimoDia.add(Calendar.DAY_OF_YEAR, -10)
            }
            Calendar.THURSDAY -> if (!isLastWeek) {
                calendarioPrimeiroDia.add(Calendar.DAY_OF_YEAR, -10)
                calendarioUltimoDia.add(Calendar.DAY_OF_YEAR, -4)
            } else {
                calendarioPrimeiroDia.add(Calendar.DAY_OF_YEAR, -17)
                calendarioUltimoDia.add(Calendar.DAY_OF_YEAR, -11)
            }
            Calendar.FRIDAY -> if (!isLastWeek) {
                calendarioPrimeiroDia.add(Calendar.DAY_OF_YEAR, -4)
                calendarioUltimoDia.add(Calendar.DAY_OF_YEAR, 2)
            } else {
                calendarioPrimeiroDia.add(Calendar.DAY_OF_YEAR, -11)
                calendarioUltimoDia.add(Calendar.DAY_OF_YEAR, -5)
            }
        }
        val primeiroDiaSemana: String? = DateFormat.format("dd", calendarioPrimeiroDia) as String? // 20
        val mesPrimeiroDiaSemana: String? = DateFormat.format("MM", calendarioPrimeiroDia) as String? // 06
        val anoPrimeiroDiaSemana: String? = DateFormat.format("yyyy", calendarioPrimeiroDia) as String? // 06
        val ultimoDiaSemana: String? = DateFormat.format("dd", calendarioUltimoDia) as String? // 20
        val mesUltimoDiaSemana: String? = DateFormat.format("MM", calendarioUltimoDia) as String? // 06
        val anoUltimoDiaSemana: String? = DateFormat.format("yyyy", calendarioUltimoDia) as String? // 06

        // "Semana de dd/mm a dd/mm"
        str = ("Semana de " + primeiroDiaSemana + "/" + mesPrimeiroDiaSemana + "/" + anoPrimeiroDiaSemana + " a " +
                ultimoDiaSemana + "/" + mesUltimoDiaSemana + "/" + anoUltimoDiaSemana)
        return str.trim({ it <= ' ' })
    }

    private fun getSemanaDataFormat(isLastWeek: Boolean): ArrayList<Data?>? {
        //devolder primeiro dia da semana
        val calendarioUltimoDia: Calendar = Calendar.getInstance()
        val calendarioPrimeiroDia: Calendar = Calendar.getInstance()
        val primeiroDia: Int = calendarioUltimoDia.get(Calendar.DAY_OF_WEEK) //primeiroDia = dia de hoje
        val str: String? = null
        when (primeiroDia) {
            Calendar.SATURDAY -> if (!isLastWeek) {
                calendarioPrimeiroDia.add(Calendar.DAY_OF_YEAR, -5)
                calendarioUltimoDia.add(Calendar.DAY_OF_YEAR, 1)
            } else {
                //TODO
                calendarioPrimeiroDia.add(Calendar.DAY_OF_YEAR, -12)
                calendarioUltimoDia.add(Calendar.DAY_OF_YEAR, -6)
            }
            Calendar.SUNDAY -> if (!isLastWeek) {
                calendarioPrimeiroDia.add(Calendar.DAY_OF_YEAR, -6)
            } else {
                calendarioPrimeiroDia.add(Calendar.DAY_OF_YEAR, -13)
                calendarioUltimoDia.add(Calendar.DAY_OF_YEAR, -7)
            }
            Calendar.MONDAY -> if (!isLastWeek) {
                calendarioPrimeiroDia.add(Calendar.DAY_OF_YEAR, -7)
                calendarioUltimoDia.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                calendarioPrimeiroDia.add(Calendar.DAY_OF_YEAR, -14)
                calendarioUltimoDia.add(Calendar.DAY_OF_YEAR, -8)
            }
            Calendar.TUESDAY -> if (!isLastWeek) {
                calendarioPrimeiroDia.add(Calendar.DAY_OF_YEAR, -8)
                calendarioUltimoDia.add(Calendar.DAY_OF_YEAR, -2)
            } else {
                calendarioPrimeiroDia.add(Calendar.DAY_OF_YEAR, -15)
                calendarioUltimoDia.add(Calendar.DAY_OF_YEAR, -9)
            }
            Calendar.WEDNESDAY -> if (!isLastWeek) {
                calendarioPrimeiroDia.add(Calendar.DAY_OF_YEAR, -9)
                calendarioUltimoDia.add(Calendar.DAY_OF_YEAR, -3)
            } else {
                calendarioPrimeiroDia.add(Calendar.DAY_OF_YEAR, -16)
                calendarioUltimoDia.add(Calendar.DAY_OF_YEAR, -10)
            }
            Calendar.THURSDAY -> if (!isLastWeek) {
                calendarioPrimeiroDia.add(Calendar.DAY_OF_YEAR, -10)
                calendarioUltimoDia.add(Calendar.DAY_OF_YEAR, -4)
            } else {
                calendarioPrimeiroDia.add(Calendar.DAY_OF_YEAR, -17)
                calendarioUltimoDia.add(Calendar.DAY_OF_YEAR, -11)
            }
            Calendar.FRIDAY -> if (!isLastWeek) {
                calendarioPrimeiroDia.add(Calendar.DAY_OF_YEAR, -4)
                calendarioUltimoDia.add(Calendar.DAY_OF_YEAR, 2)
            } else {
                calendarioPrimeiroDia.add(Calendar.DAY_OF_YEAR, -11)
                calendarioUltimoDia.add(Calendar.DAY_OF_YEAR, -5)
            }
        }
        val datasInicioFimSemana: ArrayList<Data?> = ArrayList()
        val inicio: Data = Data(calendarioPrimeiroDia?.get(Calendar.DAY_OF_MONTH)!!, calendarioPrimeiroDia.get(Calendar.MONTH), calendarioPrimeiroDia.get(Calendar.YEAR))
        val fim: Data = Data(calendarioUltimoDia?.get(Calendar.DAY_OF_MONTH)!!, calendarioUltimoDia.get(Calendar.MONTH), calendarioUltimoDia.get(Calendar.YEAR))
        datasInicioFimSemana.add(inicio)
        datasInicioFimSemana.add(fim)
        return datasInicioFimSemana
    }

    private fun findRelatorioAtualNaDB(listaRelatorios: ArrayList<RelatorioSemanal?>): RelatorioSemanal? {
        for (relatorio: RelatorioSemanal? in listaRelatorios) {
            if ((relatorio?.semana == getSemana(false))) {
                return relatorio
            }
        }
        return null
    }

    private fun findRelatorioPassadoNaDB(listaRelatorios: ArrayList<RelatorioSemanal?>): RelatorioSemanal? {
        for (relatorio: RelatorioSemanal? in listaRelatorios) {
            if ((relatorio?.semana == getSemana(true))) {
                return relatorio
            }
        }
        return null
    }
}