package tn.esprit.autoloc.io;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.service.ISimuler;

/** Factory : choisit le type de rapport (la signature de simuler ne change pas). */
@Component
public class SimulerFactory {

    private final ISimuler texte;
    private final ISimuler console;

    public SimulerFactory(@Qualifier("agenceServiceImpl") ISimuler texte, SimulerConsole console) {
        this.texte = texte;
        this.console = console;
    }

    public ISimuler creer(TypeRapport type) {
        if (type == null) {
            throw new IllegalArgumentException("le type de rapport est obligatoire");
        }
        return switch (type) {
            case TEXTE -> texte;
            case CONSOLE -> console;
        };
    }
}
