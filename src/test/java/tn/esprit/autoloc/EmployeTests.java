package tn.esprit.autoloc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.*;
import tn.esprit.autoloc.exception.*;
import tn.esprit.autoloc.service.*;


@SpringBootTest
@Transactional

public class EmployeTests {
    @Autowired private IEmployeService service;
    @Autowired private IAgenceService agenceService;

    // roles possibles : AGENT, MANAGER
    private Employe valid() {
        Employe e = new Employe();
        e.setNom("Trabelsi"); e.setPrenom("Ali");
        e.setRole(RoleEmploye.AGENT);
        return e;
    }

    private Agence agence() {
        return agenceService.create(Agence.builder().nom("Ag").telephone("71000000").adresse("adr").ville("Tunis").build());
    }

    @Test void checkValide() { Assertions.assertTrue(service.check(valid(), true)); }
    @Test void checkNomVide() { Employe e = valid(); e.setNom(""); Assertions.assertFalse(service.check(e, true)); }
    @Test void checkPrenomNull() { Employe e = valid(); e.setPrenom(null); Assertions.assertFalse(service.check(e, true)); }
    @Test void checkRoleNull() { Employe e = valid(); e.setRole(null); Assertions.assertFalse(service.check(e, true)); }

    @Test void checkRoleManager() { Employe e = valid(); e.setRole(RoleEmploye.MANAGER); Assertions.assertTrue(service.check(e, true)); }

    @Test void updateRoleVersManager() {
        Employe created = service.create(valid());
        Employe modif = valid(); modif.setRole(RoleEmploye.MANAGER);
        Assertions.assertEquals(RoleEmploye.MANAGER, service.update(created.getIdEmploye(), modif).getRole());
    }

    @Test void createSansAgence() {
        Employe created = service.create(valid());
        Assertions.assertNotNull(created.getIdEmploye());
        Assertions.assertNull(created.getAgence());
    }

    @Test void createAvecAgence() {
        Agence ag = agence();
        Employe e = valid();
        Agence ref = new Agence(); ref.setIdAgence(ag.getIdAgence());
        e.setAgence(ref);
        Employe created = service.create(e);
        Assertions.assertEquals(ag.getIdAgence(), created.getAgence().getIdAgence());
    }

    @Test void createAgenceInexistante() {
        Employe e = valid();
        Agence ref = new Agence(); ref.setIdAgence(-1L);
        e.setAgence(ref);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.create(e));
    }

    @Test void update() {
        Employe created = service.create(valid());
        Employe modif = valid(); modif.setNom("Nouveau");
        Assertions.assertEquals("Nouveau", service.update(created.getIdEmploye(), modif).getNom());
    }

    @Test void findByIdInexistant() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> { service.findById(-1L); });
    }

    @Test void deleteById() {
        Employe created = service.create(valid());
        Long id = created.getIdEmploye();
        service.deleteById(id);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(id));
    }
}