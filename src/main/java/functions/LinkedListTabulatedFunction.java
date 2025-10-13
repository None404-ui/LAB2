package functions;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Табулированная функция на основе связного списка
 */
public class LinkedListTabulatedFunction extends AbstractTabulatedFunction implements Insertable, Removable {

    private Node head;
    // count наследуется от AbstractTabulatedFunction

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
        checkLengthIsTheSame(xValues, yValues);
        checkSorted(xValues);
        
        if (xValues.length < 2) {
            throw new IllegalArgumentException("Должно быть как минимум 2 точки");
        }

        count = xValues.length;

        // Создаем циклический двусвязный список
        head = new Node(xValues[0], yValues[0]);
        head.next = head;
        head.prev = head;

        for (int i = 1; i < count; i++) {
            Node newNode = new Node(xValues[i], yValues[i]);
            Node last = head.prev;
            
            last.next = newNode;
            newNode.prev = last;
            newNode.next = head;
            head.prev = newNode;
        }
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

        // Создаем циклический двусвязный список
        head = new Node(xFrom, source.apply(xFrom));
        head.next = head;
        head.prev = head;

        for (int i = 1; i < count; i++) {
            double x = xFrom + i * step;
            double y = source.apply(x);
            Node newNode = new Node(x, y);
            Node last = head.prev;
            
            last.next = newNode;
            newNode.prev = last;
            newNode.next = head;
            head.prev = newNode;
        }
    }

    @Override
    public double getX(int index) {
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("индекс " + index + " вне допустимого диапазона [0, " + (count-1) + "]");
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
            throw new IllegalArgumentException("индекс " + index + " вне допустимого диапазона [0, " + (count-1) + "]");
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
            throw new IllegalArgumentException("индекс " + index + " вне допустимого диапазона [0, " + (count-1) + "]");
        }

        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        current.y = value;
    }

    @Override
    public int indexOfX(double x) {
        if (head == null) {
            return -1;
        }

        Node current = head;
        int index = 0;

        do {
            if (current.x == x) {
                return index;
            }
            current = current.next;
            index++;
        } while (current != head);

        return -1;
    }

    @Override
    public int indexOfY(double y) {
        if (head == null) {
            return -1;
        }

        Node current = head;
        int index = 0;

        do {
            if (current.y == y) {
                return index;
            }
            current = current.next;
            index++;
        } while (current != head);

        return -1;
    }

    @Override
    protected int floorIndexOfX(double x) {
        if (head == null) {
            return 0;
        }

        if (x < head.x) {
            throw new IllegalArgumentException("x = " + x + " меньше левой границы " + head.x);
        }

        Node current = head;
        int index = 0;

        while (current.next != head && current.next.x <= x) {
            current = current.next;
            index++;
        }

        return index;
    }

    @Override
    protected double extrapolateLeft(double x) {
        return interpolate(x, head.x, head.next.x, head.y, head.next.y);
    }

    @Override
    protected double extrapolateRight(double x) {
        Node last = head.prev;
        return interpolate(x, last.prev.x, last.x, last.prev.y, last.y);
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        if (floorIndex < 0 || floorIndex >= count) {
            throw new IllegalArgumentException("Некорректный floorIndex: " + floorIndex);
        }

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

        // Проверяем, что x находится в интервале интерполирования
        if (x < leftNode.x || x > rightNode.x) {
            throw new exceptions.InterpolationException("x is outside the interpolation interval");
        }

        return interpolate(x, leftNode.x, rightNode.x, leftNode.y, rightNode.y);
    }

    /**
     * Добавляет новый узел в конец списка
     */
    private void addNode(double x, double y) {
        Node newNode = new Node(x, y);
        if (head == null) {
            head = newNode;
            head.next = head;
            head.prev = head;
        } else {
            Node last = head.prev;
            last.next = newNode;
            newNode.prev = last;
            newNode.next = head;
            head.prev = newNode;
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
        while (current.next != head && current.next.x < x) {
            current = current.next;
            index++;
        }

        // Вставляем новый узел
        Node newNode = new Node(x, y);
        newNode.next = current.next;
        newNode.prev = current;
        current.next.prev = newNode;
        current.next = newNode;
        count++;
    }

    @Override
    public Iterator<Point> iterator() {
        return new Iterator<Point>() {
            private Node currentNode = head;  // начинаем с головы списка
            private int returnedCount = 0;    // счетчик возвращенных элементов

            @Override
            public boolean hasNext() {
                // Есть следующий элемент, если мы еще не вернули все элементы
                return returnedCount < count;
            }

            @Override
            public Point next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("Нет больше элементов в итераторе");
                }

                // Создаем точку из текущего узла
                Point point = new Point(currentNode.x, currentNode.y);

                // Переходим к следующему узлу
                currentNode = currentNode.next;
                returnedCount++;

                return point;
            }
        };
    }

    @Override
    public void remove(int index) {
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("индекс " + index + " вне допустимого диапазона [0, " + (count-1) + "]");
        }
        if (count <= 2) {
            throw new IllegalStateException("Нельзя удалить элемент из функции с менее чем 2 точками");
        }

        // Находим узел для удаления
        Node nodeToRemove = head;
        for (int i = 0; i < index; i++) {
            nodeToRemove = nodeToRemove.next;
        }

        // Если удаляем голову
        if (nodeToRemove == head) {
            head = head.next;
        }
        
        // Связываем соседние узлы
        nodeToRemove.prev.next = nodeToRemove.next;
        nodeToRemove.next.prev = nodeToRemove.prev;

        count--;
    }
}





