package entities;

import jakarta.persistence.*;

@Entity
@Table(name = "functions")
public class Function {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "function_id")
    private Integer functionId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expression", columnDefinition = "TEXT")
    private String expression; // Для обратной совместимости (опционально)

    @Column(name = "function_type", length = 50)
    private String functionType; // "ARRAY" или "LINKED_LIST"

    @Column(name = "x_values", columnDefinition = "TEXT")
    private String xValues; // JSON массив x значений

    @Column(name = "y_values", columnDefinition = "TEXT")
    private String yValues; // JSON массив y значений

    @Column(name = "count")
    private Integer count; // количество точек

    public Function() {
    }

    // Старый конструктор для обратной совместимости
    public Function(String name, User user, String expression) {
        this.name = name;
        this.user = user;
        this.expression = expression;
    }

    // Новый конструктор для создания функций из массивов
    public Function(String name, User user, String functionType, String xValues, String yValues, Integer count) {
        this.name = name;
        this.user = user;
        this.functionType = functionType;
        this.xValues = xValues;
        this.yValues = yValues;
        this.count = count;
    }

    public Integer getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Integer functionId) {
        this.functionId = functionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }

    public String getXValues() {
        return xValues;
    }

    public void setXValues(String xValues) {
        this.xValues = xValues;
    }

    public String getYValues() {
        return yValues;
    }

    public void setYValues(String yValues) {
        this.yValues = yValues;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}





