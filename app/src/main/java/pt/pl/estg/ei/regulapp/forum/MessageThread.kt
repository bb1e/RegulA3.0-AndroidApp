package pt.pl.estg.ei.regulapp.forum

import java.io.Serializable

class MessageThread : Serializable {
    var title: String? = null
    var description: String? = null
    var user_id: String? = null
    var user_name: String? = null
    var thread_id: String? = null
    var created_time: String? = null
    var profissional: String? = null
    var qtdComentarios: Int = 0

    constructor(title: String?, description: String?, user_id: String?, user_name: String?, created_time: String?) {
        this.title = title
        this.description = description
        this.user_id = user_id
        this.user_name = user_name
        this.created_time = created_time
        qtdComentarios = 0
        profissional = ""
    }

    constructor() {}

    public override fun toString(): String {
        return ("MessageThread{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", qtdComentarios='" + qtdComentarios + '\'' +
                ", profissional='" + profissional + '\'' +
                ", user_id='" + user_id + '\'' +
                ", user_name='" + user_name + '\'' +
                ", thread_id='" + thread_id + '\'' +
                ", created_time='" + created_time + '\'' +
                '}')
    }
}