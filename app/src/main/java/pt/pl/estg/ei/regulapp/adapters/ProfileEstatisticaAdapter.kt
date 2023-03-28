package pt.pl.estg.ei.regulapp.adapters

import android.widget.TextView
import pt.pl.estg.ei.regulapp.R
import pt.pl.estg.ei.regulapp.GlobalSettings
import android.content.Intent
import android.widget.ArrayAdapter
import pt.pl.estg.ei.regulapp.AreaEstrategia
import android.os.Build
import android.text.Html
import pt.pl.estg.ei.regulapp.R.layout
import android.content.Context
import android.view.*
import pt.pl.estg.ei.regulapp.classes.*
import java.util.ArrayList

class ProfileEstatisticaAdapter constructor(context: Context, private val nomeEstrategias: ArrayList<String?>, private val qtdFeedback: ArrayList<Int?>, private val estrategiasOrdenadas: ArrayList<Estrategia?>?, private val idEstrategias: ArrayList<Long?>?) : ArrayAdapter<Any?>(context, layout.profile_estatisticas_listview, R.id.lvNomeEstrategias,
    nomeEstrategias as List<Any?>
) {
    private val session: Session? = null
    public override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater? = context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val row: View = layoutInflater?.inflate(layout.profile_estatisticas_listview, parent, false)!!
        val textViewNomeEstrategias: TextView = row.findViewById(R.id.lvNomeEstrategias)
        val classificao: TextView = row.findViewById(R.id.lvClassificacao)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textViewNomeEstrategias.setText(Html.fromHtml(nomeEstrategias.get(position), Html.FROM_HTML_MODE_COMPACT))
        } else {
            textViewNomeEstrategias.setText(Html.fromHtml(nomeEstrategias.get(position)))
        }
        if ((nomeEstrategias.get(position) == "Nenhuma estratégia com feedback dado")) {
            classificao.setVisibility(View.GONE)
        } else {
            classificao.setVisibility(View.VISIBLE)

            /* Com a classificacao ordenada de estrategias com mais feedback
             */classificao.setText("${(position + 1)}.")

            /* Mostrar qtd feedback
            classificao.setText("" + qtdFeedback.get(position));
             */
        }
        row.setOnClickListener {
            if (nomeEstrategias.get(position) != "Nenhuma estratégia com feedback dado") {
                val intent: Intent?
                intent = Intent(context, AreaEstrategia::class.java)
                intent.putExtra("titulo", nomeEstrategias.get(position))
                intent.putExtra("idEstrategia", idEstrategias?.get(position).toString() + "")
                val idCrianca: String =
                    GlobalSettings.Companion.myAppInstance?.session?.thisSession?.id!!
                intent.putExtra("idCrianca", idCrianca)
                intent.putExtra("ao", estrategiasOrdenadas?.get(position)?.titulo)
                intent.putExtra(
                    "tipoAlvo",
                    estrategiasOrdenadas?.get(position)?.tipoAlvo.toString()
                )
                context.startActivity(intent)
            }
        }
        return row
    }
}