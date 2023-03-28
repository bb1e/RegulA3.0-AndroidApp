package pt.pl.estg.ei.regulapp.adapters

import android.widget.TextView
import pt.pl.estg.ei.regulapp.R
import android.annotation.SuppressLint
import android.widget.ArrayAdapter
import pt.pl.estg.ei.regulapp.R.layout
import android.content.Context
import android.view.*
import pt.pl.estg.ei.regulapp.classes.*
import java.util.ArrayList

class AreasEstrategiasAdapter constructor(context: Context, private val comentarios: ArrayList<String?>, private val data: ArrayList<String?>) : ArrayAdapter<Any>(context, layout.areas_estrategias_listview, R.id.lvComentario,
    comentarios as List<Any>
) {
    private val session: Session? = null
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = context.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val row: View = layoutInflater.inflate(layout.areas_estrategias_listview, parent, false)
        val textViewComentarios: TextView? = row.findViewById(R.id.lvComentario)
        val textViewData: TextView? = row.findViewById(R.id.lvData)
        textViewComentarios?.text = comentarios[position]
        textViewData?.text = data[position]
        if ((comentarios[position] == "Nenhum comentário a apresentar!")) {
            textViewData?.visibility = View.GONE
        }
        return row
    }
}