package tn.esprit.autoloc.calcul;

import org.springframework.stereotype.Component;

@Component("standard")
public class StandardStrategy implements OffreStrategy {

    @Override
    public double calculer(double tarifJournalier, int nbjours) {
        return tarifJournalier * nbjours;
    }
}
