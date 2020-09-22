package Task1;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Restaurant {

    int visitorsTimeout = 1000;
    int waitersNum = 4;
    int maxVisitorsBeforeClose = 5;
    int visitorsNum = 5;
    boolean isClosed = false;
    List<Order> orders;

    public Restaurant() {
        this.orders = new ArrayList<>(maxVisitorsBeforeClose);
    }

    public static void openRestaurant(Restaurant r) throws InterruptedException {
        int threadNums = r.waitersNum;
        while (threadNums > 0) {
            String name = "Официант" + threadNums;
            new Thread(null, new Waiter(r, name)::work, name).start();
            threadNums--;
        }
        Thread.sleep(r.visitorsTimeout);
        threadNums = r.visitorsNum;
        while (threadNums > 0) {
            String name = "Посетитель" + threadNums;
            new Thread(null, new Visitor(r, name)::makeOrder, name).start();
            threadNums--;
            Thread.sleep(r.visitorsTimeout);
        }
    }



    Order getOrder(OrderStatus status) {
        if (orders.size() > 0) {
            for (int i = 0; i < orders.size(); i++) {
                if (orders.get(i).getStatus() == status)
                    return orders.get(i);
            }
        }
        return null;
    }

    synchronized void getNewOrder(Waiter waiter) {
        try {
            while (getOrder(OrderStatus.NEW) == null) {
                wait();
            }
            for (int i = 0; i < orders.size(); i++) {
                if (orders.get(i).getStatus() == OrderStatus.NEW) {
                    orders.get(i).setStatus(OrderStatus.PREPARE);
                    orders.get(i).setWaiter(waiter);
                    waiter.setActiveOrder(orders.get(i));
                }
            }
        } catch (InterruptedException e) {}
    }

    synchronized Order getReadyOrder(Visitor visitor) {
        try {
            while (!(getOrder(OrderStatus.DELIVERY) != null &&
                    getOrder(OrderStatus.DELIVERY).getVisitor().equals(visitor))) {
                wait();
            }
            for (int i = 0; i < orders.size(); i++) {
                if (orders.get(i).getVisitor().equals(visitor)&&
                        orders.get(i).getStatus() == OrderStatus.DELIVERY)
                    return orders.get(i);
            }
        } catch (InterruptedException e) {}
        return null;
    }

    synchronized boolean makeOrder(Visitor visitor) {
        if (orders.size() < maxVisitorsBeforeClose) {
            orders.add(new Order(visitor, OrderStatus.NEW));
            notifyAll();
            return true;
        } else {
            System.out.println("Извините, ресторан закрывается");
            return false;
        }
    }

    synchronized void callNotify() {
        notifyAll();
    }

    synchronized boolean isClosed() {
        if (orders.size() == maxVisitorsBeforeClose &&
        orders.stream().filter(e -> e.getStatus() != OrderStatus.NEW).collect(Collectors.toList()).size() == orders.size())
            isClosed = true;
        return isClosed;
    }
}
