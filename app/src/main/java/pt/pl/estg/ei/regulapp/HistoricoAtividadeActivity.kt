package pt.pl.estg.ei.regulapp

import android.os.Bundle
import pt.pl.estg.ei.regulapp.R.layout
import android.text.format.DateFormat
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class HistoricoAtividadeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_historico_atividade)
        val diaMes: TextView = findViewById<View?>(R.id.textViewDiaMes) as TextView
        val primeiroDiaMes: TextView = findViewById<View?>(R.id.textViewIntervalo) as TextView
        val radioDiasSemana: RadioGroup = findViewById<View?>(R.id.radioGroup) as RadioGroup
        val radio7: RadioButton = findViewById<View>(R.id.radio7) as RadioButton
        val radio6: RadioButton = findViewById<View>(R.id.radio6) as RadioButton
        val radio5: RadioButton = findViewById<View>(R.id.radio5) as RadioButton
        val radio4: RadioButton = findViewById<View>(R.id.radio4) as RadioButton
        val radio3: RadioButton = findViewById<View>(R.id.radio3) as RadioButton
        val radio2: RadioButton = findViewById<View>(R.id.radio2) as RadioButton
        val radio1: RadioButton = findViewById<View>(R.id.radio1) as RadioButton
        val calendarioDiaAtual: Calendar = Calendar.getInstance()
        val calendarioAux: Calendar = Calendar.getInstance()

        //Preencher radio com os dias da semana
        radio7.text = getDiaPT(calendarioDiaAtual)
        calendarioAux.add(Calendar.DAY_OF_YEAR, -1)
        radio6.text = getDiaPT(calendarioAux)
        calendarioAux.add(Calendar.DAY_OF_YEAR, -1)
        radio5.text = getDiaPT(calendarioAux)
        calendarioAux.add(Calendar.DAY_OF_YEAR, -1)
        radio4.text = getDiaPT(calendarioAux)
        calendarioAux.add(Calendar.DAY_OF_YEAR, -1)
        radio3.text = getDiaPT(calendarioAux)
        calendarioAux.add(Calendar.DAY_OF_YEAR, -1)
        radio2.text = getDiaPT(calendarioAux)
        calendarioAux.add(Calendar.DAY_OF_YEAR, -1)
        radio1.text = getDiaPT(calendarioAux)
        val dia: String? = DateFormat.format("EEEE", calendarioDiaAtual) as String? //MONDAY
        val numeroDia: String? = DateFormat.format("dd", calendarioDiaAtual) as String? // 20
        val numeroMes: String? = DateFormat.format("MM", calendarioDiaAtual) as String? // 06
        diaMes.text = numeroDia + "-" + numeroMes
        val primeiroNnumeroDia: String? = DateFormat.format("dd", calendarioAux) as String? // 20
        val primeiroNumeroMes: String? = DateFormat.format("MM", calendarioAux) as String? // 06
        primeiroDiaMes.text = primeiroNnumeroDia + "-" + primeiroNumeroMes + " a " + numeroDia + "-" + numeroMes
    }

    private fun getDiaPT(calendario: Calendar): String? {
        val dia: Int = calendario.get(Calendar.DAY_OF_WEEK)
        return when (dia) {
            Calendar.SUNDAY ->  "Dom"
            Calendar.MONDAY ->  "Seg"
            Calendar.TUESDAY -> "Ter"
            Calendar.WEDNESDAY -> "Qua"
            Calendar.THURSDAY -> "Qui"
            Calendar.FRIDAY -> "Sex"
            Calendar.SATURDAY -> "Sab"
            else -> null
        }
    }
}