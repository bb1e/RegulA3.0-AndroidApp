package pt.pl.estg.ei.regulapp

import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import android.content.Intent
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import pt.pl.estg.ei.regulapp.enums.BD
import pt.pl.estg.ei.regulapp.R.layout
import com.google.firebase.firestore.DocumentSnapshot
import pt.pl.estg.ei.regulapp.adapters.ProfileAdapter
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import pt.pl.estg.ei.regulapp.classes.*
import java.util.ArrayList

class MenuEscolherCrianca : AppCompatActivity() {
    protected var lLayout: ListView? = null
    protected var session: Session? = null
    var child_counter: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_menu_escolher_crianca)
        lLayout = findViewById<View?>(R.id.llCriancas) as ListView?

        /*
        Button button = findViewById(R.id.btnAddCrianca);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCrianca();
            }
        });

         */
        val d: FirebaseFirestore = FirebaseFirestore.getInstance()
        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
        if (mAuth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        val id: String = mAuth.currentUser?.uid!!
        d.collection(BD.sessions_test.toString()).document(id).get().addOnCompleteListener(object : OnCompleteListener<DocumentSnapshot?> {
            override fun onComplete(task: Task<DocumentSnapshot?>) {
                if (task.isSuccessful) {
                    session = task.result?.toObject(Session::class.java)
                    if (session == null) {
                    } else {
                        session?.id = id
                        if (session?.criancas?.size == 1) {
                            val intent: Intent = Intent(applicationContext, HomePageActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            val name: String? = session?.criancas?.get(0)?.nome
                            intent.putExtra("child", name)
                            startActivity(intent)
                        } else if (session?.criancas?.size!! > 1) {
                            val names: ArrayList<String?> = ArrayList()
                            for (crianca: Crianca? in session?.criancas!!) {
                                names.add(crianca?.nome)
                            }
                            val profileAdapter: ProfileAdapter? = ProfileAdapter(applicationContext, names)
                            lLayout?.adapter = profileAdapter
                        } else {
                            startActivity(Intent(applicationContext, AddCrianca::class.java))
                        }
                        (application as GlobalSettings).session = session
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (session != null) {
            val names: ArrayList<String?> = ArrayList()
            for (crianca: Crianca? in session?.criancas!!) {
                names.add(crianca?.nome)
            }
            lLayout?.adapter = ProfileAdapter(applicationContext, names)
        }
    }

    private fun addCrianca() {
        //Para testar novas activities
        val intent: Intent = Intent(this, AddCrianca::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        startActivity(intent)
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}