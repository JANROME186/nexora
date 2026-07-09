package com.nexora.hop.platformfoundation.catalogtestconfiguration.panelcatalog.domain;

import java.util.List;
import java.util.Optional;

public interface PanelDefinitionRepository {

    PanelDefinition save(PanelDefinition panel);

    Optional<PanelDefinition> findById(String panelId);

    List<PanelDefinition> findByLaboratoryId(String laboratoryId);

    boolean existsByCode(String laboratoryId, String code, String excludePanelId);

    void replaceMembers(String panelId, List<PanelMember> members);

    List<PanelMember> findMembers(String panelId);
}
