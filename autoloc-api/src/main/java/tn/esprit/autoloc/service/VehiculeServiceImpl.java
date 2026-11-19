package tn.esprit.autoloc.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.Utils;
import tn.esprit.autoloc.calcul.OffreStrategy;
import tn.esprit.autoloc.calcul.VehiculeOffre;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.ConflictException;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IEquipementRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;

import java.util.Map;

@Service
@Primary
public class VehiculeServiceImpl extends GenericServiceImpl<Vehicule, Long> implements IVehiculeService {

    private final IAgenceRepository agenceRepository;
    private final IEquipementRepository equipementRepository;

    // cle = nom du bean = nom de l'enumeration (STANDARD, WEEKEND, LONGUE_DUREE)
    private final Map<String, OffreStrategy> strategies;

    public VehiculeServiceImpl(IVehiculeRepository repository, IAgenceRepository agenceRepository, IEquipementRepository equipementRepository, Map<String, OffreStrategy> strategies) {
        super(repository);
        this.agenceRepository = agenceRepository;
        this.equipementRepository = equipementRepository;
        this.strategies = strategies;
    }

    @Override
    protected String getEntityName() {
        return "vehicule";
    }

    @Override
    protected Long getId(Vehicule vehicule) {
        return vehicule.getIdVehicule();
    }

    @Override
    protected void setFields(Vehicule source, Vehicule target) {
        target.setImmatriculation(source.getImmatriculation());
        target.setMarque(source.getMarque());
        target.setModele(source.getModele());
        target.setCategorie(source.getCategorie());
        target.setTarifJournalier(source.getTarifJournalier());
        target.setStatut(source.getStatut());
        target.setAgence(source.getAgence());
        target.setEquipement(source.getEquipement());
    }

    @Override
    public boolean check(Vehicule vehicule, boolean isNew) {
        return Utils.isNotEmpty(vehicule.getImmatriculation()) &&
                Utils.isNotEmpty(vehicule.getMarque()) &&
                Utils.isNotEmpty(vehicule.getModele()) &&
                vehicule.getCategorie() != null &&
                vehicule.getTarifJournalier() != null &&
                vehicule.getTarifJournalier().signum() > 0 &&
                vehicule.getStatut() != null;
    }
    public double calculTarif(Vehicule vehicule, int nbjours, VehiculeOffre offre) {
        if (nbjours <= 0) {
            throw new IllegalArgumentException("le nombre de jours doit etre strictement positif");
        }
        if (offre == null) {
            throw new IllegalArgumentException("l'offre est obligatoire");
        }
        OffreStrategy strategy = strategies.get(offre.getBeanName());
        if (strategy == null) {
            throw new IllegalArgumentException("offre non supportee : " + offre);
        }
        double total = strategy.calculer(vehicule.getTarifJournalier().doubleValue(), nbjours);
        return Math.round(total * 100.0) / 100.0;
    }

    @Override
    public Vehicule desaffecter(Long id) {
        var vehicule = findById(id);
        vehicule.setAgence(null);
        return repository.save(vehicule);
    }

    @Override
    public Vehicule affecter(Long id, Long idAgence) {
        var vehicule = findById(id);
        var agence =agenceRepository.findById(idAgence).
                orElseThrow(() -> new ResourceNotFoundException("agence",idAgence.toString()));
        vehicule.setAgence(agence);
        return repository.save(vehicule);
    }

    @Override
    @Transactional
    public Vehicule affecterequipement(Long id, Long idEquipement) {
        var vehicule = repository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("vehicule",id.toString()));
        var equipement =equipementRepository.findById(idEquipement).
                orElseThrow(() -> new ResourceNotFoundException("equipement",idEquipement.toString()));
        vehicule.getEquipement().add(equipement);
        return repository.save(vehicule);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        var vehicule = findById(id); // throws ResourceNotFoundException -> 404
        if (vehicule.getEquipement() != null && !vehicule.getEquipement().isEmpty()) {
            throw new ConflictException(
                    "Impossible de supprimer le véhicule " + id + " : des équipements lui sont affectés.");
        }
        repository.delete(vehicule);
    }

}
