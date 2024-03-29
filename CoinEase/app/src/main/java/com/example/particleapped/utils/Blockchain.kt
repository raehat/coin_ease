package com.example.particleapp.utils

import com.particle.api.evm
import com.particle.api.service.data.ContractParams
import com.particle.base.ParticleNetwork
import com.particle.base.data.ErrorInfo
import com.particle.base.data.SignOutput
import com.particle.base.data.WebServiceCallback
import com.particle.network.ParticleNetworkAuth.getAddress
import com.particle.network.ParticleNetworkAuth.signAndSendTransaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.internal.toHexString
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.http.HttpService
import java.math.BigInteger
import kotlin.math.pow

object Blockchain {

    private const val sameChainEscrowContractAddress = "0xaD7243c1a5d8F32F843B2d75075E6466d1d23d93"
    private const val tokenExchangeContractAddress = "0xA596B99A9C08EA2e7e0DDda40Dc28739dA7Ff4a9"
    private val web3 = Web3j.build(HttpService("https://avalanche-fuji.infura.io/v3/11782cb03f81433d86bb20d869cdd882"))
    val chainToAddressCrossChainContract : Map<String, String> = mapOf(
        "avalanche" to "0xD1a7A0Ef93Ca5056A2F44cC1dc4ee63d770D987b",
        "polygon" to "0x172cB4b1d00B560025f22eCe88a145f4c77e2947"
    )
    val usdcAddressToChain : Map<String, String> = mapOf(
        "avalanche" to "0x5425890298aed601595a70AB815c96711a31Bc65"
    )

    suspend fun sendPayment(receivingAddress: String, amt: String) {
        val amountInValue = amt.toDouble()
        val amount = (amountInValue * 10.0.pow(18.0)).toBigDecimal().toBigInteger()

        val function = Function(
            "sendPayment",
            listOf(
                Address(receivingAddress),
                Uint256(amount)
            ),
            emptyList()
        )

        val encodedFunction = FunctionEncoder.encode(function)

        val contractParams = ContractParams.customAbiEncodeFunctionCall(
            sameChainEscrowContractAddress, "sendPayment",
            listOf(
                receivingAddress,
                10000L,
            ),
            "[\r\n\t{\r\n\t\t\"anonymous\": false,\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"payId\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"sender\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"receiver\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"amount\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"PaymentClaimed\",\r\n\t\t\"type\": \"event\"\r\n\t},\r\n\t{\r\n\t\t\"anonymous\": false,\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"payId\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"sender\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"receiver\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"amount\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"deadline\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"PaymentInitiated\",\r\n\t\t\"type\": \"event\"\r\n\t},\r\n\t{\r\n\t\t\"anonymous\": false,\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"payId\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"sender\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"receiver\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"amount\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"PaymentReverted\",\r\n\t\t\"type\": \"event\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"_paymentId\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"claimPayment\",\r\n\t\t\"outputs\": [],\r\n\t\t\"stateMutability\": \"nonpayable\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [],\r\n\t\t\"name\": \"currPaymentId\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"_receiver\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"getReceivedPayments\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"components\": [\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"address payable\",\r\n\t\t\t\t\t\t\"name\": \"sender\",\r\n\t\t\t\t\t\t\"type\": \"address\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"address payable\",\r\n\t\t\t\t\t\t\"name\": \"receiver\",\r\n\t\t\t\t\t\t\"type\": \"address\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\t\t\"name\": \"amount\",\r\n\t\t\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\t\t\"name\": \"deadline\",\r\n\t\t\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\t\t\"name\": \"claimed\",\r\n\t\t\t\t\t\t\"type\": \"bool\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\t\t\"name\": \"reverted\",\r\n\t\t\t\t\t\t\"type\": \"bool\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\t\t\"name\": \"paymentId\",\r\n\t\t\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t\t\t}\r\n\t\t\t\t],\r\n\t\t\t\t\"internalType\": \"struct ETHEscrow.Payment[]\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"tuple[]\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"_sender\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"getSentPayments\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"components\": [\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"address payable\",\r\n\t\t\t\t\t\t\"name\": \"sender\",\r\n\t\t\t\t\t\t\"type\": \"address\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"address payable\",\r\n\t\t\t\t\t\t\"name\": \"receiver\",\r\n\t\t\t\t\t\t\"type\": \"address\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\t\t\"name\": \"amount\",\r\n\t\t\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\t\t\"name\": \"deadline\",\r\n\t\t\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\t\t\"name\": \"claimed\",\r\n\t\t\t\t\t\t\"type\": \"bool\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\t\t\"name\": \"reverted\",\r\n\t\t\t\t\t\t\"type\": \"bool\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\t\t\"name\": \"paymentId\",\r\n\t\t\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t\t\t}\r\n\t\t\t\t],\r\n\t\t\t\t\"internalType\": \"struct ETHEscrow.Payment[]\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"tuple[]\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"payments\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address payable\",\r\n\t\t\t\t\"name\": \"sender\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address payable\",\r\n\t\t\t\t\"name\": \"receiver\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"amount\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"deadline\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"claimed\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"reverted\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"paymentId\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"receivedPayments\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address payable\",\r\n\t\t\t\t\"name\": \"sender\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address payable\",\r\n\t\t\t\t\"name\": \"receiver\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"amount\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"deadline\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"claimed\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"reverted\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"paymentId\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"_paymentId\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"revertPayment\",\r\n\t\t\"outputs\": [],\r\n\t\t\"stateMutability\": \"nonpayable\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address payable\",\r\n\t\t\t\t\"name\": \"_receiver\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"_timeAhead\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"sendPayment\",\r\n\t\t\"outputs\": [],\r\n\t\t\"stateMutability\": \"payable\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"sentPayments\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address payable\",\r\n\t\t\t\t\"name\": \"sender\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address payable\",\r\n\t\t\t\t\"name\": \"receiver\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"amount\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"deadline\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"claimed\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"reverted\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"paymentId\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t}\r\n]"
        )

        try {
            val txData = ParticleNetwork.evm.createTransaction(
                ParticleNetwork.getAddress(),
                sameChainEscrowContractAddress,
                "0x$amount",
                abiEncodeData = encodedFunction
            )

            ParticleNetwork.signAndSendTransaction(txData!!.serialize(), object : WebServiceCallback<SignOutput> {
                override fun success(output: SignOutput) {
                    println(output)
                }

                override fun failure(errMsg: ErrorInfo) {
                    println(errMsg)
                }
            })
        } catch (e: Exception) {
            println(e)
        }
    }
    fun getBalance(): Double {
        val balanceWei = web3.ethGetBalance(ParticleNetwork.getAddress(), DefaultBlockParameter.valueOf("latest")).sendAsync().get().balance
        return balanceWei.toDouble() / 10.0.pow(18.0)
    }

    suspend fun sendCrossChainPayment(
        amountToBeSent: String,
        senderChain: String,
        receiverChain: String,
        receivingAddress: String,
        showToast: (String) -> Unit,
        onSuccess: () -> Unit,
        onFailed: () -> Unit
    ) {
        val senderChainContractAddress =
            chainToAddressCrossChainContract[senderChain] ?: return
        val receiverChainContractAddress =
            chainToAddressCrossChainContract[receiverChain] ?: return
        val usdcCoinAddress = usdcAddressToChain[senderChain] ?: return

        val amount = (amountToBeSent.toDouble() * 10.0.pow(18)).toBigDecimal().toBigInteger()
        val allowance = getAllowance(senderChainContractAddress, usdcCoinAddress)
        if (amount > allowance) {
            requestAllowance(usdcCoinAddress, amount, senderChainContractAddress,
                onSuccess = {
                    CoroutineScope(Dispatchers.IO).launch {
                        makePayment(
                            senderChainContractAddress,
                            receivingAddress,
                            amount,
                            usdcCoinAddress,
                            onSuccess,
                            onFailed,
                            showToast
                        )
                    }
                },
                onFailed = {
                    println("Payment Failed!")
                }
            )
            return
        }

        makePayment(
            senderChainContractAddress,
            receivingAddress,
            amount,
            usdcCoinAddress,
            onSuccess,
            onFailed,
            showToast
        )
    }

    private suspend fun makePayment(
        senderChainContractAddress: String,
        receivingAddress: String,
        amount: BigInteger,
        usdcCoinAddress: String,
        onSuccess: () -> Unit,
        onFailed: () -> Unit,
        showToast: (String) -> Unit
    ) {
        try {
            val contractParams = ContractParams.customAbiEncodeFunctionCall(
                senderChainContractAddress,
                "sendPayment",
                listOf(
                    receivingAddress,
                    amount.toLong(),
                    10000L,
                    usdcCoinAddress
                ),
            )

            val transaction = ParticleNetwork.evm.createTransaction(
                ParticleNetwork.getAddress(),
                senderChainContractAddress,
                value = null,
                contractParams
            )

            ParticleNetwork.signAndSendTransaction(
                transaction!!.serialize(),
                object : WebServiceCallback<SignOutput> {
                    override fun success(output: SignOutput) {
                        println(output)
                        onSuccess()
                    }

                    override fun failure(errMsg: ErrorInfo) {
                        println(errMsg)
                        onFailed()
                    }
                })
        } catch (e: Exception) {
            println(e)
            showToast(e.message.toString())
        }
    }

    private suspend fun requestAllowance(
        usdcCoinAddress: String,
        amount: BigInteger,
        senderChainContractAddress: String,
        onSuccess: () -> Unit,
        onFailed: () -> Unit
    ) {
        val contractParams = ContractParams.erc20Approve(usdcCoinAddress, senderChainContractAddress, "0x${amount.toLong().toHexString()}")
        val iTxData = ParticleNetwork.evm.createTransaction(
            ParticleNetwork.getAddress(), usdcCoinAddress, "0x0", type = "0x0", contractParams = contractParams
        )
        ParticleNetwork.signAndSendTransaction(iTxData!!.serialize(), object : WebServiceCallback<SignOutput> {
            override fun success(output: SignOutput) {
                println(output)
            }
            override fun failure(errMsg: ErrorInfo) {
                println(errMsg)
            }
        })
    }
    private fun getAllowance(senderChainContractAddress: String, usdcCoinAddress: String): BigInteger {
        val function = Function(
            "allowance",
            listOf(
                Address(ParticleNetwork.getAddress()),
                Address(senderChainContractAddress)
            ),
            listOf(object : TypeReference<Uint256?>() {})
        )

        val encodedFunction = FunctionEncoder.encode(function)

        val ethCall = web3.ethCall(
            Transaction.createEthCallTransaction(
                ParticleNetwork.getAddress(),
                usdcCoinAddress,
                encodedFunction
            ),
            DefaultBlockParameterName.LATEST
        ).send().value

        val values = FunctionReturnDecoder.decode(
            ethCall,
            function.outputParameters
        )
        return values[0].value as BigInteger
    }

    fun getUSDCBalance(): Double {
        val function = Function(
            "balanceOf",
            listOf(
                Address(ParticleNetwork.getAddress())
            ),
            listOf(object : TypeReference<Uint256?>() {})
        )

        val encodedFunction = FunctionEncoder.encode(function)

//        val ethCall = web3.ethCall(
//            Transaction.createEthCallTransaction(
//                ParticleNetwork.getAddress(),
//                usdcAddressToChain[ParticleNetwork.chainName],
//                encodedFunction
//            ),
//            DefaultBlockParameterName.LATEST
//        ).sendAsync().get().value ?: return 0.000
//
//        val values = FunctionReturnDecoder.decode(
//            ethCall,
//            function.outputParameters
//        )
//        return (values[0].value as Double / 10.0.pow(18))
        return 0.00
    }

    suspend fun claimPayment(
        senderChainContractAddress: String,
        receivingChainContractAddress: String,
        receivingAddress: String,
        paymentId: Int,
        onSuccess: () -> Unit,
        onFailed: () -> Unit,
        showToast: (String) -> Unit
    ) {
//        try {
//            val contractParams = ContractParams.customAbiEncodeFunctionCall(
//                senderChainContractAddress,
//                "sendMessagePayLINK",
//                listOf(
//                    paymentId,
//                    "12532609583862916517",
//                    receivingChainContractAddress,
//                    receivingAddress
//                ),
//                "[\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"_router\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"_link\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"nonpayable\",\r\n\t\t\"type\": \"constructor\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint64\",\r\n\t\t\t\t\"name\": \"destinationChainSelector\",\r\n\t\t\t\t\"type\": \"uint64\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"DestinationChainNotAllowed\",\r\n\t\t\"type\": \"error\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"owner\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"target\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"value\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"FailedToWithdrawEth\",\r\n\t\t\"type\": \"error\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [],\r\n\t\t\"name\": \"InvalidReceiverAddress\",\r\n\t\t\"type\": \"error\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"router\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"InvalidRouter\",\r\n\t\t\"type\": \"error\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"currentBalance\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"calculatedFees\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"NotEnoughBalance\",\r\n\t\t\"type\": \"error\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [],\r\n\t\t\"name\": \"NothingToWithdraw\",\r\n\t\t\"type\": \"error\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"sender\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"SenderNotAllowed\",\r\n\t\t\"type\": \"error\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint64\",\r\n\t\t\t\t\"name\": \"sourceChainSelector\",\r\n\t\t\t\t\"type\": \"uint64\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"SourceChainNotAllowed\",\r\n\t\t\"type\": \"error\"\r\n\t},\r\n\t{\r\n\t\t\"anonymous\": false,\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": true,\r\n\t\t\t\t\"internalType\": \"bytes32\",\r\n\t\t\t\t\"name\": \"messageId\",\r\n\t\t\t\t\"type\": \"bytes32\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": true,\r\n\t\t\t\t\"internalType\": \"uint64\",\r\n\t\t\t\t\"name\": \"sourceChainSelector\",\r\n\t\t\t\t\"type\": \"uint64\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"sender\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"string\",\r\n\t\t\t\t\"name\": \"text\",\r\n\t\t\t\t\"type\": \"string\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"token\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"tokenAmount\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"MessageReceived\",\r\n\t\t\"type\": \"event\"\r\n\t},\r\n\t{\r\n\t\t\"anonymous\": false,\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": true,\r\n\t\t\t\t\"internalType\": \"bytes32\",\r\n\t\t\t\t\"name\": \"messageId\",\r\n\t\t\t\t\"type\": \"bytes32\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": true,\r\n\t\t\t\t\"internalType\": \"uint64\",\r\n\t\t\t\t\"name\": \"destinationChainSelector\",\r\n\t\t\t\t\"type\": \"uint64\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"receiver\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"string\",\r\n\t\t\t\t\"name\": \"text\",\r\n\t\t\t\t\"type\": \"string\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"token\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"tokenAmount\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"feeToken\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"fees\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"MessageSent\",\r\n\t\t\"type\": \"event\"\r\n\t},\r\n\t{\r\n\t\t\"anonymous\": false,\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": true,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"from\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": true,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"to\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"OwnershipTransferRequested\",\r\n\t\t\"type\": \"event\"\r\n\t},\r\n\t{\r\n\t\t\"anonymous\": false,\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": true,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"from\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": true,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"to\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"OwnershipTransferred\",\r\n\t\t\"type\": \"event\"\r\n\t},\r\n\t{\r\n\t\t\"anonymous\": false,\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"payId\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"sender\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"receiver\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"tokenAddress\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"amount\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"PaymentClaimed\",\r\n\t\t\"type\": \"event\"\r\n\t},\r\n\t{\r\n\t\t\"anonymous\": false,\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"payId\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"sender\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"receiver\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"tokenAddress\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"amount\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"deadline\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"PaymentInitiated\",\r\n\t\t\"type\": \"event\"\r\n\t},\r\n\t{\r\n\t\t\"anonymous\": false,\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"payId\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"sender\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"receiver\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"tokenAddress\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"amount\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"PaymentReverted\",\r\n\t\t\"type\": \"event\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [],\r\n\t\t\"name\": \"acceptOwnership\",\r\n\t\t\"outputs\": [],\r\n\t\t\"stateMutability\": \"nonpayable\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint64\",\r\n\t\t\t\t\"name\": \"_destinationChainSelector\",\r\n\t\t\t\t\"type\": \"uint64\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"allowed\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"allowlistDestinationChain\",\r\n\t\t\"outputs\": [],\r\n\t\t\"stateMutability\": \"nonpayable\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"_sender\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"allowed\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"allowlistSender\",\r\n\t\t\"outputs\": [],\r\n\t\t\"stateMutability\": \"nonpayable\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint64\",\r\n\t\t\t\t\"name\": \"_sourceChainSelector\",\r\n\t\t\t\t\"type\": \"uint64\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"allowed\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"allowlistSourceChain\",\r\n\t\t\"outputs\": [],\r\n\t\t\"stateMutability\": \"nonpayable\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint64\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"uint64\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"allowlistedDestinationChains\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"allowlistedSenders\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint64\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"uint64\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"allowlistedSourceChains\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"_paymentId\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint64\",\r\n\t\t\t\t\"name\": \"_destinationChainSelector\",\r\n\t\t\t\t\"type\": \"uint64\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"deployedReceivingContract\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"string\",\r\n\t\t\t\t\"name\": \"_receivingAddress\",\r\n\t\t\t\t\"type\": \"string\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"calculateOverheadFee\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"overheadFee\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"components\": [\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"bytes32\",\r\n\t\t\t\t\t\t\"name\": \"messageId\",\r\n\t\t\t\t\t\t\"type\": \"bytes32\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"uint64\",\r\n\t\t\t\t\t\t\"name\": \"sourceChainSelector\",\r\n\t\t\t\t\t\t\"type\": \"uint64\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"bytes\",\r\n\t\t\t\t\t\t\"name\": \"sender\",\r\n\t\t\t\t\t\t\"type\": \"bytes\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"bytes\",\r\n\t\t\t\t\t\t\"name\": \"data\",\r\n\t\t\t\t\t\t\"type\": \"bytes\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"components\": [\r\n\t\t\t\t\t\t\t{\r\n\t\t\t\t\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\t\t\t\t\"name\": \"token\",\r\n\t\t\t\t\t\t\t\t\"type\": \"address\"\r\n\t\t\t\t\t\t\t},\r\n\t\t\t\t\t\t\t{\r\n\t\t\t\t\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\t\t\t\t\"name\": \"amount\",\r\n\t\t\t\t\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t\t\t\t\t}\r\n\t\t\t\t\t\t],\r\n\t\t\t\t\t\t\"internalType\": \"struct Client.EVMTokenAmount[]\",\r\n\t\t\t\t\t\t\"name\": \"destTokenAmounts\",\r\n\t\t\t\t\t\t\"type\": \"tuple[]\"\r\n\t\t\t\t\t}\r\n\t\t\t\t],\r\n\t\t\t\t\"internalType\": \"struct Client.Any2EVMMessage\",\r\n\t\t\t\t\"name\": \"message\",\r\n\t\t\t\t\"type\": \"tuple\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"ccipReceive\",\r\n\t\t\"outputs\": [],\r\n\t\t\"stateMutability\": \"nonpayable\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [],\r\n\t\t\"name\": \"currPaymentId\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint8\",\r\n\t\t\t\t\"name\": \"c\",\r\n\t\t\t\t\"type\": \"uint8\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"fromHexChar\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint8\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"uint8\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"pure\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [],\r\n\t\t\"name\": \"getLastReceivedMessageDetails\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bytes32\",\r\n\t\t\t\t\"name\": \"messageId\",\r\n\t\t\t\t\"type\": \"bytes32\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"string\",\r\n\t\t\t\t\"name\": \"text\",\r\n\t\t\t\t\"type\": \"string\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"tokenAddress\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"tokenAmount\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"_receiver\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"getReceivedPayments\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"components\": [\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\t\t\"name\": \"sender\",\r\n\t\t\t\t\t\t\"type\": \"address\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\t\t\"name\": \"receiver\",\r\n\t\t\t\t\t\t\"type\": \"address\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\t\t\"name\": \"tokenAddress\",\r\n\t\t\t\t\t\t\"type\": \"address\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\t\t\"name\": \"amount\",\r\n\t\t\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\t\t\"name\": \"deadline\",\r\n\t\t\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\t\t\"name\": \"claimed\",\r\n\t\t\t\t\t\t\"type\": \"bool\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\t\t\"name\": \"reverted\",\r\n\t\t\t\t\t\t\"type\": \"bool\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\t\t\"name\": \"paymentId\",\r\n\t\t\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t\t\t}\r\n\t\t\t\t],\r\n\t\t\t\t\"internalType\": \"struct ProgrammableTokenTransfers.Payment[]\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"tuple[]\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [],\r\n\t\t\"name\": \"getRouter\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"_sender\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"getSentPayments\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"components\": [\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\t\t\"name\": \"sender\",\r\n\t\t\t\t\t\t\"type\": \"address\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\t\t\"name\": \"receiver\",\r\n\t\t\t\t\t\t\"type\": \"address\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\t\t\"name\": \"tokenAddress\",\r\n\t\t\t\t\t\t\"type\": \"address\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\t\t\"name\": \"amount\",\r\n\t\t\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\t\t\"name\": \"deadline\",\r\n\t\t\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\t\t\"name\": \"claimed\",\r\n\t\t\t\t\t\t\"type\": \"bool\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\t\t\"name\": \"reverted\",\r\n\t\t\t\t\t\t\"type\": \"bool\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\t\t\"name\": \"paymentId\",\r\n\t\t\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t\t\t}\r\n\t\t\t\t],\r\n\t\t\t\t\"internalType\": \"struct ProgrammableTokenTransfers.Payment[]\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"tuple[]\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"string\",\r\n\t\t\t\t\"name\": \"s\",\r\n\t\t\t\t\"type\": \"string\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"hexStringToAddress\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bytes\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"bytes\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"pure\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [],\r\n\t\t\"name\": \"owner\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"payments\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"sender\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"receiver\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"tokenAddress\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"amount\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"deadline\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"claimed\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"reverted\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"paymentId\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"receivedPayments\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"sender\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"receiver\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"tokenAddress\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"amount\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"deadline\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"claimed\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"reverted\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"paymentId\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"_paymentId\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"revertPayment\",\r\n\t\t\"outputs\": [],\r\n\t\t\"stateMutability\": \"nonpayable\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"_paymentId\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint64\",\r\n\t\t\t\t\"name\": \"_destinationChainSelector\",\r\n\t\t\t\t\"type\": \"uint64\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"deployedReceivingContract\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"string\",\r\n\t\t\t\t\"name\": \"_receivingAddress\",\r\n\t\t\t\t\"type\": \"string\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"sendMessagePayLINK\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bytes32\",\r\n\t\t\t\t\"name\": \"messageId\",\r\n\t\t\t\t\"type\": \"bytes32\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"nonpayable\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"_paymentId\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint64\",\r\n\t\t\t\t\"name\": \"_destinationChainSelector\",\r\n\t\t\t\t\"type\": \"uint64\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"deployedReceivingContract\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"string\",\r\n\t\t\t\t\"name\": \"_receivingAddress\",\r\n\t\t\t\t\"type\": \"string\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"sendMessagePayNative\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bytes32\",\r\n\t\t\t\t\"name\": \"messageId\",\r\n\t\t\t\t\"type\": \"bytes32\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"nonpayable\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"_receiver\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"_amount\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"_timeAhead\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"_tokenAddress\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"sendPayment\",\r\n\t\t\"outputs\": [],\r\n\t\t\"stateMutability\": \"nonpayable\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"sentPayments\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"sender\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"receiver\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"tokenAddress\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"amount\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"deadline\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"claimed\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"reverted\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"paymentId\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bytes4\",\r\n\t\t\t\t\"name\": \"interfaceId\",\r\n\t\t\t\t\"type\": \"bytes4\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"supportsInterface\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"pure\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"string\",\r\n\t\t\t\t\"name\": \"s\",\r\n\t\t\t\t\"type\": \"string\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"toAddress\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"pure\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"to\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"transferOwnership\",\r\n\t\t\"outputs\": [],\r\n\t\t\"stateMutability\": \"nonpayable\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"_beneficiary\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"withdraw\",\r\n\t\t\"outputs\": [],\r\n\t\t\"stateMutability\": \"nonpayable\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"_beneficiary\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"_token\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"withdrawToken\",\r\n\t\t\"outputs\": [],\r\n\t\t\"stateMutability\": \"nonpayable\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"stateMutability\": \"payable\",\r\n\t\t\"type\": \"receive\"\r\n\t}\r\n]"
//            )
//
//            val transaction = ParticleNetwork.evm.createTransaction(
//                ParticleNetwork.getAddress(),
//                senderChainContractAddress,
//                value = null,
//                contractParams
//            )
//
//            ParticleNetwork.signAndSendTransaction(
//                transaction!!.serialize(),
//                object : WebServiceCallback<SignOutput> {
//                    override fun success(output: SignOutput) {
//                        println(output)
//                        onSuccess()
//                    }
//
//                    override fun failure(errMsg: ErrorInfo) {
//                        println(errMsg)
//                        onFailed()
//                    }
//                })
//        } catch (e: Exception) {
//            println(e)
//            showToast(e.message.toString())
//        }
        val function = Function(
            "claimPayment",
            listOf(
                Uint256(paymentId.toLong())
            ),
            emptyList()
        )

        val encodedFunction = FunctionEncoder.encode(function)

        try {
            val txData = ParticleNetwork.evm.createTransaction(
                ParticleNetwork.getAddress(),
                sameChainEscrowContractAddress,
                abiEncodeData = encodedFunction
            )

            ParticleNetwork.signAndSendTransaction(txData!!.serialize(), object : WebServiceCallback<SignOutput> {
                override fun success(output: SignOutput) {
                    println(output)
                    onSuccess()
                }

                override fun failure(errMsg: ErrorInfo) {
                    println(errMsg)
                    onFailed()
                }
            })
        } catch (e: Exception) {
            println(e)
        }
    }

    suspend fun revertPayment(
        senderChainContractAddress: String,
        paymentId: Int,
        onSuccess: () -> Unit,
        onFailed: () -> Unit,
        showToast: (String) -> Unit
    ) {
//        try {
//            val contractParams = ContractParams.customAbiEncodeFunctionCall(
//                senderChainContractAddress,
//                "revertPayment",
//                listOf(
//                    paymentId
//                ),
//                "[\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"_router\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"_link\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"nonpayable\",\r\n\t\t\"type\": \"constructor\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint64\",\r\n\t\t\t\t\"name\": \"destinationChainSelector\",\r\n\t\t\t\t\"type\": \"uint64\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"DestinationChainNotAllowed\",\r\n\t\t\"type\": \"error\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"owner\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"target\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"value\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"FailedToWithdrawEth\",\r\n\t\t\"type\": \"error\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [],\r\n\t\t\"name\": \"InvalidReceiverAddress\",\r\n\t\t\"type\": \"error\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"router\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"InvalidRouter\",\r\n\t\t\"type\": \"error\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"currentBalance\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"calculatedFees\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"NotEnoughBalance\",\r\n\t\t\"type\": \"error\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [],\r\n\t\t\"name\": \"NothingToWithdraw\",\r\n\t\t\"type\": \"error\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"sender\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"SenderNotAllowed\",\r\n\t\t\"type\": \"error\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint64\",\r\n\t\t\t\t\"name\": \"sourceChainSelector\",\r\n\t\t\t\t\"type\": \"uint64\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"SourceChainNotAllowed\",\r\n\t\t\"type\": \"error\"\r\n\t},\r\n\t{\r\n\t\t\"anonymous\": false,\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": true,\r\n\t\t\t\t\"internalType\": \"bytes32\",\r\n\t\t\t\t\"name\": \"messageId\",\r\n\t\t\t\t\"type\": \"bytes32\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": true,\r\n\t\t\t\t\"internalType\": \"uint64\",\r\n\t\t\t\t\"name\": \"sourceChainSelector\",\r\n\t\t\t\t\"type\": \"uint64\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"sender\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"string\",\r\n\t\t\t\t\"name\": \"text\",\r\n\t\t\t\t\"type\": \"string\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"token\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"tokenAmount\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"MessageReceived\",\r\n\t\t\"type\": \"event\"\r\n\t},\r\n\t{\r\n\t\t\"anonymous\": false,\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": true,\r\n\t\t\t\t\"internalType\": \"bytes32\",\r\n\t\t\t\t\"name\": \"messageId\",\r\n\t\t\t\t\"type\": \"bytes32\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": true,\r\n\t\t\t\t\"internalType\": \"uint64\",\r\n\t\t\t\t\"name\": \"destinationChainSelector\",\r\n\t\t\t\t\"type\": \"uint64\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"receiver\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"string\",\r\n\t\t\t\t\"name\": \"text\",\r\n\t\t\t\t\"type\": \"string\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"token\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"tokenAmount\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"feeToken\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"fees\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"MessageSent\",\r\n\t\t\"type\": \"event\"\r\n\t},\r\n\t{\r\n\t\t\"anonymous\": false,\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": true,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"from\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": true,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"to\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"OwnershipTransferRequested\",\r\n\t\t\"type\": \"event\"\r\n\t},\r\n\t{\r\n\t\t\"anonymous\": false,\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": true,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"from\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": true,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"to\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"OwnershipTransferred\",\r\n\t\t\"type\": \"event\"\r\n\t},\r\n\t{\r\n\t\t\"anonymous\": false,\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"payId\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"sender\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"receiver\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"tokenAddress\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"amount\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"PaymentClaimed\",\r\n\t\t\"type\": \"event\"\r\n\t},\r\n\t{\r\n\t\t\"anonymous\": false,\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"payId\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"sender\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"receiver\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"tokenAddress\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"amount\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"deadline\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"PaymentInitiated\",\r\n\t\t\"type\": \"event\"\r\n\t},\r\n\t{\r\n\t\t\"anonymous\": false,\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"payId\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"sender\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"receiver\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"tokenAddress\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"indexed\": false,\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"amount\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"PaymentReverted\",\r\n\t\t\"type\": \"event\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [],\r\n\t\t\"name\": \"acceptOwnership\",\r\n\t\t\"outputs\": [],\r\n\t\t\"stateMutability\": \"nonpayable\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint64\",\r\n\t\t\t\t\"name\": \"_destinationChainSelector\",\r\n\t\t\t\t\"type\": \"uint64\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"allowed\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"allowlistDestinationChain\",\r\n\t\t\"outputs\": [],\r\n\t\t\"stateMutability\": \"nonpayable\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"_sender\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"allowed\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"allowlistSender\",\r\n\t\t\"outputs\": [],\r\n\t\t\"stateMutability\": \"nonpayable\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint64\",\r\n\t\t\t\t\"name\": \"_sourceChainSelector\",\r\n\t\t\t\t\"type\": \"uint64\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"allowed\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"allowlistSourceChain\",\r\n\t\t\"outputs\": [],\r\n\t\t\"stateMutability\": \"nonpayable\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint64\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"uint64\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"allowlistedDestinationChains\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"allowlistedSenders\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint64\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"uint64\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"allowlistedSourceChains\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"_paymentId\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint64\",\r\n\t\t\t\t\"name\": \"_destinationChainSelector\",\r\n\t\t\t\t\"type\": \"uint64\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"deployedReceivingContract\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"string\",\r\n\t\t\t\t\"name\": \"_receivingAddress\",\r\n\t\t\t\t\"type\": \"string\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"calculateOverheadFee\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"overheadFee\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"components\": [\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"bytes32\",\r\n\t\t\t\t\t\t\"name\": \"messageId\",\r\n\t\t\t\t\t\t\"type\": \"bytes32\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"uint64\",\r\n\t\t\t\t\t\t\"name\": \"sourceChainSelector\",\r\n\t\t\t\t\t\t\"type\": \"uint64\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"bytes\",\r\n\t\t\t\t\t\t\"name\": \"sender\",\r\n\t\t\t\t\t\t\"type\": \"bytes\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"bytes\",\r\n\t\t\t\t\t\t\"name\": \"data\",\r\n\t\t\t\t\t\t\"type\": \"bytes\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"components\": [\r\n\t\t\t\t\t\t\t{\r\n\t\t\t\t\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\t\t\t\t\"name\": \"token\",\r\n\t\t\t\t\t\t\t\t\"type\": \"address\"\r\n\t\t\t\t\t\t\t},\r\n\t\t\t\t\t\t\t{\r\n\t\t\t\t\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\t\t\t\t\"name\": \"amount\",\r\n\t\t\t\t\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t\t\t\t\t}\r\n\t\t\t\t\t\t],\r\n\t\t\t\t\t\t\"internalType\": \"struct Client.EVMTokenAmount[]\",\r\n\t\t\t\t\t\t\"name\": \"destTokenAmounts\",\r\n\t\t\t\t\t\t\"type\": \"tuple[]\"\r\n\t\t\t\t\t}\r\n\t\t\t\t],\r\n\t\t\t\t\"internalType\": \"struct Client.Any2EVMMessage\",\r\n\t\t\t\t\"name\": \"message\",\r\n\t\t\t\t\"type\": \"tuple\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"ccipReceive\",\r\n\t\t\"outputs\": [],\r\n\t\t\"stateMutability\": \"nonpayable\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [],\r\n\t\t\"name\": \"currPaymentId\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint8\",\r\n\t\t\t\t\"name\": \"c\",\r\n\t\t\t\t\"type\": \"uint8\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"fromHexChar\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint8\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"uint8\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"pure\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [],\r\n\t\t\"name\": \"getLastReceivedMessageDetails\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bytes32\",\r\n\t\t\t\t\"name\": \"messageId\",\r\n\t\t\t\t\"type\": \"bytes32\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"string\",\r\n\t\t\t\t\"name\": \"text\",\r\n\t\t\t\t\"type\": \"string\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"tokenAddress\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"tokenAmount\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"_receiver\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"getReceivedPayments\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"components\": [\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\t\t\"name\": \"sender\",\r\n\t\t\t\t\t\t\"type\": \"address\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\t\t\"name\": \"receiver\",\r\n\t\t\t\t\t\t\"type\": \"address\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\t\t\"name\": \"tokenAddress\",\r\n\t\t\t\t\t\t\"type\": \"address\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\t\t\"name\": \"amount\",\r\n\t\t\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\t\t\"name\": \"deadline\",\r\n\t\t\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\t\t\"name\": \"claimed\",\r\n\t\t\t\t\t\t\"type\": \"bool\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\t\t\"name\": \"reverted\",\r\n\t\t\t\t\t\t\"type\": \"bool\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\t\t\"name\": \"paymentId\",\r\n\t\t\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t\t\t}\r\n\t\t\t\t],\r\n\t\t\t\t\"internalType\": \"struct ProgrammableTokenTransfers.Payment[]\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"tuple[]\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [],\r\n\t\t\"name\": \"getRouter\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"_sender\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"getSentPayments\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"components\": [\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\t\t\"name\": \"sender\",\r\n\t\t\t\t\t\t\"type\": \"address\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\t\t\"name\": \"receiver\",\r\n\t\t\t\t\t\t\"type\": \"address\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\t\t\"name\": \"tokenAddress\",\r\n\t\t\t\t\t\t\"type\": \"address\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\t\t\"name\": \"amount\",\r\n\t\t\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\t\t\"name\": \"deadline\",\r\n\t\t\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\t\t\"name\": \"claimed\",\r\n\t\t\t\t\t\t\"type\": \"bool\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\t\t\"name\": \"reverted\",\r\n\t\t\t\t\t\t\"type\": \"bool\"\r\n\t\t\t\t\t},\r\n\t\t\t\t\t{\r\n\t\t\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\t\t\"name\": \"paymentId\",\r\n\t\t\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t\t\t}\r\n\t\t\t\t],\r\n\t\t\t\t\"internalType\": \"struct ProgrammableTokenTransfers.Payment[]\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"tuple[]\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"string\",\r\n\t\t\t\t\"name\": \"s\",\r\n\t\t\t\t\"type\": \"string\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"hexStringToAddress\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bytes\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"bytes\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"pure\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [],\r\n\t\t\"name\": \"owner\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"payments\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"sender\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"receiver\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"tokenAddress\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"amount\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"deadline\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"claimed\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"reverted\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"paymentId\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"receivedPayments\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"sender\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"receiver\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"tokenAddress\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"amount\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"deadline\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"claimed\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"reverted\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"paymentId\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"_paymentId\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"revertPayment\",\r\n\t\t\"outputs\": [],\r\n\t\t\"stateMutability\": \"nonpayable\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"_paymentId\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint64\",\r\n\t\t\t\t\"name\": \"_destinationChainSelector\",\r\n\t\t\t\t\"type\": \"uint64\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"deployedReceivingContract\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"string\",\r\n\t\t\t\t\"name\": \"_receivingAddress\",\r\n\t\t\t\t\"type\": \"string\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"sendMessagePayLINK\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bytes32\",\r\n\t\t\t\t\"name\": \"messageId\",\r\n\t\t\t\t\"type\": \"bytes32\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"nonpayable\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"_paymentId\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint64\",\r\n\t\t\t\t\"name\": \"_destinationChainSelector\",\r\n\t\t\t\t\"type\": \"uint64\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"deployedReceivingContract\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"string\",\r\n\t\t\t\t\"name\": \"_receivingAddress\",\r\n\t\t\t\t\"type\": \"string\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"sendMessagePayNative\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bytes32\",\r\n\t\t\t\t\"name\": \"messageId\",\r\n\t\t\t\t\"type\": \"bytes32\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"nonpayable\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"_receiver\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"_amount\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"_timeAhead\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"_tokenAddress\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"sendPayment\",\r\n\t\t\"outputs\": [],\r\n\t\t\"stateMutability\": \"nonpayable\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"sentPayments\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"sender\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"receiver\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"tokenAddress\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"amount\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"deadline\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"claimed\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"reverted\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"paymentId\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bytes4\",\r\n\t\t\t\t\"name\": \"interfaceId\",\r\n\t\t\t\t\"type\": \"bytes4\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"supportsInterface\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"pure\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"string\",\r\n\t\t\t\t\"name\": \"s\",\r\n\t\t\t\t\"type\": \"string\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"toAddress\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"pure\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"to\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"transferOwnership\",\r\n\t\t\"outputs\": [],\r\n\t\t\"stateMutability\": \"nonpayable\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"_beneficiary\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"withdraw\",\r\n\t\t\"outputs\": [],\r\n\t\t\"stateMutability\": \"nonpayable\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"_beneficiary\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"_token\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"withdrawToken\",\r\n\t\t\"outputs\": [],\r\n\t\t\"stateMutability\": \"nonpayable\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"stateMutability\": \"payable\",\r\n\t\t\"type\": \"receive\"\r\n\t}\r\n]"
//            )
//
//            val transaction = ParticleNetwork.evm.createTransaction(
//                ParticleNetwork.getAddress(),
//                senderChainContractAddress,
//                value = null,
//                contractParams
//            )
//
//            ParticleNetwork.signAndSendTransaction(
//                transaction!!.serialize(),
//                object : WebServiceCallback<SignOutput> {
//                    override fun success(output: SignOutput) {
//                        println(output)
//                        onSuccess()
//                    }
//
//                    override fun failure(errMsg: ErrorInfo) {
//                        println(errMsg)
//                        onFailed()
//                    }
//                })
//        } catch (e: Exception) {
//            println(e)
//            showToast(e.message.toString())
//        }
        val function = Function(
            "revertPayment",
            listOf(
                Uint256(paymentId.toLong())
            ),
            emptyList()
        )

        val encodedFunction = FunctionEncoder.encode(function)

        try {
            val txData = ParticleNetwork.evm.createTransaction(
                ParticleNetwork.getAddress(),
                sameChainEscrowContractAddress,
                abiEncodeData = encodedFunction
            )

            ParticleNetwork.signAndSendTransaction(txData!!.serialize(), object : WebServiceCallback<SignOutput> {
                override fun success(output: SignOutput) {
                    println(output)
                    onSuccess()
                }

                override fun failure(errMsg: ErrorInfo) {
                    println(errMsg)
                    onFailed()
                }
            })
        } catch (e: Exception) {
            println(e)
        }
    }

    suspend fun addOrder(
        tokensProvided: String,
        onSuccess: () -> Unit,
        onFailed: () -> Unit,
        showToast: (String) -> Unit
    ) {
//        try {
//            val contractParams = ContractParams.customAbiEncodeFunctionCall(
//                tokenExchangeContractAddress,
//                "addOrder",
//                listOf(
//                    tokensProvidedInWei
//                ),
//                "[\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"tokensProvidedInWei\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"addOrder\",\r\n\t\t\"outputs\": [],\r\n\t\t\"stateMutability\": \"nonpayable\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"_orderId\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address payable\",\r\n\t\t\t\t\"name\": \"fiatProviderAddress\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"releaseOrder\",\r\n\t\t\"outputs\": [],\r\n\t\t\"stateMutability\": \"nonpayable\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"_orderId\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"revertOrder\",\r\n\t\t\"outputs\": [],\r\n\t\t\"stateMutability\": \"nonpayable\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [],\r\n\t\t\"stateMutability\": \"nonpayable\",\r\n\t\t\"type\": \"constructor\"\r\n\t},\r\n\t{\r\n\t\t\"stateMutability\": \"payable\",\r\n\t\t\"type\": \"receive\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [],\r\n\t\t\"name\": \"orderCount\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"name\": \"orders\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"orderId\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address payable\",\r\n\t\t\t\t\"name\": \"tokenProvider\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"tokensProvided\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"createdAt\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"claimed\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"bool\",\r\n\t\t\t\t\"name\": \"reverted\",\r\n\t\t\t\t\"type\": \"bool\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [],\r\n\t\t\"name\": \"owner\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"address\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"address\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t},\r\n\t{\r\n\t\t\"inputs\": [],\r\n\t\t\"name\": \"revertWindow\",\r\n\t\t\"outputs\": [\r\n\t\t\t{\r\n\t\t\t\t\"internalType\": \"uint256\",\r\n\t\t\t\t\"name\": \"\",\r\n\t\t\t\t\"type\": \"uint256\"\r\n\t\t\t}\r\n\t\t],\r\n\t\t\"stateMutability\": \"view\",\r\n\t\t\"type\": \"function\"\r\n\t}\r\n]"
//            )
//
//            val transaction = ParticleNetwork.evm.createTransaction(
//                ParticleNetwork.getAddress(),
//                tokenExchangeContractAddress,
//                value = null,
//                contractParams
//            )
//
//            ParticleNetwork.signAndSendTransaction(
//                transaction!!.serialize(),
//                object : WebServiceCallback<SignOutput> {
//                    override fun success(output: SignOutput) {
//
//                        onSuccess()
//                        CoroutineScope(Dispatchers.IO).launch {
////                            val tx = ParticleNetwork.evm.createTransaction(
////                                ParticleNetwork.getAddress(),
////                                tokenExchangeContractAddress,
////                                "0x$tokensProvidedInWei.toHexString()"
////                            )
////
////                            ParticleNetwork.signAndSendTransaction(tx!!.serialize(), object : WebServiceCallback<SignOutput> {
////                                override fun failure(errMsg: ErrorInfo) {
////                                    onFailed()
////                                }
////
////                                override fun success(output: SignOutput) {
////                                    onSuccess()
////                                }
////
////                            })
//                        }
//
//                    }
//
//                    override fun failure(errMsg: ErrorInfo) {
//                        println(errMsg)
//                        onFailed()
//                    }
//                })
//        } catch (e: Exception) {
//            println(e)
//            showToast(e.message.toString())
//        }
        val amountInValue = tokensProvided.toDouble()
        val amount = (amountInValue * 10.0.pow(18.0)).toLong().toHexString()

        val function = Function(
            "addOrder",
            listOf(),
            emptyList()
        )

        val encodedFunction = FunctionEncoder.encode(function)

        try {
            val txData = ParticleNetwork.evm.createTransaction(
                ParticleNetwork.getAddress(),
                tokenExchangeContractAddress,
                "0x$amount",
                abiEncodeData = encodedFunction
            )

            ParticleNetwork.signAndSendTransaction(txData!!.serialize(), object : WebServiceCallback<SignOutput> {
                override fun success(output: SignOutput) {
                    println(output)
                    onSuccess()
                }

                override fun failure(errMsg: ErrorInfo) {
                    println(errMsg)
                    onFailed()
                }
            })
        } catch (e: Exception) {
            println(e)
        }
    }
}