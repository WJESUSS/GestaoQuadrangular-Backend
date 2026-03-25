package com.gestaoigrejaemcelula.demo.aplication.service;


import com.gestaoigrejaemcelula.demo.domain.entity.Evento;
import com.gestaoigrejaemcelula.demo.domain.enums.TipoEvento;
import com.gestaoigrejaemcelula.demo.domain.repository.EventoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EventoService {

    private final EventoRepository repository;

    public EventoService(EventoRepository repository) {
        this.repository = repository;

    }

    public Evento criar(Evento evento) {
        evento.setId(null);
        return repository.save(evento);
    }

    public Evento atualizar(Long id, Evento dados) {
        Evento evento = buscarPorId(id);

        evento.setNome(dados.getNome());
        evento.setTipo(dados.getTipo());
        evento.setData(dados.getData());
        evento.setLocal(dados.getLocal());
        evento.setAnfitriao(dados.getAnfitriao());
        evento.setMembros(dados.getMembros());
        evento.setVisitantes(dados.getVisitantes());
        evento.setTotalPresentes(dados.getTotalPresentes());
        evento.setAceitaramJesus(dados.getAceitaramJesus());
        evento.setBatizados(dados.getBatizados());

        return repository.save(evento);
    }

    public Evento buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado"));
    }

    public List<Evento> listarTodos() {
        return repository.findAll();
    }

    public List<Evento> listarPorTipo(TipoEvento tipo) {
        return repository.findByTipo(tipo);
    }

    public void remover(Long id) {
        repository.deleteById(id);
    }
}

