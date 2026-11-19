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

public class MaintenanceTests {
    @Autowired private IMaintenanceService service;

    private Maintenance valid() {
        Maintenance m = new Maintenance();
        m.setDateDebut(LocalDate.now());
        m.setDateFin(LocalDate.now().plusDays(2));
        m.setDescription("Vidange");
        return m;
    }

    @Test void checkValide() { Assertions.assertTrue(service.check(valid(), true)); }
    @Test void checkDateFinNullAccepte() { Maintenance m = valid(); m.setDateFin(null); Assertions.assertTrue(service.check(m, true)); }
    @Test void checkDateDebutNull() { Maintenance m = valid(); m.setDateDebut(null); Assertions.assertFalse(service.check(m, true)); }
    @Test void checkFinAvantDebut() { Maintenance m = valid(); m.setDateFin(m.getDateDebut().minusDays(1)); Assertions.assertFalse(service.check(m, true)); }
    @Test void checkMemeJour() { Maintenance m = valid(); m.setDateFin(m.getDateDebut()); Assertions.assertTrue(service.check(m, true)); }

    @Test void create() { Assertions.assertNotNull(service.create(valid()).getIdMaintenance()); }

    @Test void update() {
        Maintenance created = service.create(valid());
        Maintenance modif = valid(); modif.setDescription("Pneus");
        Assertions.assertEquals("Pneus", service.update(created.getIdMaintenance(), modif).getDescription());
    }

    @Test void findByIdInexistant() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> { service.findById(-1L); });
    }

    @Test void deleteById() {
        Maintenance created = service.create(valid());
        Long id = created.getIdMaintenance();
        service.deleteById(id);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(id));
    }
}