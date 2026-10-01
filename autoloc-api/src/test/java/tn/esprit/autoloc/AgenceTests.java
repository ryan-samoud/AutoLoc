package tn.esprit.autoloc;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class AgenceTests {
    @Autowired
    private AgenceRepositoryMock agenceRepository;

    @Test
    public void AddAgence(){
        Agence agence= new Agence();
        agence.setAdresse("1 Rue Hedi");
        agence.setNom("Agence ariana");
        agence.setTelephone("71585874");
        agence.setVille("Tunis");

        Vehicule v1 = new Vehicule();
        v1.setCategorie(CategorieVehicule.SUV);
        v1.setImmatriculation("785414TU96");
        v1.setMarque("Isuzu");
        v1.setModele("DMax");
        v1.setStatut(StatutVehicule.MAINTENANCE);
        v1.setTarifJournalier(BigDecimal.valueOf(100));
        v1.setAgence(agence);

        Vehicule v2 = new Vehicule();
        v2.setCategorie(CategorieVehicule.UTILITAIRE);
        v2.setImmatriculation("785414TU95");
        v2.setMarque("Toyota");
        v2.setModele("Yaris");
        v2.setStatut(StatutVehicule.DISPONIBLE);
        v2.setTarifJournalier(BigDecimal.valueOf(80));
        v2.setAgence(agence);

        agence.setVehicule(Set.of(v1, v2));

        Agence savedAgence = agenceRepository.save(agence);
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

    @Test
    public void loadAgence() {
        Set<Agence> agences = new HashSet<>();
        StringBuilder sb = new StringBuilder();

        agenceRepository.findAll().forEach(agence -> {
            agences.add(agence);
            sb.append(Builder.build(agence));
        });

        Assert.fail(sb.toString());
    }
}
interface AgenceRepositoryMock extends CrudRepository<Agence, Long> {}