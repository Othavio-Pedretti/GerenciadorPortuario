package br.upf.ccc.gerenciadorporto

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import br.upf.ccc.gerenciadorporto.model.*

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Gerenciador Portuário") {
        MaterialTheme {
            PortoApp()
        }
    }
}

@Composable
fun PortoApp() {
    val vagas = remember {
        mutableStateListOf(
            VagaCais(1, setOf(TipoNavio.PORTA_CONTAINER, TipoNavio.CARGA_GERAL), null),
            VagaCais(2, setOf(TipoNavio.GRANELEIRO), null),
            VagaCais(3, setOf(TipoNavio.PORTA_CONTAINER, TipoNavio.GRANELEIRO, TipoNavio.CARGA_GERAL), null),
            VagaCais(4, setOf(TipoNavio.CRUZEIRO), null),
        )
    }
    val setoresPatio = remember {
        mutableStateListOf(
            SetorPatio("S1", "Setor Conteineres", 5000.0, CargaConteiner::class.java, mutableListOf()),
            SetorPatio("S2", "Setor Granel", 8000.0, CargaGranel::class.java, mutableListOf()),
        )
    }
    val gerenciador = remember { GerenciadorPortuario(vagas, setoresPatio) }
    val navios = remember { mutableStateListOf<Navio>() }
    val logs = remember { mutableStateListOf<String>() }
    var aba by remember { mutableStateOf(0) }
    var erro by remember { mutableStateOf("") }

    fun log(msg: String) = logs.add(0, msg)

    Column(Modifier.fillMaxSize()) {

        // Cabeçalho
        Box(Modifier.fillMaxWidth().background(Color(0xFF1565C0)).padding(12.dp)) {
            Text("Gerenciador Portuario", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        // Tabs
        TabRow(selectedTabIndex = aba) {
            listOf("Navios", "Vagas", "Patio", "Log").forEachIndexed { i, titulo ->
                Tab(selected = aba == i, onClick = { aba = i }, text = { Text(titulo) })
            }
        }

        // Erro
        if (erro.isNotBlank()) {
            Box(Modifier.fillMaxWidth().background(Color(0xFFFFCDD2)).padding(8.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(erro, color = Color(0xFFC62828), fontSize = 13.sp)
                    TextButton(onClick = { erro = "" }) { Text("X", color = Color(0xFFC62828)) }
                }
            }
        }

        Box(Modifier.fillMaxSize().padding(12.dp)) {
            when (aba) {
                0 -> AbaNavios(navios, vagas,
                    onRegistrar = { navio ->
                        if (gerenciador.registrarEntradaNavio(navio)) { navios.add(navio); log("Navio ${navio.nome} registrado.") }
                        else erro = "ID '${navio.id}' ja esta no porto."
                    },
                    onAtracar = { id, vaga ->
                        if (gerenciador.atracarNavio(id, vaga)) log("Navio $id atracado na vaga $vaga.")
                        else erro = "Nao foi possivel atracar. Vaga ocupada ou tipo incompativel."
                    },
                    onDescarregar = { id ->
                        gerenciador.descarregarNavio(id)
                        log("Navio $id descarregado.")
                    }
                )
                1 -> AbaVagas(vagas)
                2 -> AbaPatio(setoresPatio, onSaida = { id ->
                    if (gerenciador.registrarSaidaCarga(id)) log("Carga $id retirada do patio.")
                    else erro = "Carga '$id' nao encontrada."
                })
                3 -> AbaLog(logs)
            }
        }
    }
}

// ── ABA NAVIOS ────────────────────────────────────────────────────────────────
@Composable
fun AbaNavios(
    navios: List<Navio>,
    vagas: List<VagaCais>,
    onRegistrar: (Navio) -> Unit,
    onAtracar: (String, Int) -> Unit,
    onDescarregar: (String) -> Unit,
) {
    var dialogo by remember { mutableStateOf("") } // "registrar" | "atracar" | "descarregar"

    Column(Modifier.fillMaxSize()) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { dialogo = "registrar" }) { Text("Registrar Navio") }
            Button(onClick = { dialogo = "atracar" },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF388E3C))) { Text("Atracar", color = Color.White) }
            Button(onClick = { dialogo = "descarregar" },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF57F17))) { Text("Descarregar", color = Color.White) }
        }

        Spacer(Modifier.height(12.dp))

        if (navios.isEmpty()) {
            Text("Nenhum navio registrado.", color = Color.Gray)
        } else {
            // Cabeçalho tabela
            Row(Modifier.fillMaxWidth().background(Color(0xFF1565C0)).padding(8.dp)) {
                Text("ID",        Modifier.weight(0.15f), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("Nome",      Modifier.weight(0.2f),  color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("Tipo",      Modifier.weight(0.25f), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("Status",    Modifier.weight(0.2f),  color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("Cargas",    Modifier.weight(0.1f),  color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("Vaga",      Modifier.weight(0.1f),  color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            LazyColumn {
                items(navios) { navio ->
                    val vagaAtracada = vagas.find { it.navio?.id == navio.id }
                    Row(Modifier.fillMaxWidth().border(0.5.dp, Color.LightGray).padding(8.dp)) {
                        Text(navio.id,                          Modifier.weight(0.15f), fontSize = 12.sp)
                        Text(navio.nome,                        Modifier.weight(0.2f),  fontSize = 12.sp)
                        Text(navio.categoria.name,              Modifier.weight(0.25f), fontSize = 12.sp, color = Color(0xFF1565C0))
                        Text(navio.status.name,                 Modifier.weight(0.2f),  fontSize = 12.sp)
                        Text("${navio.cargas.size}",            Modifier.weight(0.1f),  fontSize = 12.sp)
                        Text(vagaAtracada?.numero?.toString() ?: "-", Modifier.weight(0.1f), fontSize = 12.sp)
                    }
                }
            }
        }
    }

    when (dialogo) {
        "registrar"  -> DialogRegistrar(onConfirmar = { onRegistrar(it); dialogo = "" }, onCancelar = { dialogo = "" })
        "atracar"    -> DialogAtracar(navios, vagas, onConfirmar = { id, v -> onAtracar(id, v); dialogo = "" }, onCancelar = { dialogo = "" })
        "descarregar"-> DialogDescarregar(navios, onConfirmar = { id -> onDescarregar(id); dialogo = "" }, onCancelar = { dialogo = "" })
    }
}

// ── ABA VAGAS ─────────────────────────────────────────────────────────────────
@Composable
fun AbaVagas(vagas: List<VagaCais>) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Vagas do Cais", fontWeight = FontWeight.Bold)
        vagas.forEach { vaga ->
            val cor = if (vaga.ocupada) Color(0xFFFFCDD2) else Color(0xFFE8F5E9)
            val status = if (vaga.ocupada) "OCUPADA" else "LIVRE"
            Row(Modifier.fillMaxWidth().background(cor).border(1.dp, Color.LightGray).padding(10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text("Vaga ${vaga.numero}", Modifier.weight(0.15f), fontWeight = FontWeight.Bold)
                Text(status, Modifier.weight(0.15f), fontWeight = FontWeight.Bold,
                    color = if (vaga.ocupada) Color(0xFFC62828) else Color(0xFF2E7D32))
                Text("Aceita: " + vaga.tiposPermitidos.joinToString(", "), Modifier.weight(0.5f), fontSize = 12.sp)
                Text(vaga.navio?.let { "Navio: ${it.nome}" } ?: "", Modifier.weight(0.2f), fontSize = 12.sp)
            }
        }
    }
}

// ── ABA PATIO ─────────────────────────────────────────────────────────────────
@Composable
fun AbaPatio(setores: List<SetorPatio>, onSaida: (String) -> Unit) {
    var dialogo by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Patio de Cargas", fontWeight = FontWeight.Bold)
            Button(onClick = { dialogo = true },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFC62828))) {
                Text("Saida de Carga", color = Color.White)
            }
        }
        Spacer(Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(setores) { setor ->
                val pct = ((setor.ocupacaoAtual / setor.capacidadeMaxima) * 100).toInt().coerceIn(0, 100)
                Column(Modifier.fillMaxWidth().border(1.dp, Color.LightGray).padding(10.dp)) {
                    Text(setor.nome, fontWeight = FontWeight.Bold)
                    Text("Ocupacao: ${setor.ocupacaoAtual.toInt()} / ${setor.capacidadeMaxima.toInt()} ton  ($pct%)", fontSize = 12.sp, color = Color.Gray)
                    Spacer(Modifier.height(4.dp))
                    LinearProgressIndicator(progress = pct / 100f, Modifier.fillMaxWidth().height(6.dp))

                    if (setor.cargasArmazenadas.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Divider()
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Text("ID",          Modifier.weight(0.2f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Text("Peso",        Modifier.weight(0.2f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Text("Destinatario",Modifier.weight(0.35f),fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Text("Tarifa (R$)", Modifier.weight(0.25f),fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        }
                        setor.cargasArmazenadas.forEach { carga ->
                            Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                                Text(carga.id,                                       Modifier.weight(0.2f),  fontSize = 12.sp)
                                Text("${carga.peso} ton",                            Modifier.weight(0.2f),  fontSize = 12.sp)
                                Text(carga.destinatario,                             Modifier.weight(0.35f), fontSize = 12.sp)
                                Text("R$ %.2f".format(CalculadoraTarifa.calcularTotal(carga)), Modifier.weight(0.25f), fontSize = 12.sp, color = Color(0xFF2E7D32))
                            }
                        }
                    } else {
                        Text("Nenhuma carga armazenada.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 6.dp))
                    }
                }
            }
        }
    }

    if (dialogo) DialogSaida(onConfirmar = { id -> onSaida(id); dialogo = false }, onCancelar = { dialogo = false })
}

// ── ABA LOG ───────────────────────────────────────────────────────────────────
@Composable
fun AbaLog(logs: List<String>) {
    Column(Modifier.fillMaxSize()) {
        Text("Registro de Operacoes", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        if (logs.isEmpty()) {
            Text("Nenhuma operacao registrada.", color = Color.Gray)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(logs.size) { i ->
                    Row(Modifier.fillMaxWidth().border(0.5.dp, Color.LightGray).padding(8.dp)) {
                        Text("${logs.size - i}.", Modifier.width(28.dp), fontSize = 12.sp, color = Color.Gray)
                        Text(logs[i], fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// ── DIÁLOGOS ──────────────────────────────────────────────────────────────────
@Composable
fun DialogRegistrar(onConfirmar: (Navio) -> Unit, onCancelar: () -> Unit) {
    var id by remember { mutableStateOf("") }
    var nome by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf(TipoNavio.PORTA_CONTAINER) }
    var status by remember { mutableStateOf(StatusNavio.EM_TRAVESSIA) }
    var comConteiner by remember { mutableStateOf(false) }
    var comGranel by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Registrar Navio") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = id, onValueChange = { id = it }, label = { Text("ID") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                DropdownSimples("Categoria", TipoNavio.entries.map { it.name }, categoria.name) { categoria = TipoNavio.valueOf(it) }
                DropdownSimples("Status", StatusNavio.entries.map { it.name }, status.name) { status = StatusNavio.valueOf(it) }
                Text("Cargas para demonstracao:", fontSize = 13.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(comConteiner, onCheckedChange = { comConteiner = it })
                    Text("Conteiner 12m Padrao (2 dias no patio)", fontSize = 13.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(comGranel, onCheckedChange = { comGranel = it })
                    Text("Granel MINERIO 50 ton", fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (id.isBlank() || nome.isBlank()) return@Button
                val cargas = mutableListOf<Carga>()
                if (comConteiner) cargas.add(CargaConteiner("CC-$id", 20.0, 33.0, "Empresa A", "Santos", MetodoTransporte.MARITIMO, 12, TipoConteiner.PADRAO, 2))
                if (comGranel)    cargas.add(CargaGranel("CG-$id", 50.0, 80.0, "Mineradora B", "Rio Grande", MetodoTransporte.MARITIMO, "MINERIO"))
                onConfirmar(Navio(id.trim(), nome.trim(), categoria, cargas, status))
            }) { Text("Registrar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}

@Composable
fun DialogAtracar(navios: List<Navio>, vagas: List<VagaCais>, onConfirmar: (String, Int) -> Unit, onCancelar: () -> Unit) {
    var navioSel by remember { mutableStateOf(navios.firstOrNull()?.id ?: "") }
    var vagaSel by remember { mutableStateOf(vagas.firstOrNull()?.numero?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Atracar Navio") },
        text = {
            if (navios.isEmpty()) { Text("Nenhum navio registrado."); return@AlertDialog }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DropdownSimples("Navio", navios.map { "${it.id} - ${it.nome}" }, navios.find { it.id == navioSel }?.let { "${it.id} - ${it.nome}" } ?: "") {
                    navioSel = it.substringBefore(" -").trim()
                }
                DropdownSimples("Vaga", vagas.map { "Vaga ${it.numero} (${if (it.ocupada) "ocupada" else "livre"})" }, "Vaga $vagaSel") {
                    vagaSel = it.removePrefix("Vaga ").substringBefore(" ").trim()
                }
            }
        },
        confirmButton = {
            Button(onClick = { vagaSel.toIntOrNull()?.let { onConfirmar(navioSel, it) } }) { Text("Atracar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}

@Composable
fun DialogDescarregar(navios: List<Navio>, onConfirmar: (String) -> Unit, onCancelar: () -> Unit) {
    var navioSel by remember { mutableStateOf(navios.firstOrNull()?.id ?: "") }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Descarregar Navio") },
        text = {
            if (navios.isEmpty()) { Text("Nenhum navio registrado."); return@AlertDialog }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("As cargas serao movidas ao patio e as tarifas calculadas.", fontSize = 13.sp, color = Color.Gray)
                DropdownSimples("Navio", navios.map { "${it.id} - ${it.nome}" }, navios.find { it.id == navioSel }?.let { "${it.id} - ${it.nome}" } ?: "") {
                    navioSel = it.substringBefore(" -").trim()
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirmar(navioSel) }) { Text("Descarregar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}

@Composable
fun DialogSaida(onConfirmar: (String) -> Unit, onCancelar: () -> Unit) {
    var cargaId by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Saida de Carga") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Informe o ID da carga a retirar do patio.", fontSize = 13.sp, color = Color.Gray)
                OutlinedTextField(value = cargaId, onValueChange = { cargaId = it }, label = { Text("ID da Carga") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { if (cargaId.isNotBlank()) onConfirmar(cargaId.trim()) },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFC62828))) {
                Text("Confirmar", color = Color.White)
            }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}

// ── DROPDOWN SIMPLES ──────────────────────────────────────────────────────────
@Composable
fun DropdownSimples(label: String, opcoes: List<String>, selecionado: String, onSelecionar: (String) -> Unit) {
    var expandido by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selecionado, onValueChange = {},
            label = { Text(label) }, readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = { TextButton(onClick = { expandido = true }) { Text("v") } }
        )
        DropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            opcoes.forEach { op ->
                DropdownMenuItem(onClick = { onSelecionar(op); expandido = false }) { Text(op) }
            }
        }
    }
}