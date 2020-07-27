package com.skipperLab.qrcodescanner.ui.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.skipperLab.qrcodescanner.R
import com.skipperLab.qrcodescanner.db.DbHelperI
import com.skipperLab.qrcodescanner.db.entities.QrResult
import com.skipperLab.qrcodescanner.ui.dialogs.QrCodeResultDialog
import com.skipperLab.qrcodescanner.utils.gone
import com.skipperLab.qrcodescanner.utils.toFormattedDisplay
import com.skipperLab.qrcodescanner.utils.visible
import kotlinx.android.synthetic.main.layout_single_item_qr_result.view.*

/**
 * Developed by Happy on 6/7/19
 */
class ScannedResultListAdapter(
    var dbHelperI: DbHelperI,
    var context: Context,
    private var listOfScannedResult: MutableList<QrResult>
) :
    RecyclerView.Adapter<ScannedResultListAdapter.ScannedResultListViewHolder>() {

    private var resultDialog: QrCodeResultDialog =
        QrCodeResultDialog(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScannedResultListViewHolder {
        return ScannedResultListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_single_item_qr_result,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return listOfScannedResult.size
    }

    override fun onBindViewHolder(holder: ScannedResultListViewHolder, position: Int) {
        holder.bind(listOfScannedResult[position], position)
    }

    inner class ScannedResultListViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

        fun bind(qrResult: QrResult, position: Int) {
            if(URLUtil.isValidUrl(qrResult.result))
            {
                var firstIndex = qrResult.result!!.indexOfFirst{it == ':'}
                var secondIndex = qrResult.result!!.indexOfFirst{it == '.'}
                var listTitle: String
                if((firstIndex != -1) && (secondIndex != -1))
                    listTitle = qrResult.result!!.substring(firstIndex + 3, secondIndex)
                else
                    listTitle = qrResult.result

                view.result.text = listTitle //.result!!
                view.tvTime.text = qrResult.calendar.toFormattedDisplay()
                setResultTypeIcon(qrResult.resultType)
                //setFavourite(qrResult.favourite)
                onClicks(qrResult, position)
            }

        }

        private fun setResultTypeIcon(resultType: String?) {

        }

        private fun setFavourite(isFavourite: Boolean) {
            if (isFavourite)
                view.favouriteIcon.visible()
            else
                view.favouriteIcon.gone()
        }


        private fun onClicks(qrResult: QrResult, position: Int) {
            view.setOnClickListener {
                //resultDialog.show(qrResult)
                openWebBrowser(qrResult.result)
            }

            view.setOnLongClickListener {
                showDeleteDialog(qrResult, position)
                return@setOnLongClickListener true
            }
        }

        private fun openWebBrowser(strUrl: String?)
        {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse(strUrl)
            context?.startActivity(openURL)
        }

        private fun showDeleteDialog(qrResult: QrResult, position: Int) {
            AlertDialog.Builder(context, R.style.CustomAlertDialog).setTitle(context.getString(R.string.delete))
                .setMessage(context.getString(R.string.want_to_delete))
                .setPositiveButton(context.getString(R.string.delete)) { _, _ ->
                    deleteThisRecord(qrResult, position)
                }
                .setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
                    dialog.cancel()
                }.show()
        }

        private fun deleteThisRecord(qrResult: QrResult, position: Int) {
            dbHelperI.deleteQrResult(qrResult.id!!)
            listOfScannedResult.removeAt(position)
//            notifyItemRemoved(position)
            notifyDataSetChanged()
        }
    }
}