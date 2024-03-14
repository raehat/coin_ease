package com.example.particleapped.data

import kotlinx.serialization.Serializable

@Serializable
data class QrCodeData (
    var address: String = "",
    var amount: String = "",
    var chainId: String = "",
    var chainName: String = ""
)