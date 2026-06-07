package br.upf.ccc.gerenciadorporto.model

abstract class Carga(
    val id: String,
    val peso: Double,
    val volume: Double,
    val destinatario: String,
    val destino: String, val
    metodoTransporte: MetodoTransporte
) {
    abstract fun calcularTarifaBase(): Double
}

enum class TipoConteiner { PADRAO, REFRIGERADO, PERIGOSO }

class CargaConteiner(
    id: String,
    peso: Double,
    volume: Double,
    destinatario: String,
    destino: String,
    metodoTransporte: MetodoTransporte,
    val tamanho: Int, // 6 ou 12 metros
    val tipo: TipoConteiner,
    val diasNoPatio: Int
) : Carga(id, peso, volume, destinatario, destino, metodoTransporte) {

    override fun calcularTarifaBase(): Double {
        // base: 6m = 1, 12m = 2
        val mult = if (tamanho == 12) 2.0 else 1.0
        val taxaMovimentacaoBase = 500.0 * mult

        val taxaAdicional = when (tipo) {
            TipoConteiner.REFRIGERADO -> 300.0 // energia
            TipoConteiner.PERIGOSO ->   450.0  // segurança
            TipoConteiner.PADRAO ->     0.0
        }

        val taxaArmazenamento = diasNoPatio * 120.0

        return taxaMovimentacaoBase + taxaAdicional + taxaArmazenamento
    }
}

class CargaGranel(
    id: String,
    peso: Double,
    volume: Double,
    destinatario: String,
    destino: String,
    metodoTransporte: MetodoTransporte,
    val tipoGranel: String
) : Carga(id, peso, volume, destinatario, destino, metodoTransporte) {

    override fun calcularTarifaBase(): Double {
        val taxaPorTonelada = 15.0
        return peso * taxaPorTonelada
    }
}

enum class MetodoTransporte {
    MARITIMO,
    FERROVIARIO,
    RODOVIARIO
}