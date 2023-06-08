package com.lukitor.projectta.activityAdmin.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.lukitor.projectta.Model.Admin
import com.lukitor.projectta.Model.Dokter
import com.lukitor.projectta.R
import com.squareup.picasso.Picasso
import java.text.NumberFormat

class ListAdminAdapter(val mCtx: Context, val layoutResId: Int, val listAdmin:List<Admin>):
    ArrayAdapter<Admin>(mCtx,layoutResId,listAdmin)  {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId, null)

        var email : TextView = view.findViewById(R.id.tv_emailadmin)
        var hp : TextView = view.findViewById(R.id.tv_hpadmin)
        var gender : TextView = view.findViewById(R.id.tv_profilegenderadmin)
        var nama : TextView = view.findViewById(R.id.tv_profilenamapasien)
        var img : ImageView = view.findViewById(R.id.imageprofiledokter)
        var admin = listAdmin[position]
        nama.text = admin.nama
        email.text = admin.email
        hp.text = admin.telp
        gender.text = admin.jeniskelamin
        if (admin.jeniskelamin == "Pria"){
            img.setImageResource(R.drawable.ic_man)
        }
        else {
            img.setImageResource(R.drawable.ic_woman)
        }
        return view
    }
}