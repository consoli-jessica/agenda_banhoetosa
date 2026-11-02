package com.consoli.agenda_banhoetosa.controller;

import java.text.Collator;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.consoli.agenda_banhoetosa.entity.Agendamento;
import com.consoli.agenda_banhoetosa.entity.Cachorro;
import com.consoli.agenda_banhoetosa.entity.Cliente;
import com.consoli.agenda_banhoetosa.entity.Raca;
import com.consoli.agenda_banhoetosa.entity.Servico;
import com.consoli.agenda_banhoetosa.service.AgendamentoService;
import com.consoli.agenda_banhoetosa.service.CachorroService;
import com.consoli.agenda_banhoetosa.service.ClienteService;
import com.consoli.agenda_banhoetosa.service.RacaService;
import com.consoli.agenda_banhoetosa.service.ServicoService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class PainelController {

    // ponytail: senha fixa de teste; trocar por usuários persistidos se a loja tiver mais de um acesso
    public static final String USUARIO = "atendente";
    public static final String SENHA = "banho123";
    public static final String SESSAO = "usuario";

    private static final Locale PT = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter MES = DateTimeFormatter.ofPattern("MMMM yyyy", PT);
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter QUANDO = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    private final ClienteService clientes;
    private final CachorroService cachorros;
    private final RacaService racas;
    private final ServicoService servicos;
    private final AgendamentoService agendamentos;
    private final Respostas respostas;

    public PainelController(ClienteService clientes, CachorroService cachorros, RacaService racas,
            ServicoService servicos, AgendamentoService agendamentos, Respostas respostas) {
        this.clientes = clientes;
        this.cachorros = cachorros;
        this.racas = racas;
        this.servicos = servicos;
        this.agendamentos = agendamentos;
        this.respostas = respostas;
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(SESSAO) != null) {
            return "redirect:/";
        }
        model.addAttribute("titulo", "Entrar");
        return "login";
    }

    @PostMapping("/login")
    public String entrar(@RequestParam String usuario, @RequestParam String senha, HttpServletRequest request,
            Model model) {
        if (USUARIO.equals(usuario) && SENHA.equals(senha)) {
            request.getSession(true).setAttribute(SESSAO, USUARIO);
            return "redirect:/";
        }
        model.addAttribute("titulo", "Entrar");
        model.addAttribute("erro", "Usuário ou senha inválidos");
        model.addAttribute("usuario", usuario);
        return "login";
    }

    @PostMapping("/logout")
    public String sair(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("titulo", "Início");
        model.addAttribute("secao", "inicio");
        return "inicio";
    }

    @GetMapping("/agenda")
    public String agenda(@RequestParam(required = false) String mes, @RequestParam(required = false) String dia,
            Model model) {
        YearMonth ym = anoMes(mes);
        LocalDate selecionado = data(dia);
        if (selecionado != null && !YearMonth.from(selecionado).equals(ym)) {
            selecionado = null;
        }
        List<AgendamentoResposta> todos = respostas.agendamentos(agendamentos.listar(),
                Expansao.de("cliente,cachorro,servico", Respostas.AGENDAMENTO));
        LocalDate hoje = LocalDate.now();
        LocalDate cursor = ym.atDay(1).minusDays(ym.atDay(1).getDayOfWeek().getValue() - 1L);
        List<List<DiaCalendario>> semanas = new ArrayList<>();
        do {
            List<DiaCalendario> semana = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                LocalDate dataDia = cursor;
                List<Chip> chips = todos.stream()
                        .filter(item -> item.data() != null && item.data().toLocalDate().equals(dataDia))
                        .sorted(Comparator.comparing(AgendamentoResposta::data))
                        .map(item -> new Chip(item.id(), item.data().format(HORA) + " " + nomeCao(item)))
                        .toList();
                String css = "dia";
                if (!YearMonth.from(dataDia).equals(ym)) {
                    css += " fora";
                }
                if (dataDia.equals(hoje)) {
                    css += " hoje";
                }
                if (dataDia.equals(selecionado)) {
                    css += " sel";
                }
                semana.add(new DiaCalendario(dataDia.toString(), dataDia.getDayOfMonth(), css, chips));
                cursor = cursor.plusDays(1);
            }
            semanas.add(semana);
        } while (!cursor.isAfter(ym.atEndOfMonth()));

        LocalDate filtro = selecionado;
        List<LinhaAgenda> linhas = todos.stream()
                .filter(item -> item.data() != null && YearMonth.from(item.data()).equals(ym))
                .filter(item -> filtro == null || item.data().toLocalDate().equals(filtro))
                .sorted(Comparator.comparing(AgendamentoResposta::data))
                .map(this::linha)
                .toList();
        LocalDate base = selecionado != null ? selecionado : (ym.equals(YearMonth.now()) ? hoje : ym.atDay(1));
        model.addAttribute("titulo", "Agenda");
        model.addAttribute("secao", "agenda");
        model.addAttribute("mes", ym.toString());
        model.addAttribute("tituloMes", ym.format(MES));
        model.addAttribute("mesAnterior", ym.minusMonths(1).toString());
        model.addAttribute("mesSeguinte", ym.plusMonths(1).toString());
        model.addAttribute("semanas", semanas);
        model.addAttribute("linhas", linhas);
        model.addAttribute("diaSelecionado", selecionado == null ? "" : selecionado.toString());
        model.addAttribute("tituloLista", selecionado == null ? "Agendamentos do mês" : "Agendamentos do dia");
        model.addAttribute("novoEm", base + "T09:00");
        return "agenda";
    }

    @GetMapping("/app/clientes")
    public String paginaClientes(Model model) {
        model.addAttribute("titulo", "Clientes");
        model.addAttribute("secao", "clientes");
        model.addAttribute("lista", clientes.listar().stream()
                .sorted(Comparator.comparing(Cliente::getNomeCompleto, Comparator.nullsLast(alfabetica())))
                .toList());
        return "clientes";
    }

    @PostMapping("/app/clientes")
    public String criarCliente(@RequestParam String nomeCompleto, @RequestParam String telefone,
            @RequestParam(required = false) String endereco, Model model, RedirectAttributes ra) {
        if (vazio(nomeCompleto) || vazio(telefone)) {
            model.addAttribute("erro", "Nome e telefone são obrigatórios");
            model.addAttribute("nomeCompleto", nomeCompleto);
            model.addAttribute("telefone", telefone);
            model.addAttribute("endereco", endereco);
            return paginaClientes(model);
        }
        Cliente cliente = new Cliente();
        cliente.setNomeCompleto(nomeCompleto.trim());
        cliente.setTelefone(telefone.trim());
        cliente.setEndereco(endereco == null ? null : endereco.trim());
        clientes.criar(cliente);
        ra.addFlashAttribute("ok", "Cliente cadastrado");
        return "redirect:/app/clientes";
    }

    @GetMapping("/app/cachorros")
    public String paginaCachorros(Model model) {
        model.addAttribute("titulo", "Cachorros");
        model.addAttribute("secao", "cachorros");
        model.addAttribute("lista", respostas.cachorros(cachorros.listar(),
                Expansao.de("dono,raca", Respostas.CACHORRO)).stream()
                .sorted(Comparator.comparing(CachorroResposta::nome, Comparator.nullsLast(alfabetica())))
                .toList());
        model.addAttribute("donos", clientes.listar().stream()
                .sorted(Comparator.comparing(Cliente::getNomeCompleto, Comparator.nullsLast(alfabetica())))
                .toList());
        model.addAttribute("racas", racas.listar().stream()
                .sorted(Comparator.comparing(Raca::getNome, Comparator.nullsLast(alfabetica())))
                .toList());
        return "cachorros";
    }

    @PostMapping("/app/cachorros")
    public String criarCachorro(@RequestParam String nome, @RequestParam(required = false) String donoId,
            @RequestParam(required = false) String racaId, @RequestParam(required = false) String novaRaca,
            Model model, RedirectAttributes ra) {
        Long dono = longo(donoId);
        if (vazio(nome) || dono == null || (vazio(novaRaca) && longo(racaId) == null)) {
            model.addAttribute("erro", "Nome, dono e raça são obrigatórios");
            model.addAttribute("nome", nome);
            model.addAttribute("donoId", dono);
            model.addAttribute("racaId", longo(racaId));
            model.addAttribute("novaRaca", novaRaca);
            return paginaCachorros(model);
        }
        try {
            Cachorro cachorro = new Cachorro();
            cachorro.setNome(nome.trim());
            cachorro.setDono(clientes.buscar(dono));
            cachorro.setRaca(resolverRaca(racaId, novaRaca));
            cachorros.criar(cachorro);
        } catch (EntityNotFoundException e) {
            model.addAttribute("erro", "Dono ou raça não encontrado");
            model.addAttribute("nome", nome);
            model.addAttribute("donoId", dono);
            model.addAttribute("novaRaca", novaRaca);
            return paginaCachorros(model);
        }
        ra.addFlashAttribute("ok", "Cachorro cadastrado");
        return "redirect:/app/cachorros";
    }

    @GetMapping("/app/agendamentos/novo")
    public String novoAgendamento(@RequestParam(required = false) String data, Model model) {
        model.addAttribute("data", data);
        return formularioAgendamento(model);
    }

    @GetMapping("/app/agendamentos/{id}/editar")
    public String abrirAgendamento(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            Agendamento agendamento = agendamentos.buscar(id);
            model.addAttribute("id", id);
            model.addAttribute("cachorroId", agendamento.getCachorro().getId());
            model.addAttribute("servicoId", agendamento.getServico().getId());
            model.addAttribute("data", agendamento.getData().truncatedTo(ChronoUnit.MINUTES).toString());
            return formularioAgendamento(model);
        } catch (EntityNotFoundException e) {
            ra.addFlashAttribute("erro", "Agendamento não encontrado");
            return "redirect:/agenda";
        }
    }

    @PostMapping("/app/agendamentos")
    public String criarAgendamento(@RequestParam(required = false) String cachorroId,
            @RequestParam(required = false) String servicoId, @RequestParam(required = false) String data,
            Model model, RedirectAttributes ra) {
        return salvarAgendamento(null, cachorroId, servicoId, data, model, ra);
    }

    @PostMapping("/app/agendamentos/{id}")
    public String editarAgendamento(@PathVariable Long id, @RequestParam(required = false) String cachorroId,
            @RequestParam(required = false) String servicoId, @RequestParam(required = false) String data,
            Model model, RedirectAttributes ra) {
        return salvarAgendamento(id, cachorroId, servicoId, data, model, ra);
    }

    @PostMapping("/app/agendamentos/{id}/excluir")
    public String excluirAgendamento(@PathVariable Long id, RedirectAttributes ra) {
        try {
            LocalDate dia = agendamentos.buscar(id).getData().toLocalDate();
            agendamentos.deletar(id);
            ra.addFlashAttribute("ok", "Agendamento excluído");
            return "redirect:/agenda?mes=" + YearMonth.from(dia) + "&dia=" + dia;
        } catch (EntityNotFoundException e) {
            ra.addFlashAttribute("erro", "Agendamento não encontrado");
            return "redirect:/agenda";
        }
    }

    private String salvarAgendamento(Long id, String cachorroId, String servicoId, String data, Model model,
            RedirectAttributes ra) {
        Long dogId = longo(cachorroId);
        Long srvId = longo(servicoId);
        LocalDateTime quando = dataHora(data);
        if (dogId == null || srvId == null || quando == null) {
            model.addAttribute("erro", "Cachorro, serviço, data e horário são obrigatórios");
            model.addAttribute("id", id);
            model.addAttribute("cachorroId", dogId);
            model.addAttribute("servicoId", srvId);
            model.addAttribute("data", data);
            return formularioAgendamento(model);
        }
        try {
            Cachorro cachorro = cachorros.buscar(dogId);
            Servico servico = servicos.buscar(srvId);
            Agendamento agendamento = new Agendamento();
            agendamento.setCachorro(cachorro);
            agendamento.setServico(servico);
            agendamento.setCliente(cachorro.getDono());
            agendamento.setData(quando);
            if (id == null) {
                agendamentos.criar(agendamento);
                ra.addFlashAttribute("ok", "Agendamento criado");
            } else {
                agendamentos.editar(id, agendamento);
                ra.addFlashAttribute("ok", "Agendamento atualizado");
            }
            return "redirect:/agenda?mes=" + YearMonth.from(quando) + "&dia=" + quando.toLocalDate();
        } catch (EntityNotFoundException e) {
            model.addAttribute("erro", "Cachorro, serviço ou agendamento não encontrado");
            model.addAttribute("id", id);
            model.addAttribute("cachorroId", dogId);
            model.addAttribute("servicoId", srvId);
            model.addAttribute("data", data);
            return formularioAgendamento(model);
        }
    }

    private String formularioAgendamento(Model model) {
        model.addAttribute("titulo", model.getAttribute("id") == null ? "Novo agendamento" : "Editar agendamento");
        model.addAttribute("secao", "agenda");
        model.addAttribute("cachorros", respostas.cachorros(cachorros.listar(),
                Expansao.de("dono,raca", Respostas.CACHORRO)).stream()
                .sorted(Comparator.comparing(CachorroResposta::nome, Comparator.nullsLast(alfabetica())))
                .toList());
        model.addAttribute("servicos", servicos.listar().stream()
                .sorted(Comparator.comparing(Servico::getDescricao, Comparator.nullsLast(alfabetica())))
                .toList());
        return "agendamento";
    }

    private Raca resolverRaca(String racaId, String novaRaca) {
        if (!vazio(novaRaca)) {
            String nome = novaRaca.trim();
            return racas.listar().stream()
                    .filter(raca -> raca.getNome().equalsIgnoreCase(nome))
                    .findFirst()
                    .orElseGet(() -> {
                        Raca raca = new Raca();
                        raca.setNome(nome);
                        return racas.criar(raca);
                    });
        }
        return racas.buscar(longo(racaId));
    }

    private LinhaAgenda linha(AgendamentoResposta item) {
        String fim = item.servico() != null && item.servico().tempoDuracao() != null
                ? "–" + item.data().plusMinutes(item.servico().tempoDuracao()).format(HORA)
                : "";
        String resumo = nomeCao(item) + " · " + (item.servico() == null ? "" : item.servico().descricao())
                + " · " + (item.cliente() == null ? "" : item.cliente().nomeCompleto());
        return new LinhaAgenda(item.id(), item.data().format(QUANDO) + fim, resumo);
    }

    private static String nomeCao(AgendamentoResposta item) {
        return item.cachorro() == null ? "Cão" : item.cachorro().nome();
    }

    private static YearMonth anoMes(String valor) {
        try {
            return valor == null || valor.isBlank() ? YearMonth.now() : YearMonth.parse(valor);
        } catch (DateTimeException e) {
            return YearMonth.now();
        }
    }

    private static LocalDate data(String valor) {
        try {
            return valor == null || valor.isBlank() ? null : LocalDate.parse(valor);
        } catch (DateTimeException e) {
            return null;
        }
    }

    private static LocalDateTime dataHora(String valor) {
        try {
            return valor == null || valor.isBlank() ? null : LocalDateTime.parse(valor);
        } catch (DateTimeException e) {
            return null;
        }
    }

    private static Long longo(String valor) {
        try {
            return valor == null || valor.isBlank() ? null : Long.valueOf(valor);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static boolean vazio(String valor) {
        return valor == null || valor.isBlank();
    }

    private static Comparator<String> alfabetica() {
        Collator collator = Collator.getInstance(PT);
        collator.setStrength(Collator.PRIMARY);
        return (a, b) -> collator.compare(a, b);
    }

    public record DiaCalendario(String iso, int numero, String css, List<Chip> chips) {
    }

    public record Chip(Long id, String texto) {
    }

    public record LinhaAgenda(Long id, String quando, String resumo) {
    }

}
