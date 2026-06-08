package br.upf.ccc.gerenciadorporto.model

data class VagaCais(val numero: Int, val tiposPermitidos: Set<TipoNavio>, var navio: Navio?) {
    val ocupada: Boolean get() = navio != null
}