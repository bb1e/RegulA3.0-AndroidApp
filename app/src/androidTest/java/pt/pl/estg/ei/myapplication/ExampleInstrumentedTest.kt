package pt.pl.estg.ei.regulapp

import android.widget.TextView
import com.mikhaellopez.circularimageview.CircularImageView
import android.widget.ImageButton
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import androidx.recyclerview.widget.LinearLayoutManager
import pt.pl.estg.ei.regulapp.chat.MessageAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.StorageTask
import android.app.ProgressDialog
import android.os.Bundle
import pt.pl.estg.ei.regulapp.R
import pt.pl.estg.ei.regulapp.GlobalSettings
import com.google.firebase.database.FirebaseDatabase
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.MediaStoreSignature
import android.view.WindowManager
import android.content.DialogInterface
import android.content.Intent
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import android.app.Activity
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.FirebaseStorage
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.OnFailureListener
import android.widget.Toast
import kotlin.Throws
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.ValueEventListener
import android.text.TextUtils
import org.ocpsoft.prettytime.PrettyTime
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.FirebaseRecyclerAdapter
import pt.pl.estg.ei.regulapp.chat.ChatsFragment.ChatViewHolder
import pt.pl.estg.ei.regulapp.chat.ChatActivity
import pt.pl.estg.ei.regulapp.chat.MessageAdapter.MessageViewHolder
import com.squareup.picasso.Picasso
import pt.pl.estg.ei.regulapp.chat.ImageViewActivity
import pt.pl.estg.ei.regulapp.chat.ContactsFragment.ContactsViewHolder
import com.google.android.material.navigation.NavigationView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import androidx.drawerlayout.widget.DrawerLayout
import pt.pl.estg.ei.regulapp.chat.TabsAccessorAdapter
import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseUser
import pt.pl.estg.ei.regulapp.LoginActivity
import pt.pl.estg.ei.regulapp.chat.SettingsActivity
import pt.pl.estg.ei.regulapp.chat.FindFriendsActivity
import pt.pl.estg.ei.regulapp.chat.FindTerapeutActivity
import pt.pl.estg.ei.regulapp.RelatorioSemanalActivity
import pt.pl.estg.ei.regulapp.chat.MainChatActivity
import pt.pl.estg.ei.regulapp.EstrategiasPreferidas
import pt.pl.estg.ei.regulapp.chat.ChatProfileActivity
import pt.pl.estg.ei.regulapp.MenuEscolherCrianca
import pt.pl.estg.ei.regulapp.MenuOcupacoes
import pt.pl.estg.ei.regulapp.HomePageActivity
import pt.pl.estg.ei.regulapp.chat.RequestsFragment.RequestViewHolder
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import android.provider.MediaStore
import com.google.firebase.firestore.FirebaseFirestore
import pt.pl.estg.ei.regulapp.enums.BD
import androidx.fragment.app.FragmentPagerAdapter
import pt.pl.estg.ei.regulapp.chat.ChatsFragment
import pt.pl.estg.ei.regulapp.chat.ContactsFragment
import pt.pl.estg.ei.regulapp.chat.RequestsFragment
import pt.pl.estg.ei.regulapp.classes.Terapeuta
import pt.pl.estg.ei.regulapp.forum.MessagesAdapter.DataUpdateAfterMessageDelete
import pt.pl.estg.ei.regulapp.forum.MessageThread
import pt.pl.estg.ei.regulapp.forum.MessagesAdapter
import com.google.firebase.database.MutableData
import pt.pl.estg.ei.regulapp.forum.ThreadsActivity
import android.widget.ArrayAdapter
import pt.pl.estg.ei.regulapp.forum.ThreadActivity
import pt.pl.estg.ei.regulapp.classes.Crianca
import pt.pl.estg.ei.regulapp.forum.ThreadsAdapter
import lib.kingja.switchbutton.SwitchMultiButton
import lib.kingja.switchbutton.SwitchMultiButton.OnSwitchListener
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView
import pt.pl.estg.ei.regulapp.forum.NewThreadActivity
import pt.pl.estg.ei.regulapp.forum.PersonalThreadsActivity
import android.text.TextWatcher
import android.text.Editable
import pt.pl.estg.ei.regulapp.forum.PersonalThreadsAdapter
import pt.pl.estg.ei.regulapp.enums.DiaDaSemana
import pt.pl.estg.ei.regulapp.classes.Estrategia
import pt.pl.estg.ei.regulapp.enums.Genero
import pt.pl.estg.ei.regulapp.enums.TipoAlvo
import pt.pl.estg.ei.regulapp.classes.Dia
import android.view.View.MeasureSpec
import pt.pl.estg.ei.regulapp.classes.FeedbackEstrategia
import pt.pl.estg.ei.regulapp.adapters.MyAdapter
import com.like.LikeButton
import com.like.OnLikeListener
import pt.pl.estg.ei.regulapp.FeedbackEstrategiaActivity
import pt.pl.estg.ei.regulapp.AreaEstrategia
import android.os.Build
import android.text.Html
import com.google.firebase.firestore.QuerySnapshot
import com.bumptech.glide.module.AppGlideModule
import com.firebase.ui.storage.images.FirebaseImageLoader
import pt.pl.estg.ei.regulapp.AreaFeedbackEstrategiaActivity
import pt.pl.estg.ei.regulapp.adapters.EstrategiasFavoritasAdapter
import android.app.DatePickerDialog.OnDateSetListener
import android.widget.LinearLayout
import pt.pl.estg.ei.regulapp.R.layout
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.widget.RadioGroup
import android.view.Gravity
import android.util.Patterns
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentSnapshot
import pt.pl.estg.ei.regulapp.RegisterActivity
import pt.pl.estg.ei.regulapp.enums.AreasOcupacao
import pt.pl.estg.ei.regulapp.ListaEstrategias
import pt.pl.estg.ei.regulapp.MenuOcupacoesSubcat
import pt.pl.estg.ei.regulapp.ProfileActivity
import com.google.firebase.firestore.QueryDocumentSnapshot
import pt.pl.estg.ei.regulapp.adapters.AreasEstrategiasAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import pt.pl.estg.ei.regulapp.classes.MyListView
import pt.pl.estg.ei.regulapp.adapters.ProfileEstatisticaAdapter
import android.view.View.OnTouchListener
import android.view.MotionEvent
import pt.pl.estg.ei.regulapp.classes.RelatorioSemanal
import pt.pl.estg.ei.regulapp.LineChartActivity
import pt.pl.estg.ei.regulapp.adapters.HistoricoFeedbackAdapter
import android.content.pm.PackageManager
import pt.pl.estg.ei.regulapp.HistoricoAtividadeActivity
import pt.pl.estg.ei.regulapp.AddCrianca
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.components.XAxis
import pt.pl.estg.ei.regulapp.adapters.ProfileAdapter
import android.util.TypedValue
import com.xw.repo.BubbleSeekBar
import android.widget.AdapterView.OnItemSelectedListener
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.hsalf.smileyrating.SmileyRating
import com.hsalf.smileyrating.SmileyRating.OnSmileySelectedListener
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest constructor() {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext: Context? = InstrumentationRegistry.getInstrumentation().targetContext
        Assert.assertEquals("pt.pl.estg.ei.regulapp", appContext!!.packageName)

        val database: DatabaseReference = FirebaseDatabase.getInstance().reference

        database.child("message_threads").get().addOnCompleteListener(){
                snapshot -> if(snapshot.isSuccessful){
            println(snapshot.result)
        }
        else{
            println("error: " + snapshot.exception)

        }
        }
    }
}