package pt.pl.estg.ei.regulapp

import android.os.Bundle
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import pt.pl.estg.ei.regulapp.R.layout
import com.google.firebase.firestore.QueryDocumentSnapshot
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.Log
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.components.XAxis
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.gms.tasks.Task
import pt.pl.estg.ei.regulapp.classes.*
import java.util.ArrayList

class LineChartActivity : AppCompatActivity() {
    private var lineChart1: LineChart? = null
    private var lineChart2: LineChart? = null
    private var lineChart3: LineChart? = null
    private var lineChart4: LineChart? = null
    private var lineChart5: LineChart? = null
    private var lineChart6: LineChart? = null
    private var lineChart7: LineChart? = null
    private var mes: String? = null
    private var idCrianca: String? = null
    private var av1Selected: Boolean = false
    private var av2Selected: Boolean = false
    private var av3Selected: Boolean = false
    private var av4Selected: Boolean = false
    private var av5Selected: Boolean = false
    private var av6Selected: Boolean = false
    private var av7Selected: Boolean = false
    private var layoutIndividuais: ConstraintLayout? = null
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_line_chart)
        idCrianca = GlobalSettings.Companion.myAppInstance?.session?.thisSession?.id
        lineChart1 = findViewById(R.id.lineChartAv1)
        lineChart2 = findViewById(R.id.lineChartAv2)
        lineChart3 = findViewById(R.id.lineChartAv3)
        lineChart4 = findViewById(R.id.lineChartAv4)
        lineChart5 = findViewById(R.id.lineChartAv5)
        lineChart6 = findViewById(R.id.lineChartAv6)
        lineChart7 = findViewById(R.id.lineChartAv7)
        layoutIndividuais = findViewById(R.id.layoutGraficosIndividuais)
        av1Selected = true
        av2Selected = true
        av3Selected = true
        av4Selected = true
        av5Selected = true
        av6Selected = true
        av7Selected = true
        mes = "Nenhum"
        loadLineChartData(idCrianca)
        lineChart1?.animateX(500)
        lineChart2?.animateX(500)
        lineChart3?.animateX(500)
        lineChart4?.animateX(500)
        lineChart5?.animateX(500)
        lineChart6?.animateX(500)
        lineChart7?.animateX(500)
    }

    private fun loadLineChartData(idCrianca: String?) {
        db
                .collection("relatorioSemanal")
                .whereEqualTo("idCrianca", idCrianca)
                .get()
                .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot?> {
                    override fun onComplete(task: Task<QuerySnapshot?>) {
                        Log.d("ProfileActivity/Estatisticas", "Success getting RelatorioSemanal documents")
                        val relatorios: ArrayList<RelatorioSemanal?>? = ArrayList()
                        if (task.isSuccessful) {
                            for (document: QueryDocumentSnapshot in task.result!!) {
                                val relatorio: RelatorioSemanal? = document.toObject(RelatorioSemanal::class.java)
                                relatorios?.add(relatorio)
                            }
                            Log.d("ProfileActivity/Estatisticas", "Success getting Relatorios Semanais")
                            val av1ChartData: ArrayList<Entry> = ArrayList()
                            val av2ChartData: ArrayList<Entry> = ArrayList()
                            val av3ChartData: ArrayList<Entry> = ArrayList()
                            val av4ChartData: ArrayList<Entry> = ArrayList()
                            val av5ChartData: ArrayList<Entry> = ArrayList()
                            val av6ChartData: ArrayList<Entry> = ArrayList()
                            val av7ChartData: ArrayList<Entry> = ArrayList()
                            val avXstr: ArrayList<String> = ArrayList()
                            for (i in relatorios?.indices!!) {
                                av1ChartData.add(Entry(
                                    i.toFloat(),
                                    relatorios.get(i)?.avaliacao1?.toFloat()!!
                                ))
                                av2ChartData.add(Entry(i.toFloat(), relatorios[i]?.avaliacao2?.toFloat()!!))
                                av3ChartData.add(Entry(i.toFloat(), relatorios[i]?.avaliacao3?.toFloat()!!))
                                av4ChartData.add(Entry(i.toFloat(), relatorios[i]?.avaliacao4?.toFloat()!!))
                                av5ChartData.add(Entry(i.toFloat(), relatorios[i]?.avaliacao5?.toFloat()!!))
                                av6ChartData.add(Entry(i.toFloat(), relatorios[i]?.avaliacao6?.toFloat()!!))
                                av7ChartData.add(Entry(i.toFloat(), relatorios[i]?.avaliacao7?.toFloat()!!))
                                avXstr.add(relatorios[i]?.diaInicioSemana.toString())
                            }
                            val av1: LineDataSet = LineDataSet(av1ChartData, "Tomar banho")
                            av1.color = resources.getColor(R.color.amarelo_estrategias)
                            av1.lineWidth = 2f
                            av1.setDrawValues(false)
                            av1.setDrawCircles(true)
                            av1.setCircleColors(resources.getColor(R.color.amarelo_estrategias))
                            val av2: LineDataSet = LineDataSet(av2ChartData, "Vestir e despir")
                            av2.color = resources.getColor(R.color.rosa)
                            av2.setDrawCircles(true)
                            av2.setCircleColors(resources.getColor(R.color.rosa))
                            av2.setDrawValues(false)
                            av2.lineWidth = 2f
                            val av3: LineDataSet = LineDataSet(av3ChartData, "Alimentação")
                            av3.color = resources.getColor(R.color.azul_forum)
                            av3.setDrawCircles(true)
                            av3.setCircleColors(resources.getColor(R.color.azul_forum))
                            av3.setDrawValues(false)
                            av3.lineWidth = 2f
                            val av4: LineDataSet = LineDataSet(av4ChartData, "Higiene sanitária")
                            av4.color = resources.getColor(R.color.cinza2)
                            av4.setDrawCircles(true)
                            av4.setCircleColors(resources.getColor(R.color.cinza2))
                            av4.setDrawValues(false)
                            av4.lineWidth = 2f
                            val av5: LineDataSet = LineDataSet(av5ChartData, "Higiene pessoal (lavar os dentes, cortar as unhas, lavar as mãos, colocar creme, cortar o cabelo)")
                            av5.color = resources.getColor(R.color.azul3)
                            av5.setDrawCircles(true)
                            av5.setCircleColors(resources.getColor(R.color.azul3))
                            av5.setDrawValues(false)
                            av5.lineWidth = 2f
                            val av6: LineDataSet = LineDataSet(av6ChartData, "Descanso e sono")
                            av6.color = resources.getColor(R.color.verde_toolbar_avd)
                            av6.setCircleColors(resources.getColor(R.color.verde_toolbar_avd))
                            av6.setDrawCircles(true)
                            av6.setDrawValues(false)
                            av6.lineWidth = 2f
                            val av7: LineDataSet = LineDataSet(av7ChartData, "Brincar e jogar")
                            av7.color = resources.getColor(R.color.roxo)
                            av7.setDrawCircles(true)
                            av7.setCircleColors(resources.getColor(R.color.roxo))
                            av7.setDrawValues(false)
                            av7.lineWidth = 2f
                            inicializarIndividuais(av1, av2, av3, av4, av5, av6, av7, avXstr)
                        }
                    }
                })
    }

    private fun formatarLegenda(diaInicioSemana: Data?, diaFimSemana: Data?): String? {
        if ((mes == "Nenhum")) {
            mes = diaInicioSemana?.getMesStr()
            return mes
        }
        if ((mes == diaInicioSemana?.getMesStr())) {
            return null
        }
        return diaFimSemana?.getMesStr()
    }

    private fun inicializarIndividuais(av1: LineDataSet?, av2: LineDataSet?, av3: LineDataSet?, av4: LineDataSet?, av5: LineDataSet?, av6: LineDataSet?, av7: LineDataSet?, avXstr: ArrayList<String>) {
        val data1: LineData = LineData(av1)
        val data2: LineData = LineData(av2)
        val data3: LineData = LineData(av3)
        val data4: LineData = LineData(av4)
        val data5: LineData = LineData(av5)
        val data6: LineData = LineData(av6)
        val data7: LineData = LineData(av7)
        formatarGraficos(lineChart1, avXstr)
        formatarGraficos(lineChart2, avXstr)
        formatarGraficos(lineChart3, avXstr)
        formatarGraficos(lineChart4, avXstr)
        formatarGraficos(lineChart5, avXstr)
        formatarGraficos(lineChart6, avXstr)
        formatarGraficos(lineChart7, avXstr)
        lineChart1?.data = data1
        lineChart2?.data = data2
        lineChart3?.data = data3
        lineChart4?.data = data4
        lineChart5?.data = data5
        lineChart6?.data = data6
        lineChart7?.data = data7
    }

    private fun formatarGraficos(graph: LineChart?, xStr: ArrayList<String>) {
        graph?.xAxis?.setDrawAxisLine(false)
        graph?.xAxis?.setDrawGridLines(false)
        graph?.xAxis?.setDrawGridLinesBehindData(false)
        graph?.xAxis?.granularity = 1f
        graph?.xAxis?.setDrawLabels(true)
        graph?.xAxis?.position = XAxis.XAxisPosition.BOTTOM
        graph?.xAxis?.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String? {
                return xStr.get(Math.round(value))
            }
        }
        graph?.xAxis?.granularity = 1f
        graph?.axisLeft?.setLabelCount(5, true)
        graph?.axisLeft?.axisMaximum = 5f
        graph?.axisLeft?.axisMinimum = 1f
        graph?.legend?.isEnabled = false
        val des: Description = graph?.description!!
        des.isEnabled = false
        graph.axisRight.isEnabled = false
    }
}