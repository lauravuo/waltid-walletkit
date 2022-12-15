package id.walt.gateway.providers.metaco.restapi

import id.walt.gateway.dto.AssetParameter
import id.walt.gateway.dto.TickerData
import id.walt.gateway.dto.TickerParameter
import id.walt.gateway.dto.ValueWithChange
import id.walt.gateway.providers.metaco.CoinMapper.map
import id.walt.gateway.providers.metaco.repositories.TickerRepository
import id.walt.gateway.providers.metaco.restapi.ticker.model.Ticker
import id.walt.gateway.usecases.CoinUseCase
import id.walt.gateway.usecases.LogoUseCase
import id.walt.gateway.usecases.TickerUseCase

class TickerUseCaseImpl(
    private val tickerRepository: TickerRepository,
    private val coinUseCase: CoinUseCase,
    private val logoUseCase: LogoUseCase,
) : TickerUseCase {
    override fun get(parameter: TickerParameter): Result<TickerData> = runCatching {
        tickerRepository.findById(parameter.id).let {
            buildTickerData(it, parameter.currency)
        }
    }

    override fun list(currency: String): Result<List<TickerData>> = runCatching {
        tickerRepository.findAll(emptyMap()).items.map {
            buildTickerData(it, currency)
        }
    }

    private fun buildTickerData(ticker: Ticker, currency: String) = TickerData(
        id = ticker.data.id,
        kind = ticker.data.kind,
        chain = ticker.data.ledgerId,
        imageUrl = logoUseCase.get(AssetParameter(ticker.data.name, ticker.data.symbol)).data,
        name = ticker.data.name,
        price = coinUseCase.metadata(ticker.map(currency)).fold(
            onSuccess = {
                ValueWithChange(it.price, it.change, currency)
            }, onFailure = {
                ValueWithChange()
            }),
        decimals = ticker.data.decimals,
        symbol = ticker.data.symbol,
        maxFee = 1000,
    )
}