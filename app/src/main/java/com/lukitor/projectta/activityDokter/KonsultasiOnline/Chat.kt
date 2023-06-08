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
import com.lukitor.projectta.Model.Chat
import com.lukitor.projectta.Model.HistoryTransaksi
import com.lukitor.projectta.Model.Pasien
import com.lukitor.projectta.R
import com.lukitor.projectta.activityDokter.adapter.ListAntrianAdapter
import com.lukitor.projectta.databinding.FragmentAntrianBinding
import com.lukitor.projectta.databinding.FragmentChatBinding
import java.text.SimpleDateFormat
import java.util.*

class Chat : Fragment() {
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var listHistory : MutableList<HistoryTransaksi>
    private lateinit var listpasien : MutableList<Pasien>
    private lateinit var templistpasien : MutableList<Pasien>
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentChatBinding
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
        binding = FragmentChatBinding.inflate(inflater, container, false)
        binding.rvDokterAntrian.setOnItemClickListener { parent, view, position, id ->
            val pasien = listpasien[position]
            val history = listHistory[position]

            mulaiKonsultasi(history.id,pasien)
        }
        return binding.root
    }
    private fun mulaiKonsultasi(hTransID: String, pasien:Pasien){
        Intent(requireContext(), ChatDokterActivity::class.java).also {
            it.putExtra("id", hTransID)
            it.putExtra("pasien", pasien.id)
            startActivity(it)
        }
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
                                if(history.statusTrans == 1){
                                    listHistory.add(history)
                                }
                            }
                        }
                    }
                    if (listHistory.isNotEmpty()){
                        binding.rvDokterAntrian.visibility = View.VISIBLE
                        binding.txtnohistory.visibility = View.GONE
                        val adapter = ListAntrianAdapter(requireActivity(),R.layout.itemlistantrian, listHistory,templistpasien,2)
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