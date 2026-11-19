package tn.esprit.autoloc.web.dto;

import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Employe;

import java.util.List;

public record AgenceEmployesRequest(Agence agence, List<Employe> employes) {}
