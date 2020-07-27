package com.skipperLab.qrcodescanner.ui.qrscanner


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.skipperLab.qrcodescanner.R
import com.skipperLab.qrcodescanner.db.DbHelper
import com.skipperLab.qrcodescanner.db.DbHelperI
import com.skipperLab.qrcodescanner.db.database.QrResultDataBase
import com.skipperLab.qrcodescanner.ui.dialogs.QrCodeResultDialog
import kotlinx.android.synthetic.main.fragment_qrscanner.view.*
import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView

class QRScannerFragment : Fragment(), ZBarScannerView.ResultHandler {

    private var receiver: BroadcastReceiver? = null


    class FragmentReceiverScanner : BroadcastReceiver() {

        companion object {
            var fragment : QRScannerFragment? = null
        }

        override fun onReceive(context: Context?, intent: Intent?) {
             Log.d("ddddddddddddddddddddddddddd", "FragmentReceiverScanner");
            val index = intent?.getIntExtra("index", -1)
            Log.d("ddddddddddddddddddddddddddd", "index" + index);
            if (index != null) {
                currentIndex = index
            }

        }

    }

    companion object {

        private var currentIndex: Int = -1;
        fun newInstance(): QRScannerFragment {
            return QRScannerFragment()
        }
    }

    private lateinit var mView: View

    lateinit var scannerView: ZBarScannerView

    lateinit var resultDialog: QrCodeResultDialog

    private lateinit var dbHelperI: DbHelperI

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_qrscanner, container, false)
        init()
        initViews()
        receiver = FragmentReceiverScanner()
        activity!!.registerReceiver(receiver, IntentFilter("fragmentscanner"))
        onClicks()

        return mView.rootView
    }

    private fun init() {
        dbHelperI = DbHelper(QrResultDataBase.getAppDatabase(context!!)!!)
    }

    private fun initViews() {
        initializeQRCamera()
//        setResultDialog()
    }

    private fun initializeQRCamera() {
        scannerView = ZBarScannerView(context)
        scannerView.setResultHandler(this)
        scannerView.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorTranslucent))
        scannerView.setBorderColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
        scannerView.setLaserColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
        scannerView.setBorderStrokeWidth(10)
        scannerView.setSquareViewFinder(true)
        scannerView.setupScanner()
        scannerView.setAutoFocus(true)
        startQRCamera()
        mView.containerScanner.addView(scannerView)
    }

//    private fun setResultDialog() {
//        resultDialog = QrCodeResultDialog(context!!)
//        resultDialog.setOnDismissListener(object : QrCodeResultDialog.OnDismissListener {
//            override fun onDismiss() {
//                resetPreview()
//            }
//        })
//    }


    override fun handleResult(rawResult: Result?) {
        onQrResult(rawResult?.contents)
    }

    private fun onQrResult(contents: String?) {
        if (contents.isNullOrEmpty())
            showToast("Empty Qr Result")
        else {

            Log.d("ddddddddddddddddddddddddddd", "current index" + currentIndex)
            if(currentIndex.equals(1)) {
                saveToDataBase(contents)
            }
            resetPreview()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context!!, message, Toast.LENGTH_SHORT).show()
    }

    private fun saveToDataBase(contents: String) {
        val insertedResultId = dbHelperI.insertQRResult(contents)
        val qrResult = dbHelperI.getQRResult(insertedResultId)
        if( URLUtil.isValidUrl(qrResult.result))
        {
            //scannerView.stopCameraPreview()
            //scannerView.stopCamera()
            val data1 = Intent("fragmentupdater")
            data1.putExtra("key", "data")
            data1.putExtra("fragmentno", 1) // Pass the unique id of fragment we talked abt earlier
            Log.d("ddddddddddddddddddddd", "saveToDataBase")
            activity!!.sendBroadcast(data1)
            openWebBrowser(qrResult.result)
        }
        else
        {
            Log.d("ddddddddddddddddddddd", "unknown qrcode")
        }
        //resultDialog.show(qrResult)
//        resetPreview()
    }

    private fun openWebBrowser(strUrl: String?)
    {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse(strUrl)
        startActivity(openURL)
    }

    private fun startQRCamera() {
        scannerView.startCamera()
    }

    private fun resetPreview() {
        scannerView.stopCamera()
        scannerView.startCamera()
        scannerView.stopCameraPreview()
        scannerView.resumeCameraPreview(this)
    }

    private fun onClicks() {
        mView.flashToggle.setOnClickListener {
            if (mView.flashToggle.isSelected) {
                offFlashLight()
            } else {
                onFlashLight()
            }
        }
    }

    private fun onFlashLight() {
        mView.flashToggle.isSelected = true
        scannerView.flash = true
    }

    private fun offFlashLight() {
        mView.flashToggle.isSelected = false
        scannerView.flash = false
    }

    override fun onResume() {
        super.onResume()
        scannerView.setResultHandler(this)
        scannerView.startCamera()
    }

    override fun onPause() {
        super.onPause()
        scannerView.stopCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        scannerView.stopCamera()
    }

}
