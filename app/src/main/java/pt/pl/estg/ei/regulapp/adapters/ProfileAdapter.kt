package pt.pl.estg.ei.regulapp.adapters

import android.widget.TextView
import pt.pl.estg.ei.regulapp.R
import pt.pl.estg.ei.regulapp.GlobalSettings
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.MediaStoreSignature
import android.content.Intent
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.FirebaseStorage
import android.widget.Toast
import pt.pl.estg.ei.regulapp.HomePageActivity
import android.widget.ArrayAdapter
import pt.pl.estg.ei.regulapp.R.layout
import android.content.Context
import android.util.Log
import android.view.*
import de.hdodenhof.circleimageview.CircleImageView
import pt.pl.estg.ei.regulapp.classes.*
import java.util.ArrayList

class ProfileAdapter constructor(context: Context, private val nomes: ArrayList<String?>) : ArrayAdapter<Any?>(context, layout.custom_listview, R.id.title,
    nomes as List<Any?>
) {
    private val session: Session? = null
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val row: View = layoutInflater.inflate(layout.profile_button_f, parent, false)
        val button: CircleImageView = row.findViewById(R.id.btnProfile)
        val textView: TextView = row.findViewById(R.id.textProfileName)
        val nome: String = nomes.get(position)!!
        loadProfilePic(button, nome)
        textView.setText(nome)
        button.setOnClickListener {
            val intent: Intent?
            intent = Intent(context, HomePageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("child", nomes.get(position))
            Log.d("Profile Adapter", "onClick: " + nomes.get(position))
            getContext().startActivity(intent)
        }
        return row
    }

    private fun loadProfilePic(imageView: CircleImageView, name: String?) {
        val child: Crianca? = (getContext() as GlobalSettings)?.session?.getCriancaByName(name)
        if (child?.storageImageRef != null) {
            val profileImage: StorageReference = FirebaseStorage.getInstance().getReference().child(child.storageImageRef!!)
            Glide.with(context).load(profileImage).error(R.drawable.stock_profile_sized).into(imageView)
            Glide.with(context).load(profileImage).signature(MediaStoreSignature("",
                    System.currentTimeMillis(), 0)).into(imageView)
        } else {
            Toast.makeText(getContext(), "nao foi possivel ", Toast.LENGTH_LONG)
        }
    }
}