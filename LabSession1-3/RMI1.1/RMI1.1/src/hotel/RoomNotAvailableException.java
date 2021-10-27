package hotel;

public class RoomNotAvailableException extends Exception{
    public RoomNotAvailableException(String errorMessage) {
        super(errorMessage);
    }

}
