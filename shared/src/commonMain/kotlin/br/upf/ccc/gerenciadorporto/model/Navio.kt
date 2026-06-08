package br.upf.ccc.gerenciadorporto.model

data class Navio (val id: String, var nome: String, val categoria: TipoNavio, val cargas: List<Carga>, val status: StatusNavio)

enum class StatusNavio {
    ATRACADO,
    EM_MANOBRA,
    ANCORADO,
    EM_TRAVESSIA
}

enum class TipoNavio {
    PORTA_CONTAINER,
    GRANELEIRO,
    CARGA_GERAL,
    CRUZEIRO
}