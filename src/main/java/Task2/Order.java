package Task2;

public class Order {
    Visitor visitor;
    Waiter waiter;
    OrderStatus status;

    public Order(Visitor visitor, OrderStatus status) {
        this.visitor = visitor;
        this.status = status;
    }

    public Visitor getVisitor() {
        return visitor;
    }

    public Waiter getWaiter() {
        return waiter;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setWaiter(Waiter waiter) {
        this.waiter = waiter;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "[ Посетитель: " + visitor +
                ", Официант: " + waiter +
                ", Статус: " + status +
                ']';
    }
}
