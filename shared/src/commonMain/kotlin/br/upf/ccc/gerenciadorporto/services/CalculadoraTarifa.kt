package br.upf.ccc.gerenciadorporto.services

import br.upf.ccc.gerenciadorporto.model.Carga
import br.upf.ccc.gerenciadorporto.model.CargaConteiner
import br.upf.ccc.gerenciadorporto.model.CargaGranel
import br.upf.ccc.gerenciadorporto.model.TipoGranel

object CalculadoraTarifa {
    fun calcularTotal(carga: Carga): Double {
        val tarifaBase = carga.calcularTarifaBase()
        val taxaExtra = when (carga) {
            is CargaConteiner -> if (carga.tamanho == 12) 200.0 else 0.0
            is CargaGranel -> if (carga.tipoGranel == TipoGranel.MINERIO) 300.0 else 0.0
            else -> 0.0
        }
        return tarifaBase + taxaExtra
    }
}