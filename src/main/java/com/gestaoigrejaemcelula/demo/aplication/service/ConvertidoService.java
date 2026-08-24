package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.AutorizarMembresiaDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.ConvertidoRequestDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.ConvertidoResponseDTO;
import com.gestaoigrejaemcelula.demo.domain.entity.Convertido;
import com.gestaoigrejaemcelula.demo.domain.entity.Membro;
import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusConvertido;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusMembro;
import com.gestaoigrejaemcelula.demo.domain.repository.ConvertidoRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.MembroRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.UsuarioRepository;
import com.gestaoigrejaemcelula.demo.web.handler.BusinessException;
import com.gestaoigrejaemcelula.demo.web.handler.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class ConvertidoService {

    private static final String ENTIDADE = "CONVERTIDO";

    private final ConvertidoRepository convertidoRepository;
    private final MembroRepository membroRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaHelper auditoria;

    public ConvertidoService(
            ConvertidoRepository convertidoRepository,
            MembroRepository membroRepository,
            UsuarioRepository usuarioRepository,
            AuditoriaHelper auditoria) {
        this.convertidoRepository = convertidoRepository;
        this.membroRepository = membroRepository;
        this.usuarioRepository = usuarioRepository;
        this.auditoria = auditoria;
    }

    // -------------------------------------------------------
    // SECRETARIA: Registra ficha de convertido
    // -------------------------------------------------------

    @Transactional
    public ConvertidoResponseDTO registrar(ConvertidoRequestDTO dto) {
        Usuario secretario = getUsuarioLogado();

        Convertido convertido = new Convertido();
        convertido.setStatus(StatusConvertido.AGUARDANDO_BATISMO);
        convertido.setRegistradoPor(secretario);
        copiarDtoParaConvertido(dto, convertido);

        Convertido salvo = convertidoRepository.save(convertido);

        auditoria.registrar(ENTIDADE, salvo.getId(), salvo.getNome(),
                "CREATE", Map.of("secretario", secretario.getNome(), "status", "AGUARDANDO_BATISMO"));

        return new ConvertidoResponseDTO(salvo);
    }

    // -------------------------------------------------------
    // SECRETARIA: Lista convertidos
    // -------------------------------------------------------

    @Transactional(readOnly = true)
    public Page<ConvertidoResponseDTO> listar(Pageable pageable) {
        return convertidoRepository.findAll(pageable)
                .map(ConvertidoResponseDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<ConvertidoResponseDTO> listarPorStatus(StatusConvertido status, Pageable pageable) {
        return convertidoRepository.findByStatus(status, pageable)
                .map(ConvertidoResponseDTO::new);
    }

    @Transactional(readOnly = true)
    public ConvertidoResponseDTO buscarPorId(Long id) {
        return new ConvertidoResponseDTO(buscarEntidade(id));
    }

    // -------------------------------------------------------
    // SECRETARIA: Autoriza membresia após o batismo na igreja
    // -------------------------------------------------------

    @Transactional
    public ConvertidoResponseDTO autorizarMembresia(Long id, AutorizarMembresiaDTO dto) {
        Convertido convertido = buscarEntidade(id);

        if (convertido.getStatus() != StatusConvertido.AGUARDANDO_BATISMO) {
            throw new BusinessException(
                    "Este convertido já foi processado: " + convertido.getStatus().getDescricao());
        }

        Usuario secretario = getUsuarioLogado();

        // Registra o batismo nesta igreja
        convertido.setDataBatismoIgreja(dto.getDataBatismo());
        convertido.setAutorizadoPor(secretario);
        convertido.setDataAutorizacao(java.time.LocalDateTime.now());

        // Cria o membro de verdade
        Membro membro = criarMembroDoConvertido(convertido, dto.getDataBatismo());

        convertido.setStatus(StatusConvertido.MEMBRO);
        convertido.setMembroCriadoId(membro.getId());

        auditoria.registrar(ENTIDADE, convertido.getId(), convertido.getNome(),
                "AUTORIZADO",
                Map.of("secretario", secretario.getNome(),
                        "dataBatismo", dto.getDataBatismo().toString(),
                        "membroCriadoId", membro.getId()));

        return new ConvertidoResponseDTO(convertidoRepository.save(convertido));
    }

    // -------------------------------------------------------
    // PRIVADOS
    // -------------------------------------------------------

    /** Cria um Membro real a partir da ficha do convertido batizado */
    private Membro criarMembroDoConvertido(Convertido convertido, java.time.LocalDate dataBatismo) {
        Membro membro = new Membro();

        membro.setNome(convertido.getNome());
        membro.setTelefone(convertido.getTelefone());
        membro.setEmail(convertido.getEmail());
        membro.setCpf(convertido.getCpf());
        membro.setRg(convertido.getRg());
        membro.setEstadoCivil(convertido.getEstadoCivil());
        membro.setDataNascimento(convertido.getDataNascimento());
        membro.setDataConversao(convertido.getDataConversao());
        membro.setDataBatismo(dataBatismo);
        membro.setNomeMae(convertido.getNomeMae());
        membro.setNomePai(convertido.getNomePai());
        membro.setNomeConjuge(convertido.getNomeConjuge());
        membro.setNaturalidade(convertido.getNaturalidade());
        membro.setGrauEscolaridade(convertido.getGrauEscolaridade());
        membro.setCurso(convertido.getCurso());
        membro.setProfissao(convertido.getProfissao());
        membro.setEndereco(convertido.getEndereco());
        membro.setNumero(convertido.getNumero());
        membro.setBairro(convertido.getBairro());
        membro.setCidade(convertido.getCidade());
        membro.setCep(convertido.getCep());
        membro.setUf(convertido.getUf());
        membro.setPertenceOutraReligiao(convertido.getPertenceOutraReligiao());
        membro.setQualReligiao(convertido.getQualReligiao());

        // Batizou nesta igreja — marca como batizado nas águas
        membro.setBatizadoNasAguas(true);
        membro.setDataBatizadoNasAguas(dataBatismo);
        membro.setIgrejaBatizadoNasAguas(convertido.getIgrejaBatizadoNasAguas());

        membro.setBatizadoEspiritoSanto(convertido.getBatizadoEspiritoSanto());
        membro.setTipoArrolamento(convertido.getTipoArrolamento());
        membro.setJurisdicaoArrolamento(convertido.getJurisdicaoArrolamento());
        membro.setArroladoPor(convertido.getArroladoPor());
        membro.setObservacoes(convertido.getObservacoes());

        // Status padrão = ATIVO
        membro.setStatus(StatusMembro.ATIVO);

        Membro salvo = membroRepository.save(membro);

        auditoria.registrar("MEMBRO", salvo.getId(), salvo.getNome(), "CREATE",
                Map.of("origem", ENTIDADE, "convertidoId", convertido.getId()));

        return salvo;
    }

    private void copiarDtoParaConvertido(ConvertidoRequestDTO dto, Convertido convertido) {
        convertido.setNome(dto.getNome());
        convertido.setTelefone(dto.getTelefone());
        convertido.setEmail(dto.getEmail());
        convertido.setCpf(dto.getCpf());
        convertido.setRg(dto.getRg());
        convertido.setEstadoCivil(dto.getEstadoCivil());
        convertido.setDataNascimento(dto.getDataNascimento());
        convertido.setDataConversao(dto.getDataConversao());
        convertido.setNomeMae(dto.getNomeMae());
        convertido.setNomePai(dto.getNomePai());
        convertido.setNomeConjuge(dto.getNomeConjuge());
        convertido.setNaturalidade(dto.getNaturalidade());
        convertido.setGrauEscolaridade(dto.getGrauEscolaridade());
        convertido.setCurso(dto.getCurso());
        convertido.setProfissao(dto.getProfissao());
        convertido.setEndereco(dto.getEndereco());
        convertido.setNumero(dto.getNumero());
        convertido.setBairro(dto.getBairro());
        convertido.setCidade(dto.getCidade());
        convertido.setCep(dto.getCep());
        convertido.setUf(dto.getUf());
        convertido.setPertenceOutraReligiao(dto.getPertenceOutraReligiao());
        convertido.setQualReligiao(dto.getQualReligiao());
        convertido.setBatizadoNasAguas(dto.getBatizadoNasAguas());
        convertido.setDataBatizadoNasAguas(dto.getDataBatizadoNasAguas());
        convertido.setIgrejaBatizadoNasAguas(dto.getIgrejaBatizadoNasAguas());
        convertido.setBatizadoEspiritoSanto(dto.getBatizadoEspiritoSanto());
        convertido.setTipoArrolamento(dto.getTipoArrolamento());
        convertido.setJurisdicaoArrolamento(dto.getJurisdicaoArrolamento());
        convertido.setArroladoPor(dto.getArroladoPor());
        convertido.setObservacoes(dto.getObservacoes());
    }

    private Convertido buscarEntidade(Long id) {
        return convertidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Convertido não encontrado: " + id));
    }

    private Usuario getUsuarioLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário logado não encontrado"));
    }
}
