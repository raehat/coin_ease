package com.example.particleapped

import android.app.Application
import com.microblink.blinkid.MicroblinkSDK
import com.particle.base.Env
import com.particle.base.ParticleNetwork
import network.particle.chains.ChainInfo

class ParticleApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MicroblinkSDK.setLicenseFile("license.key", this)
        ParticleNetwork.init(this, Env.DEV, ChainInfo.EthereumSepolia)
    }
}