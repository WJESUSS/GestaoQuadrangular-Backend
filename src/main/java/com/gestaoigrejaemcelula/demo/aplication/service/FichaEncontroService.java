package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.FichaEncontroRequestDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.FichaEncontroResponseDTO;
import com.gestaoigrejaemcelula.demo.domain.entity.FichaEncontro;
import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;
import com.gestaoigrejaemcelula.demo.domain.enums.EstadoCivil;
import com.gestaoigrejaemcelula.demo.domain.enums.Sexo;
import com.gestaoigrejaemcelula.demo.domain.enums.TipoEncontro;
import com.gestaoigrejaemcelula.demo.domain.repository.FichaEncontroRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.UsuarioRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class FichaEncontroService {

    private final FichaEncontroRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaHelper auditoria;

    public FichaEncontroService(FichaEncontroRepository repository,
                                UsuarioRepository usuarioRepository,
                                AuditoriaHelper auditoria) {
        this.repository      = repository;
        this.usuarioRepository = usuarioRepository;
        this.auditoria       = auditoria;
    }

    // ── Helper ─────────────────────────────────────────────────────────────────
    private String str(Object o) { return o != null ? o.toString() : ""; }

    // =========================
    // CRIAR
    // =========================
    @Transactional
    public FichaEncontroResponseDTO criar(FichaEncontroRequestDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado: " + username));

        FichaEncontro ficha = new FichaEncontro();

        ficha.setNomeConvidado(dto.getNomeConvidador());
        ficha.setDataNascimento(dto.getDataNascimento());
        ficha.setEndereco(dto.getEndereco());
        ficha.setBairro(dto.getBairro());
        ficha.setCidade(dto.getCidade());
        ficha.setTelefone(dto.getTelefone());

        if (dto.getSexo() != null && !dto.getSexo().isEmpty())
            ficha.setSexo(Sexo.valueOf(dto.getSexo().toUpperCase().trim()));
        if (dto.getEstadoCivil() != null && !dto.getEstadoCivil().isEmpty())
            ficha.setEstadoCivil(EstadoCivil.valueOf(dto.getEstadoCivil().toUpperCase().trim()));

        ficha.setRg(dto.getRg());
        ficha.setEstado(dto.getEstado());
        ficha.setPeso(dto.getPeso());
        ficha.setAltura(dto.getAltura());
        ficha.setTomaMedicamento(dto.isTomaMedicamento());
        ficha.setQualMedicamento(dto.getQualMedicamento());
        ficha.setTemProblemasSaude(dto.isTemProblemasSaude());
        ficha.setQualProblemaSaude(dto.getQualProblemaSaude());
        ficha.setTemApneia(dto.isTemApneia());
        ficha.setNomeConvidador(dto.getNomeConvidador());
        ficha.setCelulaConvidador(dto.getCelulaConvidador());
        ficha.setTipoEncontro(dto.getTipoEncontro());
        ficha.setNomeLiderCelula(dto.getNomeLiderCelula());
        ficha.setNomeFamiliarContato(dto.getNomeFamiliarContato());
        ficha.setTelefoneFamiliarContato(dto.getTelefoneFamiliarContato());
        ficha.setFrequentaCelula(dto.isFrequentaCelula());
        ficha.setNomeCelula(dto.getNomeCelula());
        ficha.setAceitouJesus(dto.isAceitouJesus());
        ficha.setJaEraCristao(dto.isJaEraCristao());
        ficha.setNomeEncontro(dto.getNomeEncontro());
        ficha.setDataInicio(dto.getDataInicio());
        ficha.setDataFim(dto.getDataFim());
        ficha.setLocalEncontro(dto.getLocalEncontro());
        ficha.setUsuario(usuario);

        FichaEncontro fichaSalva = repository.save(ficha);

        auditoria.registrar("FICHA", fichaSalva.getId(), fichaSalva.getNomeConvidado(), "CREATE",
                Map.of(
                        "encontro", Map.of("para", str(fichaSalva.getNomeEncontro())),
                        "celula",   Map.of("para", str(fichaSalva.getNomeCelula())),
                        "lider",    Map.of("para", str(fichaSalva.getNomeLiderCelula()))
                )
        );

        FichaEncontroResponseDTO response = new FichaEncontroResponseDTO();
        response.setId(fichaSalva.getId());
        response.setNome(fichaSalva.getNomeConvidado());
        return response;
    }

    // =========================
    // ATUALIZAR
    // =========================
    @Transactional
    public FichaEncontro atualizar(Long id, FichaEncontro fichaAtualizada) {
        FichaEncontro fichaExistente = buscarPorId(id);
        BeanUtils.copyProperties(fichaAtualizada, fichaExistente, "id");
        FichaEncontro salva = repository.save(fichaExistente);

        auditoria.registrar("FICHA", id, salva.getNomeConvidado(), "UPDATE", null);

        return salva;
    }

    // =========================
    // EXCLUIR
    // =========================
    @Transactional
    public void excluir(Long id) {
        FichaEncontro ficha = buscarPorId(id);
        repository.delete(ficha);
        auditoria.registrar("FICHA", id, ficha.getNomeConvidado(), "DELETE", null);
    }

    // =========================
    // CONVERSÃO DTO ↔ ENTIDADE
    // =========================
    public FichaEncontro converterDtoParaEntidade(FichaEncontroRequestDTO dto) {
        FichaEncontro ficha = new FichaEncontro();

        ficha.setNomeConvidado(dto.getNome());
        ficha.setTelefone(dto.getTelefone());
        ficha.setDataNascimento(dto.getDataNascimento());
        ficha.setRg(dto.getRg());
        ficha.setPeso(dto.getPeso());
        ficha.setAltura(dto.getAltura());

        if (dto.getSexo() != null)
            ficha.setSexo(dto.getSexo().equalsIgnoreCase("M") ? Sexo.MASCULINO : Sexo.FEMININO);

        if (dto.getEstadoCivil() != null) {
            try { ficha.setEstadoCivil(EstadoCivil.valueOf(dto.getEstadoCivil().toUpperCase())); }
            catch (Exception e) { ficha.setEstadoCivil(null); }
        }

        ficha.setNomeEncontro(dto.getNomeEncontro() != null ? dto.getNomeEncontro() : "Encontro com Deus");
        ficha.setLocalEncontro(dto.getLocalEncontro() != null ? dto.getLocalEncontro() : "Sede / Acampamento");
        ficha.setNomeConvidador(dto.getNomeConvidador() != null ? dto.getNomeConvidador() : "Não informado");
        ficha.setCelulaConvidador(dto.getCelulaConvidador() != null ? dto.getCelulaConvidador() : dto.getNomeLiderCelula());
        ficha.setDataInicio(dto.getDataInicio() != null ? dto.getDataInicio() : LocalDate.now());
        ficha.setDataFim(dto.getDataFim() != null ? dto.getDataFim() : LocalDate.now().plusDays(2));
        ficha.setTipoEncontro(dto.getTipoEncontro() != null ? dto.getTipoEncontro() : TipoEncontro.ENCONTRO_COM_DEUS);
        ficha.setTomaMedicamento(dto.isTomaMedicamento());
        ficha.setQualMedicamento(dto.getQualMedicamento());
        ficha.setTemProblemasSaude(dto.isTemProblemasSaude());
        ficha.setQualProblemaSaude(dto.getQualProblemaSaude());
        ficha.setLiderResponsavel(dto.getNomeLiderCelula());
        ficha.setNomeCelula(dto.getNomeLiderCelula());
        ficha.setNomeFamiliarContato(dto.getNomeFamiliarContato());
        ficha.setTelefoneFamiliarContato(dto.getTelefoneFamiliarContato());
        ficha.setEndereco(dto.getEndereco());
        ficha.setBairro(dto.getBairro());
        ficha.setCidade(dto.getCidade());
        ficha.setEstado(dto.getEstado());
        ficha.setAceitouJesus(dto.isAceitouJesus());
        ficha.setJaEraCristao(dto.isJaEraCristao());

        return ficha;
    }

    public FichaEncontroRequestDTO converterEntidadeParaDto(FichaEncontro ficha) {
        FichaEncontroRequestDTO dto = new FichaEncontroRequestDTO();
        dto.setNome(ficha.getNomeConvidado());
        dto.setTelefone(ficha.getTelefone());
        dto.setDataNascimento(ficha.getDataNascimento());
        dto.setSexo(ficha.getSexo() == Sexo.MASCULINO ? "M" : "F");
        dto.setEstadoCivil(ficha.getEstadoCivil() != null ? ficha.getEstadoCivil().name() : null);
        dto.setPeso(ficha.getPeso());
        dto.setAltura(ficha.getAltura());
        dto.setRg(ficha.getRg());
        dto.setEndereco(ficha.getEndereco());
        dto.setBairro(ficha.getBairro());
        dto.setCidade(ficha.getCidade());
        dto.setEstado(ficha.getEstado());
        dto.setNomeLiderCelula(ficha.getLiderResponsavel());
        dto.setNomeFamiliarContato(ficha.getNomeFamiliarContato());
        dto.setTelefoneFamiliarContato(ficha.getTelefoneFamiliarContato());
        dto.setNomeEncontro(ficha.getNomeEncontro());
        dto.setDataInicio(ficha.getDataInicio());
        dto.setDataFim(ficha.getDataFim());
        dto.setLocalEncontro(ficha.getLocalEncontro());
        dto.setTomaMedicamento(ficha.isTomaMedicamento());
        dto.setQualMedicamento(ficha.getQualMedicamento());
        dto.setTemProblemasSaude(ficha.isTemProblemasSaude());
        dto.setQualProblemaSaude(ficha.getQualProblemaSaude());
        dto.setTemApneia(ficha.isTemApneia());
        dto.setFrequentaCelula(ficha.isFrequentaCelula());
        dto.setNomeCelula(ficha.getNomeCelula());
        dto.setAceitouJesus(ficha.isAceitouJesus());
        dto.setJaEraCristao(ficha.isJaEraCristao());
        return dto;
    }

    // =========================
    // LISTAGENS E BUSCAS
    // =========================
    public FichaEncontro buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ficha não encontrada com o ID: " + id));
    }

    public List<FichaEncontro> listarTodas() {
        return repository.findAll();
    }

    public List<FichaEncontro> buscarPorNomeConvidado(String nome) {
        return repository.findByNomeConvidadoContainingIgnoreCase(nome);
    }

    public List<FichaEncontro> listarPorEncontro(String nomeEncontro) {
        return repository.findByNomeEncontro(nomeEncontro);
    }

    public Long contarTotalFichas() {
        return repository.count();
    }

    public List<FichaEncontroResponseDTO> buscarPorData(LocalDate data) {
        List<FichaEncontro> fichas = data != null
                ? repository.findByDataInicio(data)
                : repository.findAll();

        return fichas.stream().map(ficha -> {
            FichaEncontroResponseDTO dto = new FichaEncontroResponseDTO();
            dto.setId(ficha.getId());
            dto.setNome(ficha.getNomeConvidado());
            dto.setTelefone(ficha.getTelefone());
            dto.setSexo(ficha.getSexo() != null ? ficha.getSexo().name() : null);
            return dto;
        }).toList();
    }

    public List<FichaEncontro> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null)
            throw new IllegalArgumentException("As datas de início e fim devem ser informadas.");
        return repository.findByDataInicioBetween(dataInicio, dataFim);
    }
}