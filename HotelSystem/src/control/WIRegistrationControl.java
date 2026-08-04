package control;

import adt.QueueInterface;
import adt.CircularArrayQueue;
import entity.Booking;

public class WIRegistrationControl {

    private QueueInterface<Booking> bookingQueue;

    public WIRegistrationControl() {
        bookingQueue = new CircularArrayQueue<>();
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
        if (    bookingQueue.isEmpty())
            return null;

        return bookingQueue.peek();
    }

    public QueueInterface<Booking> getBookingQueue() {
        return bookingQueue;
    }
}