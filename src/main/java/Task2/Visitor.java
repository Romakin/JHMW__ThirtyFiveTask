package Task2;

public class Visitor {

    final Restaurant restaurant;
    final int timeoutBeforeStep = 2000;
    String name;
    Order activeOrder;

    public Visitor(Restaurant restaurant, String name) {
        this.restaurant = restaurant;
        this.name = name;
    }

    public void visit() {
        System.out.println(name + " в ресторане");
        makeOrder();
    }

    public void makeOrder() {
        boolean result = restaurant.makeOrder(this);
        System.out.println(name + " делает заказ");
        if (result) {
            try {
                Thread.sleep(timeoutBeforeStep);
                if (!getOrder()) {
                    System.out.printf("Заказ получен: %s \n",activeOrder);
                    activeOrder.setStatus(OrderStatus.USE);
                    System.out.println(name + " приступил к еде");
                    Thread.sleep(timeoutBeforeStep);
                    activeOrder.setStatus(OrderStatus.PAY);
                    System.out.println(name + " оплатил заказ");
                    Thread.sleep(timeoutBeforeStep);
                    activeOrder.setStatus(OrderStatus.FINISH);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(name + " вышел из ресторана");
        return;
    }

    private boolean getOrder() {
        activeOrder = restaurant.getReadyOrder(this);
        return activeOrder == null;
    }

    @Override
    public String toString() {
        return name;
    }
}
