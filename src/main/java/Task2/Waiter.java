package Task2;

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
        if (!restaurant.isClosed(OrderStatus.NEW)) {
            System.out.println(name + " свободен!");
            try {
                if (getNewOrder()) {
                    if (restaurant.cooker.prepareOrder(activeOrder)) {
                        Thread.sleep(timeoutBeforeStep);
                        activeOrder = null;
                        if (getReadyOrder()) {
                            restaurant.callNotify();
                        }
                    } else {
                        System.out.println("false");
                    }
                }
                Thread.sleep(timeoutBeforeStep);
                waitToGetOrder();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            restaurant.callNotify();
            System.out.println(name + " ушел");
        }
    }

    private boolean getNewOrder() {
        restaurant.setWaiterOrder(this, OrderStatus.NEW, OrderStatus.PREPARE);
        if (activeOrder != null) {
            System.out.printf(" %s взял заказ у %s\n", name, activeOrder.getVisitor().toString());
        }
        return activeOrder != null;
    }

    private boolean getReadyOrder() {
        restaurant.setWaiterOrder(this, OrderStatus.READY, OrderStatus.DELIVERY);
        if (activeOrder != null) {
            System.out.printf(" %s несет заказ  %s \n", name, activeOrder.toString());
        }
        return activeOrder != null;
    }

    @Override
    public String toString() {
        return name;
    }

    public void setActiveOrder(Order activeOrder) {
        this.activeOrder = activeOrder;
    }
}
