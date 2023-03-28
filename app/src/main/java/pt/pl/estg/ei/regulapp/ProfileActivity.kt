package pt.pl.estg.ei.regulapp

import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.MediaStoreSignature
import android.content.Intent
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.FirebaseStorage
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import android.annotation.SuppressLint
import android.provider.MediaStore
import pt.pl.estg.ei.regulapp.enums.BD
import lib.kingja.switchbutton.SwitchMultiButton
import pt.pl.estg.ei.regulapp.R.layout
import pt.pl.estg.ei.regulapp.adapters.AreasEstrategiasAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import pt.pl.estg.ei.regulapp.adapters.ProfileEstatisticaAdapter
import android.view.View.OnTouchListener
import pt.pl.estg.ei.regulapp.adapters.HistoricoFeedbackAdapter
import android.net.Uri
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.google.firebase.firestore.*
import de.hdodenhof.circleimageview.CircleImageView
import pt.pl.estg.ei.regulapp.classes.*
import java.util.ArrayList
import java.util.HashMap

class ProfileActivity : AppCompatActivity() {
    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    private var toolbar: Toolbar? = null
    private var crianca: Crianca? = null
    private var toolbarTitle: TextView? = null
    private var toolbarTitleWSubtitle: TextView? = null
    private var toolbarSubtitle: TextView? = null
    private var name: TextView? = null
    private var age: TextView? = null
    private var btnSelecionarLayout: SwitchMultiButton? = null
    private var infoLayout: ConstraintLayout? = null
    private var estatisticasLayout: ConstraintLayout? = null
    private var listaEstrategias: MyListView? = null
    private var idCrianca: String? = null
    private var btnHistoricoFeedback: AppCompatButton? = null
    private var layoutHistorico: ConstraintLayout? = null
    private var layoutDashboard: ConstraintLayout? = null
    private var btnVoltarDashboard: AppCompatButton? = null
    private var btnVerHistoricoFb: AppCompatButton? = null
    private var btnProfile: CircleImageView? = null
    protected var storageRef: StorageReference? = null
    private var abriuHistorico: Boolean = false
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storageRef = FirebaseStorage.getInstance().reference
        setContentView(layout.activity_profile)
        crianca = (application as GlobalSettings).session?.thisSession
        idCrianca = GlobalSettings.Companion.myAppInstance?.session?.thisSession?.id
        toolbarTitle = findViewById(R.id.toolbar_title)
        toolbarSubtitle = findViewById(R.id.toolbar_subTitle)
        toolbarTitleWSubtitle = findViewById(R.id.toolbar_title_with_subtitle)
        toolbar = findViewById(R.id.toolbarEstrategias)
        drawerLayout = findViewById(R.id.drawer_layout_profile)
        navigationView = findViewById(R.id.nav_view_profile)
        toolbar = findViewById(R.id.toolbarProfile)
        name = findViewById(R.id.txtName)
        age = findViewById(R.id.txtIdade)
        btnSelecionarLayout = findViewById(R.id.btnSelecionarLayout)
        infoLayout = findViewById(R.id.profileInfoLayout)
        estatisticasLayout = findViewById(R.id.layoutDashboard)
        btnHistoricoFeedback = findViewById(R.id.btnVerHistoricofeedback)
        layoutHistorico = findViewById(R.id.layoutParentHistorico)
        layoutDashboard = findViewById(R.id.layoutParentDashboard)
        btnVoltarDashboard = findViewById(R.id.btnVoltarDashboard)
        btnVerHistoricoFb = findViewById(R.id.btnVerHistoricofeedback)
        btnProfile = findViewById(R.id.btnProfileProfile)
        abriuHistorico = false
        infoLayout?.visibility = View.VISIBLE
        estatisticasLayout?.visibility = View.GONE
        btnVerHistoricoFb?.visibility = View.GONE
        toolbarTitle?.text = "Perfil"
        toolbarTitleWSubtitle?.text = "Perfil"
        toolbarSubtitle?.text = "Histórico de feedback de estratégias"

        //textviews
        name?.text = crianca?.nome
        age?.text = crianca?.dataNascimento.toString()
        setAvs(crianca)
        carregarValoresAvaliacao()
        apresentarEstatisticas(idCrianca!!)


        //Btns separadores
        btnSelecionarLayout?.setText("Informação", "Dashboard")
        btnSelecionarLayout?.setOnSwitchListener { position, tabText ->
            if (position == 1) {
                infoLayout?.visibility = View.GONE
                estatisticasLayout?.visibility = View.VISIBLE
            } else {
                estatisticasLayout?.visibility = View.GONE
                infoLayout?.visibility = View.VISIBLE
            }
        }
        btnVerHistoricoFb?.setOnClickListener(View.OnClickListener({ v: View? -> mostrarHistoricoFeedback() }))
        //remover btn - aqui caso seja necessário
        //btnVoltarDashboard.setOnClickListener(v -> esconderHistoricoFeedback());
        btnVoltarDashboard?.setVisibility(View.GONE)
        btnProfile?.setOnClickListener {
            val intent: Intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1000)
        }
        loadProfilePic(btnProfile)

        //toolbar
        iniciarToolbar()

        if(savedInstanceState == null) {
            Sidebar.installSidebar(supportFragmentManager,drawerLayout!!,navigationView!!);
        }
    }

    private fun carregarValoresAvaliacao() {
        db
                .collection(BD.criancas_test.toString())
                .document(idCrianca!!)
                .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document: DocumentSnapshot = task.result!!
                    if (document.exists()) {
                        val criancaBD: Crianca? = document.toObject(Crianca::class.java)
                        setAvs(criancaBD)
                    }
                }
            }
    }

    private fun setAvs(child: Crianca?) {
        val ssav1: TextView? = findViewById(R.id.txtSSTatil)
        val ssav2: TextView? = findViewById(R.id.txtSSAuditivo)
        val ssav3: TextView? = findViewById(R.id.txtSSVisual)
        val ssav4: TextView? = findViewById(R.id.txtSSOlfativo)
        val ssav5: TextView? = findViewById(R.id.txtSSGustativo)
        val ssav6: TextView? = findViewById(R.id.txtSSPropriocetivo)
        val ssav7: TextView? = findViewById(R.id.txtSSVestibular)
        val comentarios: TextView? = findViewById(R.id.txtAvaliacao)
        ssav1?.setText(child?.ssAv1)
        ssav2?.setText(child?.ssAv2)
        ssav3?.setText(child?.ssAv3)
        ssav4?.setText(child?.ssAv4)
        ssav5?.setText(child?.ssAv5)
        ssav6?.setText(child?.ssAv6)
        ssav7?.setText(child?.ssAv7)
        comentarios?.setText(child?.comentario)
    }

    private fun loadProfilePic(profilePic: CircleImageView?) {
        if (crianca?.storageImageRef != null) {
            val profileImage: StorageReference? = FirebaseStorage.getInstance().reference.child(crianca?.storageImageRef!!)
            Glide.with(this@ProfileActivity).load(profileImage).signature(MediaStoreSignature("",
                    System.currentTimeMillis(), 0)).into(profilePic!!)
        } else {
            Toast.makeText(applicationContext, "nao foi possivel ", Toast.LENGTH_LONG)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {
                val imageUri: Uri = data?.data!!
                btnProfile?.setImageURI(imageUri)
                val query: String = "profilePictures/" + crianca?.id + ".png"
                val mountainImagesRef: StorageReference = storageRef?.child(query)!!
                mountainImagesRef.putFile(imageUri)
                val session: Session = (application as GlobalSettings).session!!
                session.thisSession?.storageImageRef = query
                FirebaseFirestore.getInstance().collection(BD.criancas_test.toString()).document(crianca?.id!!).update("storageImageRef", query).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "onComplete: Imagem carregada")
                    }
                }
                val db: FirebaseFirestore = FirebaseFirestore.getInstance()
                val dr: DocumentReference = db.collection(BD.sessions_test.toString()).document(FirebaseAuth.getInstance().uid!!)
                dr.update("criancas", session.criancas).addOnSuccessListener {
                    Log.d(
                        "AdicionarEstrategiasFav",
                        "DocumentSnapshot added with ID: "
                    )
                }
            }
        }
    }

    private fun esconderHistoricoFeedback() {
        layoutHistorico?.setVisibility(View.GONE)
        layoutDashboard?.setVisibility(View.VISIBLE)
        abriuHistorico = false
        toolbarTitle?.setVisibility(View.VISIBLE)
        toolbarTitleWSubtitle?.setVisibility(View.GONE)
        toolbarSubtitle?.setVisibility(View.GONE)
    }

    private fun mostrarHistoricoFeedback() {
        layoutHistorico?.setVisibility(View.VISIBLE)
        layoutDashboard?.setVisibility(View.GONE)
        abriuHistorico = true
        toolbarTitle?.setVisibility(View.GONE)
        toolbarTitleWSubtitle?.setVisibility(View.VISIBLE)
        toolbarSubtitle?.setVisibility(View.VISIBLE)
    }

    private fun iniciarToolbar() {
        setSupportActionBar(toolbar)
        navigationView?.bringToFront()
        //Auto selecionar Estrategias
        navigationView?.setCheckedItem(R.id.nav_profile)
    }

    fun clickMenu(view: View?) {
        //abrir janela menu
        openDrawer(drawerLayout!!)
    }

    private fun openDrawer(drawerLayout: DrawerLayout) {
        //abrir o layout da janela
        drawerLayout.openDrawer(GravityCompat.END)
    }

    override fun onBackPressed() {
        if (drawerLayout?.isDrawerOpen(GravityCompat.END)!!) {
            drawerLayout?.closeDrawer(GravityCompat.END)
        } else {
            if (abriuHistorico) {
                esconderHistoricoFeedback()
            } else {
                super.onBackPressed()
            }
        }
    }
    private fun apresentarEstatisticas(idCrianca: String) {
        estrategiasComFeedback(idCrianca)
        relatorioSemanalEstatisticas(idCrianca)
    }

    private fun getNomesEstrategiasTop(listaEstrategias: ArrayList<Estrategia?>, top: Int): ArrayList<String?> {
        var nomes: ArrayList<String?> = ArrayList()
        if (listaEstrategias.size > 0) {
            if (top <= listaEstrategias.size) {
                for (i in 0 until top) {
                    //Nome Estrategias Sem Repeticao
                    val nome: String = listaEstrategias[i]?.descricao!!
                    nomes.add(nome)
                }
            } else {
                nomes = getNomesEstrategias(listaEstrategias)!!
            }
        }
        return nomes
    }

    private fun getIdsEstrategiasTop(listaEstrategias: ArrayList<Estrategia?>, top: Int): ArrayList<Long?> {
        val ids: ArrayList<Long?> = ArrayList()
        if (listaEstrategias?.size > 0) {
            if (top <= listaEstrategias?.size) {
                for (i in 0 until top) {
                    //Nome Estrategias Sem Repeticao
                    val id: Long = listaEstrategias[i]?.id!!
                    ids.add(id)
                }
            } else {
                for (estrategia: Estrategia? in listaEstrategias) {
                    ids.add(estrategia?.id)
                }
            }
        }
        return ids
    }

    private fun getNomesEstrategias(listaEstrategias: ArrayList<Estrategia?>): ArrayList<String?> {
        val nomes: ArrayList<String?> = ArrayList()
        if (listaEstrategias?.size > 0) {
            for (i in listaEstrategias.indices) {
                //Nome Estrategias Sem Repeticao
                val nome: String = listaEstrategias[i]?.descricao!!
                nomes.add(nome)
            }
        }
        return nomes
    }

    private fun estrategiasComFeedback(idCrianca: String) {
        //buscar todas as estrategias com feedback
        db //feedbacks
                .collection("" + BD.feedbackEstrategias_demo)
                .whereEqualTo("idCrianca", idCrianca)
                .get()
                .addOnCompleteListener { task ->
                    Log.d("ProfileActivity", "Success getting documents of FeedbackEstrategias")
                    val feedbacks: ArrayList<FeedbackEstrategia?> = ArrayList()
                    if (task.isSuccessful) {
                        for (document: QueryDocumentSnapshot? in task.result!!) {
                            val feedback: FeedbackEstrategia =
                                document?.toObject(FeedbackEstrategia::class.java)!!
                            feedbacks.add(feedback)
                            Log.d(
                                "ProfileActivity",
                                "Success getting Feedbacks: " + feedback.id
                            )
                        }
                        db
                            .collection("" + BD.estrategias_demo)
                            .get()
                            .addOnCompleteListener { task ->
                                Log.d(
                                    "ProfileActivity",
                                    "Success getting documents of Estrategias"
                                )
                                val estrategias: ArrayList<Estrategia?> = ArrayList()
                                if (task.isSuccessful) {
                                    for (document: QueryDocumentSnapshot? in task.result!!) {
                                        val estrategia: Estrategia =
                                            document?.toObject(Estrategia::class.java)!!
                                        estrategias.add(estrategia)
                                    }
                                    apresentaEstatisticasFeedback(feedbacks, estrategias)
                                }
                            }
                    }
                }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun apresentaEstatisticasFeedback(feedbacks: ArrayList<FeedbackEstrategia?>, estrategias: ArrayList<Estrategia?>) {
        //Log.d("ProfileActivity", "Success getting Estrategias");
        val estrategiasComFeedback: ArrayList<Estrategia?> = ArrayList()
        val estrategiasComFeedbackRepetidas: ArrayList<Estrategia?> = ArrayList()
        val datasFeedbacks: ArrayList<Data?> = ArrayList()
        val feedbackDaCrianca: ArrayList<FeedbackEstrategia?> = ArrayList()
        if (feedbacks?.size > 0 && estrategias.size > 0) {
            Log.d("ProfileActivity", "QTD ESTRATEGIAS: " + estrategias.size + " ||| QTD FEEDBACKS: " + feedbacks.size)
            val numFeedbacksDB: Int = feedbacks.size
            val numEstrategias: Int = estrategias.size
            for (i in 0 until numEstrategias) {
                for (j in 0 until numFeedbacksDB) {
                    if ((java.lang.Long.toString(estrategias[i]?.id!!) == feedbacks.get(j)?.idEstrategia)) {
                        estrategiasComFeedbackRepetidas.add(estrategias.get(i))
                        datasFeedbacks.add(feedbacks.get(j)?.data)
                        feedbackDaCrianca.add(feedbacks.get(j))
                        if (!estrategiasComFeedback.contains(estrategias.get(i))) {
                            estrategiasComFeedback.add(estrategias.get(i))
                        }
                        continue
                    }
                }
            }
        }

        //Historico de Feedbacks
        apresentarHistoricoFeedbacks(estrategiasComFeedbackRepetidas, datasFeedbacks, feedbackDaCrianca)

        //Lista de estrategias com feedback dado
        val frequencyMap: MutableMap<Estrategia?, Int?> = HashMap()
        for (s: Estrategia? in estrategiasComFeedbackRepetidas) {
            var count: Int? = frequencyMap.get(s)
            if (count == null) {
                count = 0
            }
            frequencyMap.put(s, count + 1)
        }
        val estrategiasOrdenadas: ArrayList<Estrategia?> = ArrayList()
        val contadorFeedbacks: ArrayList<Int?> = ArrayList()
        if (estrategiasComFeedback.size > 0) {
            var countIndI: Int = 0
            var i: Int = 0
            while (i < estrategiasComFeedback.size) {
                var max: Int = 0
                var saveIndJ: Int = 0
                for (j in i until estrategiasComFeedback.size) {
                    if (max < frequencyMap.get(estrategiasComFeedback.get(j))!!) {
                        max = frequencyMap.get(estrategiasComFeedback.get(j)!!)!!
                        saveIndJ = j
                        if (estrategiasOrdenadas.size <= countIndI) {
                            estrategiasOrdenadas.add(estrategiasComFeedback.get(j))
                            contadorFeedbacks.add(max)
                        } else {
                            estrategiasOrdenadas.set(countIndI, estrategiasComFeedback.get(j))
                            contadorFeedbacks.set(countIndI, max)
                        }
                    }
                }
                estrategiasComFeedback.removeAt(saveIndJ)
                i = 0
                countIndI++
                if (estrategiasComFeedback.size == 1) {
                    estrategiasOrdenadas.add(estrategiasComFeedback.get(0))
                    contadorFeedbacks.add(frequencyMap.get(estrategiasComFeedback.get(0)))
                }
                i++
            }
        }
        var nomesEstrategias: ArrayList<String?>? = ArrayList()
        nomesEstrategias = getNomesEstrategiasTop(estrategiasOrdenadas, 5)
        if (nomesEstrategias.size == 0) {
            nomesEstrategias.add("Nenhuma estratégia com feedback dado")
            btnHistoricoFeedback?.setVisibility(View.GONE)
        } else {
            btnHistoricoFeedback?.setVisibility(View.VISIBLE)
        }
        val ids: ArrayList<Long?>? = getIdsEstrategiasTop(estrategiasOrdenadas, 5)
        listaEstrategias = findViewById(R.id.listViewPEListaEstrategias)
        val profileEstatisticaAdapter: ProfileEstatisticaAdapter = ProfileEstatisticaAdapter(this@ProfileActivity, nomesEstrategias, contadorFeedbacks, estrategiasOrdenadas, ids)
        listaEstrategias?.setAdapter(profileEstatisticaAdapter)
        listaEstrategias?.setOnTouchListener { v, event ->
            val action: Int = event.getAction()
            when (action) {
                MotionEvent.ACTION_DOWN ->                         // Disallow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(true)
                MotionEvent.ACTION_UP ->                         // Allow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(false)
            }

            // Handle ListView touch events.
            v.onTouchEvent(event)
            true
        }
    }

    private fun relatorioSemanalEstatisticas(idCrianca: String) {
        db
                .collection("relatorioSemanal")
                .whereEqualTo("idCrianca", idCrianca)
                .get()
                .addOnCompleteListener { task ->
                    Log.d(
                        "ProfileActivity/Estatisticas",
                        "Success getting RelatorioSemanal documents"
                    )
                    val relatorios: ArrayList<RelatorioSemanal?> = ArrayList()
                    if (task.isSuccessful) {
                        for (document: QueryDocumentSnapshot? in task.result!!) {
                            val relatorio: RelatorioSemanal =
                                document?.toObject(RelatorioSemanal::class.java)!!
                            relatorios.add(relatorio)
                        }
                        Log.d("ProfileActivity/Estatisticas", "Success getting Relatorios Semanais")
                        val totalRelatorios: Int = relatorios.size
                        apresentarComentariosRelatorios(relatorios)
                        apresentarTotal(totalRelatorios)
                        apresentarMedias(relatorios)
                        apresentarGrafico(totalRelatorios)
                    }
                }
    }

    private fun apresentarGrafico(totalRelatorios: Int) {
        val abrirGraph: AppCompatButton = findViewById(R.id.btnVerGraph)
        val layoutMedia: LinearLayout = findViewById(R.id.linearLayoutMediaRelatorios)
        val textViewHistoricoComentariosRTitulo: TextView = findViewById(R.id.textViewHistoricoComentariosRTitulo)
        val listViewHistoricoComentariosRelatorio: ListView = findViewById(R.id.listViewHistoricoComentariosRelatorio)
        if (totalRelatorios == 0) {
            abrirGraph.setVisibility(View.GONE)
            layoutMedia.setVisibility(View.GONE)
            textViewHistoricoComentariosRTitulo.setVisibility(View.GONE)
            listViewHistoricoComentariosRelatorio.setVisibility(View.GONE)
        }
        if (totalRelatorios < 2) {
            abrirGraph.setVisibility(View.GONE)
        } else {
            abrirGraph.setVisibility(View.VISIBLE)
        }
        abrirGraph.setOnClickListener(View.OnClickListener({ openAtividadeGraph() }))
    }

    private fun openAtividadeGraph() {
        val intent: Intent = Intent(this, LineChartActivity::class.java)
        startActivity(intent)
    }

    private fun apresentarMedias(relatorios: ArrayList<RelatorioSemanal?>?) {
        var soma1: Long? = java.lang.Long.valueOf(0)
        var soma2: Long? = java.lang.Long.valueOf(0)
        var soma3: Long? = java.lang.Long.valueOf(0)
        var soma4: Long? = java.lang.Long.valueOf(0)
        var soma5: Long? = java.lang.Long.valueOf(0)
        var soma6: Long? = java.lang.Long.valueOf(0)
        var soma7: Long? = java.lang.Long.valueOf(0)
        var media1: Long? = java.lang.Long.valueOf(0)
        var media2: Long? = java.lang.Long.valueOf(0)
        var media3: Long? = java.lang.Long.valueOf(0)
        var media4: Long? = java.lang.Long.valueOf(0)
        var media5: Long? = java.lang.Long.valueOf(0)
        var media6: Long? = java.lang.Long.valueOf(0)
        var media7: Long? = java.lang.Long.valueOf(0)
        val av1: TextView? = findViewById(R.id.textViewMediaAv1)
        val av2: TextView? = findViewById(R.id.textViewMediaAv2)
        val av3: TextView? = findViewById(R.id.textViewMediaAv3)
        val av4: TextView? = findViewById(R.id.textViewMediaAv4)
        val av5: TextView? = findViewById(R.id.textViewMediaAv5)
        val av6: TextView? = findViewById(R.id.textViewMediaAv6)
        val av7: TextView? = findViewById(R.id.textViewMediaAv7)
        if (relatorios?.size!! > 0) {
            //AV1
            soma1 = calcularSoma(relatorios, 1)
            media1 = calcularMedia(relatorios.size, soma1)
            //AV2
            soma2 = calcularSoma(relatorios, 2)
            media2 = calcularMedia(relatorios.size, soma2)
            //AV3
            soma3 = calcularSoma(relatorios, 3)
            media3 = calcularMedia(relatorios.size, soma3)
            //AV4
            soma4 = calcularSoma(relatorios, 4)
            media4 = calcularMedia(relatorios.size, soma4)
            //AV5
            soma5 = calcularSoma(relatorios, 5)
            media5 = calcularMedia(relatorios.size, soma5)
            //AV6
            soma6 = calcularSoma(relatorios, 6)
            media6 = calcularMedia(relatorios.size, soma6)
            //AV7
            soma7 = calcularSoma(relatorios, 7)
            media7 = calcularMedia(relatorios.size, soma7)
            av1?.setText("" + media1)
            av2?.setText("" + media2)
            av3?.setText("" + media3)
            av4?.setText("" + media4)
            av5?.setText("" + media5)
            av6?.setText("" + media6)
            av7?.setText("" + media7)
        }
    }

    private fun calcularMedia(size: Int, soma: Long): Long {
        var media: Long = java.lang.Long.valueOf(0)
        if (soma != 0L) {
            media = soma / size
        }
        return media
    }

    private fun calcularSoma(relatorios: ArrayList<RelatorioSemanal?>, av: Int): Long {
        var soma: Long = java.lang.Long.valueOf(0)
        when (av) {
            1 -> {
                var i: Int = 0
                while (i < relatorios.size) {
                    soma += relatorios.get(i)?.avaliacao1!!
                    i++
                }
            }
            2 -> {
                var i: Int = 0
                while (i < relatorios.size) {
                    soma = soma + relatorios.get(i)?.avaliacao2!!
                    i++
                }
            }
            3 -> {
                var i: Int = 0
                while (i < relatorios.size) {
                    soma = soma + relatorios.get(i)?.avaliacao3!!
                    i++
                }
            }
            4 -> {
                var i: Int = 0
                while (i < relatorios.size) {
                    soma = soma + relatorios.get(i)?.avaliacao4!!
                    i++
                }
            }
            5 -> {
                var i: Int = 0
                while (i < relatorios.size) {
                    soma = soma + relatorios.get(i)?.avaliacao5!!
                    i++
                }
            }
            6 -> {
                var i: Int = 0
                while (i < relatorios.size) {
                    soma = soma + relatorios.get(i)?.avaliacao6!!
                    i++
                }
            }
            7 -> {
                var i: Int = 0
                while (i < relatorios.size) {
                    soma = soma + relatorios.get(i)?.avaliacao7!!
                    i++
                }
            }
        }
        return soma
    }

    private fun apresentarTotal(total: Int) {
        val totalRelatorios: TextView = findViewById(R.id.textViewQtdRelatorios)
        totalRelatorios.setText("" + total)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun apresentarComentariosRelatorios(relatorios: ArrayList<RelatorioSemanal?>) {
        val comentariosDaDB: ArrayList<String?> = ArrayList()
        val datasDaDB: ArrayList<String?> = ArrayList()
        if (relatorios.size > 0) {
            for (i in relatorios.indices.reversed()) {
                val com: String = relatorios[i]?.comentario!!
                val dataComentario: Data? = relatorios[i]?.data
                if (com.isEmpty()) {
                    continue
                }
                comentariosDaDB.add(com)
                datasDaDB.add(dataComentario.toString())
            }
        }
        if (comentariosDaDB.size == 0) {
            comentariosDaDB.add("Nenhum comentário a apresentar!")
            datasDaDB.add("")
        }
        val comentariosRelatorios: ListView = findViewById(R.id.listViewHistoricoComentariosRelatorio)
        val areaEstrategiaAdapter: AreasEstrategiasAdapter = AreasEstrategiasAdapter(this@ProfileActivity, comentariosDaDB, datasDaDB)
        comentariosRelatorios.adapter = areaEstrategiaAdapter
        comentariosRelatorios.setOnTouchListener(
        (OnTouchListener { v, event ->
        val action: Int = event?.getAction()!!
        when (action) {
            MotionEvent.ACTION_DOWN ->                         // Disallow ScrollView to intercept touch events.
                v?.getParent()?.requestDisallowInterceptTouchEvent(true)
            MotionEvent.ACTION_UP ->                         // Allow ScrollView to intercept touch events.
                v?.getParent()?.requestDisallowInterceptTouchEvent(false)
        }

        // Handle ListView touch events.
        v?.onTouchEvent(event)
        true
    })
        )
    }

    private fun apresentarHistoricoFeedbacks(estrategiasComFeedbackRepetidas: ArrayList<Estrategia?>, datasFeedbacks: ArrayList<Data?>, feedbacks: ArrayList<FeedbackEstrategia?>) {
        val datasFbOrdenadas: ArrayList<Data?> = datasFeedbacks
        val estrategiasOrdenadosPDataDescrecente: ArrayList<Estrategia?> = estrategiasComFeedbackRepetidas
        val feedbacksOrdenadosPData: ArrayList<FeedbackEstrategia?> = feedbacks
        if (datasFbOrdenadas.size > 0) {
            for (i in 0 until datasFbOrdenadas.size - 1) {
                var guardaDataInicial: Data = datasFbOrdenadas.get(i)!!
                for (j in i + 1 until datasFbOrdenadas.size) {
                    if (guardaDataInicial.comparar(datasFbOrdenadas.get(j)) < 0) { //encontrou data maior
                        guardaDataInicial = datasFbOrdenadas.get(j)!!
                        val aux: Data? = datasFbOrdenadas.get(i)
                        datasFbOrdenadas.set(i, datasFbOrdenadas.get(j))
                        datasFbOrdenadas.set(j, aux)
                        val etgAux: Estrategia? = estrategiasOrdenadosPDataDescrecente.get(i)
                        estrategiasOrdenadosPDataDescrecente.set(i, estrategiasOrdenadosPDataDescrecente.get(j))
                        estrategiasOrdenadosPDataDescrecente.set(j, etgAux)
                        val fbAux: FeedbackEstrategia? = feedbacksOrdenadosPData.get(i)
                        feedbacksOrdenadosPData.set(i, feedbacksOrdenadosPData.get(j))
                        feedbacksOrdenadosPData.set(j, fbAux)
                    }
                }
            }
        }
        val nomesEstrategias: ArrayList<String?> = ArrayList()
        val datasFeedbacksStr: ArrayList<String?> = ArrayList()
        val idFeedbacks: ArrayList<String?> = ArrayList()
        if (estrategiasOrdenadosPDataDescrecente.size > 0) {
            for (i in estrategiasOrdenadosPDataDescrecente.indices) {
                val com: String? = estrategiasOrdenadosPDataDescrecente.get(i)?.descricao
                val dataFeedback: Data? = datasFbOrdenadas.get(i)
                val fb: FeedbackEstrategia? = feedbacksOrdenadosPData.get(i)
                if (com?.isEmpty()!!) {
                    continue
                }
                nomesEstrategias.add(com)
                datasFeedbacksStr.add(dataFeedback.toString())
                idFeedbacks.add(fb?.id.toString() + "")
            }
        }
        val historicoFeedback: ListView? = findViewById(R.id.lvHistoricoFeedbacks)
        val hFAdapter: HistoricoFeedbackAdapter = HistoricoFeedbackAdapter(this@ProfileActivity, nomesEstrategias, datasFeedbacksStr, feedbacksOrdenadosPData)
        historicoFeedback?.adapter = hFAdapter
    }

    companion object {
        private val TAG: String? = "ProfileActivity"
    }
}