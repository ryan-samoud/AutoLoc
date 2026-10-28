// ClientServiceImpl.java
package tn.esprit.autoloc.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.Utils;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.repository.IClientRepository;

@Service
@Primary
public class ClientServiceImpl extends GenericServiceImpl<Client, Long> implements IClientService {

    public ClientServiceImpl(IClientRepository repository) {
        super(repository);
    }

    @Override
    protected String getEntityName() {
        return "client";
    }

    @Override
    protected Long getId(Client client) {
        return client.getIdClient();
    }

    @Override
    protected void setFields(Client source, Client target) {
        target.setNom(source.getNom());
        target.setPrenom(source.getPrenom());
        target.setEmail(source.getEmail());
        target.setTelephone(source.getTelephone());
        target.setNumPermis(source.getNumPermis());
        target.setDateInscription(source.getDateInscription());
    }

    @Override
    public boolean check(Client client, boolean isNew) {
        return Utils.isNotEmpty(client.getNom()) &&
                Utils.isNotEmpty(client.getPrenom()) &&
                Utils.isNotEmpty(client.getEmail()) &&
                Utils.isNotEmpty(client.getTelephone()) &&
                Utils.isNotEmpty(client.getNumPermis()) &&
                client.getDateInscription() != null;
    }
}