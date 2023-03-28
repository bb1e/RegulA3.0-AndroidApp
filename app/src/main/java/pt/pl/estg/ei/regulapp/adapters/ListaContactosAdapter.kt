package pt.pl.estg.ei.regulapp.adapters

import pt.pl.estg.ei.regulapp.R
import android.annotation.SuppressLint
import android.widget.ArrayAdapter
import pt.pl.estg.ei.regulapp.R.layout
import android.content.Context
import android.view.*
import pt.pl.estg.ei.regulapp.chat.*
import java.util.ArrayList

class ListaContactosAdapter constructor(private val c: Context, contactsList: ArrayList<Contacts?>) : ArrayAdapter<Any?>(c, layout.users_display_layout, R.id.title,
    contactsList as List<Any?>
) {
    @SuppressLint("ViewHolder")
    public override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = c.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val row: View? = layoutInflater.inflate(layout.users_display_layout, parent, false)
        return super.getView(position, convertView, parent)
    }
}