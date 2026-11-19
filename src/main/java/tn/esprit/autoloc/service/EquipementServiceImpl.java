package tn.esprit.autoloc.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.Utils;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.Vehicule;                 // added
import tn.esprit.autoloc.repository.IEquipementRepository;

@Service
@Primary
public class EquipementServiceImpl extends GenericServiceImpl<Equipement, Long> implements IEquipementService {

    public EquipementServiceImpl(IEquipementRepository repository) {
        super(repository);
    }

    @Override
    protected String getEntityName() {
        return "equipement";
    }

    @Override
    protected Long getId(Equipement equipement) {
        return equipement.getIdEquipement();
    }

    @Override
    protected void setFields(Equipement source, Equipement target) {
        target.setLibelle(source.getLibelle());
    }

    @Override
    public boolean check(Equipement equipement, boolean isNew) {
        return Utils.isNotEmpty(equipement.getLibelle());
    }

    @Override                                              // added
    @Transactional
    public void deleteById(Long id) {
        Equipement equipement = findById(id);              // 404 if missing
        if (equipement.getVehicule() != null) {
            for (Vehicule v : equipement.getVehicule()) {
                v.getEquipement().remove(equipement);
            }
        }
        repository.delete(equipement);
    }
}