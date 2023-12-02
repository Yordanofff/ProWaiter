package BackEnd.DB;

public class BadSqlDataException extends RuntimeException {
    public BadSqlDataException(String s) {
        super(s);
    }
}
