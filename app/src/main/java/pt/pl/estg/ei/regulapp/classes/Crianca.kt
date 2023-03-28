package pt.pl.estg.ei.regulapp.classes

import pt.pl.estg.ei.regulapp.enums.BD
import pt.pl.estg.ei.regulapp.enums.Genero
import pt.pl.estg.ei.regulapp.enums.TipoAlvo
import android.util.Log
import com.google.firebase.firestore.*
import java.io.Serializable
import java.util.*

class Crianca : Serializable {
    var id: String? = null
    var nome: String? = null
    var genero: Genero? = null
    var tipoAutismo: TipoAlvo? = null
    var estrategiasFavoritas: ArrayList<Estrategia?>? = null
    var estrategiasRecomendadas: ArrayList<String?>? = null
    var dataNascimento: Data? = null
    var parentName: String? = null
    var created_at: Long = 0
    var storageImageRef: String? = null
    var status: String? = null
    var idSession: String? = null

    //Avaliacao
    var comentario: String? = null
    var ssAv1: String? = null
    var ssAv2: String? = null
    var ssAv3: String? = null
    var ssAv4: String? = null
    var ssAv5: String? = null
    var ssAv6: String? = null
    var ssAv7: String? = null
    var dataUltimaAvaliacao: String? = null
    var idTerapeuta: String? = null

    constructor() {}
    constructor(nome: String?, genero: Genero?, tipoAutismo: TipoAlvo?, dataNascimento: Data?, parentName: String?, idSession: String?) {
        this.nome = nome
        this.genero = genero
        this.tipoAutismo = tipoAutismo
        estrategiasFavoritas = ArrayList()
        estrategiasRecomendadas = ArrayList()
        created_at = System.currentTimeMillis()
        this.parentName = parentName
        id = created_at.toString() + ""
        this.dataNascimento = dataNascimento
        status = "Hello World!!!"
        this.idSession = idSession
        idTerapeuta = ""
        comentario = ""
        ssAv1 = TipoAlvo.Nenhum.toString()
        ssAv2 = TipoAlvo.Nenhum.toString()
        ssAv3 = TipoAlvo.Nenhum.toString()
        ssAv4 = TipoAlvo.Nenhum.toString()
        ssAv5 = TipoAlvo.Nenhum.toString()
        ssAv6 = TipoAlvo.Nenhum.toString()
        ssAv7 = TipoAlvo.Nenhum.toString()
        dataUltimaAvaliacao = "Sem avaliação"

        //imagem
        var random: Int = 1
        if (genero == Genero.Masculino) {
            random = Random().nextInt(2) + 1
        } else if (genero == Genero.Feminino) {
            random = Random().nextInt(2) + 4
        }
        storageImageRef = "default_images/perfil" + random + ".png"
    }

    fun removeEstrategiaFavorita(estrategia: Estrategia?): Int {
        for (estrategiapreferida: Estrategia? in estrategiasFavoritas!!) {
            if (estrategiapreferida?.id == estrategia?.id) {
                Analytics.fireEvent(AnalyticsEvent.ESTRATEGIA_FAVORITA_REMOVIDA);
                estrategiasFavoritas?.remove(estrategiapreferida)
                atualizarDB()
                return 1
            }
        }
        return 0
    }

    fun addEstrategiaFavorita(estrategia: Estrategia?) {
        Analytics.fireEvent(AnalyticsEvent.ESTRATEGIA_FAVORITA_NEW);
        estrategiasFavoritas?.add(estrategia)
        atualizarDB()
    }

    private fun atualizarDB() {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val dr: DocumentReference = db?.collection(BD.criancas_test.toString()).document(id!!)
        dr.update("estrategiasFavoritas", estrategiasFavoritas).addOnSuccessListener {
            Log.d(
                "AdicionarEstrategiasFav",
                "DocumentSnapshot added with ID: "
            )
        }
    }
}