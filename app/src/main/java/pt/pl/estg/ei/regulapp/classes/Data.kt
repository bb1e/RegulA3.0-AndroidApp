package pt.pl.estg.ei.regulapp.classes

import kotlin.Throws
import java.util.*

class Data {
    var dia: Int = 0
    var mes: Int = 0
    var ano: Int = 0
    var horas: Int = 0
    var minutos: Int = 0

    constructor() {}
    constructor(dia: Int, mes: Int, ano: Int) {
        this.dia = dia
        this.mes = mes
        this.ano = ano
    }

    constructor(dia: Int, mes: Int, ano: Int, horas: Int, minutos: Int) {
        this.dia = dia
        this.mes = mes
        this.ano = ano
        this.horas = horas
        this.minutos = minutos
    }

    override fun toString(): String {
        return dia.toString() + "/" + mes + "/" + ano
    }

    fun horasToString(): String? {
        return horas.toString() + ":" + minutos
    }

    fun comparar(data: Data?): Int {
        /*
            <0 se data1 for inferior à data2
            0 se forem iguais
            >0 se data1 for superior à data2
         */
        val data1: Data? = this
        val data2: Data? = data
        if (Integer.compare(data1?.ano!!, data2?.ano!!) == 0) {
            if (Integer.compare(data1.mes, data2.mes) == 0) {
                if (Integer.compare(data1.dia, data2.dia) == 0) {
                    if (Integer.compare(data1.horas, data2.horas) == 0) {
                        return Integer.compare(data1.minutos, data2.minutos)
                    }
                    return Integer.compare(data1.horas, data2.horas)
                }
                return Integer.compare(data1.dia, data2.dia)
            }
            return Integer.compare(data1.mes, data2.mes)
        }
        return Integer.compare(data1.ano, data2.ano)
    }

    fun getIdade(): Int {
        var idade: Int = 0
        val hoje: Calendar? = Calendar.getInstance()
        idade = hoje?.get(Calendar.YEAR)!! - ano
        if (hoje.get(Calendar.MONTH) + 1 < mes) { //ainda não fez anos (mes atual é inferior ao dos anos)
            if (hoje.get(Calendar.DAY_OF_MONTH) < dia) { //dia atual inferior ao dos anos (mes tambem)
                return --idade
            }
            return --idade
        }
        return idade
    }

    fun getMesStr(): String? {
        val str: String?
        when (mes) {
            1 -> str = "Jan"
            2 -> str = "Fev"
            3 -> str = "Mar"
            4 -> str = "Abr"
            5 -> str = "Mai"
            6 -> str = "Jun"
            7 -> str = "Jul"
            8 -> str = "Ago"
            9 -> str = "Set"
            10 -> str = "Out"
            11 -> str = "Nov"
            12 -> str = "Dez"
            else -> str = null
        }
        return str
    }

    fun getMiniAno(): String? {
        return ano.toString().substring(2)
    }

    companion object {
        @Throws(InvalidPropertiesFormatException::class)
        fun parseData(data: String?): Data? {
            val partes: Array<String?> = data?.split("/")?.toTypedArray()!!
            if (partes.size != 3) {
                throw InvalidPropertiesFormatException("Data inválida!")
            }
            return Data(partes.get(0)?.toInt()!!, partes.get(1)!!.toInt(), partes.get(2)!!.toInt())
        }
    }
}