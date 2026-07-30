package control;

import adt.QueueInterface;
import adt.ArrayQueue;
import entity.Booking;

public class WIRegistrationControl {

    private QueueInterface<Booking> bookingQueue;

    public WIRegistrationControl() {
        bookingQueue = new ArrayQueue<>();
    }

    public void registerGuest(Booking booking) {
        bookingQueue.enqueue(booking);
    }

    public Booking processGuest() {
        if (bookingQueue.isEmpty())
            return null;

        return bookingQueue.dequeue();
    }

    public Booking viewNextGuest() {
        if (bookingQueue.isEmpty())
            return null;

        return bookingQueue.getFront();
    }

    public QueueInterface<Booking> getBookingQueue() {
        return bookingQueue;
    }
}