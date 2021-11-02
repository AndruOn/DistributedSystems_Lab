package be.kuleuven.foodrestservice.exceptions;


public class FormatNotAcceptedException extends RuntimeException{
    public FormatNotAcceptedException() {
        super("Meal is in unsopported format ");
    }
}
