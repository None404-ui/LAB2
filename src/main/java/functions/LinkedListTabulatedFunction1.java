package functions;

public class LinkedListTabulatedFunction1 implements TabulatedFunction, Insertable, Removable {

    static class Node {
        public Node next;
        public Node prev;
        public double x;
        public double y;

        public Node(double x, double y) {
            this.x = x;
            this.y = y;
            this.next = null;
            this.prev = null;
        }
    }

    private Node head;
    private int count;




    public LinkedListTabulatedFunction1(double[] xValues, double[] yValues) {
        if (xValues.length != yValues.length) {
            throw new IllegalArgumentException("Arrays must have the same length");
        }
        if (xValues.length < 2) {
            throw new IllegalArgumentException("At least 2 points required");
        }

        for (int i = 1; i < xValues.length; i++) {
            if (xValues[i] <= xValues[i - 1]) {
                throw new IllegalArgumentException("x values must be strictly increasing");
            }
        }


        this.count = 0;  // ИНИЦИАЛИЗИРУЕМ count ПЕРЕД вызовами addNode
        this.head = null;

        for (int i = 0; i < xValues.length; i++) {
            addNode(xValues[i], yValues[i]);
        }
    }

    public LinkedListTabulatedFunction1(MathFunction func, double xFrom, double xTo, int count) {
        if (count < 2) {
            throw new IllegalArgumentException("At least 2 points required");
        }

        if (xFrom > xTo) {
            double temp = xFrom;
            xFrom = xTo;
            xTo = temp;
        }


        this.count = 0;
        this.head = null;

        if (xFrom == xTo) {
            double y = func.apply(xFrom);
            for (int i = 0; i < count; i++) {
                addNode(xFrom, y);
            }
        } else {
            double step = (xTo - xFrom) / (count - 1);
            for (int i = 0; i < count; i++) {
                double x = xFrom + i * step;
                double y = func.apply(x);
                addNode(x, y);
            }
        }
    }


    private void addNode(double x, double y) {
        Node newNode = new Node(x, y);

        if (head == null) {
            // Первый узел - создаем циклические ссылки
            head = newNode;
            head.next = head;
            head.prev = head;
        } else {
            // Получаем последний узел (предыдущий от head)
            Node last = head.prev;

            // Связываем последний узел с новым
            last.next = newNode;
            newNode.prev = last;

            // Связываем новый узел с head (замыкаем круг)
            newNode.next = head;
            head.prev = newNode;
        }

        count++;
        System.out.println("Added node: x=" + x + ", y=" + y + ", count=" + count); // Для отладки
    }

    // для 3 задания remove
    @Override
    public void remove(int index) {
        // Проверка корректности индекса
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException(
                    "Индекс " + index + " вне диапазона [0, " + (count - 1) + "]");
        }

        // Проверка минимального количества точек
        if (count <= 2) {
            throw new IllegalStateException(
                    "Нельзя удалить элемент: функция должна содержать минимум 2 точки");
        }

        // Находим узел для удаления
        Node nodeToRemove = getNode(index);

        // Если удаляем единственный узел (невозможно из-за проверки выше)
        if (count == 1) {
            head = null;
        } else {
            // Связываем соседние узлы между собой
            Node prevNode = nodeToRemove.prev;
            Node nextNode = nodeToRemove.next;

            prevNode.next = nextNode;
            nextNode.prev = prevNode;

            // Если удаляем голову, обновляем ссылку на голову
            if (nodeToRemove == head) {
                head = nextNode;
            }
        }

        // Уменьшаем счетчик
        count--;
    }


    //ДЛЯ 3 ЗАДАНИЯ
    @Override
    public void insert(double x, double y) {
        // Если список пустой, просто добавляем узел
        if (head == null) {
            addNode(x, y);
            return;
        }

        // Ищем позицию для вставки
        Node current = head;
        Node prev = head.prev; // последний узел

        do {
            if (current.x == x) {
                // Если x уже существует, заменяем y
                current.y = y;
                return;
            }

            if (current.x > x) {
                // Нашли позицию для вставки перед current
                insertBefore(current, x, y);
                return;
            }

            prev = current;
            current = current.next;
        } while (current != head);

        // Если дошли до конца и не нашли позицию, вставляем в конец
        insertAfter(prev, x, y);
    }


    private void insertBefore(Node node, double x, double y) {
        Node newNode = new Node(x, y);

        Node prevNode = node.prev;

        // Связываем предыдущий узел с новым
        prevNode.next = newNode;
        newNode.prev = prevNode;

        // Связываем новый узел с текущим
        newNode.next = node;
        node.prev = newNode;

        // Если вставляем перед головой, обновляем голову
        if (node == head) {
            head = newNode;
        }

        count++;
    }


    private void insertAfter(Node node, double x, double y) {
        Node newNode = new Node(x, y);

        Node nextNode = node.next;

        // Связываем текущий узел с новым
        node.next = newNode;
        newNode.prev = node;

        // Связываем новый узел со следующим
        newNode.next = nextNode;
        nextNode.prev = newNode;

        count++;
    }
// КОНЕЦ ДОБАВОК ДЛЯ 3 ЗАДАНИЯ

    @Override
    public int getCount() {
        return count;
    }

    // Остальные методы (getNode, getX, getY, leftBound, rightBound и т.д.)
    // должны быть реализованы как в предыдущем ответе

    private Node getNode(int index) {
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + count);
        }

        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current;
    }

    @Override
    public double getX(int index) {
        return getNode(index).x;
    }

    @Override
    public double getY(int index) {
        return getNode(index).y;
    }

    @Override
    public void setY(int index, double value) {
        getNode(index).y = value;
    }

    @Override
    public double leftBound() {
        return head.x;
    }

    @Override
    public double rightBound() {
        return head.prev.x;
    }

    @Override
    public int indexOfX(double x) {
        Node current = head;
        for (int i = 0; i < count; i++) {
            if (current.x == x) {
                return i;
            }
            current = current.next;
        }
        return -1;
    }

    @Override
    public int indexOfY(double y) {
        Node current = head;
        for (int i = 0; i < count; i++) {
            if (current.y == y) {
                return i;
            }
            current = current.next;
        }
        return -1;
    }

//
    @Override
    public double apply(double x) {
        if (count == 1) {
            return head.y; // только одна точка
        }

        // Находим интервал для x
        int floorIndex = floorIndexOfX(x);

        if (floorIndex == 0 && x < getX(0)) {
            // Экстраполяция слева
            return extrapolateLeft(x);
        } else if (floorIndex == count - 1) {
            // Экстраполяция справа
            return extrapolateRight(x);
        } else {
            // Интерполяция между точками
            return interpolate(x, floorIndex);
        }
    }

    /**
     * Находит индекс наибольшего x, который <= заданному x
     */
    private int floorIndexOfX(double x) {
        if (x < getX(0)) {
            return 0; // экстраполяция слева
        }

        // Ищем интервал, в который попадает x
        for (int i = 0; i < count - 1; i++) {
            if (x >= getX(i) && x <= getX(i + 1)) {
                return i;
            }
        }

        return count - 1; // экстраполяция справа
    }

    /**
     * Линейная интерполяция между двумя точками
     */
    private double interpolate(double x, int floorIndex) {
        double x0 = getX(floorIndex);
        double x1 = getX(floorIndex + 1);
        double y0 = getY(floorIndex);
        double y1 = getY(floorIndex + 1);

        return y0 + (y1 - y0) * (x - x0) / (x1 - x0);
    }

    /**
     * Экстраполяция слева (используем первый интервал)
     */
    private double extrapolateLeft(double x) {
        return interpolate(x, 0);
    }

    /**
     * Экстраполяция справа (используем последний интервал)
     */
    private double extrapolateRight(double x) {
        return interpolate(x, count - 2);
    }


}