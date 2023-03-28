package pt.pl.estg.ei.regulapp.forum

import com.google.firebase.auth.FirebaseAuth
import pt.pl.estg.ei.regulapp.R
import org.ocpsoft.prettytime.PrettyTime
import pt.pl.estg.ei.regulapp.R.layout
import android.content.Context
import android.graphics.Color
import android.view.*
import android.widget.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

internal class ThreadsAdapter constructor(context: Context, resource: Int, objects: MutableList<MessageThread?>, threadsActivity: ThreadsActivity?) : ArrayAdapter<MessageThread?>(context, resource, objects) {
    private val ctx: Context?
    private var messageThread: MessageThread? = null
    private val dataUpdateAfterDelete: DataUpdateAfterDelete
    private val mAuth: FirebaseAuth
    private val TAG: String = "demoThreadAdapter"
    private val messageThreadObjects: ArrayList<MessageThread?>

    //Idade da thread
    private val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.UK)
    private val p: PrettyTime = PrettyTime(Locale("pt"))
    private var convertedDate: Date? = null
    public override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView: View? = convertView
        messageThread = getItem(position)
        val viewHolder: ViewHolder
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(layout.threads_listview, parent, false)
            viewHolder = ViewHolder()
            viewHolder.threadTitleTV = convertView.findViewById(R.id.threadTitleTV)
            viewHolder.threadAuthor = convertView.findViewById(R.id.forumAutor)
            viewHolder.time = convertView.findViewById(R.id.forumThreadAge)
            viewHolder.qtdComentarios = convertView.findViewById(R.id.forumQtdComentarios)
            viewHolder.deleteThreadButton = convertView.findViewById(R.id.deleteThreadButton)
            viewHolder.verified = convertView.findViewById(R.id.verifiedUser)
            convertView.setTag(viewHolder)
        } else {
            viewHolder = convertView.getTag() as ViewHolder
        }
        viewHolder.threadAuthor?.setText(messageThread?.user_name)
        viewHolder.qtdComentarios?.setText(messageThread?.qtdComentarios.toString() + "")
        if (!messageThread?.profissional?.isEmpty()!!) {
            viewHolder.verified?.setVisibility(View.VISIBLE)
        }
        try {
            convertedDate = dateFormat.parse(messageThread?.created_time)
            p.setReference(Date())
        } catch (e: ParseException) {
            e.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(e); // log it to Crashalytics despite catching the exception
        }
        viewHolder.time?.setText(p.format(convertedDate))
        viewHolder.threadTitleTV?.setTextColor(Color.parseColor("#000000"))
        viewHolder.threadTitleTV?.setText(messageThread?.title)
        try {
            if (!(messageThread?.user_id == mAuth.getCurrentUser()?.getUid())) {
                viewHolder.deleteThreadButton?.setVisibility(View.INVISIBLE)
            } else {
                viewHolder.deleteThreadButton?.setVisibility(View.VISIBLE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        viewHolder.deleteThreadButton?.setOnClickListener {
            dataUpdateAfterDelete.deleteThread(
                messageThreadObjects.get(position)?.thread_id
            )
        }
        viewHolder.deleteThreadButton?.setVisibility(View.GONE)
        return convertView!!
    }

    private class ViewHolder constructor() {
        var threadTitleTV: TextView? = null
        var threadAuthor: TextView? = null
        var deleteThreadButton: ImageButton? = null
        var time: TextView? = null
        var qtdComentarios: TextView? = null
        var verified: ImageView? = null
    }

    open interface DataUpdateAfterDelete {
        open fun deleteThread(thread_id: String?)
    }

    init {
        ctx = context
        dataUpdateAfterDelete = threadsActivity!!
        messageThreadObjects = objects as ArrayList<MessageThread?>
        mAuth = FirebaseAuth.getInstance()
    }
}