package tn.esprit.autoloc.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.Utils;
import tn.esprit.autoloc.calcul.VehiculeOffre;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IAgenceRepository;

import java.util.List;

@Service
@Primary
public class AgenceServiceImpl extends GenericServiceImpl<Agence, Long> implements IAgenceService, ISimuler {
    private final VehiculeServiceImpl vehiculeServiceImpl;
    private final IEmployeService employeService;

    public AgenceServiceImpl(IAgenceRepository repository,
                             VehiculeServiceImpl vehiculeServiceImpl,
                             IEmployeService employeService) {
        super(repository);
        this.vehiculeServiceImpl = vehiculeServiceImpl;
        this.employeService = employeService;
    }

    @Override
    protected String getEntityName() {
        return "agence";
    }

    @Override
    protected Long getId(Agence agence) {
        return agence.getIdAgence();
    }

    @Override
    protected void setFields(Agence source, Agence target) {
        target.setNom(source.getNom());
        target.setTelephone(source.getTelephone());
        target.setAdresse(source.getAdresse());
        target.setVille(source.getVille());
        target.setEffectif(source.getEffectif());
        // employes : modifiés uniquement via addEmploye / removeEmploye
    }

    @Override
    public boolean check(Agence agence, boolean isNew) {
        return Utils.isNotEmpty(agence.getNom()) &&
                Utils.isNotEmpty(agence.getTelephone()) &&
                agence.getEffectif() >= 0;
    }

    @Override
    @Transactional
    public Agence ajouterEmployes(Long id, Agence agence, List<Employe> employes) {
        if (employes == null || employes.isEmpty()) {
            throw new IllegalArgumentException("la liste des employes est vide");
        }

        // 0. valider TOUS les employés avant la moindre écriture
        for (Employe employe : employes) {
            if (!employeService.check(employe, true)) {
                throw new IllegalArgumentException("employe invalide : " + employe.getNom());
            }
        }

        // 1. mettre à jour l'agence
        Agence updated = update(id, agence);

        // 2. insérer les employés
        for (Employe employe : employes) {
            employe.setAgence(updated);
            employeService.create(employe);
        }

        // 3. effectif = nombre réel d'employés de l'agence
        updated.setEffectif(updated.getEmployes().size());
        return repository.save(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasVehicules(Long id) {
        Agence agence = findById(id);
        return agence.getVehicule() != null && !agence.getVehicule().isEmpty();
    }

    public String simuler(Agence agence, int nbjours, VehiculeOffre offre){
        StringBuilder rapport = new StringBuilder();
        rapport.append("id: "+agence.getIdAgence()+", nom: "+agence.getNom()+", numero de tel: "+agence.getTelephone()+"\n");
        double total = 0;
        for (Vehicule vehicule : agence.getVehicule()){
            total+= vehiculeServiceImpl.calculTarif(vehicule,nbjours,offre);
        }
        rapport.append("total jours: "+total);
        return rapport.toString();
    }
}