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
import java.time.LocalDate;

@SpringBootTest
@Transactional

public class ContratTests {
    @Autowired private IContratService service;

    private Contrat valid() {
        Contrat c = new Contrat();
        c.setDateSignature(LocalDate.now());
        c.setMontantTotal(BigDecimal.valueOf(500));
        c.setValide(true);
        return c;
    }

    @Test void checkValide() { Assertions.assertTrue(service.check(valid(), true)); }
    @Test void checkMontantZeroAccepte() { Contrat c = valid(); c.setMontantTotal(BigDecimal.ZERO); Assertions.assertTrue(service.check(c, true)); }
    @Test void checkMontantNegatif() { Contrat c = valid(); c.setMontantTotal(BigDecimal.valueOf(-1)); Assertions.assertFalse(service.check(c, true)); }
    @Test void checkMontantNull() { Contrat c = valid(); c.setMontantTotal(null); Assertions.assertFalse(service.check(c, true)); }
    @Test void checkDateNull() { Contrat c = valid(); c.setDateSignature(null); Assertions.assertFalse(service.check(c, true)); }

    @Test void create() {
        Assertions.assertNotNull(service.create(valid()).getIdContrat());
    }

    @Test void update() {
        Contrat created = service.create(valid());
        Contrat modif = valid(); modif.setMontantTotal(BigDecimal.valueOf(900)); modif.setValide(false);
        Contrat updated = service.update(created.getIdContrat(), modif);
        Assertions.assertEquals(0, BigDecimal.valueOf(900).compareTo(updated.getMontantTotal()));
        Assertions.assertFalse(updated.isValide());
    }

    @Test void findByIdInexistant() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> { service.findById(-1L); });
    }

    @Test void deleteById() {
        Contrat created = service.create(valid());
        Long id = created.getIdContrat();
        service.deleteById(id);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(id));
    }
}