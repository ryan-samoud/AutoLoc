package tn.esprit.autoloc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.*;
import tn.esprit.autoloc.exception.*;
import tn.esprit.autoloc.service.*;

import java.time.LocalDate;

@SpringBootTest
@Transactional

public class ClientTests {
    @Autowired private IClientService service;

    private Client valid() {
        Client c = new Client();
        c.setNom("Ben Ali"); c.setPrenom("Sami"); c.setEmail("sami" + System.nanoTime() + "@mail.tn");
        c.setTelephone("22111222"); c.setNumPermis("P-" + System.nanoTime());
        c.setDateInscription(LocalDate.now());
        return c;
    }

    @Test void checkValide() { Assertions.assertTrue(service.check(valid(), true)); }
    @Test void checkNomVide() { Client c = valid(); c.setNom(""); Assertions.assertFalse(service.check(c, true)); }
    @Test void checkEmailNull() { Client c = valid(); c.setEmail(null); Assertions.assertFalse(service.check(c, true)); }
    @Test void checkPermisVide() { Client c = valid(); c.setNumPermis(" "); Assertions.assertFalse(service.check(c, true)); }
    @Test void checkDateNull() { Client c = valid(); c.setDateInscription(null); Assertions.assertFalse(service.check(c, true)); }

    @Test void create() {
        Client created = service.create(valid());
        Assertions.assertNotNull(created.getIdClient());
    }

    @Test void update() {
        Client created = service.create(valid());
        Client modif = valid(); modif.setNom("Nouveau");
        Client updated = service.update(created.getIdClient(), modif);
        Assertions.assertEquals("Nouveau", updated.getNom());
    }

    @Test void findByIdExistant() {
        Client created = service.create(valid());
        Assertions.assertEquals(created.getIdClient(), service.findById(created.getIdClient()).getIdClient());
    }

    @Test void findByIdInexistant() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> { service.findById(-1L); });
    }

    @Test void deleteById() {
        Client created = service.create(valid());
        Long id = created.getIdClient();
        service.deleteById(id);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(id));
    }
}