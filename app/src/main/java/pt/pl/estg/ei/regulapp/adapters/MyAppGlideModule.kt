package pt.pl.estg.ei.regulapp.adapters

import com.bumptech.glide.Glide
import com.google.firebase.storage.StorageReference
import com.bumptech.glide.module.AppGlideModule
import com.firebase.ui.storage.images.FirebaseImageLoader
import android.content.Context
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import java.io.InputStream

@GlideModule
class MyAppGlideModule constructor() : AppGlideModule() {
    public override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        // Register FirebaseImageLoader to handle StorageReference
        registry.append(StorageReference::class.java, InputStream::class.java,
                FirebaseImageLoader.Factory())
    }
}