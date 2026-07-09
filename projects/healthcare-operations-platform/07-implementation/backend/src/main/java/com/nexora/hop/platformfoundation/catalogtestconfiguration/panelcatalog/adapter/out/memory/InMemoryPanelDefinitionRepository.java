package com.nexora.hop.platformfoundation.catalogtestconfiguration.panelcatalog.adapter.out.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.panelcatalog.domain.PanelDefinition;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.panelcatalog.domain.PanelDefinitionRepository;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.panelcatalog.domain.PanelMember;

@Repository
@Profile("!local")
class InMemoryPanelDefinitionRepository implements PanelDefinitionRepository {

    private final Map<String, PanelDefinition> panels = new ConcurrentHashMap<>();
    private final Map<String, List<PanelMember>> members = new ConcurrentHashMap<>();

    @Override
    public PanelDefinition save(PanelDefinition panel) {
        panels.put(panel.panelId(), panel);
        return panel;
    }

    @Override
    public Optional<PanelDefinition> findById(String panelId) {
        return Optional.ofNullable(panels.get(panelId));
    }

    @Override
    public List<PanelDefinition> findByLaboratoryId(String laboratoryId) {
        return panels.values().stream().filter(panel -> panel.laboratoryId().equals(laboratoryId)).toList();
    }

    @Override
    public boolean existsByCode(String laboratoryId, String code, String excludePanelId) {
        return panels.values().stream()
                .anyMatch(panel -> panel.laboratoryId().equals(laboratoryId)
                        && panel.code().equals(code)
                        && !panel.panelId().equals(excludePanelId));
    }

    @Override
    public void replaceMembers(String panelId, List<PanelMember> panelMembers) {
        members.put(panelId, new ArrayList<>(panelMembers));
    }

    @Override
    public List<PanelMember> findMembers(String panelId) {
        return List.copyOf(members.getOrDefault(panelId, List.of()));
    }
}
