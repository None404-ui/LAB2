package entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
    private String expression;

    @OneToMany(mappedBy = "function", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ComputedPoint> computedPoints = new ArrayList<>();

    public Function() {
    }

    public Function(String name, User user, String expression) {
        this.name = name;
        this.user = user;
        this.expression = expression;
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

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public List<ComputedPoint> getComputedPoints() {
        return computedPoints;
    }

    public void setComputedPoints(List<ComputedPoint> computedPoints) {
        this.computedPoints = computedPoints;
    }
}





