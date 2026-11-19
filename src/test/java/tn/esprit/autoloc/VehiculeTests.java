package tn.esprit.autoloc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.*;
import tn.esprit.autoloc.exception.*;
import tn.esprit.autoloc.service.*;

import java.math.BigDecimal;
import java.util.HashSet;

import tn.esprit.autoloc.calcul.VehiculeOffre;

@SpringBootTest
@Transactional

public class VehiculeTests {
    @Autowired private VehiculeServiceImpl service;
    @Autowired private IAgenceService agenceService;
    @Autowired private IEquipementService equipementService;

    private Vehicule valid() {
        Vehicule v = new Vehicule();
        v.setCategorie(CategorieVehicule.SUV);
        v.setImmatriculation("VT" + System.nanoTime());
        v.setMarque("Toyota"); v.setModele("Yaris");
        v.setStatut(StatutVehicule.DISPONIBLE);
        v.setTarifJournalier(BigDecimal.valueOf(80));
        v.setEquipement(new HashSet<>());
        return v;
    }

    private Equipement equipement() {
        Equipement e = new Equipement(); e.setLibelle("GPS");
        return equipementService.create(e);
    }

    // ---- check
    @Test void checkValide() { Assertions.assertTrue(service.check(valid(), true)); }
    @Test void checkImmatNull() { Vehicule v = valid(); v.setImmatriculation(null); Assertions.assertFalse(service.check(v, true)); }
    @Test void checkMarqueVide() { Vehicule v = valid(); v.setMarque(""); Assertions.assertFalse(service.check(v, true)); }
    @Test void checkModeleVide() { Vehicule v = valid(); v.setModele(""); Assertions.assertFalse(service.check(v, true)); }
    @Test void checkCategorieNull() { Vehicule v = valid(); v.setCategorie(null); Assertions.assertFalse(service.check(v, true)); }
    @Test void checkTarifNull() { Vehicule v = valid(); v.setTarifJournalier(null); Assertions.assertFalse(service.check(v, true)); }
    @Test void checkTarifZero() { Vehicule v = valid(); v.setTarifJournalier(BigDecimal.ZERO); Assertions.assertFalse(service.check(v, true)); }
    @Test void checkTarifNegatif() { Vehicule v = valid(); v.setTarifJournalier(BigDecimal.valueOf(-5)); Assertions.assertFalse(service.check(v, true)); }
    @Test void checkStatutNull() { Vehicule v = valid(); v.setStatut(null); Assertions.assertFalse(service.check(v, true)); }

    // ---- CRUD
    @Test void create() { Assertions.assertNotNull(service.create(valid()).getIdVehicule()); }

    @Test void update() {
        Vehicule created = service.create(valid());
        Vehicule modif = valid(); modif.setMarque("Isuzu");
        Assertions.assertEquals("Isuzu", service.update(created.getIdVehicule(), modif).getMarque());
    }

    @Test void findByIdInexistant() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(-1L));
    }

    @Test void deleteByIdSansEquipement() {
        Vehicule created = service.create(valid());
        Long id = created.getIdVehicule();
        service.deleteById(id);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(id));
    }

    @Test void deleteByIdAvecEquipementInterdit() {
        Vehicule created = service.create(valid());
        Long id = created.getIdVehicule();
        Equipement eq = equipement();
        service.affecterequipement(id, eq.getIdEquipement());
        Assertions.assertThrows(ConflictException.class, () -> service.deleteById(id));
    }

    // ---- affectations
    @Test void affecterAgence() {
        Agence ag = agenceService.create(Agence.builder().nom("Ag").telephone("71000000").adresse("a").ville("Tunis").build());
        Vehicule created = service.create(valid());
        Vehicule res = service.affecter(created.getIdVehicule(), ag.getIdAgence());
        Assertions.assertEquals(ag.getIdAgence(), res.getAgence().getIdAgence());
    }

    @Test void affecterAgenceInexistante() {
        Long id = service.create(valid()).getIdVehicule();
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.affecter(id, -1L));
    }

    @Test void affecterVehiculeInexistant() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.affecter(-1L, 1L));
    }

    @Test void desaffecter() {
        Agence ag = agenceService.create(Agence.builder().nom("Ag").telephone("71000000").adresse("a").ville("Tunis").build());
        Vehicule created = service.create(valid());
        service.affecter(created.getIdVehicule(), ag.getIdAgence());
        Assertions.assertNull(service.desaffecter(created.getIdVehicule()).getAgence());
    }

    @Test void affecterEquipement() {
        Vehicule created = service.create(valid());
        Equipement eq = equipement();
        Vehicule res = service.affecterequipement(created.getIdVehicule(), eq.getIdEquipement());
        Assertions.assertEquals(1, res.getEquipement().size());
    }

    @Test void affecterEquipementInexistant() {
        Long id = service.create(valid()).getIdVehicule();
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.affecterequipement(id, -1L));
    }

    @Test void affecterEquipementVehiculeInexistant() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.affecterequipement(-1L, 1L));
    }

    // ---- calculTarif
    @Test void calculTarifJoursZero() {
        Vehicule v = valid();
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.calculTarif(v, 0, VehiculeOffre.STANDARD));
    }

    @Test void calculTarifJoursNegatif() {
        Vehicule v = valid();
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.calculTarif(v, -2, VehiculeOffre.STANDARD));
    }

    @Test void calculTarifOffreNull() {
        Vehicule v = valid();
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.calculTarif(v, 3, null));
    }

    // valid() : tarif journalier = 80
    @Test void calculTarifStandard() {
        Assertions.assertEquals(240.0, service.calculTarif(valid(), 3, VehiculeOffre.STANDARD), 0.001);   // 80 * 3
    }

    @Test void calculTarifWeekend() {
        Assertions.assertEquals(192.0, service.calculTarif(valid(), 3, VehiculeOffre.WEEKEND), 0.001);    // 80 * 0.8 * 3
    }

    @Test void calculTarifLongueDureeSansRemiseSupplementaire() {
        Assertions.assertEquals(600.0, service.calculTarif(valid(), 10, VehiculeOffre.LONGUE_DUREE), 0.001);   // 80 * 0.75 * 10
        Assertions.assertEquals(1800.0, service.calculTarif(valid(), 30, VehiculeOffre.LONGUE_DUREE), 0.001);  // < 2000
    }

    @Test void calculTarifLongueDureeRemiseSiTotalSuperieurA2000() {
        Vehicule v = valid(); v.setTarifJournalier(BigDecimal.valueOf(100));
        Assertions.assertEquals(2137.5, service.calculTarif(v, 30, VehiculeOffre.LONGUE_DUREE), 0.001);   // 100*0.75*30 = 2250 -> -5 %
    }

    @Test void calculTarifArrondiADeuxDecimales() {
        Vehicule v = valid(); v.setTarifJournalier(new BigDecimal("33.33"));
        Assertions.assertEquals(79.99, service.calculTarif(v, 3, VehiculeOffre.WEEKEND), 0.0001);          // 79.992 -> 79.99
    }

    @Test void calculTarifOffresMoinsCheresQueStandard() {
        double std = service.calculTarif(valid(), 30, VehiculeOffre.STANDARD);
        Assertions.assertTrue(service.calculTarif(valid(), 30, VehiculeOffre.WEEKEND) < std);
        Assertions.assertTrue(service.calculTarif(valid(), 30, VehiculeOffre.LONGUE_DUREE) < std);
    }
}