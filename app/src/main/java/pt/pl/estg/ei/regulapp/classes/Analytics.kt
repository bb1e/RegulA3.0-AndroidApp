package pt.pl.estg.ei.regulapp.classes

import android.os.Bundle
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import pt.pl.estg.ei.regulapp.forum.MessageThread

sealed class AnalyticsEvent(vararg params: Pair<String,String>) {
    val args: Map<String,String> = mapOf(*params);

    object LOGIN_PASSWORD : AnalyticsEvent()
    object LOGIN_PASSWORD_FAILED : AnalyticsEvent()
    object LOGIN_GOOGLE : AnalyticsEvent()
    object LOGIN_GOOGLE_FAILED : AnalyticsEvent()
    object LOGIN_AUTO : AnalyticsEvent()
    object LOGOUT : AnalyticsEvent()

    object SIGNUP_PASSWORD : AnalyticsEvent()
    object SIGNUP_PASSWORD_FAILED : AnalyticsEvent()
    object SIGNUP_GOOGLE : AnalyticsEvent()
    object SIGNUP_GOOGLE_FAILED : AnalyticsEvent()

    object RESETPASSWORD_MAILSENT : AnalyticsEvent()
    object RESETPASSWORD_MAILSENT_FAILED : AnalyticsEvent()
    object RESETPASSWORD_OPENMENU : AnalyticsEvent()

    class ADDCRIANCA_FAILED(reason:String): AnalyticsEvent("reason" to reason)
    object ADDCRIANCA_SUCCESS : AnalyticsEvent()

    // Este menu já é o submenu das "listas de estratégias", o submenu das "Atividades diárias" não é registado
    object OCUPACOES_OPENMENU: AnalyticsEvent()
    class LISTAESTRATEGIAS_OPENMENU(opcao: String): AnalyticsEvent("opcao" to opcao)
    object ESTRATEGIA_VIEWED : AnalyticsEvent()
    object ESTRATEGIA_FEEDBACKENVIADO : AnalyticsEvent()
    object ESTRATEGIA_FAVORITA_NEW : AnalyticsEvent()
    object ESTRATEGIA_FAVORITA_REMOVIDA : AnalyticsEvent()
    object LISTAESTRATEGIASFAVORITAS_OPENMENU : AnalyticsEvent()

    object RELATORIOSEMANAL_OPENMENU:AnalyticsEvent()
    class RELATORIOSEMANAL_SENT(relatorio: RelatorioSemanal):AnalyticsEvent(
            "data" to relatorio.data.toString(),
            "comComentario" to (!relatorio.comentario.isNullOrEmpty()).toString()
    )

    object CHAT_SIGNUP_OPENMENU:AnalyticsEvent()
    object CHAT_SIGNUP_SUCCESS:AnalyticsEvent()
    object CHAT_SIGNUP_FAILED:AnalyticsEvent()

    object CHAT_MESSAGE_SUCCESS:AnalyticsEvent()
    class CHAT_MESSAGE_FAILED(error: String):AnalyticsEvent("error" to error)

    object FORUM_OPENMENU:AnalyticsEvent()
    class FORUM_SWITCHFILTER(filter: String) : AnalyticsEvent("filter" to filter)

    // nao mandamos o post inteiro pois dá o erro 4 "event parameter too long"
    // https://firebase.google.com/docs/analytics/errors
    class FORUM_VIEWPOST(post: MessageThread?) : AnalyticsEvent("post" to post?.title.toString()) {

    }

    // FORUM_SEARCH nao implementado. mandaria muitos eventos, pois é triggered a cada textchange.
    object FORUM_MEUSPOSTS_OPENMENU : AnalyticsEvent()
    object FORUM_SENDCOMMENT : AnalyticsEvent()
    object FORUM_NEWPOST_VALIDATIONERROR : AnalyticsEvent()
    object FORUM_NEWPOST_SUCCESS : AnalyticsEvent()
    object FORUM_DELETEPOST: AnalyticsEvent()
}

object Analytics {
    fun fireEvent(event: AnalyticsEvent) {
        val bundle = Bundle();
        event.args.forEach { entry ->
            bundle.putString(entry.key,entry.value)
        }
        Firebase.analytics.logEvent(event::class.simpleName!!, bundle)
    }
}