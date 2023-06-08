package com.lukitor.projectta.activityPasien

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.Telephony.BaseMmsColumns.TRANSACTION_ID
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lukitor.projectta.MainActivity
import com.lukitor.projectta.Model.Dokter
import com.lukitor.projectta.Model.HistoryPaket
import com.lukitor.projectta.Model.HistoryTransaksi
import com.lukitor.projectta.R
import com.lukitor.projectta.activityDokter.KonsultasiOnline.ChatDokterActivity
import com.lukitor.projectta.activityPasien.adapter.ListDokterKonsultasiAdapter
import com.lukitor.projectta.databinding.ActivityKonsultasiOnlineBinding
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.BillingAddress
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ItemDetails
import com.midtrans.sdk.corekit.models.ShippingAddress
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


class KonsultasiOnlineActivity : AppCompatActivity()  {

    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityKonsultasiOnlineBinding
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var listDokter : MutableList<Dokter>
    private lateinit var listHistory : MutableList<HistoryTransaksi>
    var ctr = 1
    var tanggal = ""
    var diskon = 0
    var biayakonsultasi = 10000
    var statusKonsul = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityKonsultasiOnlineBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            firebaseUser = auth.currentUser!!
            listDokter = mutableListOf()
            listHistory = mutableListOf()
            userInfo()
            Loading()
            cekVocer()
            var simpleDateFormat= SimpleDateFormat("dd-MM-yyyy")
            tanggal = simpleDateFormat.format(Date())

            binding.imgbackprofilepasien.setOnClickListener {
                finish()
                overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
            }

            binding.rvHistorysaldo.setOnItemClickListener { parent, view, position, id ->
                val dokter = listDokter.get(position)
                val builder = AlertDialog.Builder(this)
                val temp = NumberFormat.getInstance().format(dokter.tarif)
                val tempbiayakonsul = NumberFormat.getInstance().format(biayakonsultasi)
                val tempdiskon = NumberFormat.getInstance().format(diskon)
                val temptotal = (dokter.tarif!!.toInt() + biayakonsultasi) - diskon
                val total = NumberFormat.getInstance().format(temptotal)
                var tempstr = ""
                if (diskon > 0){
                    tempstr = "Apakah anda yakin melakukan sesi konsultsi dengan Dokter " + dokter.nama +  "?\nTotal Biaya : \nTarif Dokter : Rp. $temp,-\nFee Konsultasi : Rp. $tempbiayakonsul,-\nPotongan Voucher : Rp. $tempdiskon,-\nTotal : Rp. $total,-"
                }
                else{
                    tempstr = "Apakah anda yakin melakukan sesi konsultsi dengan Dokter " + dokter.nama +  "?\nTotal Biaya : \nTarif Dokter : Rp. $temp,-\nFee Konsultasi : Rp. $tempbiayakonsul,-\nTotal : Rp. $total,-"
                }
                builder.setTitle("Konfirmasi")
                builder.setMessage(tempstr)
                builder.setPositiveButton("Ya") { dialog, which ->
                    statusKonsul = true
                    cekVocer()
                    InserData(auth.currentUser!!.uid, dokter.id, dokter.tarif!!, tanggal,"","",0,0,ctr)
                }

                builder.setNegativeButton("Tidak") { dialog, which ->
                    Toast.makeText(this, "Konsultasi dibatalkan", Toast.LENGTH_SHORT).show()
                }
                val dialog = builder.create()
                dialog.show()

//                SdkUIFlowBuilder.init().setClientKey("SB-Mid-client-aJzv5XCgy6JobNa6") .setContext(applicationContext) .setTransactionFinishedCallback {this} .setMerchantBaseUrl("https://tarocoin.net/charge/").enableLog(true).setColorTheme(CustomColorTheme("#FFE51255","#B61548", "#FFE51255")).buildSDK()
//                val transactionRequest = TransactionRequest(TRANSACTION_ID, 50000.00)
//                val customerDetails = CustomerDetails()
//                customerDetails.setCustomerIdentifier("budi-6789")
//                customerDetails.setPhone("08123456789")
//                customerDetails.setFirstName("Budi")
//                customerDetails.setLastName("Utama")
//                customerDetails.setEmail("budi@utomo123.com")
//                val shippingAddress = ShippingAddress()
//                shippingAddress.address = "Jalan Sultan Andara Gang Sebelah No. 1"
//                shippingAddress.city = "Jakarta"
//                shippingAddress.postalCode = "10220"
//                customerDetails.setShippingAddress(shippingAddress)
//                val billingAddress = BillingAddress()
//                billingAddress.address = "Jalan Andalas Gang Sebelah No. 1"
//                billingAddress.city = "Jakarta"
//                billingAddress.postalCode = "10220"
//                customerDetails.setBillingAddress(billingAddress)
//                transactionRequest.setCustomerDetails(customerDetails)
//                val itemDetails1 =
//                    ItemDetails("ITEM_ID_1", 25000.00, 1, "ITEM_NAME_1")
//                val itemDetails2 =
//                    ItemDetails("ITEM_ID_2", 25000.00, 1, "ITEM_NAME_2")
//                val itemDetailsList: ArrayList<ItemDetails> = ArrayList()
//                itemDetailsList.add(itemDetails1)
//                itemDetailsList.add(itemDetails2)
//                transactionRequest.itemDetails = itemDetailsList
//                MidtransSDK.getInstance().setTransactionRequest(transactionRequest);
//                MidtransSDK.getInstance().startPaymentUiFlow(this);
            }
        }
        else{
            signout()
        }
    }

//    fun onTransactionFinished(result: TransactionResult) {
//        if (result.response != null) {
//            when (result.status) {
//                TransactionResult.STATUS_SUCCESS -> Toast.makeText(
//                    this,
//                    "Transaction Finished. ID: " + result.response.transactionId,
//                    Toast.LENGTH_LONG
//                ).show()
//                TransactionResult.STATUS_PENDING -> Toast.makeText(
//                    this,
//                    "Transaction Pending. ID: " + result.response.transactionId,
//                    Toast.LENGTH_LONG
//                ).show()
//                TransactionResult.STATUS_FAILED -> Toast.makeText(
//                    this,
//                    "Transaction Failed. ID: " + result.response.transactionId + ". Message: " + result.response.statusMessage,
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//            result.response.validationMessages
//        } else if (result.isTransactionCanceled) {
//            Toast.makeText(this, "Transaction Canceled", Toast.LENGTH_LONG).show()
//        } else {
//            if (result.status.equals(TransactionResult.STATUS_INVALID, ignoreCase = true)) {
//                Toast.makeText(this, "Transaction Invalid", Toast.LENGTH_LONG).show()
//            } else {
//                Toast.makeText(this, "Transaction Finished with failure.", Toast.LENGTH_LONG).show()
//            }
//        }
//    }

    private fun cekVocer(){
        val ref = FirebaseDatabase.getInstance().getReference().child("HISTORYPAKET")
        val subscriptionQuery = ref.orderByChild("tglexp").endAt(tanggal)
        subscriptionQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var subscriptionExpired = true
                var maxScanAllowed = 0
                var subscriptionKey: String? = null
                for (snapshot in dataSnapshot.children) {
                    val subscription = snapshot.getValue(HistoryPaket::class.java)
                    if (subscription != null && subscription.jmlvocer!! > 0) {
                        subscriptionExpired = false
                        maxScanAllowed = subscription.jmlvocer!!
                        diskon = subscription.nominalvocer!!
                        subscriptionKey = snapshot.key
                        break
                    }
                }
                if (statusKonsul){
                    if (!subscriptionExpired) {
                        subscriptionKey?.let {
                            val subscriptionRef = ref.child(it)
                            subscriptionRef.child("jmlvocer").setValue(maxScanAllowed - 1)
                        }
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                println("Error: ${databaseError.message}")
            }
        })
    }

    private fun InserData(
        pasienId: String,
        dokterId: String,
        tarifKonsultasi: Int,
        tanggal: String,
        jammulai: String,
        jamselesai: String,
        statusTrans: Int,
        statusRM: Int,
        ctr:Int
    ){
        val ref = FirebaseDatabase.getInstance().getReference("HISTORYTRANSAKSI")
        val historyId = ref.push().key
        var tempbiaya = biayakonsultasi
        var statusvocr = 0
        if (diskon > 0){
            if (tempbiaya >= diskon){
                tempbiaya -= diskon
            }
            else{
                tempbiaya = 0
            }
            statusvocr = 1

        }
        val hTrans = HistoryTransaksi(historyId.toString(),pasienId,dokterId,tarifKonsultasi,tempbiaya ,statusvocr,tanggal,jammulai,jamselesai,0,0,0,"",5)
        ref.child(historyId.toString()).setValue(hTrans).addOnCompleteListener {
            Toast.makeText(this@KonsultasiOnlineActivity, "Konsultasi Online sedang diproses", Toast.LENGTH_SHORT).show()
        }
        finish()
        overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
    }

    private fun userInfo(){
        val ref = FirebaseDatabase.getInstance().getReference().child("DOKTER")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    listDokter.clear()
                    for (h in snapshot.children){
                        val dokter = h.getValue(Dokter::class.java)
                        if(dokter!=null){
                            listDokter.add(dokter)
                            val adapter = ListDokterKonsultasiAdapter(this@KonsultasiOnlineActivity,R.layout.itemlistdokter,listDokter)
                            binding.rvHistorysaldo.adapter = adapter
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun signout(){
        auth.signOut()
        Intent(this, MainActivity::class.java).also {
            startActivity(it)
            finish()
            overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
        }
    }

    private fun Loading(){
        binding.mainLayout.visibility = View.GONE
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        Handler().postDelayed({
            progressDialog.dismiss()
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            binding.mainLayout.visibility = View.VISIBLE
        }, 2000) // 2000 mill
    }
}