package Task2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Cooker {

    final Restaurant restaurant;
    final int timeoutBeforeStep = 3000;
    String name;
    List<Order> activeOrder;
    Lock lock;
    Condition condition;
    Condition busy;
    boolean isBusy = false;

    public Cooker(Restaurant restaurant, String name) {
        this.restaurant = restaurant;
        this.name = name;
        lock = new ReentrantLock();
        condition = lock.newCondition();
        busy = lock.newCondition();
    }

    public void work() {
        System.out.println(name + " на работе!");
        activeOrder = new ArrayList<>();
        waitToGetOrder();
    }

    public void waitToGetOrder() {
        try {
            lock.lock();
            if (!restaurant.isClosed(OrderStatus.PREPARE)) {
                while (activeOrder.size() == 0) {
                    condition.await();
                }
                System.out.printf("  %s готовит заказ \n", name);
                isBusy = true;
                Thread.sleep(timeoutBeforeStep);
                activeOrder.remove(0).setStatus(OrderStatus.READY);
                System.out.printf("  %s закончил готовить \n", name);
                isBusy = false;
                restaurant.callNotify();
                waitToGetOrder();
            } else {
                System.out.println(name + " ушел");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public boolean prepareOrder(Order order) {
        try {
            lock.lock();
            activeOrder.add(order);
            condition.signalAll();
            return true;
        } finally {
            lock.unlock();
        }
    }
}
