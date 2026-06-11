package br.upf.ccc.gerenciadorporto.model

import kotlin.reflect.KClass

class SetorPatio(val id: String, val nome: String, val capacidadeMaxima: Double, val tipoCarga: KClass<out Carga>, val cargasArmazenadas: MutableList<Carga>) {
    val ocupacaoAtual: Double get() = cargasArmazenadas.sumOf { it.volume }
    var qtdContaineres: Int? = null

    fun alocarCarga(carga: Carga): Boolean {
        if (tipoCarga.isInstance(carga) && ocupacaoAtual + carga.volume <= capacidadeMaxima) {
            cargasArmazenadas.add(carga)
            if (carga is CargaConteiner)
                qtdContaineres = (qtdContaineres ?: 0) + carga.qtdContaineres

            return true
        }
        return false
    }

    override fun toString(): String {
        return "SetorPatio(id=$id, nome=$nome, capacidadeMaxima=$capacidadeMaxima, tipoCarga=${tipoCarga.simpleName}, cargasArmazenadas=$cargasArmazenadas, ocupacaoAtual=$ocupacaoAtual, qtdContaineres=$qtdContaineres)"
    }
}