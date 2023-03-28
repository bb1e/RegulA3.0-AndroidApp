package pt.pl.estg.ei.regulapp.chat

import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import java.util.*

class TabsAccessorAdapter constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    public override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ChatsFragment()
            1 -> ContactsFragment()
            2 -> RequestsFragment()
            else -> throw InvalidPropertiesFormatException("")
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> return "Conversas"
            1 -> return "Contactos"
            2 -> return "Pedidos"
        }
        return null
    }
}