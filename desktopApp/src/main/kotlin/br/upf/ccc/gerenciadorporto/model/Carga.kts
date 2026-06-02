#!/usr/bin/env kotlin

class Carga (val id: Int) {
    val tipoCarga: TipoCarga
    var quantidade: Int
}

enum class TipoCarga(val tarifaBase: Double) {
    GRAOS(1),
    CONTAINERES(1),
    VEICULOS(1),
    PASSAGEIROS(1),
    COMBUSTIVEL(1),
    OUTROS(1)
}