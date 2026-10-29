package tn.esprit.autoloc.service;

import tn.esprit.autoloc.calcul.VehiculeOffre;
import tn.esprit.autoloc.domain.Agence;

public interface ISimuler {
    String simuler(Agence agence, int nbjours, VehiculeOffre offre);
}