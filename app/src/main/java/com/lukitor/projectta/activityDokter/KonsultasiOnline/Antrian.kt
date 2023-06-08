package com.lukitor.projectta.activityDokter.KonsultasiOnline

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lukitor.projectta.Model.*
import com.lukitor.projectta.Model.Chat
import com.lukitor.projectta.R
import com.lukitor.projectta.activityDokter.adapter.ListAntrianAdapter
import com.lukitor.projectta.databinding.FragmentAntrianBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class Antrian : Fragment() {
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var listHistory : MutableList<HistoryTransaksi>
    private lateinit var listpasien : MutableList<Pasien>
    private lateinit var templistpasien : MutableList<Pasien>
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentAntrianBinding
    var ctr = 1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser!!
        if(firebaseUser != null) {
            listHistory = mutableListOf()
            listpasien = mutableListOf()
            templistpasien = mutableListOf()
            var simpleDateFormat= SimpleDateFormat("dd-MM-yyyy")
            val tanggal:String = simpleDateFormat.format(Date())
            listHistory(tanggal)
        }
        binding = FragmentAntrianBinding.inflate(inflater, container, false)
        binding.rvDokterAntrian.setOnItemClickListener { parent, view, position, id ->
            val pasien = listpasien[position]
            val history = listHistory[position]
            val simpleDateFormat= SimpleDateFormat("HH:mm:ss")
            val jammulai:String = simpleDateFormat.format(Date())
            val date = Date()
            val hrs:Int = date.hours
            val min:Int = date.minutes
            var waktu = ""
            if (hrs>=1  && hrs<=10){
                waktu = "Pagi"
            }
            else if (hrs>=11  && hrs<=14){
                waktu = "Siang"
            }
            else if (hrs>=15  && hrs<=18){
                waktu = "Sore"
            }
            else if (hrs>=19  && hrs<=24){
                waktu = "Malam"
            }
            var jk = ""
            if (pasien.jeniskelamin == "Pria"){
                jk = "Bapak"
            }
            else{
                jk = "Ibu"
            }
            val pesan = "Halo Selamat $waktu, $jk ${pasien.nama}"
            mulaiKonsultasi(history.id,pesan,jammulai,"Dokter", pasien, jk,history)
        }
        return binding.root
    }

    private fun mulaiKonsultasi(hTransID: String,pesan: String,jamKirim: String, tipePengirim: String, pasien:Pasien, jk:String, history:HistoryTransaksi){
        val ref1 = FirebaseDatabase.getInstance().getReference("HISTORYTRANSAKSI")
        history.statusTrans = 1
        ref1.child(hTransID).setValue(history)
        val ref = FirebaseDatabase.getInstance().getReference("CHAT").child(hTransID)
        val historyId = ref.push().key
        val hTrans =    Chat(historyId.toString(),pesan,jamKirim,tipePengirim)
        ref.child(historyId.toString()).setValue(hTrans).addOnCompleteListener {
            Toast.makeText(requireContext(), "Konsultasi Dengan $jk ${pasien.nama} Dimulai", Toast.LENGTH_SHORT).show()
        }
        addSaldo()
//        Intent(requireContext(), ChatDokterActivity::class.java).also {
//            it.putExtra("id", hTransID)
//            it.putExtra("pasien", pasien.id)
//            startActivity(it)
//        }
    }
    private fun addSaldo(){
        val ref = FirebaseDatabase.getInstance().getReference().child("DOKTER").child(auth.currentUser!!.uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user =snapshot.getValue<Dokter>(Dokter::class.java)
                    val ref2 = FirebaseDatabase.getInstance().getReference("HISTORYSALDO").child(auth.currentUser!!.uid)
                    val historyId1 = ref2.push().key
                    val saldoawal = user!!.saldo
                    val akunsaldo = user.tarif
                    val saldoakhir = saldoawal!!.toInt() + akunsaldo!!.toInt()
                    val hSaldo = HistorySaldo(historyId1.toString(),saldoawal,akunsaldo,saldoakhir,0,0)
                    ref2.child(historyId1.toString()).setValue(hSaldo).addOnCompleteListener {

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
    private fun listHistory(tanggal:String){
        val refpasien = FirebaseDatabase.getInstance().getReference("PASIEN")
        refpasien.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    if (listpasien.count() > 1){
                        listpasien.clear()
                    }
                    for (h in snapshot.children) {
                        val pasien = h.getValue(Pasien::class.java)
                        if (pasien != null) {
                            listpasien.add(pasien)
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        val ref = FirebaseDatabase.getInstance().getReference("HISTORYTRANSAKSI")
        val query = ref.orderByChild("tanggal").equalTo(tanggal)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    listHistory.clear()
                    for (h in snapshot.children) {
                        val history = h.getValue(HistoryTransaksi::class.java)
                        if (history != null) {
                            for (p in listpasien){
                                if (p.id == history.pasienId){
                                    templistpasien.add(p)
                                }
                            }
                            if (history.dokterId == firebaseUser.uid) {
//                                if(history.statusTrans == 0 && history.statusFoto !=0){
//                                    listHistory.add(history)
//                                }
                                if(history.statusTrans == 0){
                                    listHistory.add(history)
                                }
                            }
                        }
                    }
                    if (listHistory.isNotEmpty()){
                        binding.rvDokterAntrian.visibility = View.VISIBLE
                        binding.txtnohistory.visibility = View.GONE
                        val adapter = ListAntrianAdapter(requireActivity(),R.layout.itemlistantrian, listHistory,templistpasien,1)
                        binding.rvDokterAntrian.adapter = adapter

                    }
                    else{
                        binding.rvDokterAntrian.visibility = View.GONE
                        binding.txtnohistory.visibility = View.VISIBLE
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}