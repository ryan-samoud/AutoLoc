package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.Utils;
import tn.esprit.autoloc.calcul.VehiculeOffre;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IAgenceRepository;

import java.util.List;

@Service //annotation bean
@RequiredArgsConstructor //annotation Lombok
public class BasicAgenceServiceImpl implements IAgenceService{
    final private IAgenceRepository repository;
    @Override
    public boolean check(Agence agence, boolean isNew) {
        return Utils.isNotEmpty(agence.getNom()) &&
                Utils.isNotEmpty(agence.getTelephone());
    }

    @Override
    public Agence create(Agence agence) {
        if (agence.getIdAgence()!= null){
            throw new IllegalArgumentException("l'identifiant non null");
        }
        if (!check(agence,true)) {
            throw new IllegalArgumentException("le nom ou telephone non null");
        }
        return repository.save(agence);
    }

    @Override
    public Agence findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("agence",id.toString()));
    }

    @Override
    public List<Agence> findAll() {
        return List.of();
    }

    @Override
    public Agence update(Long id, Agence agence) {
        Agence updated = findById(id);
        if (!check(agence,true)) {
            throw new IllegalArgumentException("le nom ou telephone non null");
        }
        updated.setNom(agence.getNom());
        updated.setAdresse(agence.getAdresse());
        updated.setTelephone(agence.getTelephone());
        updated.setVille(agence.getVille());
        updated.setVehicule(agence.getVehicule());
        updated.setEmployes(agence.getEmployes());
        return repository.save(updated);
    }

    @Override
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("agence", id.toString());
        }
        repository.deleteById(id);
    }

    @Override
    public boolean hasVehicules(Long id) {
        return false;
    }


}
