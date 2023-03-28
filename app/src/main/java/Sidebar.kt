import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import pt.pl.estg.ei.regulapp.*
import pt.pl.estg.ei.regulapp.chat.ChatProfileActivity
import pt.pl.estg.ei.regulapp.chat.FindFriendsActivity
import pt.pl.estg.ei.regulapp.chat.FindTerapeutActivity
import pt.pl.estg.ei.regulapp.chat.MainChatActivity
import pt.pl.estg.ei.regulapp.classes.Analytics
import pt.pl.estg.ei.regulapp.classes.AnalyticsEvent
import pt.pl.estg.ei.regulapp.forum.ThreadsActivity
import java.io.Serializable


class Sidebar: Fragment(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var navigationView : NavigationView;
    lateinit var drawerLayout: DrawerLayout;

    //can't use constructors with Fragments
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        navigationView = activity?.findViewById(requireArguments().getInt("navigationViewId")) as NavigationView
        drawerLayout = activity?.findViewById(requireArguments().getInt("drawerLayoutId")) as DrawerLayout
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_home -> openMenu(HomePageActivity::class.java)
            R.id.nav_estrategias -> openMenu(MenuOcupacoes::class.java)
            R.id.nav_estrategias_fav -> openMenu(EstrategiasPreferidas::class.java)
            R.id.nav_chat -> openMenu(MainChatActivity::class.java)
            R.id.nav_forum -> openMenu(ThreadsActivity::class.java)
            R.id.nav_registo_semanal -> openMenu(RelatorioSemanalActivity::class.java)
            R.id.nav_profile -> openMenu(ProfileActivity::class.java)
            R.id.nav_crianca -> openMenu(MenuEscolherCrianca::class.java)
            R.id.nav_add_crianca -> openMenu(AddCrianca::class.java)

            // chat menu
            R.id.nav_perfil_chat -> openMenu(ChatProfileActivity::class.java)
            R.id.nav_terapeutas ->  openMenu(FindTerapeutActivity::class.java)
            R.id.nav_amigos -> openMenu(FindFriendsActivity::class.java)

            R.id.nav_logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(activity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                Analytics.fireEvent(AnalyticsEvent.LOGOUT);
            }
        }
        drawerLayout.closeDrawer(GravityCompat.END)
        return true
    }

    private fun openMenu(newActivity: Class<*>) {
        if(activity?.javaClass  != newActivity) {
            startActivity(
                    Intent(activity, newActivity).putExtra("ArrivedByToolbar", true)
            )
        }
    }

    companion object {
        fun installSidebar(fm: FragmentManager,drawerLayout: DrawerLayout, navigationView: NavigationView): Sidebar {
            val sidebar = Sidebar();
            val args = Bundle();
            args.putInt("drawerLayoutId", drawerLayout.id);
            args.putSerializable("navigationViewId",navigationView.id);
            sidebar.arguments = args;
            fm.beginTransaction()
               .add(sidebar, "sidebar_fragment")
               .commitNow()
            return sidebar
        }
    }
}