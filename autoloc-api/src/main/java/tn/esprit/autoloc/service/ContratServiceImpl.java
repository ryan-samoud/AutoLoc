package tn.esprit.autoloc.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.repository.IContratRepository;

import java.math.BigDecimal;

@Service
@Primary
public class ContratServiceImpl extends GenericServiceImpl<Contrat, Long> implements IContratService {

    public ContratServiceImpl(IContratRepository repository) {
        super(repository);
    }

    @Override
    protected String getEntityName() {
        return "contrat";
    }

    @Override
    protected Long getId(Contrat contrat) {
        return contrat.getIdContrat();
    }

    @Override
    protected void setFields(Contrat source, Contrat target) {
        target.setDateSignature(source.getDateSignature());
        target.setMontantTotal(source.getMontantTotal());
        target.setValide(source.isValide());
    }

    @Override
    public boolean check(Contrat contrat, boolean isNew) {
        return contrat.getDateSignature() != null &&
                contrat.getMontantTotal() != null &&
                contrat.getMontantTotal().signum() >= 0;
    }



}