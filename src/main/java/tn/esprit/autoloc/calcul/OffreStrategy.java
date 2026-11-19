package tn.esprit.autoloc.calcul;

/** Strategie de tarification : une implementation par offre. */
public interface OffreStrategy {
    double calculer(double tarifJournalier, int nbjours);
}
