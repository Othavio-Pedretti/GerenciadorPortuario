package br.upf.ccc.gerenciadorporto
import br.upf.ccc.gerenciadorporto.model.*
import br.upf.ccc.gerenciadorporto.services.*


fun main() {
    ConsoleApp().run()
}

class ConsoleApp {

    private val vagas = mutableListOf(
        VagaCais(numero = 1, tiposPermitidos = setOf(TipoNavio.PORTA_CONTAINER, TipoNavio.CARGA_GERAL), navio = null),
        VagaCais(numero = 2, tiposPermitidos = setOf(TipoNavio.GRANELEIRO), navio = null)
    )

    private val setoresPatio = mutableListOf(
        SetorPatio(
            id = "P1",
            nome = "Pátio Conteineres",
            capacidadeMaxima = 100_000.0,
            tipoCarga = CargaConteiner::class.java,
            cargasArmazenadas = mutableListOf()
        ),
        SetorPatio(
            id = "P2",
            nome = "Pátio Granel",
            capacidadeMaxima = 200_000.0,
            tipoCarga = CargaGranel::class.java,
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
            categoria = lerTipoNavio(),
            cargas = lerCargasDoNavio(),
            status = StatusNavio.ANCORADO
        )

        val sucesso = gerenciador.registrarEntradaNavio(navio)
        if (sucesso) {
            navios.add(navio)
            println("Navio registrado.")
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
        } else {
            println("Falha ao atracar o navio.")
        }
    }

    private fun descarregarNavio() {
        println("\n- Descarregar Navio")
        val navioId = lerTexto("ID do navio: ")
        gerenciador.descarregarNavio(navioId)
        println("Processo de descarregamento executado.")
    }

    private fun consultarEstado() {
        println("\nEstado do Porto")

        println("\n- Vagas de cais: ")
        vagas.forEach { vaga ->
            val navioAtual = vaga.navio?.nome ?: "Vazia"
            println("Vaga ${vaga.numero} | Ocupada: ${vaga.ocupada} | Navio: $navioAtual")
        }

        println("\n- Setores do pátio: ")
        setoresPatio.forEach { setor ->
            println(
                "${setor.nome} | Ocupação: ${setor.ocupacaoAtual}/${setor.capacidadeMaxima} | Cargas: ${setor.cargasArmazenadas.size}"
            )
        }

        println("\n- Navios registrados: ")
        navios.forEach { navio ->
            println("${navio.id} - ${navio.nome} | Categoria: ${navio.categoria} | Status: ${navio.status}")
        }
    }

    private fun registrarSaidaCarga() {
        println("\nRegistrar Saída de Carga")
        val cargaId = lerTexto("ID da carga: ")
        val sucesso = gerenciador.registrarSaidaCarga(cargaId)

        if (sucesso) {
            println("Carga removida.")
        } else {
            println("Carga não encontrada.")
        }
    }

    private fun lerTexto(rotulo: String): String {
        print("$rotulo: ")
        return readlnOrNull().orEmpty().trim()
    }

    private fun lerTipoNavio(): TipoNavio {
        println("Tipos disponíveis:")
        TipoNavio.entries.forEachIndexed { index, tipo ->
            println("${index + 1} - $tipo")
        }

        while (true) {
            val opcao = lerTexto("Escolha o tipo do navio: ").toIntOrNull()
            if (opcao != null && opcao in 1..TipoNavio.entries.size) {
                return TipoNavio.entries[opcao - 1]
            }
            println("Tipo inválido, tente novamente.")
        }
    }

    private fun lerCargasDoNavio(): List<Carga> {
        val cargas = mutableListOf<Carga>()
        val quantidade = lerTexto("Quantidade de cargas do navio").toIntOrNull() ?: 0

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

        return when (lerTexto("Escolha").toIntOrNull()) {
            1 -> lerCargaConteiner()
            2 -> lerCargaGranel()
            else -> {
                println("Tipo inválido. Criando contêiner padrão.")
                lerCargaConteiner()
            }
        }
    }

    private fun lerCargaConteiner(): CargaConteiner {
        return CargaConteiner(
            id = lerTexto("ID carga: "),
            peso = lerTexto("Peso: ").toDoubleOrNull() ?: 0.0,
            volume = lerTexto("Volume: ").toDoubleOrNull() ?: 0.0,
            destinatario = lerTexto("Destinatário: "),
            destino = lerTexto("Destino: "),
            metodoTransporte = lerMetodoTransporte(),
            tamanho = lerTexto("Tamanho (6 ou 12): ").toIntOrNull() ?: 6,
            tipo = lerTipoConteiner(),
            diasNoPatio = lerTexto("Dias no pátio: ").toIntOrNull() ?: 0
        )
    }

    private fun lerCargaGranel(): CargaGranel {
        return CargaGranel(
            id = lerTexto("ID carga: "),
            peso = lerTexto("Peso: ").toDoubleOrNull() ?: 0.0,
            volume = lerTexto("Volume: ").toDoubleOrNull() ?: 0.0,
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