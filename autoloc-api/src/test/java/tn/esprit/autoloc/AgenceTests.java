package tn.esprit.autoloc;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IAgenceRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class AgenceTests {
    @Autowired
    private AgenceRepositoryMock basicAgenceRepository;

    @Autowired
    private IAgenceRepository fullAgenceRepository;

    private void AddAgence(CrudRepository<Agence,Long> repository){
        Agence agence= new Agence();
        agence.setAdresse("1 Rue Hedi");
        agence.setNom("Agence ariana");
        agence.setTelephone("71585874");
        agence.setVille("Tunis");
        int ms = (int)System.currentTimeMillis();

        Vehicule v1 = new Vehicule();
        v1.setCategorie(CategorieVehicule.SUV);
        v1.setImmatriculation("785414TU96"+ms);
        v1.setMarque("Isuzu");
        v1.setModele("DMax");
        v1.setStatut(StatutVehicule.MAINTENANCE);
        v1.setTarifJournalier(BigDecimal.valueOf(100));
        v1.setAgence(agence);

        Vehicule v2 = new Vehicule();
        v2.setCategorie(CategorieVehicule.UTILITAIRE);
        v2.setImmatriculation("785414TU95"+ms);
        v2.setMarque("Toyota");
        v2.setModele("Yaris");
        v2.setStatut(StatutVehicule.DISPONIBLE);
        v2.setTarifJournalier(BigDecimal.valueOf(80));
        v2.setAgence(agence);

        agence.setVehicule(Set.of(v1, v2));

        repository.save(agence);
    }
    @Test
    public void basicAddAgence(){
        AddAgence(basicAgenceRepository);
    }

    @Test
    public void fullAddAgence(){
        AddAgence(fullAgenceRepository);
    }

    public class Builder {

        public static String build(Agence agence) {
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


    private void loadAgence(CrudRepository<Agence,Long> repository,String label) {
        Set<Agence> agences = new HashSet<>();
        StringBuilder sb = new StringBuilder();
        sb.append("TYPE =").append(label).append("\n");

        basicAgenceRepository.findAll().forEach(agence -> {
            agences.add(agence);
            sb.append(Builder.build(agence));
        });

        Assert.fail(sb.toString());
    }

    @Test
    public void basicLoadAgence(){
        loadAgence(basicAgenceRepository, "basic");
    }

    @Test
    public void fullLoadAgence(){
        loadAgence(fullAgenceRepository, "full");
    }

    @Test
    public void loadSortedAgences() {
        StringBuilder sb = new StringBuilder();

        List<Agence> agences = fullAgenceRepository.findAll(Sort.by("idAgence").descending());

        for (Agence agence : agences) {
            sb.append(agence.getIdAgence()).append(" | ").append(agence.getNom()).append("\n");
        }

        Assert.fail(sb.toString());
    }

    @Test
    public void loadPagedAgences() {
        StringBuilder sb = new StringBuilder();

        int currentPage = 0;
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

            currentPage++;
        } while (currentPage < page.getTotalPages());

        Assert.fail(sb.toString());
    }
}
interface AgenceRepositoryMock extends CrudRepository<Agence, Long> {}