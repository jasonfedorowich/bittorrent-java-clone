package error;

public class PieceHashException extends RuntimeException {

    public PieceHashException(String expected, String actual) {
        super(String.format("Piece hashes are inconsistent: expected %s, actual %s", expected, actual));
    }
}
