package tn.esprit.autoloc.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.Utils;
import tn.esprit.autoloc.calcul.VehiculeOffre;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IAgenceRepository;

@Service
@Primary
public class AgenceServiceImpl extends GenericServiceImpl<Agence, Long> implements IAgenceService,ISimuler {
    private final VehiculeServiceImpl vehiculeServiceImpl;

    public AgenceServiceImpl(IAgenceRepository repository, VehiculeServiceImpl vehiculeServiceImpl) {
        super(repository);
        this.vehiculeServiceImpl = vehiculeServiceImpl;
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
        target.setEmployes(source.getEmployes());
        target.setVehicule(source.getVehicule());
    }

    @Override
    public boolean check(Agence agence, boolean isNew) {
        return Utils.isNotEmpty(agence.getNom()) &&
                Utils.isNotEmpty(agence.getTelephone());
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