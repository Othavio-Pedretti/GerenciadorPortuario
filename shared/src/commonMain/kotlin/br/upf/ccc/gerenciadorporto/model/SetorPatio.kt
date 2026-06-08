package br.upf.ccc.gerenciadorporto.model

class SetorPatio(val id: String, val nome: String, val capacidadeMaxima: Double, val tipoCarga: Class<out Carga>, val cargasArmazenadas: MutableList<Carga>) {
    val ocupacaoAtual: Double get() = cargasArmazenadas.sumOf { it.peso }

    fun alocarCarga(carga: Carga): Boolean {
        if (carga.javaClass == tipoCarga && ocupacaoAtual + carga.peso <= capacidadeMaxima) {
            cargasArmazenadas.add(carga)
            return true
        }
        return false
    }
}