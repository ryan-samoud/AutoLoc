package tn.esprit.autoloc.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.Utils;
import tn.esprit.autoloc.calcul.OffreStrategy;
import tn.esprit.autoloc.calcul.VehiculeOffre;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IVehiculeRepository;

import java.util.Map;

@Service
@Primary
public class VehiculeServiceImpl extends GenericServiceImpl<Vehicule, Long> implements IVehiculeService {

    // cle = nom du bean = nom de l'enumeration (STANDARD, WEEKEND, LONGUE_DUREE)
    private final Map<String, OffreStrategy> strategies;

    public VehiculeServiceImpl(IVehiculeRepository repository, Map<String, OffreStrategy> strategies) {
        super(repository);
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
        OffreStrategy strategy = strategies.get(offre.name());
        if (strategy == null) {
            throw new IllegalArgumentException("offre non supportee : " + offre);
        }
        double total = strategy.calculer(vehicule.getTarifJournalier().doubleValue(), nbjours);
        return Math.round(total * 100.0) / 100.0;
    }

}
