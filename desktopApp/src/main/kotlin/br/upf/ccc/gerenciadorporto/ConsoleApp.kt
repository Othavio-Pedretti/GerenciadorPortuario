package br.upf.ccc.gerenciadorporto
import br.upf.ccc.gerenciadorporto.model.*
import br.upf.ccc.gerenciadorporto.services.*
import kotlin.reflect.KClass


fun main() {
    ConsoleApp().run()
}

class ConsoleApp {

    private val vagas = mutableListOf(
        VagaCais(numero = 1, navio = null),
        VagaCais(numero = 2, navio = null)
    )

    private val setoresPatio = mutableListOf(
        SetorPatio(
            id = "P1",
            nome = "Pátio Conteineres",
            capacidadeMaxima = 100_000.0,
            tipoCarga = CargaConteiner::class,
            cargasArmazenadas = mutableListOf()
        ),
        SetorPatio(
            id = "P2",
            nome = "Pátio Granel",
            capacidadeMaxima = 200_000.0,
            tipoCarga = CargaGranel::class,
            cargasArmazenadas = mutableListOf()
        )
    )

    private val navios = mutableListOf<Navio>()

    private val gerenciador = GerenciadorPortuario(vagas, setoresPatio)

    fun run() {
        var opcao: Int?
        while (true) {
            menu()
            opcao = lerOpcao()

            if (opcao == null) {
                println("Encerrando aplicação...")
                break
            }

            when (opcao) {
                1 -> registrarNavio()
                2 -> atracarNavio()
                3 -> descarregarNavio()
                4 -> consultarEstado()
                5 -> registrarSaidaCarga()
                6 -> registrarSaidaNavio()
                0 -> {
                    println("Encerrando aplicação...")
                    break
                }
                else -> println("Opção inválida.")
            }

            println()
        }
    }

    private fun menu() {
        println("      GERENCIADOR PORTUÁRIO    \n")
        println("1 - Registrar navio")
        println("2 - Atracar navio")
        println("3 - Descarregar navio")
        println("4 - Consultar estado do porto")
        println("5 - Registrar saída de carga")
        println("6 - Registrar saída de navio")
        println("0 - Sair")
        print("Escolha uma opção: ")
    }

    private fun lerOpcao(): Int? {
        return readlnOrNull()?.trim()?.toIntOrNull()
    }

    private fun registrarNavio() {
        println("\n- Registrar Navio")

        val navio = Navio(
            id = lerTexto("ID navio: "),
            nome = lerTexto("Nome navio: "),
            cargas = lerCargasDoNavio(),
            status = StatusNavio.ANCORADO
        )

        val sucesso = gerenciador.registrarEntradaNavio(navio)
        if (sucesso) {
            navios.add(navio)
            println("Navio registrado.")
            println(navio)
        } else {
            println("Já existe um navio com esse ID.")
        }
    }

    private fun atracarNavio() {
        println("\n- Atracar Navio")
        val navioId = lerTexto("ID do navio: ")
        val numeroVaga = lerTexto("Número da vaga: ").toIntOrNull() ?: -1

        val sucesso = gerenciador.atracarNavio(navioId, numeroVaga)
        if (sucesso) {
            println("Navio atracado.")
            println(vagas.find { it.numero == numeroVaga })
            println(navios.find { it.id == navioId })
        } else {
            println("Falha ao atracar o navio.")
        }
    }

    private fun descarregarNavio() {
        println("\n- Descarregar Navio")
        val navioId = lerTexto("ID do navio: ")
        gerenciador.descarregarNavio(navioId)
        println("Processo de descarregamento executado.")
        println(navios.find { it.id == navioId })
    }

    private fun consultarEstado() {
        println("\nEstado do Porto")

        println("\n- Vagas de cais: ")
        vagas.forEach { println(it) }

        println("\n- Setores do pátio: ")

        setoresPatio.forEach { println(it) }
        println("\n- Navios registrados: ")

        navios.forEach { println(it) }
    }

    private fun registrarSaidaCarga() {
        println("\nRegistrar Saída de Carga")
        val cargaId = lerTexto("ID da carga: ")
        val sucesso = gerenciador.registrarSaidaCarga(cargaId)

        if (sucesso) {
            println("Carga removida.")
            consultarEstado()
        } else {
            println("Carga não encontrada.")
        }
    }

    private fun registrarSaidaNavio() {
        println("\nRegistrar Saída de Navio")
        val navioId = lerTexto("ID do navio:")
        val sucesso = gerenciador.liberarNavio(navioId)

        if (sucesso) {
            println("Navio saiu do porto.")
            consultarEstado()
        }
        else println("Navio não encontrado.")
    }

    private fun lerTexto(rotulo: String): String {
        print(rotulo)
        return readlnOrNull().orEmpty().trim()
    }

    private fun lerCargasDoNavio(): List<Carga> {
        val cargas = mutableListOf<Carga>()
        val quantidade = lerTexto("Quantidade de cargas do navio: ").toIntOrNull() ?: 0

        repeat(quantidade) { indice ->
            println("\nCarga ${indice + 1}")
            val carga = lerCarga()
            cargas.add(carga)
        }

        return cargas
    }

    private fun lerCarga(): Carga {
        println("Tipo de carga:")
        println("1 - Contêiner")
        println("2 - Granel")

        val carga = when (lerTexto("Escolha: ").toIntOrNull()) {
            1 -> lerCargaConteiner()
            2 -> lerCargaGranel()
            else -> {
                println("Tipo inválido. Criando contêiner padrão.")
                lerCargaConteiner()
            }
        }

        println("Tarifa: ${carga.calcularTarifaBase()} reais")

        return carga
    }

    private fun lerCargaConteiner(): CargaConteiner {
        return CargaConteiner(
            id = lerTexto("ID carga: "),
            nome = lerTexto("Nome da carga: "),
            qtdContaineres = lerTexto("Quantidade de Containeres: ").toIntOrNull() ?: 0,
            tamanho = lerTexto("Tamanho dos containeres (6 ou 12): ").toIntOrNull() ?: 6,
            destinatario = lerTexto("Destinatário: "),
            destino = lerTexto("Destino: "),
            metodoTransporte = lerMetodoTransporte(),
            diasNoPatio = lerTexto("Dias no pátio: ").toIntOrNull() ?: 0,
            tipo = lerTipoConteiner()
        )
    }

    private fun lerCargaGranel(): CargaGranel {
        return CargaGranel(
            id = lerTexto("ID carga: "),
            nome = lerTexto("Nome da carga: "),
            volume = lerTexto("Volume (m³): ").toDoubleOrNull() ?: 0.0,
            destinatario = lerTexto("Destinatário: "),
            destino = lerTexto("Destino: "),
            metodoTransporte = lerMetodoTransporte(),
            tipoGranel = lerTipoGranel()
        )
    }

    private fun lerTipoGranel(): TipoGranel {
        println("Tipos de granel:")
        TipoGranel.entries.forEachIndexed { index, tipo ->
            println("${index + 1} - $tipo")
        }

        while (true) {
            val opcao = lerTexto("Escolha: ").toIntOrNull()
            if (opcao != null && opcao in 1..TipoGranel.entries.size) {
                return TipoGranel.entries[opcao - 1]
            }
            println("Tipo inválido, tente novamente.")
        }
    }

    private fun lerMetodoTransporte(): MetodoTransporte {
        println("Método de transporte:")
        MetodoTransporte.entries.forEachIndexed { index, tipo ->
            println("${index + 1} - $tipo")
        }

        while (true) {
            val opcao = lerTexto("Escolha: ").toIntOrNull()
            if (opcao != null && opcao in 1..MetodoTransporte.entries.size) {
                return MetodoTransporte.entries[opcao - 1]
            }
            println("Método inválido, tente novamente.")
        }
    }

    private fun lerTipoConteiner(): TipoConteiner {
        println("Tipo de contêiner:")
        TipoConteiner.entries.forEachIndexed { index, tipo ->
            println("${index + 1} - $tipo")
        }

        while (true) {
            val opcao = lerTexto("Escolha: ").toIntOrNull()
            if (opcao != null && opcao in 1..TipoConteiner.entries.size) {
                return TipoConteiner.entries[opcao - 1]
            }
            println("Tipo inválido, tente novamente.")
        }
    }
}