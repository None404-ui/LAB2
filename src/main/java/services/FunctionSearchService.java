package services;

import entities.ComputedPoint;
import entities.Function;
import entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repositories.ComputedPointRepository;
import repositories.FunctionRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class FunctionSearchService {
    private static final Logger logger = LoggerFactory.getLogger(FunctionSearchService.class);

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private ComputedPointRepository computedPointRepository;

    /**
     * Поиск в глубину (DFS) - рекурсивный поиск функций пользователя
     */
    public List<Function> searchDepthFirst(User user, Integer startFunctionId, int maxDepth) {
        logger.info("Starting DFS search for user {} from function {} with max depth {}", 
                   user.getUsername(), startFunctionId, maxDepth);
        
        Set<Integer> visited = new HashSet<>();
        List<Function> result = new ArrayList<>();
        
        dfsHelper(user, startFunctionId, maxDepth, 0, visited, result);
        
        logger.info("DFS search completed, found {} functions", result.size());
        return result;
    }

    private void dfsHelper(User user, Integer functionId, int maxDepth, int currentDepth, 
                          Set<Integer> visited, List<Function> result) {
        if (currentDepth > maxDepth || visited.contains(functionId)) {
            logger.debug("DFS: Skipping function {} at depth {} (max depth: {}, visited: {})", 
                        functionId, currentDepth, maxDepth, visited.contains(functionId));
            return;
        }
        
        visited.add(functionId);
        Optional<Function> functionOpt = functionRepository.findById(functionId);
        
        if (functionOpt.isPresent()) {
            Function function = functionOpt.get();
            if (function.getUser().getUserId().equals(user.getUserId())) {
                result.add(function);
                logger.debug("DFS: Added function {} '{}' at depth {}", 
                           function.getFunctionId(), function.getName(), currentDepth);
                
                // Рекурсивный поиск всех функций пользователя (имитация иерархии)
                List<Function> userFunctions = functionRepository.findByUser(user);
                for (Function nextFunction : userFunctions) {
                    if (!visited.contains(nextFunction.getFunctionId())) {
                        dfsHelper(user, nextFunction.getFunctionId(), maxDepth, 
                                currentDepth + 1, visited, result);
                    }
                }
            } else {
                logger.warn("DFS: Function {} does not belong to user {}", functionId, user.getUsername());
            }
        } else {
            logger.warn("DFS: Function {} not found", functionId);
        }
    }

    /**
     * Поиск в ширину (BFS) - итеративный поиск функций пользователя
     */
    public List<Function> searchBreadthFirst(User user, Integer startFunctionId, int maxDepth) {
        logger.info("Starting BFS search for user {} from function {} with max depth {}", 
                   user.getUsername(), startFunctionId, maxDepth);
        
        List<Function> result = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        Queue<SearchNode> queue = new LinkedList<>();
        
        queue.add(new SearchNode(startFunctionId, 0));
        visited.add(startFunctionId);
        
        while (!queue.isEmpty()) {
            SearchNode node = queue.poll();
            
            if (node.depth > maxDepth) {
                logger.debug("BFS: Reached max depth {}, stopping", maxDepth);
                continue;
            }
            
            Optional<Function> functionOpt = functionRepository.findById(node.functionId);
            if (functionOpt.isPresent()) {
                Function function = functionOpt.get();
                if (function.getUser().getUserId().equals(user.getUserId())) {
                    result.add(function);
                    logger.debug("BFS: Added function {} '{}' at depth {}", 
                               function.getFunctionId(), function.getName(), node.depth);
                    
                    // Добавление всех функций пользователя в очередь
                    List<Function> userFunctions = functionRepository.findByUser(user);
                    for (Function nextFunction : userFunctions) {
                        if (!visited.contains(nextFunction.getFunctionId())) {
                            visited.add(nextFunction.getFunctionId());
                            queue.add(new SearchNode(nextFunction.getFunctionId(), node.depth + 1));
                            logger.trace("BFS: Added function {} to queue at depth {}", 
                                        nextFunction.getFunctionId(), node.depth + 1);
                        }
                    }
                } else {
                    logger.warn("BFS: Function {} does not belong to user {}", 
                               node.functionId, user.getUsername());
                }
            } else {
                logger.warn("BFS: Function {} not found", node.functionId);
            }
        }
        
        logger.info("BFS search completed, found {} functions", result.size());
        return result;
    }

    /**
     * Поиск по иерархии - поиск всех функций пользователя с группировкой
     */
    public List<Function> searchByHierarchy(User user) {
        logger.info("Starting hierarchy search for user {}", user.getUsername());
        
        List<Function> allFunctions = functionRepository.findByUser(user);
        
        // Группировка по имени (имитация иерархии)
        Map<String, List<Function>> grouped = allFunctions.stream()
                .collect(Collectors.groupingBy(Function::getName));
        
        logger.info("Hierarchy search completed, found {} functions in {} groups", 
                   allFunctions.size(), grouped.size());
        
        for (Map.Entry<String, List<Function>> entry : grouped.entrySet()) {
            logger.debug("Hierarchy: Group '{}' contains {} functions", 
                        entry.getKey(), entry.getValue().size());
        }
        
        return allFunctions;
    }

    /**
     * Одиночный поиск функции по ID с проверкой доступа
     */
    public Optional<Function> findSingle(User user, Integer functionId) {
        logger.info("Single search for function {} by user {}", functionId, user.getUsername());
        
        Optional<Function> function = functionRepository.findByFunctionIdAndUser(functionId, user);
        
        if (function.isPresent()) {
            logger.info("Function {} '{}' found for user {}", 
                       functionId, function.get().getName(), user.getUsername());
            return function;
        } else {
            logger.warn("Function {} not found or not accessible by user {}", 
                       functionId, user.getUsername());
            return Optional.empty();
        }
    }

    /**
     * Множественный поиск с фильтрацией и сортировкой
     */
    public Page<Function> findMultiple(User user, String nameFilter, String expressionFilter, 
                                     Pageable pageable, Sort sort) {
        logger.info("Multiple search for user {} with filters: nameFilter={}, expressionFilter={}, page={}, sort={}", 
                   user.getUsername(), nameFilter, expressionFilter, 
                   pageable.getPageNumber(), sort != null ? sort.toString() : "none");
        
        List<Function> allFunctions = functionRepository.findByUser(user);
        
        // Фильтрация
        List<Function> filtered = allFunctions.stream()
                .filter(f -> nameFilter == null || nameFilter.isEmpty() || 
                           f.getName().toLowerCase().contains(nameFilter.toLowerCase()))
                .filter(f -> expressionFilter == null || expressionFilter.isEmpty() || 
                           (f.getExpression() != null && 
                            f.getExpression().toLowerCase().contains(expressionFilter.toLowerCase())))
                .collect(Collectors.toList());
        
        logger.debug("Filtered {} functions from {} total", filtered.size(), allFunctions.size());
        
        // Сортировка
        if (sort != null && sort.isSorted()) {
            Comparator<Function> comparator = null;
            for (Sort.Order order : sort) {
                Comparator<Function> orderComparator = null;
                switch (order.getProperty()) {
                    case "name":
                        orderComparator = Comparator.comparing(Function::getName);
                        break;
                    case "functionId":
                        orderComparator = Comparator.comparing(Function::getFunctionId);
                        break;
                    case "expression":
                        orderComparator = Comparator.comparing(
                            f -> f.getExpression() != null ? f.getExpression() : "");
                        break;
                }
                if (orderComparator != null) {
                    if (!order.isAscending()) {
                        orderComparator = orderComparator.reversed();
                    }
                    comparator = comparator == null ? orderComparator : comparator.thenComparing(orderComparator);
                }
            }
            if (comparator != null) {
                filtered.sort(comparator);
                logger.debug("Applied sorting: {}", sort);
            }
        }
        
        // Пагинация
        int totalElements = filtered.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), totalElements);
        List<Function> pageContent = start < totalElements ? filtered.subList(start, end) : new ArrayList<>();
        
        logger.info("Multiple search completed, found {} functions, returning page {} with {} items", 
                   totalElements, pageable.getPageNumber(), pageContent.size());
        
        return new PageImpl<>(pageContent, pageable, totalElements);
    }

    /**
     * Поиск точек функции с сортировкой
     */
    public List<ComputedPoint> findPoints(Function function, Sort sort) {
        logger.info("Searching points for function {} '{}' with sort {}", 
                   function.getFunctionId(), function.getName(), 
                   sort != null ? sort.toString() : "none");
        
        List<ComputedPoint> points;
        
        if (sort != null && sort.isSorted()) {
            boolean sorted = false;
            points = computedPointRepository.findByFunction(function);
            for (Sort.Order order : sort) {
                switch (order.getProperty()) {
                    case "xValue":
                        points = computedPointRepository.findByFunctionOrderByXValue(function);
                        if (!order.isAscending()) {
                            Collections.reverse(points);
                        }
                        sorted = true;
                        logger.debug("Sorted points by xValue, order: {}", 
                                   order.isAscending() ? "ASC" : "DESC");
                        break;
                    case "yValue":
                        points = computedPointRepository.findByFunction(function);
                        points.sort(Comparator.comparing(ComputedPoint::getYValue));
                        if (!order.isAscending()) {
                            Collections.reverse(points);
                        }
                        sorted = true;
                        logger.debug("Sorted points by yValue, order: {}", 
                                   order.isAscending() ? "ASC" : "DESC");
                        break;
                }
            }
            if (!sorted) {
                points = computedPointRepository.findByFunction(function);
            }
        } else {
            points = computedPointRepository.findByFunction(function);
        }
        
        logger.info("Found {} points for function {}", points.size(), function.getName());
        return points;
    }

    /**
     * Вспомогательный класс для BFS
     */
    private static class SearchNode {
        Integer functionId;
        int depth;
        
        SearchNode(Integer functionId, int depth) {
            this.functionId = functionId;
            this.depth = depth;
        }
    }
}





