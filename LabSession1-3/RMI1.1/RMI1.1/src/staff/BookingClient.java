package staff;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.util.Set;

import hotel.BookingDetail;
import hotel.BookingManager;
import hotel.IBookingManager;
import hotel.RoomNotAvailableException;

public class BookingClient extends AbstractScriptedSimpleTest {

	private IBookingManager bm = null;

	public static void main(String[] args) throws Exception {
		BookingClient client = new BookingClient();


		Registry registry = LocateRegistry.getRegistry();
		client.bm = (IBookingManager) registry.lookup("BookingManager");

		client.run();
	}

	/***************
	 * CONSTRUCTOR *
	 ***************/
	public BookingClient() {
		try {
			//Look up the registered remote instance
			bm = new BookingManager();
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}

	@Override
	public boolean isRoomAvailable(Integer roomNumber, LocalDate date) throws RemoteException {
		return bm.isRoomAvailable(roomNumber,date);
	}

	@Override
	public void addBooking(BookingDetail bookingDetail) throws RemoteException{
		try{
			bm.addBooking(bookingDetail);
		} catch (RoomNotAvailableException e) {
			System.out.println(String.format("Room:%d is already booked on %s",bookingDetail.getRoomNumber(),bookingDetail.getDate()));
		}
	}

	@Override
	public Set<Integer> getAvailableRooms(LocalDate date) throws RemoteException{
		return bm.getAvailableRooms(date);

	}

	@Override
	public Set<Integer> getAllRooms() throws RemoteException {
		return bm.getAllRooms();
	}
}
