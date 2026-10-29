package tn.esprit.autoloc.io;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.calcul.VehiculeOffre;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.service.ISimuler;

/**
 * Decorator de ISimuler : delegue le calcul au vrai service, affiche l'entete
 * du rapport sur la console et retourne uniquement le total.
 */
@Component
public class SimulerConsole implements ISimuler {

    private static final String PREFIXE_TOTAL = "total des tarifs des vehicules: ";

    private final ISimuler delegate;

    public SimulerConsole(@Qualifier("agenceServiceImpl") ISimuler delegate) {
        this.delegate = delegate;
    }

    @Override
    public String simuler(Agence agence, int nbjours, VehiculeOffre offre) {
        String rapport = delegate.simuler(agence, nbjours, offre);

        int dernierSaut = rapport.lastIndexOf('\n');
        String entete = rapport.substring(0, dernierSaut);
        String ligneTotal = rapport.substring(dernierSaut + 1);

        System.out.println(entete);
        return ligneTotal.startsWith(PREFIXE_TOTAL)
                ? ligneTotal.substring(PREFIXE_TOTAL.length())
                : ligneTotal;
    }
}
