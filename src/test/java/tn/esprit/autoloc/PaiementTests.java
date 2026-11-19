package tn.esprit.autoloc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.exception.*;
import tn.esprit.autoloc.service.*;


@SpringBootTest
@Transactional

public class PaiementTests {
    @Autowired private IPaiementService service;

    @Test  void findAllAutorise() { Assertions.assertNotNull(service.findAll()); }

    @Test  void findByIdInexistant() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> { service.findById(-1L); });
    }
}