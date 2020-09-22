package Task2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class Restaurant {

    int visitorsTimeout = 3000;
    int waitersNum = 4;
    int maxVisitorsBeforeClose = 5;
    int visitorsNum = 5;
    boolean isClosed = false;
    List<Order> orders;
    Cooker cooker;
    Lock lock;
    Condition condition;

    public Restaurant() {
        this.orders = new ArrayList<>(maxVisitorsBeforeClose);
        lock = new ReentrantLock();
        condition = lock.newCondition();
    }

    public static void openRestaurant(Restaurant r) throws InterruptedException {
        int threadNums = r.waitersNum;
        while (threadNums > 0) {
            String name = "Официант" + threadNums;
            new Thread(null, new Waiter(r, name)::work, name).start();
            threadNums--;
        }

        String cookerName = "Повар";
        r.cooker = new Cooker(r, cookerName);
        new Thread(null, r.cooker::work, cookerName).start();

        Thread.sleep(r.visitorsTimeout);
        threadNums = r.visitorsNum;
        while (threadNums > 0) {
            String name = "Посетитель" + threadNums;
            new Thread(null, new Visitor(r, name)::visit, name).start();
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

    void setWaiterOrder(Waiter waiter, OrderStatus oldStatus, OrderStatus newStatus) {
        try {
            lock.lock();
            while (getOrder(oldStatus) == null && !isClosed) {
                condition.await();
            }
            for (int i = 0; i < orders.size(); i++) {
                if (orders.get(i).getStatus() == oldStatus &&
                        (orders.get(i).getWaiter() == null ||
                                orders.get(i).getWaiter().equals(waiter))) {
                    if (orders.get(i).getWaiter() == null) {
                        orders.get(i).setWaiter(waiter);
                    }
                    orders.get(i).setStatus(newStatus);
                    waiter.setActiveOrder(orders.get(i));
                }
            }
        } catch (InterruptedException e) {} finally {
            lock.unlock();
        }
    }

    Order getReadyOrder(Visitor visitor) {
        try {
            lock.lock();
            while (!(getOrder(OrderStatus.DELIVERY) != null &&
                    getOrder(OrderStatus.DELIVERY).getVisitor().equals(visitor))) {
                condition.await();
            }
            for (int i = 0; i < orders.size(); i++) {
                if (orders.get(i).getVisitor().equals(visitor)&&
                        orders.get(i).getStatus() == OrderStatus.DELIVERY)
                    return orders.get(i);
            }
        } catch (InterruptedException e) {} finally {
            lock.unlock();
        }
        return null;
    }

    boolean makeOrder(Visitor visitor) {
        try {
            lock.lock();
            if (orders.size() < maxVisitorsBeforeClose) {
                orders.add(new Order(visitor, OrderStatus.NEW));
                condition.signalAll();
                return true;
            } else {
                System.out.println("Извините, ресторан закрывается");
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    void callNotify() {
        try {
            lock.lock();
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    boolean isClosed(OrderStatus status) {
        if (orders.size() == maxVisitorsBeforeClose &&
                orders.stream().filter(e -> e.getStatus() != status).collect(Collectors.toList()).size() == orders.size())
            isClosed = true;
        return isClosed;
    }


}
