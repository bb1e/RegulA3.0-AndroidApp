package pt.pl.estg.ei.regulapp.chat

import com.mikhaellopez.circularimageview.CircularImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import androidx.recyclerview.widget.RecyclerView
import pt.pl.estg.ei.regulapp.R
import com.google.firebase.database.FirebaseDatabase
import android.content.Intent
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import pt.pl.estg.ei.regulapp.chat.MessageAdapter.MessageViewHolder
import com.squareup.picasso.Picasso
import android.app.AlertDialog
import pt.pl.estg.ei.regulapp.R.layout
import android.net.Uri
import android.view.*
import android.widget.*

class MessageAdapter(private val UserMessageList: MutableList<Messages?>?, private val messagesenderid: String?) : RecyclerView.Adapter<MessageViewHolder?>() {
    private var userRef: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layout.custom_messages_layout, parent, false)
        val messageViewHolder = MessageViewHolder(view)
        mAuth = FirebaseAuth.getInstance()
        return messageViewHolder
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val messages = UserMessageList?.get(position)
        val fromuserid = messages?.from
        val frommessagetype = messages?.type
        userRef = FirebaseDatabase.getInstance().reference.child("Users").child(fromuserid!!)
        userRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild("image")) {
                    val receiverprofileimage = dataSnapshot.child("image").value.toString()
                    Picasso.get().load(receiverprofileimage).placeholder(R.drawable.doctor).into(holder.receiverprofileimage)
                } else {
                    Picasso.get().load(R.drawable.doctor).into(holder.receiverprofileimage)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        holder.receivermessagetext?.setVisibility(View.GONE)
        holder.receiverprofileimage?.setVisibility(View.GONE)
        holder.sendermessagetext?.setVisibility(View.GONE)
        holder.messageSenderPicture?.setVisibility(View.GONE)
        holder.messageReceiverPicture?.setVisibility(View.GONE)
        if (frommessagetype == "text") {
            if (fromuserid == messagesenderid) {
                holder.sendermessagetext?.setVisibility(View.VISIBLE)
                holder.sendermessagetext?.setBackgroundResource(R.drawable.sender_message_layout)
                holder.sendermessagetext?.setText("""${messages.message}
 
${messages.time} - ${messages.date}""")
            } else {
                holder.receivermessagetext?.setVisibility(View.VISIBLE)
                holder.receiverprofileimage?.setVisibility(View.VISIBLE)
                holder.receivermessagetext?.setBackgroundResource(R.drawable.receiver_message_layout)
                holder.receivermessagetext?.setText("""${messages.message}
 
${messages.time} - ${messages.date}""")
            }
        } else if (frommessagetype == "image") {
            if (fromuserid == messagesenderid) {
                holder.messageSenderPicture?.setVisibility(View.VISIBLE)
                Picasso.get().load(messages.message).into(holder.messageSenderPicture)
            } else {
                holder.messageReceiverPicture?.setVisibility(View.VISIBLE)
                holder.receiverprofileimage?.setVisibility(View.VISIBLE)
                Picasso.get().load(messages.message).into(holder.messageReceiverPicture)
            }
        } else if (frommessagetype == "pdf" || frommessagetype == "docx") {
            if (fromuserid == messagesenderid) {
                holder.messageSenderPicture?.setVisibility(View.VISIBLE)
                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatapp-9849c.appspot.com/o/Image%20Files%2Ffile.png?alt=media&token=34fb4f27-54c8-4c06-b6de-59b6b8deddd2")
                        .into(holder.messageSenderPicture)
            } else {
                holder.messageReceiverPicture?.setVisibility(View.VISIBLE)
                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatapp-9849c.appspot.com/o/Image%20Files%2Ffile.png?alt=media&token=34fb4f27-54c8-4c06-b6de-59b6b8deddd2")
                        .into(holder.messageReceiverPicture)
                holder.receiverprofileimage?.setVisibility(View.VISIBLE)
            }
        }
        if (fromuserid == messagesenderid) {
            holder.itemView.setOnClickListener {
                if (UserMessageList!!.get(position)!!.type == "pdf" || UserMessageList.get(position)!!.type == "docx") {
                    val options = arrayOf<CharSequence?>(
                            "Delete for me", "Download and view content", "Cancel", "Delete for everyone"
                    )
                    val builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("Delete Message?")
                    builder.setItems(options) { dialog, which ->
                        if (which == 0) {
                            deleteSentMessage(position, holder)
                        } else if (which == 1) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(UserMessageList.get(position)!!.message))
                            holder.itemView.context.startActivity(intent)
                        } else if (which == 2) {
                            //for cancel do not do anything
                        } else if (which == 3) {
                            deleteMessageForEveryone(position, holder)
                        }
                    }
                    builder.show()
                } else if (UserMessageList!!.get(position)!!.type == "text") {
                    val options = arrayOf<CharSequence?>(
                            "Delete for me", "Cancel", "Delete for everyone"
                    )
                    val builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("Delete Message?")
                    builder.setItems(options) { dialog, which ->
                        if (which == 0) {
                            deleteSentMessage(position, holder)
                        } else if (which == 1) {
                            //for cancel do not do anything
                        } else if (which == 2) {
                            deleteMessageForEveryone(position, holder)
                        }
                    }
                    builder.show()
                } else if (UserMessageList.get(position)!!.type == "image") {
                    val options = arrayOf<CharSequence?>(
                            "Delete for me", "View This Image", "Cancel", "Delete for everyone"
                    )
                    val builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("Delete Message?")
                    builder.setItems(options) { dialog, which ->
                        if (which == 0) {
                            deleteSentMessage(position, holder)
                        } else if (which == 1) {
                            val intent = Intent(holder.itemView.context, ImageViewActivity::class.java)
                            intent.putExtra("url", UserMessageList.get(position)!!.message)
                            holder.itemView.context.startActivity(intent)
                        } else if (which == 2) {
                            //for cancel do not do anything
                        } else if (which == 3) {
                            deleteMessageForEveryone(position, holder)
                        }
                    }
                    builder.show()
                }
            }
        } else {
            holder.itemView.setOnClickListener {
                if (UserMessageList!!.get(position)!!.type == "pdf" || UserMessageList.get(position)!!.type == "docx") {
                    val options = arrayOf<CharSequence?>(
                            "Delete for me", "Download and view content", "Cancel"
                    )
                    val builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("Delete Message?")
                    builder.setItems(options) { dialog, which ->
                        if (which == 0) {
                            deleteReceiveMessage(position, holder)
                        } else if (which == 1) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(UserMessageList.get(position)!!.message))
                            holder.itemView.context.startActivity(intent)
                        } else if (which == 2) {
                            //for cancel do not do anything
                        }
                    }
                    builder.show()
                } else if (UserMessageList.get(position)!!.type == "text") {
                    val options = arrayOf<CharSequence?>(
                            "Delete for me", "Cancel"
                    )
                    val builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("Delete Message?")
                    builder.setItems(options) { dialog, which ->
                        if (which == 0) {
                            deleteReceiveMessage(position, holder)
                        } else if (which == 1) {
                            //for cancel do not do anything
                        }
                    }
                    builder.show()
                } else if (UserMessageList.get(position)!!.type == "image") {
                    val options = arrayOf<CharSequence?>(
                            "Delete for me", "View This Image", "Cancel"
                    )
                    val builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("Delete Message?")
                    builder.setItems(options) { dialog, which ->
                        if (which == 0) {
                            deleteReceiveMessage(position, holder)
                        } else if (which == 1) {
                            val intent = Intent(holder.itemView.context, ImageViewActivity::class.java)
                            intent.putExtra("url", UserMessageList.get(position)!!.message)
                            holder.itemView.context.startActivity(intent)
                        } else if (which == 2) {
                            //for cancel do not do anything
                        }
                    }
                    builder.show()
                }
            }
        }
    }

    private fun deleteSentMessage(position: Int, holder: MessageViewHolder?) {
        val rootRef = FirebaseDatabase.getInstance().reference
        rootRef.child("Messages").child(UserMessageList!!.get(position)!!.from!!)
                .child(UserMessageList.get(position)!!.to!!).child(UserMessageList.get(position)!!.messageID!!)
                .removeValue().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        notifyItemRemoved(position)
                        UserMessageList.removeAt(position)
                        notifyItemRangeChanged(position, UserMessageList.size)
                        Toast.makeText(holder!!.itemView.context, "Message deleted...", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(holder!!.itemView.context, "Error...", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun deleteReceiveMessage(position: Int, holder: MessageViewHolder?) {
        val rootRef = FirebaseDatabase.getInstance().reference
        rootRef.child("Messages").child(UserMessageList!!.get(position)!!.to!!)
                .child(UserMessageList.get(position)!!.from!!).child(UserMessageList.get(position)!!.messageID!!)
                .removeValue().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        notifyItemRemoved(position)
                        UserMessageList.removeAt(position)
                        notifyItemRangeChanged(position, UserMessageList.size)
                        Toast.makeText(holder!!.itemView.context, "Message deleted...", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(holder!!.itemView.context, "Error...", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun deleteMessageForEveryone(position: Int, holder: MessageViewHolder?) {
        val rootRef = FirebaseDatabase.getInstance().reference
        rootRef.child("Messages").child(UserMessageList!!.get(position)!!.from!!)
                .child(UserMessageList.get(position)!!.to!!).child(UserMessageList.get(position)!!.messageID!!)
                .removeValue().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val rootRef = FirebaseDatabase.getInstance().reference
                        rootRef.child("Messages").child(UserMessageList.get(position)!!.to!!)
                                .child(UserMessageList.get(position)!!.from!!).child(UserMessageList.get(position)!!.messageID!!)
                                .removeValue().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        notifyItemRemoved(position)
                                        UserMessageList.removeAt(position)
                                        notifyItemRangeChanged(position, UserMessageList.size)
                                        Toast.makeText(holder!!.itemView.context, "Message deleted...", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(holder!!.itemView.context, "Error...", Toast.LENGTH_SHORT).show()
                                    }
                                }
                    } else {
                        Toast.makeText(holder!!.itemView.context, "Error...", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    override fun getItemCount(): Int {
        return UserMessageList!!.size
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var sendermessagetext: TextView?
        var receivermessagetext: TextView?
        var receiverprofileimage: CircularImageView?
        var messageSenderPicture: ImageView?
        var messageReceiverPicture: ImageView?

        init {
            sendermessagetext = itemView.findViewById(R.id.sender_message_text)
            receivermessagetext = itemView.findViewById(R.id.receiver_message_text)
            receiverprofileimage = itemView.findViewById(R.id.message_profile_image)
            messageSenderPicture = itemView.findViewById(R.id.message_sender_image_view)
            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image_view)
        }
    }
}