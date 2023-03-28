package pt.pl.estg.ei.regulapp.forum

import com.google.firebase.auth.FirebaseAuth
import pt.pl.estg.ei.regulapp.R
import org.ocpsoft.prettytime.PrettyTime
import pt.pl.estg.ei.regulapp.R.layout
import android.content.Context
import android.view.*
import android.widget.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

internal class MessagesAdapter constructor(context: Context, resource: Int, objects: MutableList<Message?>, chatActivity: ThreadActivity?) : ArrayAdapter<Message?>(context, resource, objects) {
    private val ctx: Context?
    var message: Message? = null
    private val TAG: String? = "demoMessageAdapter"
    private val dataUpdateAfterMessageDelete: DataUpdateAfterMessageDelete?
    private val mAuth: FirebaseAuth?
    private val messageObjects: ArrayList<Message?>?
    private val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US)
    private val p: PrettyTime = PrettyTime(Locale("pt"))
    private var convertedDate: Date? = null
    public override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView: View? = convertView
        message = getItem(position)
        val viewHolder: ViewHolder?
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(layout.messages_listview, parent, false)
            viewHolder = ViewHolder()
            viewHolder.messageTV = convertView.findViewById(R.id.messageTV)
            viewHolder.messageSenderTV = convertView.findViewById(R.id.forumAutorComentario)
            viewHolder.messageTimeTV = convertView.findViewById(R.id.messageTimeTV)
            viewHolder.deleteMessageButton = convertView.findViewById(R.id.deleteMessageButton)
            viewHolder.verified = convertView.findViewById(R.id.verifiedUser)
            convertView.setTag(viewHolder)
        } else {
            viewHolder = convertView.getTag() as ViewHolder?
        }
        if (!(message?.user_id == mAuth?.getCurrentUser()?.uid)) {
            viewHolder?.deleteMessageButton?.setVisibility(View.INVISIBLE)
        } else {
            viewHolder?.deleteMessageButton?.setVisibility(View.VISIBLE)
        }
        viewHolder?.messageSenderTV?.setText(message?.user_name)
        viewHolder?.messageTV?.setText(message?.message)
        if (!message?.profissional?.isEmpty()!!) {
            viewHolder?.verified?.setVisibility(View.VISIBLE)
        }
        try {
            convertedDate = dateFormat.parse(message?.created_time)
            p.setReference(Date())
        } catch (e: ParseException) {
            e.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(e); // log it to Crashalytics despite catching the exception
        }
        viewHolder?.messageTimeTV?.setText(p.format(convertedDate))
        viewHolder?.deleteMessageButton?.setOnClickListener {
            dataUpdateAfterMessageDelete?.deleteMessage(
                messageObjects?.get(position)?.message_id
            )
        }
        return convertView!!
    }

    private class ViewHolder constructor() {
        var messageTV: TextView? = null
        var messageSenderTV: TextView? = null
        var messageTimeTV: TextView? = null
        var deleteMessageButton: ImageView? = null
        var verified: ImageView? = null
    }

    open interface DataUpdateAfterMessageDelete {
        open fun deleteMessage(message_id: String?)
    }

    init {
        ctx = context
        messageObjects = objects as ArrayList<Message?>?
        dataUpdateAfterMessageDelete = chatActivity
        mAuth = FirebaseAuth.getInstance()
    }
}