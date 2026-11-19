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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootTest
@Transactional

public class EquipementTests {
    @Autowired private IEquipementService service;
    @Autowired private IVehiculeService vehiculeService;
    @PersistenceContext private EntityManager em;

    private Equipement valid() { Equipement e = new Equipement(); e.setLibelle("GPS"); return e; }

    @Test void checkValide() { Assertions.assertTrue(service.check(valid(), true)); }
    @Test void checkLibelleVide() { Equipement e = valid(); e.setLibelle(""); Assertions.assertFalse(service.check(e, true)); }
    @Test void checkLibelleNull() { Equipement e = valid(); e.setLibelle(null); Assertions.assertFalse(service.check(e, true)); }

    @Test void create() { Assertions.assertNotNull(service.create(valid()).getIdEquipement()); }

    @Test void update() {
        Equipement created = service.create(valid());
        Equipement modif = valid(); modif.setLibelle("Siege bebe");
        Assertions.assertEquals("Siege bebe", service.update(created.getIdEquipement(), modif).getLibelle());
    }

    @Test void findByIdInexistant() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> { service.findById(-1L); });
    }

    @Test void deleteByIdInexistant() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> { service.deleteById(-1L); });
    }

    @Test void deleteByIdDetacheDesVehicules() {
        Equipement eq = service.create(valid());
        Vehicule v = new Vehicule();
        v.setCategorie(CategorieVehicule.SUV);
        v.setImmatriculation("EQ" + System.nanoTime());
        v.setMarque("Isuzu"); v.setModele("DMax");
        v.setStatut(StatutVehicule.DISPONIBLE);
        v.setTarifJournalier(BigDecimal.valueOf(100));
        v.setEquipement(new HashSet<>());
        Vehicule saved = vehiculeService.create(v);
        vehiculeService.affecterequipement(saved.getIdVehicule(), eq.getIdEquipement());
        em.flush();
        em.clear();

        service.deleteById(eq.getIdEquipement());

        em.flush();
        em.clear();

        Assertions.assertTrue(vehiculeService.findById(saved.getIdVehicule()).getEquipement().isEmpty());
    }
}