package pt.pl.estg.ei.regulapp.classes

import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

class RelatorioSemanal {
    var id: Long = 0
    var idCrianca: String? = null
    var data: Data? = null
    var semana: String? = null
    var avaliacao1: Int = 0
    var avaliacao2: Int = 0
    var avaliacao3: Int = 0
    var avaliacao4: Int = 0
    var avaliacao5: Int = 0
    var avaliacao6: Int = 0
    var avaliacao7: Int = 0
    var comentario: String? = null
    var diaInicioSemana: Data? = null
    var diaFimSemana: Data? = null

    constructor() {}
    constructor(idCrianca: String?, data: Data?, semana: String?, diaInicioSemana: Data?, diaFimSemana: Data?, avaliacao1: Int, avaliacao2: Int, avaliacao3: Int, avaliacao4: Int, avaliacao5: Int, avaliacao6: Int, avaliacao7: Int, comentario: String?) {
        this.idCrianca = idCrianca
        this.data = data
        this.semana = semana
        this.avaliacao1 = avaliacao1
        this.avaliacao2 = avaliacao2
        this.avaliacao3 = avaliacao3
        this.avaliacao4 = avaliacao4
        this.avaliacao5 = avaliacao5
        this.avaliacao6 = avaliacao6
        this.avaliacao7 = avaliacao7
        this.comentario = comentario
        this.diaInicioSemana = diaInicioSemana
        this.diaFimSemana = diaFimSemana
        id = System.currentTimeMillis()
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        db
                .collection("relatorioSemanal")
                .document(id.toString() + "")
                .set(this)
                .addOnSuccessListener { Log.d("RelatorioSemanal", "done adding Relatorio Semanal") }
            .addOnFailureListener { e ->
                Log.w(
                    "RelatorioSemanal",
                    "Error adding Relatorio Semanal doccument: ",
                    e
                )
            }
    }

}