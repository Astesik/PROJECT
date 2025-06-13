package com.example.ioproject.repository;

import com.example.ioproject.models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link Reservation} entities.
 *
 * Extends JpaRepository to provide basic CRUD operations.
 * Additionally provides custom queries for reservation-specific operations.
 */
 @Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Finds all reservations that conflict with the specified date range for a given vehicle.
     * Conflicting means reservations where the reserved period overlaps with the given start and end dates.
     *
     * @param vehicleId the ID of the vehicle to check for conflicts
     * @param startDate the start date of the period to check (inclusive), format as String
     * @param endDate the end date of the period to check (inclusive), format as String
     * @return list of conflicting {@link Reservation} objects, empty if none found
     */
    @Query("SELECT r FROM Reservation r WHERE r.vehicle_id = :vehicleId AND " +
            "((r.start_date <= :endDate AND r.end_date >= :startDate))")
    List<Reservation> findConflictingReservations(@Param("vehicleId") int vehicleId,
                                                  @Param("startDate") String startDate,
                                                  @Param("endDate") String endDate);

    /**
     * Finds a reservation by its Stripe payment session ID.
     *
     * @param stripeSessionId the Stripe session ID linked to the reservation
     * @return an {@link Optional} containing the reservation if found, or empty if not
     */
    Optional<Reservation> findByStripeSessionId(String stripeSessionId);

    /**
     * Retrieves all reservations made by a specific client.
     *
     * @param clientId the ID of the client
     * @return list of {@link Reservation} objects made by the client
     */
    @Query("SELECT r FROM Reservation r WHERE r.client_id = :clientId")
    List<Reservation> findByClientId(@Param("clientId") Long clientId);

    /**
     * Retrieves all reservations for a specific vehicle that have been paid.
     *
     * @param vehicleId the ID of the vehicle
     * @return list of paid {@link Reservation} objects for the vehicle
     */
    @Query("SELECT r FROM Reservation r WHERE r.vehicle_id = :vehicleId AND r.status = 'PAID'")
    List<Reservation> findByVehicleId(@Param("vehicleId") Long vehicleId);
}
