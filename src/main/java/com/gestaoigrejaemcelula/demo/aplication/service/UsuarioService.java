package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.CadastroUsuarioDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.FichaEncontroResponseDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.UsuarioResponseDTO;
import com.gestaoigrejaemcelula.demo.domain.entity.Celula;
import com.gestaoigrejaemcelula.demo.domain.entity.FichaEncontro;
import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;
import com.gestaoigrejaemcelula.demo.domain.enums.Perfil;
import com.gestaoigrejaemcelula.demo.domain.repository.CelulaRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.FichaEncontroRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final CelulaRepository celulaRepository;
    private final FichaEncontroRepository fichaEncontroRepository;

    // 1️⃣ Cadastrar usuário
    @Transactional
    public Usuario cadastrar(CadastroUsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setPerfil(dto.getPerfil());
        usuario.setAtivo(dto.isAtivo());

        if (dto.getCelulaId() != null) {
            Celula celula = celulaRepository.findById(dto.getCelulaId())
                    .orElseThrow(() -> new EntityNotFoundException("Célula não encontrada com ID: " + dto.getCelulaId()));
            usuario.setCelula(celula);
        }

        return usuarioRepository.save(usuario);
    }

    // 2️⃣ Listar todos usuários
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponseDTO::new)
                .collect(Collectors.toList());
    }

    // 3️⃣ Buscar usuário por ID
    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));
    }

    // 4️⃣ Atualizar usuário
    @Transactional
    public Usuario atualizar(Long id, CadastroUsuarioDTO dto) {
        Usuario usuario = buscarPorId(id);
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        if (dto.getSenha() != null && !dto.getSenha().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        }
        usuario.setPerfil(dto.getPerfil());
        usuario.setAtivo(dto.isAtivo());

        if (dto.getCelulaId() != null) {
            Celula celula = celulaRepository.findById(dto.getCelulaId())
                    .orElseThrow(() -> new EntityNotFoundException("Célula não encontrada com ID: " + dto.getCelulaId()));
            usuario.setCelula(celula);
        }

        return usuarioRepository.save(usuario);
    }

    // 5️⃣ Deletar usuário
    @Transactional
    public void deletar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuarioRepository.delete(usuario);
    }

    // 6️⃣ Ativar usuário
    @Transactional
    public void ativar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);
    }

    // 7️⃣ Desativar usuário
    @Transactional
    public void desativar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    // 8️⃣ Alternar status (ativo/inativo)
    @Transactional
    public void alternarStatus(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(!usuario.isAtivo());
        usuarioRepository.save(usuario);
    }

    // 9️⃣ Obter usuário logado (baseado no token JWT)
    @Transactional(readOnly = true)
    public Usuario getUsuarioLogado() throws AccessDeniedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Usuário não autenticado");
        }

        String email = authentication.getName();

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
    }
    @Transactional(readOnly = true)
    public List<FichaEncontroResponseDTO> findByUsuarioLogado(String username) {
        // username aqui é o email (padrão Spring Security)
        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        List<FichaEncontro> fichas = fichaEncontroRepository
                .findByUsuarioIdOrderByDataCriacaoDesc(usuario.getId());

        return fichas.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
    /**
     * Converte FichaEncontro → FichaEncontroResponseDTO (manual, sem MapStruct)
     */
    private FichaEncontroResponseDTO toResponseDTO(FichaEncontro entity) {
        if (entity == null) {
            return null;
        }

        FichaEncontroResponseDTO dto = new FichaEncontroResponseDTO();

        // ID e auditoria
        dto.setId(entity.getId());
        dto.setDataCriacao(entity.getDataCriacao());
        dto.setDataAtualizacao(entity.getDataAtualizacao());
        dto.setStatus(entity.getStatus() != null ? entity.getStatus().name() : "PENDENTE");
        dto.setCriadoPor(entity.getCriadoPor());
        dto.setUsuarioId(entity.getUsuario() != null ? entity.getUsuario().getId() : null);

        // Dados pessoais
        dto.setNome(entity.getNomeConvidado());
        dto.setDataNascimento(entity.getDataNascimento());
        dto.setEndereco(entity.getEndereco());
        dto.setBairro(entity.getBairro());
        dto.setCidade(entity.getCidade());
        dto.setTelefone(entity.getTelefone());
        dto.setSexo(entity.getSexo() != null ? entity.getSexo().name() : null);
        dto.setEstadoCivil(entity.getEstadoCivil() != null ? entity.getEstadoCivil().name() : null);
        dto.setRg(entity.getRg());
        dto.setEstado(entity.getEstado());
        dto.setPeso(entity.getPeso());
        dto.setAltura(entity.getAltura());

        // Saúde
        dto.setTomaMedicamento(entity.isTomaMedicamento());
        dto.setQualMedicamento(entity.getQualMedicamento());
        dto.setTemProblemasSaude(entity.isTemProblemasSaude());
        dto.setQualProblemaSaude(entity.getQualProblemaSaude());
        dto.setTemApneia(entity.isTemApneia());

        // Contatos e líderes
        dto.setNomeConvidador(entity.getNomeConvidador());
        dto.setCelulaConvidador(entity.getCelulaConvidador());
        dto.setNomeLiderCelula(entity.getNomeLiderCelula());
        dto.setNomeFamiliarContato(entity.getNomeFamiliarContato());
        dto.setTelefoneFamiliarContato(entity.getTelefoneFamiliarContato());

        // Participação e célula
        dto.setFrequentaCelula(entity.isFrequentaCelula());
        dto.setNomeCelula(entity.getNomeCelula());
        dto.setOutrosParticipantes(entity.getOutrosParticipantes());

        // Decisões espirituais
        dto.setAceitouJesus(entity.isAceitouJesus());
        dto.setJaEraCristao(entity.isJaEraCristao());

        // Dados do encontro
        dto.setNomeEncontro(entity.getNomeEncontro());
        dto.setLocalEncontro(entity.getLocalEncontro());
        dto.setTipoEncontro(entity.getTipoEncontro());
        dto.setDataInicio(entity.getDataInicio());
        dto.setDataFim(entity.getDataFim());

        return dto;
    }
}