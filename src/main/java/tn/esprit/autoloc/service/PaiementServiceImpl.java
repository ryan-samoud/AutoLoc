package tn.esprit.autoloc.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.repository.IPaiementRepository;

@Service
@Primary
public class PaiementServiceImpl extends GenericServiceImpl<Paiement, Long> implements IPaiementService {

    private static final String READ_ONLY = "paiement est en lecture seule";

    public PaiementServiceImpl(IPaiementRepository repository) {
        super(repository);
    }

    @Override
    protected String getEntityName() {
        return "paiement";
    }

    @Override
    protected Long getId(Paiement paiement) {
        return paiement.getIdPaiement();
    }

    @Override
    protected void setFields(Paiement source, Paiement target) {
        throw new UnsupportedOperationException(READ_ONLY);
    }

    @Override
    public boolean check(Paiement paiement, boolean isNew) {
        return false;
    }

    @Override
    public Paiement create(Paiement paiement) {
        throw new UnsupportedOperationException(READ_ONLY);
    }

    @Override
    public Paiement update(Long id, Paiement paiement) {
        throw new UnsupportedOperationException(READ_ONLY);
    }

    @Override
    public void deleteById(Long id) {
        throw new UnsupportedOperationException(READ_ONLY);
    }
}