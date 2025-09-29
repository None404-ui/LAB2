package functions;

/**
 * Табулированная функция на основе связного списка
 */
public class LinkedListTabulatedFunction extends AbstractTabulatedFunction implements Insertable {

    private Node head;
    private Node tail;

    /**
     * Узел связного списка
     */
    private static class Node {
        double x;
        double y;
        Node next;
        Node prev;

        Node(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Конструктор из массивов
     */
    public LinkedListTabulatedFunction(double[] xValues, double[] yValues) {
        if (xValues.length != yValues.length) {
            throw new IllegalArgumentException("Массивы xValues и yValues должны иметь одинаковую длину");
        }
        if (xValues.length < 2) {
            throw new IllegalArgumentException("Должно быть как минимум 2 точки");
        }

        count = xValues.length;

        // Проверяем, что xValues упорядочены
        for (int i = 1; i < count; i++) {
            if (xValues[i] <= xValues[i - 1]) {
                throw new IllegalArgumentException("Значения x должны быть упорядочены по возрастанию");
            }
        }

        // Создаем связный список
        head = new Node(xValues[0], yValues[0]);
        Node current = head;

        for (int i = 1; i < count; i++) {
            Node newNode = new Node(xValues[i], yValues[i]);
            current.next = newNode;
            newNode.prev = current;
            current = newNode;
        }

        tail = current;
    }

    /**
     * Конструктор для табулирования функции на интервале
     */
    public LinkedListTabulatedFunction(MathFunction source, double xFrom, double xTo, int count) {
        if (count < 2) {
            throw new IllegalArgumentException("Количество точек должно быть как минимум 2");
        }

        // Меняем местами если xFrom > xTo
        if (xFrom > xTo) {
            double temp = xFrom;
            xFrom = xTo;
            xTo = temp;
        }

        this.count = count;

        // Заполняем xValues равномерно
        double step = (xTo - xFrom) / (count - 1);

        // Создаем связный список
        head = new Node(xFrom, source.apply(xFrom));
        Node current = head;

        for (int i = 1; i < count; i++) {
            double x = xFrom + i * step;
            double y = source.apply(x);
            Node newNode = new Node(x, y);
            current.next = newNode;
            newNode.prev = current;
            current = newNode;
        }

        tail = current;
    }

    @Override
    public double getX(int index) {
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException("Индекс вне диапазона");
        }

        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.x;
    }

    @Override
    public double getY(int index) {
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException("Индекс вне диапазона");
        }

        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.y;
    }

    @Override
    public void setY(int index, double value) {
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException("Индекс вне диапазона");
        }

        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        current.y = value;
    }

    @Override
    public int indexOfX(double x) {
        Node current = head;
        int index = 0;
        while (current != null) {
            if (current.x == x) {
                return index;
            }
            current = current.next;
            index++;
        }
        return -1;
    }

    @Override
    public int indexOfY(double y) {
        Node current = head;
        int index = 0;
        while (current != null) {
            if (current.y == y) {
                return index;
            }
            current = current.next;
            index++;
        }
        return -1;
    }

    @Override
    protected int floorIndexOfX(double x) {
        if (x < head.x) {
            return 0;
        }

        Node current = head;
        int index = 0;
        while (current.next != null) {
            if (current.x <= x && current.next.x > x) {
                return index;
            }
            current = current.next;
            index++;
        }

        return count;
    }

    @Override
    protected double extrapolateLeft(double x) {
        if (count == 1) {
            return head.y;
        }
        return interpolate(x, head.x, head.next.x, head.y, head.next.y);
    }

    @Override
    protected double extrapolateRight(double x) {
        if (count == 1) {
            return tail.y;
        }
        return interpolate(x, tail.prev.x, tail.x, tail.prev.y, tail.y);
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        if (floorIndex == count) {
            return extrapolateRight(x);
        }
        if (floorIndex < 0) {
            return extrapolateLeft(x);
        }

        Node leftNode = head;
        for (int i = 0; i < floorIndex; i++) {
            leftNode = leftNode.next;
        }

        Node rightNode = leftNode.next;
        if (rightNode == null) {
            return extrapolateRight(x);
        }

        return interpolate(x, leftNode.x, rightNode.x, leftNode.y, rightNode.y);
    }

    /**
     * Добавляет новый узел в конец списка
     */
    private void addNode(double x, double y) {
        Node newNode = new Node(x, y);
        if (head == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        count++;
    }

    @Override
    public void insert(double x, double y) {
        // Если список пустой, просто добавляем узел
        if (count == 0) {
            addNode(x, y);
            return;
        }

        // Проверяем, существует ли уже x
        int existingIndex = indexOfX(x);
        if (existingIndex != -1) {
            setY(existingIndex, y);
            return;
        }

        // Ищем место для вставки
        Node current = head;
        int index = 0;

        // Проверяем, нужно ли вставить в начало
        if (x < head.x) {
            Node newNode = new Node(x, y);
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
            count++;
            return;
        }

        // Ищем место для вставки в середину или конец
        while (current.next != null && current.next.x < x) {
            current = current.next;
            index++;
        }

        // Вставляем новый узел
        Node newNode = new Node(x, y);
        newNode.next = current.next;
        newNode.prev = current;

        if (current.next != null) {
            current.next.prev = newNode;
        } else {
            tail = newNode;
        }

        current.next = newNode;
        count++;
    }
}

