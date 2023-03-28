package pt.pl.estg.ei.regulapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import pt.pl.estg.ei.regulapp.R.layout
import pt.pl.estg.ei.regulapp.classes.Analytics
import pt.pl.estg.ei.regulapp.classes.AnalyticsEvent
import pt.pl.estg.ei.regulapp.classes.Session

class RegisterActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var email: String? = null
    private var nome: String? = null

    //private String idCuidador;
    private var password: String? = null
    private var password2: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_register_email)
        val check_email: Button = findViewById(R.id.registerNext)
        check_email.setOnClickListener(({ v: View? -> checkEmail() }))
        mAuth = FirebaseAuth.getInstance()
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.azul_claro)
    }

    private fun checkPassword() {
        val email: TextView = findViewById(R.id.editTextPassword)
        val passswordConfirmar: TextView = findViewById(R.id.editTextPassword2)
        password = email.text.toString()
        password2 = passswordConfirmar.text.toString()
        val email_string: String? = this.email
        if (password?.isEmpty()!!) {
            email.error = "Password é necessária"
            email.requestFocus()
            return
        }
        if (!(5 <= password?.length!! && password?.length!! <= 12)) {
            email.error = "Inserir password de 5 a 12 caracteres"
            email.requestFocus()
            return
        }
        if (password != password2) {
            passswordConfirmar.error = "As passwords não são iguais!"
            email.requestFocus()
            return
        }


        //this.cuidador = new Cuidador(this.nome, this.email);
        //this.idCuidador = cuidador.getId();
        //createCuidador();
        val progressBar: ProgressBar? = findViewById(R.id.progressBar)
        progressBar?.visibility = View.VISIBLE
        mAuth?.createUserWithEmailAndPassword(this.email!!, password!!)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val session = Session(nome, email_string)
                Session.saveNewSession (session) { isSuccessful ->
                    progressBar?.visibility = View.VISIBLE
                    if (isSuccessful) {
                        Analytics.fireEvent(AnalyticsEvent.SIGNUP_PASSWORD)
                        Toast.makeText(
                                applicationContext,
                                "Utilizador registado com sucesso",
                                Toast.LENGTH_LONG
                        ).show()
                        goMenuCriancas()
                    } else {
                        Analytics.fireEvent(AnalyticsEvent.SIGNUP_PASSWORD_FAILED)
                        Toast.makeText(
                                applicationContext,
                                "Utilizador não foi registado",
                                Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } else {
                Analytics.fireEvent(AnalyticsEvent.SIGNUP_PASSWORD_FAILED)
                Toast.makeText(applicationContext, "Utilizador não foi registado!", Toast.LENGTH_LONG).show()
                progressBar?.visibility = View.VISIBLE
            }
        }
    }

    private fun goMenuCriancas() {
        val intent = Intent(applicationContext, MenuEscolherCrianca::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        startActivity(intent)
    }

    private fun checkEmail() {
        val email: TextView = findViewById(R.id.editTextTextEmailAddress)
        this.email = email.text.toString()
        if (this.email?.isEmpty()!!) {
            email.requestFocus()
            email.setError("Email é necessário")
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(this.email!!).matches()) {
            email.requestFocus()
            email.setError("Inserir email válido")
            return
        }
        setContentView(layout.activity_register_dados)
        val check_name: Button? = findViewById(R.id.registerNext2)
        check_name?.setOnClickListener(View.OnClickListener { checkName() })
    }

    private fun checkName() {
        val name: TextView = findViewById(R.id.editTextTextParentName)
        nome = name.getText().toString()
        if (nome?.isEmpty()!!) {
            name.requestFocus()
            name.setError("Nome é necessário")
            return
        }
        setContentView(layout.activity_register_password)
        val check_password: Button = findViewById(R.id.registerFinish)
        check_password.setOnClickListener(View.OnClickListener { v: View? -> checkPassword() })
    } /*
    private void createCuidador(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(BD.cuidadores_test.toString()).document(cuidador.getId()).set(cuidador).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Register", "Cuidador criado na db");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Register", "Error adding document: Cuidador", e);
            }
        });

    }

     */
}