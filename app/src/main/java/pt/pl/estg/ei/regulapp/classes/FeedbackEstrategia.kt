package pt.pl.estg.ei.regulapp.classes

import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

class FeedbackEstrategia {
    var id: Long = 0
    var idEstrategia: String? = null
    var idCrianca: String? = null
    var realizou: Boolean = false
    var avaliacao: Int = 0

    /*
        -1: Nada selecionado
         1: "Mau"
         3: "Indiferente
         5: "Bom
     */
    var comentario: String? = null
    var data: Data? = null

    constructor() {}
    constructor(idCrianca: String?, idEstrategia: String?, realizou: Boolean, avaliacao: Int, comentario: String?, data: Data?) {
        this.idEstrategia = idEstrategia
        this.idCrianca = idCrianca
        this.realizou = realizou
        this.avaliacao = avaliacao
        this.comentario = comentario
        this.data = data
        id = System.currentTimeMillis()
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        db.collection("feedbackEstrategias_demo").document(id.toString() + "").set(this).addOnSuccessListener {
            Log.d(
                "ListaFeedbackEstrategias",
                "done"
            )
        }.addOnFailureListener { e ->
            Log.w(
                "ListaFeedbackEstrategias",
                "Error adding document: Feedback Estrategia",
                e
            )
        }
    }

}