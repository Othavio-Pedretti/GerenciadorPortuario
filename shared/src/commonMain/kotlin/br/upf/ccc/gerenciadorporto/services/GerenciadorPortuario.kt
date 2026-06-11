package br.upf.ccc.gerenciadorporto.services

import br.upf.ccc.gerenciadorporto.model.*

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

        if (navio.status == StatusNavio.ATRACADO) return false
        if (vaga.ocupada) return false

        navio.status = StatusNavio.ATRACADO
        vaga.navio = navio
        return true
    }

    fun descarregarNavio(navioId: String) {
        val navio = naviosNoPorto.find { it.id == navioId } ?: return

        navio.cargas.forEach { carga ->
            val setor = setoresPatio.find { it.tipoCarga.isInstance(carga) }
            setor?.alocarCarga(carga)
        }

        navio.status = StatusNavio.DESCARREGADO
    }

    fun liberarNavio(navioId: String): Boolean {
        val navio: Navio = naviosNoPorto.find { it.id == navioId } ?: return false
        val vaga = vagas.find { it.navio?.id == navioId } ?: return false

        navio.status = StatusNavio.AUSENTE
        vaga.navio = null
        return true
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