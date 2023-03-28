package pt.pl.estg.ei.regulapp.classes

import pt.pl.estg.ei.regulapp.enums.DiaDaSemana
import java.util.ArrayList

class Horario constructor() {
    protected var horario: ArrayList<Dia?> = ArrayList()
    fun inserirEstrategia(estrategia: Estrategia?, diaDaSemana: DiaDaSemana?) {
        var index: Int = -1
        var diaAux: Dia? = null
        for (dia: Dia? in horario) {
            index++
            if (dia?.name == diaDaSemana) {
                diaAux = horario.get(index)
            }
        }
        if (index == -1) {
            diaAux = Dia(diaDaSemana)
            horario.add(diaAux)
        }
        diaAux?.estrategias?.add(estrategia)
    }

}