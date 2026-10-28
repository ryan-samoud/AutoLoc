package tn.esprit.autoloc.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.repository.IMaintenanceRepository;

import java.time.LocalDate;

@Service
@Primary
public class MaintenanceServiceImpl extends GenericServiceImpl<Maintenance, Long> implements IMaintenanceService {

    public MaintenanceServiceImpl(IMaintenanceRepository repository) {
        super(repository);
    }

    @Override
    protected String getEntityName() {
        return "maintenance";
    }

    @Override
    protected Long getId(Maintenance maintenance) {
        return maintenance.getIdMaintenance();
    }

    @Override
    protected void setFields(Maintenance source, Maintenance target) {
        target.setDateDebut(source.getDateDebut());
        target.setDateFin(source.getDateFin());
        target.setDescription(source.getDescription());
        target.setVehicule(source.getVehicule());
    }

    @Override
    public boolean check(Maintenance maintenance, boolean isNew) {
        return maintenance.getDateDebut() != null &&
                (maintenance.getDateFin() == null ||
                        !maintenance.getDateFin().isBefore(maintenance.getDateDebut()));
    }

}