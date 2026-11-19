package tn.esprit.autoloc.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.Utils;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IEmployeRepository;

@Service
@Primary
public class EmployeServiceImpl extends GenericServiceImpl<Employe, Long> implements IEmployeService {

    private final IAgenceRepository agenceRepository;

    public EmployeServiceImpl(IEmployeRepository repository, IAgenceRepository agenceRepository) {
        super(repository);
        this.agenceRepository = agenceRepository;
    }

    @Override
    protected String getEntityName() {
        return "employe";
    }

    @Override
    protected Long getId(Employe employe) {
        return employe.getIdEmploye();
    }

    @Override
    protected void setFields(Employe source, Employe target) {
        target.setNom(source.getNom());
        target.setPrenom(source.getPrenom());
        target.setRole(source.getRole());
        target.affecterAgence(resolveAgence(source.getAgence()));
    }

    @Override
    public boolean check(Employe employe, boolean isNew) {
        return Utils.isNotEmpty(employe.getNom()) &&
                Utils.isNotEmpty(employe.getPrenom()) &&
                employe.getRole() != null;
    }

    @Override
    @Transactional
    public Employe create(Employe employe) {
        Agence agence = resolveAgence(employe.getAgence());
        employe.setAgence(null);                 // on affecte ensuite via la méthode symétrique
        Employe saved = super.create(employe);
        saved.affecterAgence(agence);
        return saved;
    }

    @Override
    @Transactional
    public Employe update(Long id, Employe employe) {
        return super.update(id, employe);
    }

    // recharge l'agence depuis la base (l'objet du JSON est détaché et incomplet)
    private Agence resolveAgence(Agence detached) {
        if (detached == null || detached.getIdAgence() == null) {
            return null;
        }
        return agenceRepository.findById(detached.getIdAgence())
                .orElseThrow(() -> new ResourceNotFoundException("agence", detached.getIdAgence().toString()));
    }
}