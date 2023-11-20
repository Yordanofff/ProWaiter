package FrontEnd;

// This method allows passing in a method reference with an integer as a parameter
@FunctionalInterface
public interface MenuAction {
    void execute(int option);
}
