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

public class ReservationTests {
    @Autowired private IReservationService service;

    private Reservation valid() {
        Reservation r = new Reservation();
        r.setDateDebut(LocalDate.now());
        r.setDateFin(LocalDate.now().plusDays(3));
        r.setStatut(StatutReservation.EN_ATTENTE);
        return r;
    }

    @Test void checkValide() { Assertions.assertTrue(service.check(valid(), true)); }
    @Test void checkDateDebutNull() { Reservation r = valid(); r.setDateDebut(null); Assertions.assertFalse(service.check(r, true)); }
    @Test void checkDateFinNull() { Reservation r = valid(); r.setDateFin(null); Assertions.assertFalse(service.check(r, true)); }
    @Test void checkFinAvantDebut() { Reservation r = valid(); r.setDateFin(r.getDateDebut().minusDays(1)); Assertions.assertFalse(service.check(r, true)); }
    @Test void checkMemeJour() { Reservation r = valid(); r.setDateFin(r.getDateDebut()); Assertions.assertTrue(service.check(r, true)); }
    @Test void checkStatutNull() { Reservation r = valid(); r.setStatut(null); Assertions.assertFalse(service.check(r, true)); }

    @Test void checkTousLesStatutsAcceptes() {
        for (StatutReservation st : StatutReservation.values()) {
            Reservation r = valid(); r.setStatut(st);
            Assertions.assertTrue(service.check(r, true));
        }
    }

    @Test void updateStatutConfirmee() {
        Reservation created = service.create(valid());
        Reservation modif = valid(); modif.setStatut(StatutReservation.CONFIRMEE);
        Assertions.assertEquals(StatutReservation.CONFIRMEE, service.update(created.getIdReservation(), modif).getStatut());
    }

    @Test void updateStatutAnnulee() {
        Reservation created = service.create(valid());
        Reservation modif = valid(); modif.setStatut(StatutReservation.ANNULEE);
        Assertions.assertEquals(StatutReservation.ANNULEE, service.update(created.getIdReservation(), modif).getStatut());
    }

    @Test void create() { Assertions.assertNotNull(service.create(valid()).getIdReservation()); }

    @Test void update() {
        Reservation created = service.create(valid());
        Reservation modif = valid(); modif.setDateFin(LocalDate.now().plusDays(10));
        Assertions.assertEquals(LocalDate.now().plusDays(10), service.update(created.getIdReservation(), modif).getDateFin());
    }

    @Test void findByIdInexistant() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> { service.findById(-1L); });
    }

    @Test void deleteById() {
        Reservation created = service.create(valid());
        Long id = created.getIdReservation();
        service.deleteById(id);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(id));
    }
}