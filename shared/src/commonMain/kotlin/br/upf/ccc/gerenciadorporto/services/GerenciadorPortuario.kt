package br.upf.ccc.gerenciadorporto.services

import br.upf.ccc.gerenciadorporto.model.Navio
import br.upf.ccc.gerenciadorporto.model.SetorPatio
import br.upf.ccc.gerenciadorporto.model.TipoNavio
import br.upf.ccc.gerenciadorporto.model.VagaCais

class GerenciadorPortuario(val vagas: List<VagaCais>, val setoresPatio: List<SetorPatio>) {
    private val naviosNoPorto = mutableListOf<Navio>()

    fun registrarEntradaNavio(navio: Navio): Boolean {
        if ( naviosNoPorto.any { it.id == navio.id } ) return false
        naviosNoPorto.add(navio)
        return true
    }

    fun atracarNavio(navioId: String, numeroVaga: Int): Boolean {
        val navio = naviosNoPorto.find { it.id == navioId } ?: return false
        val vaga = vagas.find { it.numero == numeroVaga } ?: return false

        if ( vaga.ocupada || !vaga.tiposPermitidos.contains(TipoNavio.valueOf(navio.categoria.name)) )
            return false

        vaga.navio = navio
        return true
    }

    fun descarregarNavio(navioId: String) {
        val navio = naviosNoPorto.find { it.id == navioId } ?: return
        val vaga = vagas.find { it.navio?.id == navioId } ?: return
        vaga.navio = null

        navio.cargas.forEach { carga ->
            val setor = setoresPatio.find { it.tipoCarga.isInstance(carga) }
            setor?.alocarCarga(carga)
            val tarifa = CalculadoraTarifa.calcularTotal(carga)
            println("Tarifa para carga ${carga.id}: $tarifa")
        }
    }

    fun registrarSaidaCarga(cargaId: String): Boolean {
        for (setor in setoresPatio) {
            val carga = setor.cargasArmazenadas.find { it.id == cargaId } ?: continue
            setor.cargasArmazenadas.remove(carga)
            return true
        }
        return false
    }

}