#!/usr/bin/env kotlin

class Navio (val id: Int, var nome: String) {
    val categoria: TipoNavio
    val carga: Carga
}

enum class TipoNavio {
    PORTA_CONTAINER,
    GRANELEIRO,
    TANQUE,
    RO_RO,
    CARGA_GERAL,
    CRUZEIRO
}