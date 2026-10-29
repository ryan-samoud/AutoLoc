package tn.esprit.autoloc.calcul;

import org.springframework.stereotype.Component;

@Component("WEEKEND")
public class WeekendStrategy implements OffreStrategy {

    @Override
    public double calculer(double tarifJournalier, int nbjours) {
        return tarifJournalier * 0.8 * nbjours;          // tarif journalier -20 %
    }
}
