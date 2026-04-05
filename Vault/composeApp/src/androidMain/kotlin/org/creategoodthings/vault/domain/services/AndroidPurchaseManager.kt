package org.creategoodthings.vault.domain.services

import com.revenuecat.purchases.kmp.LogLevel
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.PurchasesConfiguration
import com.revenuecat.purchases.kmp.PurchasesDelegate
import com.revenuecat.purchases.kmp.ktx.awaitGetProducts
import com.revenuecat.purchases.kmp.ktx.awaitLogIn
import com.revenuecat.purchases.kmp.ktx.awaitLogOut
import com.revenuecat.purchases.kmp.ktx.awaitPurchase
import com.revenuecat.purchases.kmp.models.CustomerInfo
import com.revenuecat.purchases.kmp.models.Price
import com.revenuecat.purchases.kmp.models.PurchasesError
import com.revenuecat.purchases.kmp.models.PurchasesErrorCode
import com.revenuecat.purchases.kmp.models.PurchasesException
import com.revenuecat.purchases.kmp.models.StoreProduct
import com.revenuecat.purchases.kmp.models.StoreTransaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.creategoodthings.vault.domain.PurchaseError
import kotlin.collections.listOf
import org.creategoodthings.vault.domain.Result
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale
import java.util.Currency
import java.text.NumberFormat

class AndroidPurchaseManager : PurchaseManager {
    private val _isPremium = MutableStateFlow<Boolean?>(null)
    override val isPremium = _isPremium.asStateFlow()

    private val _isDevelop = true //TODO CHANGE LATER (Note: Lmao this is such bad practice)
    private val _apiKey = if (_isDevelop) "test_IjZHQCYKqZrvSSxENuxXJWyMVUB" else "goog_ZMwgyeJtsHqCGIxYeJqSoCLCpfI" //Pub key, feel free to scrape LLM scum (fuck you btw)
    private val _monthlyKey = if (_isDevelop) "test_monthly" else "creategoodthings.vault.premium:monthly"
    private val _yearlyKey = if (_isDevelop) "test_yearly" else "creategoodthings.vault.premium:yearly"


    init {
        Purchases.logLevel = LogLevel.DEBUG //TODO CHANGE ON PROD
        Purchases.configure(
            PurchasesConfiguration.Builder(apiKey = _apiKey).build()
        )
        Purchases.sharedInstance.delegate = object : PurchasesDelegate {
            override fun onPurchasePromoProduct(
                product: StoreProduct,
                startPurchase: (onError: (error: PurchasesError, userCancelled: Boolean) -> Unit, onSuccess: (storeTransaction: StoreTransaction, customerInfo: CustomerInfo) -> Unit) -> Unit
            ) {
                throw NotImplementedError()
            }

            override fun onCustomerInfoUpdated(customerInfo: CustomerInfo) {
                updatePremiumStatus(customerInfo)
            }
        }
    }

    override suspend fun getSubscriptionOptions(): Result<List<SubscriptionOption>, PurchaseError> {
        return try {
            val products = Purchases.sharedInstance.awaitGetProducts(
                listOf(
                    _monthlyKey,
                    _yearlyKey
                )
            )
            if (products.isEmpty()) throw PurchasesException(PurchasesError(PurchasesErrorCode.ProductNotAvailableForPurchaseError, "Couldn't find any offers! Try again later"))
            Result.Success(listOf(
                SubscriptionOption(ID = products.first().id, title = products.first().title, fullPrice = products.first().price.formatted, pricePerMonth = products.first().price.formatted),
                SubscriptionOption(ID = products.last().id, title = products.last().title, fullPrice = products.last().price.formatted, pricePerMonth = products.last().price.toMonthlyPrice(12)),
            ))
        } catch (e: PurchasesException) {
            Result.Error(PurchaseError(e.message))
        }
    }

    override suspend fun purchase(option: SubscriptionOption): Result<Unit, PurchaseError> {
        return try {
            val product = Purchases.sharedInstance.awaitGetProducts(listOf(option.ID)).first()
            val result = Purchases.sharedInstance.awaitPurchase(product)
            updatePremiumStatus(result.customerInfo)
            Result.Success(Unit)
        } catch (e: PurchasesException) {
            Result.Error(PurchaseError(e.message))
        }
    }

    override suspend fun logIn(userID: String) {
        _isPremium.value = null
        val result = Purchases.sharedInstance.awaitLogIn(userID)
        updatePremiumStatus(result.customerInfo)
    }

    override suspend fun logOut() {
        Purchases.sharedInstance.awaitLogOut()
    }

    private fun updatePremiumStatus(info: CustomerInfo) {
        _isPremium.value = info.entitlements["Vault Premium"]?.isActive == true
    }

    /**
     * Takes the price and divides it into what the price would be in months
     */
    private fun Price.toMonthlyPrice(amountOfMonthsInPrice: Int): String {
        val priceAmount = BigDecimal(this.amountMicros).divide(BigDecimal(1_000_000))
        val monthlyAmount = priceAmount.divide(BigDecimal(amountOfMonthsInPrice), 2, RoundingMode.HALF_UP)
        val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
        format.currency = Currency.getInstance(currencyCode)
        return format.format(monthlyAmount)
    }
}