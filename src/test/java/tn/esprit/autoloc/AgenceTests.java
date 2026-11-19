package tn.esprit.autoloc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.service.IAgenceService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SpringBootTest
class AgenceTests {

    private static final Logger log = LoggerFactory.getLogger(AgenceTests.class);

    @Autowired
    private AgenceRepositoryMock basicAgenceRepository;

    @Autowired
    private IAgenceRepository fullAgenceRepository;

    @Autowired
    private IAgenceService agenceService;

    private Agence addAgence(CrudRepository<Agence, Long> repository) {
        Agence agence = new Agence();
        agence.setAdresse("1 Rue Hedi");
        agence.setNom("Agence ariana");
        agence.setTelephone("71585874");
        agence.setVille("Tunis");
        long ms = System.nanoTime() % 1_000_000_000L;

        Vehicule v1 = new Vehicule();
        v1.setCategorie(CategorieVehicule.SUV);
        v1.setImmatriculation("785414TU96" + ms);
        v1.setMarque("Isuzu");
        v1.setModele("DMax");
        v1.setStatut(StatutVehicule.MAINTENANCE);
        v1.setTarifJournalier(BigDecimal.valueOf(100));
        v1.setAgence(agence);

        Vehicule v2 = new Vehicule();
        v2.setCategorie(CategorieVehicule.UTILITAIRE);
        v2.setImmatriculation("785414TU95" + ms);
        v2.setMarque("Toyota");
        v2.setModele("Yaris");
        v2.setStatut(StatutVehicule.DISPONIBLE);
        v2.setTarifJournalier(BigDecimal.valueOf(80));
        v2.setAgence(agence);

        agence.setVehicule(Set.of(v1, v2));

        return repository.save(agence);
    }

    @Test
    void basicAddAgence() {
        Agence saved = addAgence(basicAgenceRepository);
        Assertions.assertNotNull(saved.getIdAgence());
        Assertions.assertTrue(basicAgenceRepository.existsById(saved.getIdAgence()));
    }

    @Test
    void fullAddAgence() {
        Agence saved = addAgence(fullAgenceRepository);
        Assertions.assertNotNull(saved.getIdAgence());
        Assertions.assertTrue(fullAgenceRepository.existsById(saved.getIdAgence()));
    }

    static class Builder {

        private Builder() {
        }

        static String build(Agence agence) {
            StringBuilder sb = new StringBuilder();
            sb.append("\n").append(agence.getIdAgence()).append("|");
            sb.append(agence.getNom()).append("\n");
            sb.append("Vehicule Count :").append(agence.getVehicule().size()).append("\n");

            for (Vehicule v : agence.getVehicule()) {
                sb.append(v.getIdVehicule()).append("==").append(v.getImmatriculation()).append("\n");
            }

            return sb.toString();
        }
    }

    private void loadAgence(CrudRepository<Agence, Long> repository, String label) {
        Agence saved = addAgence(repository);

        List<Agence> all = new ArrayList<>();
        repository.findAll().forEach(all::add);

        StringBuilder sb = new StringBuilder();
        sb.append("TYPE =").append(label).append("\n");
        all.forEach(agence -> sb.append(Builder.build(agence)));
        log.info("{}", sb);

        Assertions.assertTrue(all.stream().anyMatch(a -> a.getIdAgence().equals(saved.getIdAgence())));
    }

    @Test
    void basicLoadAgence() {
        loadAgence(basicAgenceRepository, "basic");
    }

    @Test
    void fullLoadAgence() {
        loadAgence(fullAgenceRepository, "full");
    }

    @Test
    void loadSortedAgences() {
        addAgence(fullAgenceRepository);
        addAgence(fullAgenceRepository);

        List<Agence> agences = fullAgenceRepository.findAll(Sort.by("idAgence").descending());

        StringBuilder sb = new StringBuilder();
        for (Agence agence : agences) {
            sb.append(agence.getIdAgence()).append(" | ").append(agence.getNom()).append("\n");
        }
        log.info("{}", sb);

        Assertions.assertTrue(agences.size() >= 2);
        for (int i = 1; i < agences.size(); i++) {
            Assertions.assertTrue(agences.get(i - 1).getIdAgence() > agences.get(i).getIdAgence());
        }
    }

    @Test
    void loadPagedAgences() {
        addAgence(fullAgenceRepository);
        addAgence(fullAgenceRepository);
        addAgence(fullAgenceRepository);

        StringBuilder sb = new StringBuilder();
        int currentPage = 0;
        int totalElements = 0;
        Page<Agence> page;
        boolean first = true;

        do {
            Pageable pageable = PageRequest.of(currentPage, 2, Sort.by("idAgence").descending());
            page = fullAgenceRepository.findAll(pageable);

            if (first) {
                sb.append("Nombre total de pages : ").append(page.getTotalPages()).append("\n");
                first = false;
            }

            sb.append("Page en cours : ").append(page.getNumber()).append("\n");

            for (Agence agence : page.getContent()) {
                sb.append(agence.getIdAgence()).append(" | ").append(agence.getNom()).append("\n");
            }

            Assertions.assertTrue(page.getContent().size() <= 2);
            totalElements += page.getContent().size();
            currentPage++;
        } while (currentPage < page.getTotalPages());

        log.info("{}", sb);
        Assertions.assertTrue(totalElements >= 3);
    }

    @Test
    void createAgence() {
        Agence agence = Agence.builder()
                .nom("nom")
                .telephone("95811925")
                .adresse("adr")
                .ville("Tunis")
                .build();
        Agence created = agenceService.create(agence);
        Assertions.assertNotNull(created);
        Assertions.assertNotNull(created.getIdAgence());
    }
}

@Repository
interface AgenceRepositoryMock extends CrudRepository<Agence, Long> {
}