package pt.pl.estg.ei.regulapp.adapters

import android.widget.TextView
import pt.pl.estg.ei.regulapp.R
import pt.pl.estg.ei.regulapp.GlobalSettings
import android.content.Intent
import android.widget.ArrayAdapter
import android.os.Build
import android.text.Html
import pt.pl.estg.ei.regulapp.AreaFeedbackEstrategiaActivity
import pt.pl.estg.ei.regulapp.R.layout
import android.content.Context
import android.view.*
import pt.pl.estg.ei.regulapp.classes.*
import java.util.ArrayList

class HistoricoFeedbackAdapter constructor(context: Context, private val comentarios: ArrayList<String?>, private val data: ArrayList<String?>?, private val feedbacks: ArrayList<FeedbackEstrategia?>?) : ArrayAdapter<Any?>(context, layout.areas_estrategias_listview, R.id.lvComentario,
    comentarios as List<Any?>
) {
    private val session: Session? = null
    public override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = context.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val row: View = layoutInflater?.inflate(layout.areas_estrategias_listview, parent, false)
        val textViewComentarios: TextView? = row.findViewById(R.id.lvComentario)
        val textViewData: TextView? = row.findViewById(R.id.lvData)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textViewComentarios?.setText(Html.fromHtml(comentarios?.get(position), Html.FROM_HTML_MODE_COMPACT))
        } else {
            textViewComentarios?.setText(Html.fromHtml(comentarios?.get(position)))
        }
        textViewData?.setText(data?.get(position))
        row.setOnClickListener {
            val intent: Intent?
            intent = Intent(context, AreaFeedbackEstrategiaActivity::class.java)
            intent.putExtra("titulo", comentarios?.get(position))
            intent.putExtra("idFeedback", feedbacks?.get(position)?.id.toString() + "")
            val idCrianca: String? =
                GlobalSettings.Companion.myAppInstance?.session?.thisSession?.id
            intent.putExtra("idCrianca", idCrianca)
            context.startActivity(intent)
        }
        return row
    }
}