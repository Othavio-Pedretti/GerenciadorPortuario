package br.upf.ccc.gerenciadorporto

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import br.upf.ccc.gerenciadorporto.model.*

@Composable
fun App() {
    // --- CONTROLE DE ABAS ---
    var abaSelecionada by remember { mutableStateOf(0) }

    // --- ESTADOS DOS PÁTIOS REATIVOS ---
    val cargasPatioConteiner = remember { mutableStateListOf<Carga>() }
    val cargasPatioGranel = remember { mutableStateListOf<Carga>() }

    // Atualizado para usar KClass (::class) conforme a tua nova classe SetorPatio
    val patioConteiner = remember {
        SetorPatio("P-CONT", "Pátio de Contêineres", 5000.0, CargaConteiner::class, cargasPatioConteiner)
    }
    val patioGranel = remember {
        SetorPatio("P-GRAN", "Pátio de Granéis", 20000.0, CargaGranel::class, cargasPatioGranel)
    }

    // --- ESTADOS DO FORMULÁRIO PRINCIPAL ---
    var idNavio by remember { mutableStateOf("") }
    var nomeNavio by remember { mutableStateOf("") } // Novo campo do construtor
    var nomeCarga by remember { mutableStateOf("") } // Novo campo do construtor
    var destinatario by remember { mutableStateOf("") }
    var destino by remember { mutableStateOf("") }
    var statusNavioSelecionado by remember { mutableStateOf(StatusNavio.ANCORADO) }
    var mensagemFeedback by remember { mutableStateOf("") }

    // Opções Principais de Carga
    val tiposCarga = listOf("Conteiner", "Granel")
    var tipoCargaSelecionado by remember { mutableStateOf(tiposCarga[0]) }

    // --- ESTADOS ESPECÍFICOS PARA CONTÊINER ---
    var tipoConteinerSelecionado by remember { mutableStateOf("Comum") }
    var tamanhoConteinerSelecionado by remember { mutableStateOf(6) }
    var quantidadeConteinersStr by remember { mutableStateOf("1") }
    var diasPatioStr by remember { mutableStateOf("") }

    // --- ESTADOS ESPECÍFICOS PARA GRANEL ---
    var tipoGranelSelecionado by remember { mutableStateOf("Grãos") }
    var volumeGranelStr by remember { mutableStateOf("") } // Substituiu o peso

    // A lista reativa global de Navios no porto
    val listaNavios = remember { mutableStateListOf<Navio>() }

    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

            Text(
                text = "Gerenciador Portuário",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            // --- BARRA DE ABAS ---
            TabRow(selectedTabIndex = abaSelecionada) {
                Tab(selected = abaSelecionada == 0, onClick = { abaSelecionada = 0 }, text = { Text("Gerenciador") })
                Tab(selected = abaSelecionada == 1, onClick = { abaSelecionada = 1 }, text = { Text("Visualizar Pátios") })
            }
            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // ABA 0: GERENCIADOR PRINCIPAL
            // ==========================================
            if (abaSelecionada == 0) {
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

                    // LINHA 1 DE INPUTS: ID e Nome do Navio
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = idNavio, onValueChange = { idNavio = it }, label = { Text("ID do Navio") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = nomeNavio, onValueChange = { nomeNavio = it }, label = { Text("Nome do Navio") }, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // LINHA 2 DE INPUTS: Nome Carga, Destinatário e Destino
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = nomeCarga, onValueChange = { nomeCarga = it }, label = { Text("Nome da Carga") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = destinatario, onValueChange = { destinatario = it }, label = { Text("Destinatário") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = destino, onValueChange = { destino = it }, label = { Text("Destino") }, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // SELEÇÃO DO STATUS DO NAVIO
                    Text("Status do Navio:", style = MaterialTheme.typography.titleSmall)
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        StatusNavio.values().forEach { status ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 12.dp)) {
                                RadioButton(selected = (statusNavioSelecionado == status), onClick = { statusNavioSelecionado = status })
                                Text(status.name, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // SELEÇÃO DO TIPO DE CARGA
                    Text("Tipo de Carga:", style = MaterialTheme.typography.titleSmall)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        tiposCarga.forEach { tipo ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 12.dp)) {
                                RadioButton(selected = (tipoCargaSelecionado == tipo), onClick = { tipoCargaSelecionado = tipo })
                                Text(tipo, modifier = Modifier.padding(start = 4.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // CAMPOS DINÂMICOS (Cores removidas do Card conforme pedido)
                    if (tipoCargaSelecionado == "Conteiner") {
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Opções do Contêiner:", style = MaterialTheme.typography.titleSmall)
                                Spacer(modifier = Modifier.height(4.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    listOf("Comum", "Refrigerado", "Perigoso").forEach { tipo ->
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 12.dp)) {
                                            RadioButton(selected = (tipoConteinerSelecionado == tipo), onClick = { tipoConteinerSelecionado = tipo })
                                            Text(tipo, modifier = Modifier.padding(start = 4.dp))
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))

                                Text("Tamanho do Contêiner:", style = MaterialTheme.typography.labelMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                    val selecionou6 = tamanhoConteinerSelecionado == 6
                                    Button(
                                        onClick = { tamanhoConteinerSelecionado = 6 },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (selecionou6) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = if (selecionou6) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("6 Metros")
                                    }

                                    val selecionou12 = tamanhoConteinerSelecionado == 12
                                    Button(
                                        onClick = { tamanhoConteinerSelecionado = 12 },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (selecionou12) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = if (selecionou12) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("12 Metros")
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(value = quantidadeConteinersStr, onValueChange = { quantidadeConteinersStr = it }, label = { Text("Qtd de Contêiners") }, modifier = Modifier.weight(1f))
                                    OutlinedTextField(value = diasPatioStr, onValueChange = { diasPatioStr = it }, label = { Text("Dias no Pátio") }, modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    } else if (tipoCargaSelecionado == "Granel") {
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Opções de Granel:", style = MaterialTheme.typography.titleSmall)
                                Spacer(modifier = Modifier.height(4.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    listOf("Grãos", "Minério", "Líquido").forEach { tipo ->
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 12.dp)) {
                                            RadioButton(selected = (tipoGranelSelecionado == tipo), onClick = { tipoGranelSelecionado = tipo })
                                            Text(tipo, modifier = Modifier.padding(start = 4.dp))
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(value = volumeGranelStr, onValueChange = { volumeGranelStr = it }, label = { Text("Volume (m³)") }, modifier = Modifier.fillMaxWidth())
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (mensagemFeedback.isNotBlank()) {
                        Text(text = mensagemFeedback, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 4.dp))
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // --- BOTÕES DE AÇÃO ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                if (idNavio.isNotBlank()) {
                                    val qtd = quantidadeConteinersStr.toIntOrNull() ?: 1
                                    val dias = diasPatioStr.toIntOrNull() ?: 0

                                    // Utilizando os novos construtores atualizados do ficheiro Carga.kt
                                    val carga: Carga = if (tipoCargaSelecionado == "Conteiner") {
                                        val tipoEnum = when (tipoConteinerSelecionado) {
                                            "Refrigerado" -> TipoConteiner.REFRIGERADO
                                            "Perigoso" -> TipoConteiner.PERIGOSO
                                            else -> TipoConteiner.PADRAO
                                        }
                                        CargaConteiner(
                                            id = "C-$idNavio",
                                            nome = nomeCarga.ifEmpty { "Carga $idNavio" },
                                            destino = destino,
                                            destinatario = destinatario,
                                            metodoTransporte = MetodoTransporte.MARITIMO,
                                            tamanho = tamanhoConteinerSelecionado,
                                            tipo = tipoEnum,
                                            diasNoPatio = dias,
                                            qtdContaineres = qtd
                                        )
                                    } else {
                                        val volume = volumeGranelStr.toDoubleOrNull() ?: 0.0
                                        val tipoGranelEnum = when (tipoGranelSelecionado) {
                                            "Minério" -> TipoGranel.MINERIO
                                            "Líquido" -> TipoGranel.LIQUIDO
                                            else -> TipoGranel.GRAOS
                                        }
                                        CargaGranel(
                                            id = "C-$idNavio",
                                            nome = nomeCarga.ifEmpty { "Carga $idNavio" },
                                            destino = destino,
                                            destinatario = destinatario,
                                            metodoTransporte = MetodoTransporte.MARITIMO,
                                            volume = volume,
                                            tipoGranel = tipoGranelEnum
                                        )
                                    }

                                    // Navio sem a propriedade categoria
                                    val novoNavio = Navio(
                                        id = idNavio,
                                        nome = nomeNavio.ifEmpty { "Navio $idNavio" },
                                        cargas = listOf(carga),
                                        status = statusNavioSelecionado
                                    )

                                    listaNavios.add(novoNavio)

                                    // Limpeza e resets
                                    idNavio = ""; nomeNavio = ""; nomeCarga = ""; destinatario = ""; destino = ""; diasPatioStr = ""; mensagemFeedback = ""
                                    quantidadeConteinersStr = "1"; volumeGranelStr = ""
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Adicionar")
                        }

                        Button(
                            onClick = {
                                if (idNavio.isNotBlank()) {
                                    val removido = listaNavios.removeAll { it.id == idNavio }
                                    if (removido) {
                                        mensagemFeedback = "Navio $idNavio removido com sucesso!"
                                        idNavio = ""
                                    } else {
                                        mensagemFeedback = "Erro: Navio ID $idNavio não encontrado."
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Text("Remover Navio")
                        }

                        Button(
                            onClick = {
                                if (idNavio.isNotBlank()) {
                                    val navioAlvo = listaNavios.find { it.id == idNavio }
                                    if (navioAlvo != null) {
                                        var todasCargasAlocadasComSucesso = true

                                        navioAlvo.cargas.forEach { carga ->
                                            val alocou = if (carga is CargaConteiner) {
                                                patioConteiner.alocarCarga(carga)
                                            } else {
                                                patioGranel.alocarCarga(carga)
                                            }
                                            if (!alocou) {
                                                todasCargasAlocadasComSucesso = false
                                            }
                                        }

                                        if (todasCargasAlocadasComSucesso) {
                                            listaNavios.remove(navioAlvo)
                                            mensagemFeedback = "Navio descarregado e cargas enviadas ao pátio com sucesso!"
                                            idNavio = ""
                                        } else {
                                            mensagemFeedback = "Erro: O pátio atingiu a capacidade máxima e não pôde receber todas as cargas!"
                                        }
                                    } else {
                                        mensagemFeedback = "Erro: Navio ID $idNavio não encontrado para descarregar."
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                            modifier = Modifier.weight(1.3f)
                        ) {
                            Text("Descarregar Navio")
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // --- TABELA DE NAVIOS NO PORTO ---
                    Text("Navios no Porto:", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))

                    // Sem cor de fundo extra no cabeçalho
                    Row(modifier = Modifier.fillMaxWidth().border(1.dp, Color.Gray).padding(8.dp)) {
                        Text("ID", modifier = Modifier.weight(0.7f))
                        Text("Destinatário", modifier = Modifier.weight(1.2f))
                        Text("Destino", modifier = Modifier.weight(1.2f))
                        Text("Status", modifier = Modifier.weight(1.1f))
                        Text("Tipo", modifier = Modifier.weight(0.9f))
                        Text("Info Extra", modifier = Modifier.weight(1.8f))
                        Text("Tarifa Total", modifier = Modifier.weight(1.1f))
                    }

                    if (listaNavios.isEmpty()) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.Center) {
                            Text("Nenhum navio registrado no momento.", color = Color.Gray)
                        }
                    } else {
                        listaNavios.forEach { navio ->
                            val carga = navio.cargas.firstOrNull()

                            // AQUI: Usamos a função nativa da carga para a tarifa!
                            val tarifaTotalNavio = navio.cargas.sumOf { it.calcularTarifaBase() }

                            val tipoTexto = if (carga is CargaConteiner) "Contêiner" else "Granel"
                            val infoExtra = when (carga) {
                                is CargaConteiner -> "Qtd: ${carga.qtdContaineres} | Tam: ${carga.tamanho}m | Vol: ${carga.volume}m³"
                                is CargaGranel -> "Tipo: ${carga.tipoGranel} | Vol: ${carga.volume}m³"
                                else -> "-"
                            }

                            Row(modifier = Modifier.fillMaxWidth().border(0.5.dp, Color.LightGray).padding(8.dp)) {
                                Text(navio.id, modifier = Modifier.weight(0.7f))
                                Text(carga?.destinatario ?: "-", modifier = Modifier.weight(1.2f))
                                Text(carga?.destino ?: "-", modifier = Modifier.weight(1.2f))
                                Text(navio.status.name, modifier = Modifier.weight(1.1f))
                                Text(tipoTexto, modifier = Modifier.weight(0.9f))
                                Text(infoExtra, modifier = Modifier.weight(1.8f))
                                Text(String.format("R$ %.2f", tarifaTotalNavio), modifier = Modifier.weight(1.1f))
                            }
                        }
                    }
                }
            }

            // ==========================================
            // ABA 1: VISUALIZAÇÃO DOS PÁTIOS
            // ==========================================
            if (abaSelecionada == 1) {
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

                    // CARD DO PÁTIO 1 (Sem cor customizada)
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(patioConteiner.nome, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Capacidade Máxima Suportada: ${patioConteiner.capacidadeMaxima} m³")
                            Text("Ocupação de Carga Atual: ${patioConteiner.ocupacaoAtual} m³", style = MaterialTheme.typography.bodyLarge, color = Color.DarkGray)
                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Itens Armazenados neste Pátio:", style = MaterialTheme.typography.labelLarge)
                            if (cargasPatioConteiner.isEmpty()) {
                                Text("Nenhum item alocado.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            } else {
                                cargasPatioConteiner.forEach { c ->
                                    if (c is CargaConteiner) {
                                        Text("• ID: ${c.id} | Destinatário: ${c.destinatario} | Tam: ${c.tamanho}m | Vol: ${c.volume}m³", style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }

                    // CARD DO PÁTIO 2 (Sem cor customizada)
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(patioGranel.nome, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Capacidade Máxima Suportada: ${patioGranel.capacidadeMaxima} m³")
                            Text("Ocupação de Carga Atual: ${patioGranel.ocupacaoAtual} m³", style = MaterialTheme.typography.bodyLarge, color = Color.DarkGray)
                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Itens Armazenados neste Pátio:", style = MaterialTheme.typography.labelLarge)
                            if (cargasPatioGranel.isEmpty()) {
                                Text("Nenhum item alocado.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            } else {
                                cargasPatioGranel.forEach { c ->
                                    if (c is CargaGranel) {
                                        Text("• ID: ${c.id} | Destinatário: ${c.destinatario} | Tipo: ${c.tipoGranel} | Vol: ${c.volume}m³", style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }

                    // ARMAZENAMENTO COMBINADO TOTAL (Sem cor customizada)
                    val armazenamentoTotalGlobal = patioConteiner.ocupacaoAtual + patioGranel.ocupacaoAtual
                    val capacidadeTotalGlobal = patioConteiner.capacidadeMaxima + patioGranel.capacidadeMaxima

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Balanço Geral do Porto", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Armazenamento Total Ocupado: $armazenamentoTotalGlobal m³ de $capacidadeTotalGlobal m³", style = MaterialTheme.typography.titleSmall)
                        }
                    }
                }
            }
        }
    }
}