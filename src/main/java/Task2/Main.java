package Task2;

public class Main {
    public static void main(String[] args) {
        final Restaurant rest = new Restaurant();
        try {
            Restaurant.openRestaurant(rest);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
