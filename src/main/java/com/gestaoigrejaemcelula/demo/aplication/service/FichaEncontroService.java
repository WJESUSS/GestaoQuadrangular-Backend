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

@Service
public class FichaEncontroService {

    private final FichaEncontroRepository repository;
    private final UsuarioRepository usuarioRepository;


    public FichaEncontroService(FichaEncontroRepository repository, UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }

    // =========================
    // CRIAÇÃO
    // =========================
    @Transactional
    public FichaEncontroResponseDTO criar(FichaEncontroRequestDTO dto) {
        // 1. Busca o usuário logado
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado: " + username));

        // 2. CONVERSÃO MANUAL: DTO -> ENTITY
        FichaEncontro ficha = new FichaEncontro();

        // Dados Pessoais
        // CORREÇÃO: No DTO é getNome(), na ficha é setNomeConvidador?
        // Geralmente na ficha o campo principal é setNome(dto.getNome())
        ficha.setNomeConvidado(dto.getNomeConvidador());
        ficha.setDataNascimento(dto.getDataNascimento());
        ficha.setEndereco(dto.getEndereco());
        ficha.setBairro(dto.getBairro());
        ficha.setCidade(dto.getCidade());
        ficha.setTelefone(dto.getTelefone());

        // Enums (com tratamento para evitar quebras)
        if (dto.getSexo() != null && !dto.getSexo().isEmpty()) {
            ficha.setSexo(Sexo.valueOf(dto.getSexo().toUpperCase().trim()));
        }
        if (dto.getEstadoCivil() != null && !dto.getEstadoCivil().isEmpty()) {
            ficha.setEstadoCivil(EstadoCivil.valueOf(dto.getEstadoCivil().toUpperCase().trim()));
        }

        ficha.setRg(dto.getRg());
        ficha.setEstado(dto.getEstado());
        ficha.setPeso(dto.getPeso());
        ficha.setAltura(dto.getAltura());

        // Saúde
        ficha.setTomaMedicamento(dto.isTomaMedicamento());
        ficha.setQualMedicamento(dto.getQualMedicamento());
        ficha.setTemProblemasSaude(dto.isTemProblemasSaude());
        ficha.setQualProblemaSaude(dto.getQualProblemaSaude());
        ficha.setTemApneia(dto.isTemApneia());

        // Convidador e Célula
        ficha.setNomeConvidador(dto.getNomeConvidador());
        ficha.setCelulaConvidador(dto.getCelulaConvidador());
        ficha.setTipoEncontro(dto.getTipoEncontro());
        ficha.setNomeLiderCelula(dto.getNomeLiderCelula());
        ficha.setNomeFamiliarContato(dto.getNomeFamiliarContato());
        ficha.setTelefoneFamiliarContato(dto.getTelefoneFamiliarContato());
        ficha.setFrequentaCelula(dto.isFrequentaCelula());
        ficha.setNomeCelula(dto.getNomeCelula());

        // Espiritual e Encontro
        ficha.setAceitouJesus(dto.isAceitouJesus());
        ficha.setJaEraCristao(dto.isJaEraCristao());
        ficha.setNomeEncontro(dto.getNomeEncontro());
        ficha.setDataInicio(dto.getDataInicio());
        ficha.setDataFim(dto.getDataFim());
        ficha.setLocalEncontro(dto.getLocalEncontro());

        // 3. VINCULA O USUÁRIO (Resolve o erro do usuario_id null)
        ficha.setUsuario(usuario);

        // 4. Salva no banco
        FichaEncontro fichaSalva = repository.save(ficha);

        // 5. CONVERSÃO MANUAL: ENTITY -> RESPONSE DTO
        FichaEncontroResponseDTO response = new FichaEncontroResponseDTO();
        response.setId(fichaSalva.getId());
        response.setNome(fichaSalva.getNomeConvidado()); // Use o campo que você salvou no banco

        // Se precisar de mais campos no retorno para o React, adicione aqui:
        // response.setTelefone(fichaSalva.getTelefone());

        return response;
    }
    // =========================
    // CONVERSÃO DTO ↔ ENTIDADE
    // =========================
    public FichaEncontro converterDtoParaEntidade(FichaEncontroRequestDTO dto) {
        FichaEncontro ficha = new FichaEncontro();

        // 1️⃣ Dados Pessoais
        ficha.setNomeConvidado(dto.getNome());
        ficha.setTelefone(dto.getTelefone());
        ficha.setDataNascimento(dto.getDataNascimento());
        ficha.setRg(dto.getRg());
        ficha.setPeso(dto.getPeso());
        ficha.setAltura(dto.getAltura());

        if (dto.getSexo() != null) {
            ficha.setSexo(dto.getSexo().equalsIgnoreCase("M") ? Sexo.MASCULINO : Sexo.FEMININO);
        }

        if (dto.getEstadoCivil() != null) {
            try {
                ficha.setEstadoCivil(EstadoCivil.valueOf(dto.getEstadoCivil().toUpperCase()));
            } catch (Exception e) { ficha.setEstadoCivil(null); }
        }

        // 2️⃣ Campos Obrigatórios (Proteção contra o erro 'local_encontro' violates not-null)
        ficha.setNomeEncontro(dto.getNomeEncontro() != null ? dto.getNomeEncontro() : "Encontro com Deus");
        ficha.setLocalEncontro(dto.getLocalEncontro() != null ? dto.getLocalEncontro() : "Sede / Acampamento");
        ficha.setDataInicio(dto.getDataInicio() != null ? dto.getDataInicio() : LocalDate.now());
// Dentro do seu método converterDtoParaEntidade:

// Dados do Convidador (Proteção para o erro atual)
        ficha.setNomeConvidador(dto.getNomeConvidador() != null ? dto.getNomeConvidador() : "Não informado");
        ficha.setCelulaConvidador(dto.getCelulaConvidador() != null ? dto.getCelulaConvidador() : dto.getNomeLiderCelula());

// Datas (Garantir que não fiquem nulas)
        ficha.setDataInicio(dto.getDataInicio() != null ? dto.getDataInicio() : LocalDate.now());
        ficha.setDataFim(dto.getDataFim() != null ? dto.getDataFim() : LocalDate.now().plusDays(2));

// Outros campos obrigatórios que podem travar o banco
        // O erro acontece aqui porque você mistura String com Enum
// CORREÇÃO:
        ficha.setTipoEncontro(dto.getTipoEncontro() != null
                ? dto.getTipoEncontro()
                : TipoEncontro.ENCONTRO_COM_DEUS); // Use a constante do Enum, não "String"
        ficha.setLocalEncontro(dto.getLocalEncontro() != null ? dto.getLocalEncontro() : "Local Padrão");
        // 3️⃣ Saúde e Célula
        ficha.setTomaMedicamento(dto.isTomaMedicamento());
        ficha.setQualMedicamento(dto.getQualMedicamento());
        ficha.setTemProblemasSaude(dto.isTemProblemasSaude());
        ficha.setQualProblemaSaude(dto.getQualProblemaSaude());
        ficha.setLiderResponsavel(dto.getNomeLiderCelula());
        ficha.setNomeCelula(dto.getNomeLiderCelula());
        ficha.setNomeFamiliarContato(dto.getNomeFamiliarContato());
        ficha.setTelefoneFamiliarContato(dto.getTelefoneFamiliarContato());

        // 4️⃣ Endereço
        ficha.setEndereco(dto.getEndereco());
        ficha.setBairro(dto.getBairro());
        ficha.setCidade(dto.getCidade());
        ficha.setEstado(dto.getEstado());

        // 5️⃣ Decisões
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

    @Transactional
    public FichaEncontro atualizar(Long id, FichaEncontro fichaAtualizada) {
        FichaEncontro fichaExistente = buscarPorId(id);
        // "id" é ignorado para não sobrescrever a chave primária
        BeanUtils.copyProperties(fichaAtualizada, fichaExistente, "id");
        return repository.save(fichaExistente);
    }

    @Transactional
    public void excluir(Long id) {
        FichaEncontro ficha = buscarPorId(id);
        repository.delete(ficha);
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

        List<FichaEncontro> fichas;

        if (data != null) {
            fichas = repository.findByDataInicio(data);
        } else {
            fichas = repository.findAll();
        }

        return fichas.stream()
                .map(ficha -> {

                    FichaEncontroResponseDTO dto = new FichaEncontroResponseDTO();

                    dto.setId(ficha.getId());
                    dto.setNome(ficha.getNomeConvidado()); // ✅ CORRETO
                    dto.setTelefone(ficha.getTelefone());

                    dto.setSexo(
                            ficha.getSexo() != null ? ficha.getSexo().name() : null
                    );

                    return dto;
                })
                .toList();


}

    public List<FichaEncontro> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null) {
            // Exemplo: se não enviar data, busca os últimos 30 dias ou retorna erro
            throw new IllegalArgumentException("As datas de início e fim devem ser informadas.");
        }
        return repository.findByDataInicioBetween(dataInicio, dataFim);
    }
}
