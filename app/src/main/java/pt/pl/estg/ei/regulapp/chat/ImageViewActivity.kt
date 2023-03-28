package pt.pl.estg.ei.regulapp.chat

import android.os.Bundle
import pt.pl.estg.ei.regulapp.R
import com.squareup.picasso.Picasso
import pt.pl.estg.ei.regulapp.R.layout
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ImageViewActivity : AppCompatActivity() {
    private var imageView: ImageView? = null
    private var imageUrl: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_image_view)
        imageView = findViewById(R.id.image_view)
        imageUrl = intent.getStringExtra("url")
        Picasso.get().load(imageUrl).into(imageView)
    }
}