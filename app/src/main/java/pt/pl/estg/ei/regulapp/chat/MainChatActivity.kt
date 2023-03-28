package pt.pl.estg.ei.regulapp.chat

import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import android.os.Bundle
import pt.pl.estg.ei.regulapp.R
import pt.pl.estg.ei.regulapp.GlobalSettings
import com.google.firebase.database.FirebaseDatabase
import android.content.Intent
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.android.material.navigation.NavigationView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import androidx.drawerlayout.widget.DrawerLayout
import android.annotation.SuppressLint
import pt.pl.estg.ei.regulapp.LoginActivity
import pt.pl.estg.ei.regulapp.RelatorioSemanalActivity
import pt.pl.estg.ei.regulapp.EstrategiasPreferidas
import pt.pl.estg.ei.regulapp.MenuEscolherCrianca
import pt.pl.estg.ei.regulapp.MenuOcupacoes
import pt.pl.estg.ei.regulapp.HomePageActivity
import pt.pl.estg.ei.regulapp.R.layout
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import java.text.SimpleDateFormat
import java.util.*

class MainChatActivity : AppCompatActivity() {
    private var toolbar: Toolbar? = null
    private var myviewPager: ViewPager? = null
    private var mytabLayout: TabLayout? = null
    private var drawerLayout: DrawerLayout? = null
    private var mytabsAccessorAdapter: TabsAccessorAdapter? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var databaseReference: DatabaseReference? = null
    private var currentUserId: String? = null
    private var navigationView: NavigationView? = null
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference
        currentUserId = firebaseAuth?.uid

        drawerLayout = findViewById(R.id.drawerLayoutChat)
        navigationView = findViewById(R.id.nav_chat_v2)
        toolbar = findViewById(R.id.toolbarChat)
        val textView = toolbar?.findViewById<TextView?>(R.id.toolbar_title)
        textView?.text = "Chat de Apoio"
        setSupportActionBar(toolbar)
        navigationView?.bringToFront()
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.vermelho_chat)
        myviewPager = findViewById(R.id.main_tabs_pager)
        mytabsAccessorAdapter = TabsAccessorAdapter(supportFragmentManager)
        myviewPager?.setAdapter(mytabsAccessorAdapter)
        mytabLayout = findViewById(R.id.main_tabs)
        mytabLayout?.setupWithViewPager(myviewPager)

        if(savedInstanceState == null) {
            Sidebar.installSidebar(supportFragmentManager,drawerLayout!!,navigationView!!);
        }
    }

    fun clickMenu(view: View) {
        //abrir janela menu
        drawerLayout?.openDrawer(GravityCompat.END)
    }

    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth?.getCurrentUser()
        if (currentUser == null) {
            sendUserToLoginActivity()
        } else {
            updateUserStatus("online")
            VerifyUserexistance()
        }
    }

    override fun onStop() {
        super.onStop()
        val currentUser = firebaseAuth?.getCurrentUser()
        if (currentUser != null) {
            updateUserStatus("offline")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val currentUser = (application as GlobalSettings).session
        if (currentUser != null) {
            updateUserStatus("offline")
        }
    }

    private fun VerifyUserexistance() {
        databaseReference!!.child("Users").child(currentUserId!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.child("name").exists()) {
                    sendUserToSettingsActivity()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun sendUserToLoginActivity() {
        val loginintent = Intent(this@MainChatActivity, LoginActivity::class.java)
        loginintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(loginintent)
        finish()
    }

    private fun sendUserToSettingsActivity() {
        val settingsintent = Intent(this, SettingsActivity::class.java)
        startActivity(settingsintent)
    }

    private fun sendUserToFindFriendsActivity() {
        val friendsintent = Intent(this@MainChatActivity, FindFriendsActivity::class.java)
        startActivity(friendsintent)
    }

    private fun updateUserStatus(state: String?) {
        val savecurrentTime: String
        val savecurrentDate: String
        val calendar = Calendar.getInstance()
        val currentDate = SimpleDateFormat("dd/MM/yyyy")
        savecurrentDate = currentDate.format(calendar.time)
        val currentTime = SimpleDateFormat("hh:mm a")
        savecurrentTime = currentTime.format(calendar.time)
        val hashMap = HashMap<String?, Any?>()
        hashMap["time"] = savecurrentTime
        hashMap["date"] = savecurrentDate
        hashMap["state"] = state
        databaseReference?.child("Users")!!.child(currentUserId!!).child("userState").updateChildren(hashMap)
    }

    override fun onBackPressed() {
        if (drawerLayout?.isDrawerOpen(GravityCompat.END)!!) {
            drawerLayout?.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }
}