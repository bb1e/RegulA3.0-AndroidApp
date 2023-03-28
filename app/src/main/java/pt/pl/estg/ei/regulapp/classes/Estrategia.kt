package pt.pl.estg.ei.regulapp.classes

import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.firestore.FirebaseFirestore
import pt.pl.estg.ei.regulapp.enums.BD
import pt.pl.estg.ei.regulapp.enums.TipoAlvo
import android.util.Log
import java.io.Serializable
import java.lang.Exception

class Estrategia : Serializable {
    var id: Long = 0
    var titulo: String? = null
    var descricao: String? = null
    var tipoAlvo: TipoAlvo? = null
    var verificado: Boolean? = null
    var data: Long = 0

    constructor() {}
    constructor(titulo: String?, descricao: String?, tipoAlvo: TipoAlvo?) {
        this.titulo = titulo
        this.descricao = descricao
        data = System.currentTimeMillis()
        verificado = false
        this.tipoAlvo = tipoAlvo
        id = data
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        db.collection(BD.estrategias_demo.toString()).document(id.toString() + "")
                .set(this).addOnSuccessListener(object : OnSuccessListener<Void?> {
                    public override fun onSuccess(aVoid: Void?) {
                        Log.d("ListaEstrategias", "done")
                    }
                }).addOnFailureListener(object : OnFailureListener {
                    public override fun onFailure(e: Exception) {
                        Log.w("ListaEstrategias", "Error adding document", e)
                    }
                })
    }

}