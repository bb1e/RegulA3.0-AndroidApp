package pt.pl.estg.ei.regulapp.chat

class Contacts {
    var name: String? = null
    var image: String? = null
    var description: String? = null
    var age: String? = null
    var uid: String? = null

    constructor() {}
    constructor(id: String?, name: String?, age: String?, image: String?) {
        uid = id
        this.name = name
        this.age = age
        this.image = image
    }
}