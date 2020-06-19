package com.kevalpatel2106.fxratesample.repo

import com.kevalpatel2106.fxratesample.entity.Currency
import com.kevalpatel2106.fxratesample.repo.dto.CurrencyListDtoMapper
import com.kevalpatel2106.fxratesample.repo.network.CurrencyListApi
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface CurrencyListRepository {

    fun monitorCurrencyList(
        baseCurrency: String = BASE_CURRENCY,
        intervalInMills: Long = FX_RATE_UPDATE_INTERVAL
    ): Flowable<List<Currency>>

    companion object {
        private const val BASE_CURRENCY = "EUR"
        private const val FX_RATE_UPDATE_INTERVAL = 1000L  // MILLISECONDS
    }
}

class CurrencyListRepositoryImpl @Inject constructor(
    private val currencyListApi: CurrencyListApi,
    private val currencyListDtoMapper: CurrencyListDtoMapper
) : CurrencyListRepository {

    override fun monitorCurrencyList(
        baseCurrency: String,
        intervalInMills: Long
    ): Flowable<List<Currency>> {
        return currencyListApi.getListOfBaseCurrency(baseCurrency)
            .repeatWhen { it.delay(intervalInMills, TimeUnit.MILLISECONDS) }
            .map(currencyListDtoMapper::toEntity)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}
