package Task1;

public class Waiter {

    final Restaurant restaurant;
    final int timeoutBeforeStep = 3000;
    String name;
    Order activeOrder;

    public Waiter(Restaurant restaurant, String name) {
        this.restaurant = restaurant;
        this.name = name;
    }

    public void work() {
        System.out.println(name + " на работе!");
        waitToGetOrder();
    }

    public void waitToGetOrder() {
        if (!restaurant.isClosed()) {
            System.out.println(name + " свободен!");
            try {
                if (!getOrder()) {
                    Thread.sleep(timeoutBeforeStep);
                    System.out.println(name + " несет заказ");
                    activeOrder.setStatus(OrderStatus.DELIVERY);
                    restaurant.callNotify();
                }
                Thread.sleep(timeoutBeforeStep);
                waitToGetOrder();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean getOrder() {
        restaurant.getNewOrder(this);
        if (activeOrder != null) {
            System.out.printf(" %s взял заказ у %s\n", name, activeOrder.getVisitor().toString());
        }
        return activeOrder == null;
    }

    @Override
    public String toString() {
        return name;
    }

    public void setActiveOrder(Order activeOrder) {
        this.activeOrder = activeOrder;
    }
}
