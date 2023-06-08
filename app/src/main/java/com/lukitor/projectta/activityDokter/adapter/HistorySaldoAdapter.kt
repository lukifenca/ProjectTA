package com.lukitor.projectta.activityDokter.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.lukitor.projectta.Model.HistorySaldo
import com.lukitor.projectta.R
import java.text.NumberFormat

class HistorySaldoAdapter(val mCtx: Context, val layoutResId: Int, val historysaldoList:List<HistorySaldo>, val cari:Int):
    ArrayAdapter<HistorySaldo>(mCtx,layoutResId,historysaldoList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId, null)

        val tipe : TextView = view.findViewById(R.id.txtTipe)
        val tanggal : TextView = view.findViewById(R.id.txtTanggal)
        val saldo : TextView = view.findViewById(R.id.txtsaldo)
        val img : ImageView = view.findViewById(R.id.imageView2)
        val cv : CardView = view.findViewById(R.id.cvlayout)
        val status: TextView = view.findViewById(R.id.txtStatus)
        val historySaldo = historysaldoList[position]
        if (historySaldo.tipe == 0){
            tipe.text = "Saldo Jasa Konsultasi"
            //tanggal.text = historySaldo.sks.toString()
            val myString = NumberFormat.getInstance().format(historySaldo!!.akunsaldo);
            saldo.text = "+ Rp. " + myString + ",-"
            saldo.setTextColor(Color.parseColor("#1FC8C6"))
            cv.setCardBackgroundColor(Color.parseColor("#1FC8C6"))
            img.setImageResource(R.drawable.ic_baseline_account_balance_wallet_24);
            status.visibility = View.GONE
        }
        else if (historySaldo.tipe == 1){
            tipe.text = "Penarikan Saldo"
            //tanggal.text = historySaldo.sks.toString()
            val myString = NumberFormat.getInstance().format(historySaldo!!.akunsaldo);
            saldo.text = "- Rp. " + myString + ",-"
            saldo.setTextColor(Color.parseColor("#FF1337"))
            cv.setCardBackgroundColor(Color.parseColor("#FF1337"))
            img.setImageResource(R.drawable.wallenegatif);
            if (historySaldo!!.status == 0){
                status.text = "Sukses"
                status.setTextColor(Color.parseColor("#1FC8C6"))
            }
            else{
                status.text = "Pending"
                status.setTextColor(Color.parseColor("#E9B64E"))
            }
        }
        return view
    }
}