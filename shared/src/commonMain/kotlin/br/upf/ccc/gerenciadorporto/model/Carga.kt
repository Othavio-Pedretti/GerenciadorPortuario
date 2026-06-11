package br.upf.ccc.gerenciadorporto.model

abstract class Carga(
    val id: String,
    val nome: String,
    val destino: String,
    val destinatario: String,
    val metodoTransporte: MetodoTransporte
) {
    abstract val volume: Double
    abstract fun calcularTarifaBase(): Double

    override fun toString(): String {
        return "id=$id, nome=$nome, destino=$destino, destinatario=$destinatario, metodoTransporte=$metodoTransporte, volume=$volume"
    }
}

class CargaConteiner(
    id: String,
    nome: String,
    destino: String,
    destinatario: String,
    metodoTransporte: MetodoTransporte,
    val tamanho: Int, // 6 ou 12 metros
    val tipo: TipoConteiner,
    val diasNoPatio: Int,
    val qtdContaineres: Int
) : Carga(id, nome, destino, destinatario, metodoTransporte) {
    override val volume: Double get() = if (tamanho == 12) 30.0 * qtdContaineres else (15.0 * qtdContaineres)
    // container de 12m -> 30m³
    // container de 6m -> 15m³

    override fun toString(): String {
        return "id=$id, nome=$nome, destino=$destino, destinatario=$destinatario, metodoTransporte=$metodoTransporte, volume=$volume m³, tamanho=$tamanho m, tipo=$tipo, diasNoPatio=$diasNoPatio, qtdContaineres=$qtdContaineres"
    }

    override fun calcularTarifaBase(): Double {
        // base: 6m = 1, 12m = 2
        val mult = if (tamanho == 12) 2.0 else 1.0
        val taxaMovimentacaoBase = 500.0 * mult * qtdContaineres

        val taxaAdicional = when (tipo) {
            TipoConteiner.REFRIGERADO -> 300.0 * qtdContaineres // energia
            TipoConteiner.PERIGOSO ->   400.0 * qtdContaineres // segurança
            TipoConteiner.PADRAO ->     0.0
        }

        val taxaArmazenamento = diasNoPatio * 500.0 * qtdContaineres

        return taxaMovimentacaoBase + taxaAdicional + taxaArmazenamento
    }
}

enum class TipoConteiner { PADRAO, REFRIGERADO, PERIGOSO }


class CargaGranel(
    id: String,
    nome: String,
    destino: String,
    destinatario: String,
    metodoTransporte: MetodoTransporte,
    override var volume: Double,
    val tipoGranel: TipoGranel
) : Carga(id, nome, destino, destinatario, metodoTransporte) {

    override fun calcularTarifaBase(): Double {
        val mult: Double = when(tipoGranel){
            TipoGranel.MINERIO -> 3.0
            TipoGranel.LIQUIDO -> 2.0
            TipoGranel.GRAOS -> 1.0
        }

        val taxaPorMetroCub = 50;
        return volume * taxaPorMetroCub * mult
    }

    override fun toString(): String {
        return "id=$id, nome=$nome, destino=$destino, destinatario=$destinatario, metodoTransporte=$metodoTransporte, volume=$volume m³, tipoGranel=$tipoGranel"
    }
}

enum class TipoGranel { GRAOS, MINERIO, LIQUIDO }
enum class MetodoTransporte { MARITIMO, FERROVIARIO, RODOVIARIO }