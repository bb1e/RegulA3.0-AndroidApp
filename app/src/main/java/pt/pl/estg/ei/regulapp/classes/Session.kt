package pt.pl.estg.ei.regulapp.classes

import com.google.firebase.auth.FirebaseAuth
import pt.pl.estg.ei.regulapp.enums.BD
import com.google.firebase.firestore.*
import java.io.Serializable
import java.util.ArrayList

class Session : Serializable {
    var id: String? = null
    var created_at: Long = 0
    var nome: String? = null
        get() = field
    var criancas: ArrayList<Crianca?> = ArrayList<Crianca?>()
    var thisSession: Crianca? = null
    var email: String? = null
    var storageImageRef: String? = null
    var chatName: String? = null
    var idCuidador: String? = null

    constructor() {
        criancas = ArrayList()
    }

    constructor(nome: String?, email: String?) {
        id = FirebaseAuth.getInstance().getUid()
        this.nome = nome
        this.email = email
        created_at = System.currentTimeMillis()
        idCuidador = ""
        storageImageRef = ""
    }

    fun addCrianca(criancas: Crianca?) {
        this.criancas.add(criancas)
    }

    fun getCriancaByName(name: String?): Crianca? {
        for (crianca: Crianca? in criancas) {
            if (crianca?.nome?.compareTo(name!!) == 0) {
                return crianca
            }
        }
        return null
    }

    companion object {
        private fun docRef(): DocumentReference {
            return FirebaseFirestore.getInstance().collection(BD.sessions_test.toString())
                    .document(FirebaseAuth.getInstance().uid!!)
        }

        fun getSession(callback: (Session?) -> Unit) {
            docRef().get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val session: Session? = task.result!!.toObject(Session::class.java)
                    callback(session)
                }
                else {
                    callback(null);
                }
            }
        }

        fun saveNewSession(session: Session, cb: (Boolean) -> Unit) {
            docRef().set(session).addOnCompleteListener { t -> cb(t.isSuccessful) }
        }
    }
}