package sk.marcel.rtvt_moja_karta

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ViewPagerAdapter(fragmentManager: FragmentManager?) :
    FragmentStatePagerAdapter(fragmentManager!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    var transferMoneyFragment: TransferMoneyFragment = TransferMoneyFragment.newInstance()
    var shopFragment: ShopFragment = ShopFragment.newInstance()
    var cardInfoFragment: CardInfoFragment = CardInfoFragment.newInstance()

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> transferMoneyFragment
            1 -> shopFragment
            2 -> cardInfoFragment
            else -> AddMoneyFragment.newInstance()
        }
    }

    override fun getCount(): Int {
        return TAB_COUNT
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> TransferMoneyFragment.TITLE
            1 -> ShopFragment.TITLE
            2 -> CardInfoFragment.TITLE
            else -> ""
        }
    }

    companion object {
        private const val TAB_COUNT = 3
    }
}