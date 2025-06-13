package com.example.ioproject.payload.request;

/**
 * Request payload used for initiating a Stripe Checkout session.
 * <p>
 * Sent from the frontend when a user proceeds to payment for a vehicle reservation.
 * Contains information about the reservation ID, the amount to be charged, and the vehicle name.
 * </p>
 */
public class CheckoutRequest {

    /**
     * ID of the reservation being paid for.
     */
    private long reservationId;

    /**
     * Payment amount in PLN (converted to grosze in Stripe backend).
     */
    private long amount;

    /**
     * Name of the car associated with the reservation.
     */
    private String carName;

    /**
     * Gets the reservation ID associated with this checkout request.
     *
     * @return the reservation ID
     */
    public long getReservationId() {
        return reservationId;
    }

    /**
     * Sets the reservation ID for this checkout request.
     *
     * @param reservationId the ID of the reservation
     */
    public void setReservationId(long reservationId) {
        this.reservationId = reservationId;
    }

    /**
     * Gets the name of the car to be displayed in the payment description.
     *
     * @return the car name
     */
    public String getCarName() {
        return carName;
    }

    /**
     * Sets the name of the car to be included in the payment description.
     *
     * @param carName the name of the car
     */
    public void setCarName(String carName) {
        this.carName = carName;
    }

    /**
     * Gets the amount to be charged for the reservation.
     *
     * @return the amount in PLN
     */
    public Long getAmount() {
        return amount;
    }

    /**
     * Sets the amount to be charged for the reservation.
     *
     * @param amount the amount in PLN
     */
    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
