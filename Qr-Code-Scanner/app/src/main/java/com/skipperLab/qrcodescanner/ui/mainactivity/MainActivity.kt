package com.skipperLab.qrcodescanner.ui.mainactivity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.skipperLab.qrcodescanner.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setViewPager()
        setBottomViewListener()
        setViewPagerListener()
    }


    private fun setViewPager() {
        viewPager.adapter = MainPagerAdapter(supportFragmentManager)
        viewPager.offscreenPageLimit = 1
    }

    private fun setBottomViewListener() {
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.qrScanMenuId -> {
                    viewPager.currentItem = 1
                    tabSelected(1)
                }
                R.id.scannedResultMenuId -> {
                    viewPager.currentItem = 0
                    tabSelected(0)

                }
//                R.id.favouriteScannedMenuId -> {
//                    viewPager.currentItem = 2
//                }
            }
            return@setOnNavigationItemSelectedListener true
        }
    }

    private fun tabSelected(index: Int)
    {
        val data1 = Intent("fragmentscanner")
        data1.putExtra("index", index)
        data1.putExtra("fragmentno", 2) // Pass the unique id of fragment we talked abt earlier
        this.sendBroadcast(data1)
    }


    private fun setViewPagerListener() {
        viewPager.setOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        bottomNavigationView.selectedItemId = R.id.scannedResultMenuId
                        tabSelected(0)
                    }
                    1 -> {
                        bottomNavigationView.selectedItemId = R.id.qrScanMenuId
                        tabSelected(1)
                    }
//                    2 -> {
//                        bottomNavigationView.selectedItemId = R.id.favouriteScannedMenuId
//                    }
                }
            }
        })
    }
}