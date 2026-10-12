package com.consoli.agenda_banhoetosa.controller;

import java.util.LinkedHashSet;
import java.util.Set;

public final class Expansao {

    private final Set<String> nomes;

    private Expansao(Set<String> nomes) {
        this.nomes = nomes;
    }

    public static Expansao de(String valor, Set<String> permitidas) {
        if (valor == null || valor.isBlank()) {
            return new Expansao(Set.of());
        }
        Set<String> nomes = new LinkedHashSet<>();
        boolean todas = false;
        for (String parte : valor.split(",")) {
            String nome = parte.trim();
            if (nome.isEmpty()) {
                continue;
            }
            if ("*".equals(nome)) {
                todas = true;
                continue;
            }
            if (!permitidas.contains(nome)) {
                throw new IllegalArgumentException("Expansão inválida: " + nome);
            }
            nomes.add(nome);
        }
        return new Expansao(todas ? Set.copyOf(permitidas) : Set.copyOf(nomes));
    }

    public boolean pede(String nome) {
        if (nomes.contains(nome)) {
            return true;
        }
        String prefixo = nome + ".";
        for (String item : nomes) {
            if (item.startsWith(prefixo)) {
                return true;
            }
        }
        return false;
    }

    public Expansao aninhada(String prefixo) {
        String ponto = prefixo + ".";
        Set<String> filhos = new LinkedHashSet<>();
        for (String nome : nomes) {
            if (nome.startsWith(ponto)) {
                filhos.add(nome.substring(ponto.length()));
            }
        }
        return new Expansao(Set.copyOf(filhos));
    }

}
