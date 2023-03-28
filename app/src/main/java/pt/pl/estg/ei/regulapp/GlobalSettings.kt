package pt.pl.estg.ei.regulapp

import android.app.Application
import android.util.Log
import com.google.firebase.firestore.*
import pt.pl.estg.ei.regulapp.classes.*

class GlobalSettings constructor() : Application() {
    public var session: Session? = null
    protected var db: FirebaseFirestore? = null

    fun saveSession(session: Session) {
        db?.collection("criancas")?.document(session.id + "")?.set(this)?.addOnSuccessListener { Log.d("ListaEstrategias", "done") }?.addOnFailureListener{ Log.w("ListaEstrategias", "Error adding document", it)}
    }

    fun saveCrianca(crianca: Crianca?) {
        db = FirebaseFirestore.getInstance()
        db?.collection("criancas")?.document()?.set(this)?.addOnSuccessListener { Log.d("ListaEstrategias", "done") }?.addOnFailureListener {
                Log.w(
                    "ListaEstrategias",
                    "Error adding document",
                    it
                )
            }
    }

    fun loadSession(): Session? {
        val sessao: Array<Session?> = arrayOfNulls<Session>(1)
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        db.collection("estrategias").whereEqualTo("titulo", "Duarte Chapiro").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                    task.getResult()!!.forEach{document ->
                        Log.d("ListaEstrategias", document.id + " => " + document.data)
                        sessao[0] = document.toObject(Session::class.java)
                    }
            } else {
                Log.d("ListaEstrategias", "Error getting documents: ", task.exception)
            }
        }
        session = sessao[0]
        return session
    }

    fun loadCrianca(id: Long): Crianca? {
        val crianca: Array<Crianca?> = arrayOfNulls<Crianca?>(1)
        val docRef: DocumentReference = db?.collection("criancas")?.document(id.toString() + "")!!
        docRef.get().addOnSuccessListener { documentSnapshot ->
            crianca[0] = documentSnapshot.toObject(Crianca::class.java)
        }
        return crianca[0]
    }

    companion object {
        var myAppInstance: GlobalSettings? = null
            get
    }

    init {
        myAppInstance = this
        //Crianca crianca = new Crianca("Joana Antonio", Genero.Feminino, TipoAlvo.hiper_reativo,16);
        // Crianca crianca1 = new Crianca("Joana Duarte", Genero.Feminino, TipoAlvo.hiper_reativo,16);
        // loadSession();
//        this.db = FirebaseFirestore.getInstance();

        //   session.addCrianca(crianca);
        //  session.addCrianca(crianca1);
    }
}