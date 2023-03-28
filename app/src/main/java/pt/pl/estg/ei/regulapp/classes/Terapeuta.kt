package pt.pl.estg.ei.regulapp.classes

class Terapeuta {
    var uid: String? = null
    var image: String? = null
    var name: String? = null
    var description: String? = null

    constructor() {}
    constructor(uid: String?, image: String?, name: String?, status: String?) {
        this.uid = uid
        this.image = image
        this.name = name
        description = status
    }

}