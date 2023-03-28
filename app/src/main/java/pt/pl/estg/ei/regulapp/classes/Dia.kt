package pt.pl.estg.ei.regulapp.classes

import pt.pl.estg.ei.regulapp.enums.DiaDaSemana
import java.util.*

class Dia constructor(var name: DiaDaSemana?) {
    var estrategias: LinkedList<Estrategia?>

    init {
        estrategias = LinkedList()
    }
}