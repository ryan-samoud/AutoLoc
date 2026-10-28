// ReservationServiceImpl.java
package tn.esprit.autoloc.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.repository.IReservationRepository;

@Service
@Primary
public class ReservationServiceImpl extends GenericServiceImpl<Reservation, Long> implements IReservationService {

    public ReservationServiceImpl(IReservationRepository repository) {
        super(repository);
    }

    @Override
    protected String getEntityName() {
        return "reservation";
    }

    @Override
    protected Long getId(Reservation reservation) {
        return reservation.getIdReservation();
    }

    @Override
    protected void setFields(Reservation source, Reservation target) {
        target.setDateDebut(source.getDateDebut());
        target.setDateFin(source.getDateFin());
        target.setStatut(source.getStatut());
        target.setVehicule(source.getVehicule());
        target.setClient(source.getClient());
        target.setContrat(source.getContrat());
    }

    @Override
    public boolean check(Reservation reservation, boolean isNew) {
        return reservation.getDateDebut() != null &&
                reservation.getDateFin() != null &&
                !reservation.getDateFin().isBefore(reservation.getDateDebut()) &&
                reservation.getStatut() != null;
    }

}