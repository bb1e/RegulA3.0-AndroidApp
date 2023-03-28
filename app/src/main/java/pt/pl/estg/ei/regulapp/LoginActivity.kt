package pt.pl.estg.ei.regulapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.crashlytics.FirebaseCrashlytics
import pt.pl.estg.ei.regulapp.R.layout
import pt.pl.estg.ei.regulapp.classes.*
import java.util.*

class LoginActivity constructor() : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    protected var progressBar: ProgressBar? = null
    private var register: TextView? = null
    private var tvEmail: TextView? = null
    private var tvPassword: TextView? = null
    private var btnResetPassword: Button? = null

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (FirebaseAuth.getInstance().currentUser != null) {
            Analytics.fireEvent(AnalyticsEvent.LOGIN_AUTO);
            startActivity(Intent(this, MenuEscolherCrianca::class.java))
        }
        setContentView(layout.activity_login_email)
        mAuth = FirebaseAuth.getInstance()
        btnResetPassword = findViewById(R.id.appCompatButton2)
        tvEmail = findViewById(R.id.editTextTextEmailAddress)
        tvPassword = findViewById(R.id.editTextTextPassword)
        progressBar = findViewById(R.id.progressBar)
        val login: Button = findViewById(R.id.btnLogin)
        login.setOnClickListener { login() }
        register = findViewById<View?>(R.id.btnRegistar) as TextView
        register?.setOnClickListener { registar() }
        findViewById<View>(R.id.btnGoogle).setOnClickListener { googleLogin() }
        btnResetPassword?.setOnClickListener { v: View? ->
            setContentView(layout.activity_register_email)
            Analytics.fireEvent(AnalyticsEvent.RESETPASSWORD_OPENMENU)
            (findViewById<View?>(R.id.textView4) as TextView).text = "Redefenir senha"
            val btnEnviar: Button? = findViewById(R.id.registerNext)
            btnEnviar?.text = "Enviar"
            btnEnviar?.setOnClickListener { v1: View ->
                try {
                    val email: CharSequence? = (findViewById<View?>(R.id.editTextTextEmailAddress) as TextView).text
                    if (!email.toString().isEmpty()) {
                        FirebaseAuth.getInstance().sendPasswordResetEmail(email.toString())
                                .addOnCompleteListener { t: Task<Void> ->
                                    if(t.isSuccessful) {
                                        Analytics.fireEvent(AnalyticsEvent.RESETPASSWORD_MAILSENT)
                                        Toast.makeText(
                                                applicationContext,
                                                "Email para redefenir senha enviado",
                                                Toast.LENGTH_LONG
                                        ).show()
                                    }
                                    else {
                                        Analytics.fireEvent(AnalyticsEvent.RESETPASSWORD_MAILSENT_FAILED)
                                        Toast.makeText(applicationContext, "Email invalido", Toast.LENGTH_LONG)
                                    }
                                }
                    } else {
                        Analytics.fireEvent(AnalyticsEvent.RESETPASSWORD_MAILSENT_FAILED)
                        Toast.makeText(
                                applicationContext,
                                "Email para redefenir senha não enviado",
                                Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, e.toString());
                    Analytics.fireEvent(AnalyticsEvent.RESETPASSWORD_MAILSENT_FAILED)
                    Toast.makeText(
                            getApplicationContext(),
                            "Email para redefenir senha não enviado",
                            Toast.LENGTH_LONG
                    ).show()
                    FirebaseCrashlytics.getInstance().recordException(e); // log it to Crashalytics despite catching the exception
                }
            }
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.azul_claro)

        Log.d("WARN", R.string.web_client_id.toString());
        // Google login
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun login() {
        val password: String = tvPassword?.text.toString().trim { it <= ' ' }
        val email: String = tvEmail?.text.toString().trim { it <= ' ' }
        if (password.isEmpty()) {
            tvPassword?.error = "Password necessário"
            tvPassword?.requestFocus()
            return
        }
        if (!(password.length in 5..12)) {
            tvPassword?.error = "Password tem de ter 5 a 12 caracteres"
            tvPassword?.requestFocus()
            return
        }
        if (email.isEmpty()) {
            tvEmail?.error = "Email necessário"
            tvEmail?.requestFocus()
            return
        }
        if (!(Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            tvEmail?.error = "Email inválido"
            tvEmail?.requestFocus()
            return
        }
        mAuth.signInWithEmailAndPassword(email, password)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Analytics.fireEvent(AnalyticsEvent.LOGIN_PASSWORD);
                onLoginComplete()
            } else {
                Analytics.fireEvent(AnalyticsEvent.LOGIN_PASSWORD_FAILED);
                Toast.makeText(
                        applicationContext,
                        "Erro no Login, verificar credenciais",
                        Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun googleLogin() {
        startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN);
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Analytics.fireEvent(AnalyticsEvent.LOGIN_GOOGLE_FAILED);
                Toast.makeText(
                        applicationContext,
                        "Erro no Login",
                        Toast.LENGTH_LONG
                ).show()
                Log.w(TAG, "Google sign in failed", e)
                FirebaseCrashlytics.getInstance().recordException(e); // log it to Crashalytics despite catching the exception
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithCredential:success")
                        if(task.result.additionalUserInfo?.isNewUser == true){
                            Session.saveNewSession(Session(mAuth.currentUser?.displayName,mAuth.currentUser?.email)) { isSuccessful ->
                                if(isSuccessful) {
                                    Analytics.fireEvent(AnalyticsEvent.SIGNUP_GOOGLE);
                                    // para menu criancas, completar o registro
                                    startActivity(
                                            Intent(applicationContext, MenuEscolherCrianca::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                    )
                                }
                                else {
                                    Analytics.fireEvent(AnalyticsEvent.SIGNUP_GOOGLE_FAILED);
                                    Toast.makeText(
                                            applicationContext,
                                            "Erro ao criar nova conta",
                                            Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                        else {
                            Analytics.fireEvent(AnalyticsEvent.LOGIN_GOOGLE);
                            onLoginComplete()
                        }
                    } else {
                        Analytics.fireEvent(AnalyticsEvent.LOGIN_GOOGLE_FAILED);
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        Toast.makeText(
                                applicationContext,
                                "Erro no Login, tente mais tarde.",
                                Toast.LENGTH_LONG
                        ).show()
                    }
                }
    }

    private fun onLoginComplete() {
        progressBar?.visibility = View.VISIBLE
        register?.visibility = View.INVISIBLE
        Log.d("TAG", "onComplete: " + mAuth.uid)
        Session.getSession { session ->
            if(session == null){
                Toast.makeText(applicationContext, "Esta conta não é valida para este acesso", Toast.LENGTH_LONG).show()
                mAuth.signOut()
                register?.visibility = View.VISIBLE
                progressBar?.visibility = View.GONE
                return@getSession
            }

            Toast.makeText(
                    applicationContext,
                    "Utilizador logado com sucesso",
                    Toast.LENGTH_LONG
            ).show()

            val name: String = session.criancas[0]?.nome!!
            val intent = Intent(applicationContext, HomePageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("child", name)
            val child: Crianca? = session.getCriancaByName(name)
            session.thisSession = child
            (application as GlobalSettings).session = session
            startActivity(intent)
        }
    }

    private fun registar() {
        val intent = Intent(applicationContext, RegisterActivity::class.java)
        startActivity(intent)
    }
    companion object {
        private const val TAG = "LoginActivity"
        private const val RC_SIGN_IN = 9001
    }
}
