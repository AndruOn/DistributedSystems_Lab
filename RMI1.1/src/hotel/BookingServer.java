package hotel;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class BookingServer implements Remote{

    public static void main(String[] args) throws RemoteException {
        System.setSecurityManager(null);

        BookingManager bookM = new BookingManager();

        BookingManager stub = (BookingManager) UnicastRemoteObject.exportObject((Remote) bookM, 0);
        Registry registry = LocateRegistry.getRegistry();
        registry.rebind("BookingServer", stub);

    }
}
