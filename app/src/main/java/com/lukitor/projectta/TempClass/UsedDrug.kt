package com.lukitor.projectta.TempClass

data class UsedDrug(
    val userId: String,
    val drugPenyakitBiang: String?,
    val drugPenyakitBiduran: String?,
    val drugPenyakitBisul: String?,
    val drugPenyakitCacar: String?,
    val drugPenyakitFlek: String?,
    val drugPenyakitJerawat: String?,
    val drugPenyakitKetombe: String?,
    val drugPenyakitKurap: String?,
    val drugPenyakitPanu: String?,
    val drugPenyakitVitiligo: String?
)
fun UsedDrug.getDrugIds(): List<String> {
    return listOfNotNull(
        drugPenyakitBiang,
        drugPenyakitBiduran,
        drugPenyakitBisul,
        drugPenyakitCacar,
        drugPenyakitFlek,
        drugPenyakitJerawat,
        drugPenyakitKetombe,
        drugPenyakitKurap,
        drugPenyakitPanu,
        drugPenyakitVitiligo
    )
}