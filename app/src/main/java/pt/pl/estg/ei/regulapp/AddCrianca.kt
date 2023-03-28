package pt.pl.estg.ei.regulapp

import android.os.Bundle
import android.content.Intent
import com.squareup.picasso.Picasso
import android.annotation.SuppressLint
import pt.pl.estg.ei.regulapp.enums.BD
import pt.pl.estg.ei.regulapp.enums.Genero
import pt.pl.estg.ei.regulapp.enums.TipoAlvo
import android.app.DatePickerDialog.OnDateSetListener
import pt.pl.estg.ei.regulapp.R.layout
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.Color
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.*
import pt.pl.estg.ei.regulapp.classes.*
import java.util.*

class AddCrianca constructor() : AppCompatActivity() {
    protected var name: TextView? = null
    protected var dataNascimento: TextView? = null
    protected var btnConfirmar: AppCompatButton? = null
    protected var validData: Boolean? = false
    var setListener: OnDateSetListener? = null
    var dataLayout: LinearLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_add_crianca)
        btnConfirmar = findViewById(R.id.btnConfirmar)
        name = findViewById(R.id.editTextPersonName)
        dataNascimento = findViewById(R.id.editTextNumber2)
        dataLayout = findViewById(R.id.layoutData)
        val profilePicture: ImageView? = findViewById(R.id.imageViewAddCrianca)
        Picasso.get().load(R.drawable.perfil1).into(profilePicture)
        val calendar: Calendar? = Calendar.getInstance()
        val ano: Int = calendar?.get(Calendar.YEAR)!!
        val mes: Int = calendar?.get(Calendar.MONTH)!!
        val dia: Int = calendar?.get(Calendar.DAY_OF_MONTH)!!
        dataLayout?.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this@AddCrianca, android.R.style.Theme_Holo_Light_Dialog_MinWidth, setListener, ano, mes, dia)
            datePickerDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            datePickerDialog.getDatePicker().setMaxDate(Date().getTime())
            datePickerDialog.show()
        }
        setListener = OnDateSetListener { view, year, month, dayOfMonth ->
            var month: Int = month
            month+= 1;
            val date: String? = "$dayOfMonth/$month/$year"
            dataNascimento?.text = date
        }
        mudarStatusBarColor()
        btnConfirmar?.setOnClickListener(({ checkInputData() }))
    }

    @SuppressLint("ResourceAsColor")
    protected fun checkInputData() {
        var validated: Int = 0
        val name: String = name?.getText().toString()
        val context: Context = getApplicationContext()
        var text: CharSequence? = ""
        val duration: Int = Toast.LENGTH_SHORT

        //name
        if (name.compareTo("") == 0) {
            text = text.toString() + " · Por favor submeter um nome \n"
        } else if (name.length > 30 || name.length < 2) {
            text = text.toString() + " · Por favor submeter um nome entre 30 e 2 caracteres \n"
        } else {
            validated++
        }

        //idade
        var idade: Int = 0
        var data: Data = Data()
        var diaNascimento: Int = 0
        var mesNascimento: Int = 0
        var anoNascimento: Int = 0
        val dataNascimentoFromForm: String = dataNascimento?.getText().toString() + ""
        if (dataNascimentoFromForm.compareTo("") == 0) {
            text = text.toString() + " · Por favor submeter uma idade válida\n"
        } else {
            try {
                data = Data.Companion.parseData(dataNascimentoFromForm)!!
            } catch (e: InvalidPropertiesFormatException) {
                e.printStackTrace()
                FirebaseCrashlytics.getInstance().recordException(e); // log it to Crashalytics despite catching the exception
            }
            idade = data.getIdade()
            if (!(idade >= 3 && idade <= 6)) {
                text = text.toString() + " · Esta Aplicação destina se a crianças dos 3 aos 6 anos\n"
            } else {
                validated++
                diaNascimento = data.dia
                mesNascimento = data.mes
                anoNascimento = data.ano
            }
        }
        /*
        int idade = 0;
        CharSequence age_ = this.dataNascimento.getText();
        if (age_.toString().compareTo("")==0){
            text = text + " · Por favor submeter uma idade válida\n";
        }
        else {
            idade = Integer.parseInt(age_.toString());
            if (!(idade >= 3 && idade <= 6)) {
                text = text + " · Esta Aplicação destina se a crianças dos 3 aos 6 anos\n";
            }
            else {
                validated++;
            }
        }
 */


        //sexo
        val radioGroup: RadioGroup = findViewById(R.id.radioGroup2)
        var radioButton_G: RadioButton? = null
        val radioButton_E: RadioButton? = null
        val selected_id: Int = radioGroup.getCheckedRadioButtonId()
        if (selected_id == -1) {
            text = text.toString() + " · Selecionar uma opção de Gênero\n"
        } else {
            radioButton_G = findViewById(selected_id)
            validated++
        }
        if (validated == 3) {
            Analytics.fireEvent(AnalyticsEvent.ADDCRIANCA_SUCCESS)
            val tipoAlvo: TipoAlvo = TipoAlvo.Nenhum
            var genero: Genero? = null
            val text_genero: String = radioButton_G?.getText().toString()
            when (text_genero) {
                "Masculino" -> genero = Genero.Masculino
                "Femenino" -> genero = Genero.Feminino
                "Outro" -> genero = Genero.Outro
                else -> {
                }
            }
            val session = (application as GlobalSettings?)?.session!!
            val crianca = Crianca(name, genero, tipoAlvo, data, session.nome, session.id)
            session.addCrianca(crianca)
            val db: FirebaseFirestore = FirebaseFirestore.getInstance()
            val dr: DocumentReference = db.collection(BD.sessions_test.toString()).document(session.id + "")
            dr.update("criancas", session.criancas).addOnSuccessListener {
                Log.d(
                    "MenuEscolher",
                    "DocumentSnapshot added with ID: "
                )
            }
            db.collection(BD.criancas_test.toString()).document(crianca.id + "").set(crianca).addOnSuccessListener {
                Log.d(
                    "TAG",
                    "onSuccess: "
                )
            }
                redirectHomepage(crianca.nome)
        } else {
            Analytics.fireEvent(AnalyticsEvent.ADDCRIANCA_FAILED(text.toString()));
            val toast: Toast = Toast.makeText(context, text, duration)
            toast.setGravity(Gravity.TOP, 0, 50)
            toast.show()
        }
        return
    }

    protected fun redirectHomepage(name: String?) {
        val intent: Intent?
        intent = Intent(getApplicationContext(), HomePageActivity::class.java)
        intent.putExtra("child", name)
        intent.putExtra("addCrianca", true)
        startActivity(intent)
    }

    private fun mudarStatusBarColor() {
        val window: Window = getWindow()
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setStatusBarColor(getResources().getColor(R.color.azul_claro))
    }
}