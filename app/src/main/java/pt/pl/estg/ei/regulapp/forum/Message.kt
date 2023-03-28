package pt.pl.estg.ei.regulapp.forum

class Message {
    var message_id: String? = null
    var message: String? = null
    var user_id: String? = null
    var user_name: String? = null
    var created_time: String? = null
    var profissional: String? = null

    constructor(message: String?, user_id: String?, user_name: String?, created_time: String?) {
        this.message = message
        this.user_id = user_id
        this.user_name = user_name
        this.created_time = created_time
        profissional = ""
    }

    constructor() {}

    public override fun toString(): String {
        return ("Message{" +
                "message_id='" + message_id + '\'' +
                ", message='" + message + '\'' +
                ", user_id='" + user_id + '\'' +
                ", user_name='" + user_name + '\'' +
                ", profissional='" + profissional + '\'' +
                ", created_time='" + created_time + '\'' +
                '}')
    }
}