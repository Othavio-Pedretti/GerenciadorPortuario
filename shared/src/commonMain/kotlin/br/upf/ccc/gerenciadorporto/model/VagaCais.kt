package br.upf.ccc.gerenciadorporto.model

data class VagaCais(val numero: Int, var navio: Navio?) {
    val ocupada: Boolean get() = navio != null
}