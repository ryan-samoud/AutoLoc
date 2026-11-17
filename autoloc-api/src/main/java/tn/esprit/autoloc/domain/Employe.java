package tn.esprit.autoloc.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employe")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEmploye;

    @Column(nullable = false, length = 50)
    private String nom;

    @Column(nullable = false, length = 50)
    private String prenom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoleEmploye role;

    @ManyToOne
    private Agence agence;

    // Point d'entrée côté Employe : délègue à Agence pour garder les deux côtés cohérents
    public void affecterAgence(Agence nouvelle) {
        if (nouvelle == null) {
            if (agence != null) {
                agence.removeEmploye(this);
            }
        } else {
            nouvelle.addEmploye(this);
        }
    }
}