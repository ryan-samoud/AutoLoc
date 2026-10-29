package tn.esprit.autoloc.calcul;

import org.springframework.stereotype.Component;

@Component("LONGUE_DUREE")
public class LongueDureeStrategy implements OffreStrategy {

    @Override
    public double calculer(double tarifJournalier, int nbjours) {
        double total = tarifJournalier * 0.75 * nbjours; // tarif journalier -25 %
        if (total > 2000) {
            total *= 0.95;                               // -5 % si le total depasse 2000
        }
        return total;
    }
}
