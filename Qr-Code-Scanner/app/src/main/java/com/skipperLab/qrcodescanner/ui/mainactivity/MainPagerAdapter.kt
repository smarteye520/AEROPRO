package com.skipperLab.qrcodescanner.ui.mainactivity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.skipperLab.qrcodescanner.ui.qrscanner.QRScannerFragment
import com.skipperLab.qrcodescanner.ui.scanner_history.ScannedHistoryFragment

/**
 * Developed by Happy on 6/7/19
 */
class MainPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {


    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                ScannedHistoryFragment.newInstance(ScannedHistoryFragment.ResultListType.ALL_RESULT)
            }

            1 -> {
                QRScannerFragment.newInstance()

            }

//            2 -> {
//                ScannedHistoryFragment.newInstance(ScannedHistoryFragment.ResultListType.FAVOURITE_RESULT)
//            }

            else -> {
                ScannedHistoryFragment.newInstance(ScannedHistoryFragment.ResultListType.ALL_RESULT)
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }
}