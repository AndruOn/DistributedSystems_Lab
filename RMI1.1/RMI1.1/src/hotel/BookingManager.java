package hotel;

import java.time.LocalDate;
import java.util.*;

//import java.rmi.registry.Registry;
import java.rmi.Remote;
import java.rmi.RemoteException;

public class BookingManager implements IBookingManager {

	private Room[] rooms;

	public BookingManager() {
		this.rooms = initializeRooms();

	}

	public Set<Integer> getAllRooms() {
		Set<Integer> allRooms = new HashSet<Integer>();
		Iterable<Room> roomIterator = Arrays.asList(rooms);
		for (Room room : roomIterator) {
			allRooms.add(room.getRoomNumber());
		}
		return allRooms;
	}

	public boolean isRoomAvailable(Integer roomNumber, LocalDate date) {
		for (Room room : rooms) {
			if(room.getRoomNumber().equals(roomNumber)){
				synchronized (room) {
					List<BookingDetail> bookings = room.getBookings();
					if (bookings.isEmpty()) {
						return true;
					}
					for (BookingDetail bDetail : bookings) {
						if (bDetail.getDate().equals(date)) {
							return false;
						}
					}
					return true;
				}
			}
		}
		return false;
	}

	public  void addBooking(BookingDetail bookingDetail) throws RoomNotAvailableException {
		for (Room room : rooms) {
			Integer roomNumber = bookingDetail.getRoomNumber();
			if(room.getRoomNumber().equals(roomNumber)){
				synchronized (room) {
					if (!isRoomAvailable(roomNumber, bookingDetail.getDate())) {
						throw new RoomNotAvailableException("Room:" + Integer.toString(roomNumber) + " is not available");
					}
					room.getBookings().add(bookingDetail);
				}
			}

		}
	}

	public Set<Integer> getAvailableRooms(LocalDate date) {
		Set<Integer> availableRooms = new HashSet<>();
		for (Room room:rooms) {
			synchronized (room) {
				if (isRoomAvailable(room.getRoomNumber(), date)) {
					availableRooms.add(room.getRoomNumber());
				}
			}
		}
		return availableRooms;
	}

	private static Room[] initializeRooms() {
		Room[] rooms = new Room[4];
		rooms[0] = new Room(101);
		rooms[1] = new Room(102);
		rooms[2] = new Room(201);
		rooms[3] = new Room(203);
		return rooms;
	}
}
