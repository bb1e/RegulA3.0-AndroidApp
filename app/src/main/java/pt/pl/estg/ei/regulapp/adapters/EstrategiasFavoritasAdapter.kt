package pt.pl.estg.ei.regulapp.adapters

import pt.pl.estg.ei.regulapp.R
import pt.pl.estg.ei.regulapp.GlobalSettings
import android.content.Intent
import pt.pl.estg.ei.regulapp.EstrategiasPreferidas
import com.google.firebase.firestore.FirebaseFirestore
import pt.pl.estg.ei.regulapp.enums.BD
import pt.pl.estg.ei.regulapp.classes.Crianca
import pt.pl.estg.ei.regulapp.classes.Estrategia
import com.like.LikeButton
import com.like.OnLikeListener
import pt.pl.estg.ei.regulapp.FeedbackEstrategiaActivity
import pt.pl.estg.ei.regulapp.AreaEstrategia
import android.os.Build
import android.text.Html
import pt.pl.estg.ei.regulapp.R.layout
import android.content.Context
import android.util.Log
import android.view.*
import android.widget.*
import java.util.ArrayList

/**
 * Created by joel on 3/3/18.
 */
class EstrategiasFavoritasAdapter constructor(context: Context, estrategias: ArrayList<Estrategia?>, estrategiasPreferidas: ArrayList<Estrategia?>?) : ArrayAdapter<Any?>(context, layout.custom_listview, R.id.title,
    estrategias as List<Any?>
) {
    private val rTitle: ArrayList<String?>?
    private val idEstrategias: ArrayList<Long?>?
    private val estrategias: ArrayList<Estrategia?>?
    private val estrategiasPreferidas: ArrayList<Estrategia?>?
    private val thisAdapter: EstrategiasFavoritasAdapter? = null
    public override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = context.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val row: View = layoutInflater.inflate(layout.custom_listview, parent, false)
        val myTitle: TextView? = row.findViewById(R.id.title)
        val myButton: LikeButton = row.findViewById(R.id.like_button)
        val feedbackButton: ImageView? = row.findViewById(R.id.feedback_button)
        myButton.setUnlikeDrawable(context.getResources().getDrawable(R.drawable.custom_heart))
        myButton.setLikeDrawable(context.getResources().getDrawable(R.drawable.custom_heart_filled))
        myButton.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton?) {
                Toast.makeText(context, "Adicionado ao Favoritos", Toast.LENGTH_SHORT).show()
                val TAG: String = "Adapter "
                Log.d(TAG, "" + rTitle?.get(position))
                GlobalSettings.Companion.myAppInstance?.session?.thisSession?.addEstrategiaFavorita(estrategias?.get(position))
            }

            public override fun unLiked(likeButton: LikeButton?) {
                Toast.makeText(getContext(), "Retirado dos Favoritos", Toast.LENGTH_SHORT).show()
                val here: Int = GlobalSettings.Companion.myAppInstance?.session?.thisSession?.removeEstrategiaFavorita(estrategias?.get(position))!!
                val intent: Intent = Intent(context, EstrategiasPreferidas::class.java)
                context.startActivity(intent)
            }
        })
        feedbackButton?.setOnClickListener(View.OnClickListener {
            val intent: Intent?
            intent = Intent(context, FeedbackEstrategiaActivity::class.java)
            intent.putExtra("titulo", rTitle?.get(position))
            intent.putExtra("idEstrategia", idEstrategias?.get(position))
            val idCrianca: String = GlobalSettings.Companion.myAppInstance?.session?.thisSession?.id!!
            intent.putExtra("idCrianca", idCrianca)
            context.startActivity(intent)
        })
        myTitle?.setOnClickListener(View.OnClickListener {
            val intent: Intent = Intent(context, AreaEstrategia::class.java)
            intent.putExtra("titulo", rTitle?.get(position))
            intent.putExtra("idEstrategia", idEstrategias?.get(position).toString() + "")
            val idCrianca: String? = GlobalSettings.Companion.myAppInstance?.session?.thisSession?.id
            intent.putExtra("idCrianca", idCrianca)
            intent.putExtra("ao", estrategias?.get(position)?.titulo)
            intent.putExtra("tipoAlvo", estrategias?.get(position)?.tipoAlvo.toString())
            context.startActivity(intent)
        })


        // now set our resources on views

        //myTitle.setText(rTitle.get(position));
        // ====> Html
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            myTitle?.setText(Html.fromHtml(rTitle?.get(position), Html.FROM_HTML_MODE_COMPACT))
        } else {
            myTitle?.setText(Html.fromHtml(rTitle?.get(position)))
        }
        if (estrategiasPreferidas != null) {
            for (estrategia: Estrategia? in estrategiasPreferidas) {
                if ((estrategia?.descricao == rTitle?.get(position))) {
                    myButton.setLiked(true)
                }
            }
        } else {
            myButton.setLiked(true)
        }
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val idCrianca: String = GlobalSettings.Companion.myAppInstance?.session?.thisSession?.id!!
        db
                .collection(BD.criancas_test.toString())
                .whereEqualTo("id", idCrianca)
                .get().addOnCompleteListener { task ->
                Log.d("FAV", "Success getting crianca document: ")
                if (task.isSuccessful) {
                    val criancas: MutableList<Crianca?>? =
                        task.result!!.toObjects(Crianca::class.java)
                    if (!(criancas?.isEmpty()!!)) {
                        val crianca: Crianca? = criancas.get(0)
                        Log.d("FAV", "Success getting crianca document: " + crianca?.nome)
                        val estrategiasPref: ArrayList<Estrategia?>? = crianca?.estrategiasFavoritas

                        for (i in estrategiasPref?.indices!!) {
                            Log.d(
                                "FAV",
                                "estrategiasPref(" + i + ") = " + estrategiasPref.get(i)?.id
                            )
                            val idFav: String? = "" + estrategiasPref.get(i)?.id
                            val thisRow: String? = idEstrategias?.get(position).toString() + ""
                            if ((idFav == thisRow)) {
                                myButton.setLiked(true)
                            }
                        }
                    }
                }
            }
        return row
    }

    init {
        val nomes: ArrayList<String?> = ArrayList<String?>()
        for (estrategia: Estrategia? in estrategias) {
            nomes.add(estrategia?.descricao)
        }
        rTitle = nomes
        this.estrategias = estrategias
        this.estrategiasPreferidas = estrategiasPreferidas
        val ids: ArrayList<Long?> = ArrayList()
        for (estrategia: Estrategia? in estrategias) {
            ids.add(estrategia?.id)
        }
        idEstrategias = ids
    }
}