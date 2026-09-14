package br.com.devops.devops.controller;

import br.com.devops.devops.entity.Produto;
import br.com.devops.devops.service.ProdutoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/loja")
public class LojaController {
    private static final String CARRINHO = "carrinhoProdutos";
    private final ProdutoService produtoService;
    public LojaController(ProdutoService produtoService) { this.produtoService = produtoService; }

    @GetMapping
    public String loja(@RequestParam(defaultValue = "") String busca, Model model) {
        var produtos = produtoService.listarTodos().stream()
                .filter(p -> busca.isBlank() || p.getNomeProduto().toLowerCase().contains(busca.toLowerCase())
                        || p.getDescricaoProduto().toLowerCase().contains(busca.toLowerCase())).toList();
        model.addAttribute("produtos", produtos); model.addAttribute("busca", busca); return "loja/index";
    }

    @GetMapping("/produto/{id}")
    public String detalhe(@PathVariable Integer id, Model model) {
        return produtoService.buscarPorId(id).map(p -> { model.addAttribute("produto", p); return "loja/detalhe"; })
                .orElse("redirect:/loja");
    }

    @PostMapping("/carrinho/adicionar/{id}")
    public String adicionar(@PathVariable Integer id, @RequestParam(defaultValue = "1") Integer quantidade, HttpSession session) {
        produtoService.buscarPorId(id).ifPresent(p -> { Map<Integer,Integer> c = carrinho(session); c.merge(id, Math.max(1, quantidade), Integer::sum); });
        return "redirect:/loja/carrinho";
    }

    @GetMapping("/carrinho")
    public String carrinho(Model model, HttpSession session) {
        Map<Integer,Integer> c = carrinho(session); Map<Produto,Integer> itens = new LinkedHashMap<>(); BigDecimal total = BigDecimal.ZERO;
        for (var e : c.entrySet()) produtoService.buscarPorId(e.getKey()).ifPresent(p -> { itens.put(p, e.getValue()); });
        for (var e : itens.entrySet()) total = total.add(e.getKey().getPrecoProduto().multiply(BigDecimal.valueOf(e.getValue())));
        model.addAttribute("itens", itens); model.addAttribute("total", total); return "loja/carrinho";
    }

    @PostMapping("/carrinho/atualizar/{id}")
    public String atualizar(@PathVariable Integer id, @RequestParam Integer quantidade, HttpSession session) {
        if (quantidade == null || quantidade <= 0) carrinho(session).remove(id); else carrinho(session).put(id, quantidade); return "redirect:/loja/carrinho";
    }

    @PostMapping("/carrinho/remover/{id}")
    public String remover(@PathVariable Integer id, HttpSession session) { carrinho(session).remove(id); return "redirect:/loja/carrinho"; }

    @SuppressWarnings("unchecked") private Map<Integer,Integer> carrinho(HttpSession session) {
        Map<Integer,Integer> c = (Map<Integer,Integer>) session.getAttribute(CARRINHO);
        return c == null ? carrinhoInit(session) : c;
    }
    private Map<Integer,Integer> carrinhoInit(HttpSession session) { Map<Integer,Integer> c = new LinkedHashMap<>(); session.setAttribute(CARRINHO, c); return c; }
}
