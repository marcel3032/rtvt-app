package sk.marcel.rtvt_payment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ViewPagerAdapter(fragmentManager: FragmentManager?) :
    FragmentStatePagerAdapter(fragmentManager!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    var addMoneyFragment: AddMoneyFragment = AddMoneyFragment.newInstance()
    var transferMoneyFragment: TransferMoneyFragment = TransferMoneyFragment.newInstance()
    var shopFragment: ShopFragment = ShopFragment.newInstance()

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> addMoneyFragment
            1 -> transferMoneyFragment
            2 -> shopFragment
            else -> AddMoneyFragment.newInstance()
        }
    }

    override fun getCount(): Int {
        return TAB_COUNT
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> AddMoneyFragment.TITLE
            1 -> TransferMoneyFragment.TITLE
            2 -> ShopFragment.TITLE
            else -> ""
        }
    }

    companion object {
        private const val TAB_COUNT = 3
    }
}