package br.upf.ccc.gerenciadorporto.model

data class Navio (val id: String, var nome: String, val cargas: List<Carga>, var status: StatusNavio)

enum class StatusNavio {
    ATRACADO,
    ANCORADO,
    AUSENTE,
    DESCARREGADO
}