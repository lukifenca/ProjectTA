package com.lukitor.projectta.activityDokter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.lukitor.projectta.Model.Chat
import com.lukitor.projectta.R
import java.util.*

class ListChatDokterAdapter(val mCtx: Context, val layoutResId: Int, val listChat:List<Chat>,val tipe:Int ):
    ArrayAdapter<Chat>(mCtx,layoutResId,listChat) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId, null)

        val txtMsgPasien : TextView = view.findViewById(R.id.txtMsgPasien)
        val txtWaktuMsgPasien : TextView = view.findViewById(R.id.txtWaktuMsgPasien)
        val txtMsgDokter : TextView = view.findViewById(R.id.txtMsgDokter)
        val txtWaktuMsgDokter : TextView = view.findViewById(R.id.txtWaktuMsgDokter)
        val chat = listChat[position]
        if (tipe == 1){
            if (chat.tipePengirim == "Dokter"){
                txtMsgPasien.visibility = View.GONE
                txtWaktuMsgPasien.visibility = View.GONE
                txtMsgDokter.text = chat.pesan
                val waktu = chat.jamKirim?.subSequence(0,5)
                txtWaktuMsgDokter.text = waktu.toString()
            }
            else{
                txtMsgDokter.visibility = View.GONE
                txtWaktuMsgDokter.visibility = View.GONE
                txtMsgPasien.text = chat.pesan
                val waktu = chat.jamKirim?.subSequence(0,5)
                txtWaktuMsgPasien.text = waktu.toString()
            }
        }
        else{
            if (chat.tipePengirim == "Pasien"){
                txtMsgPasien.visibility = View.GONE
                txtWaktuMsgPasien.visibility = View.GONE
                txtMsgDokter.text = chat.pesan
                val waktu = chat.jamKirim?.subSequence(0,5)
                txtWaktuMsgDokter.text = waktu.toString()
            }
            else{
                txtMsgDokter.visibility = View.GONE
                txtWaktuMsgDokter.visibility = View.GONE
                txtMsgPasien.text = chat.pesan
                val waktu = chat.jamKirim?.subSequence(0,5)
                txtWaktuMsgPasien.text = waktu.toString()
            }
        }
        return view
    }
}