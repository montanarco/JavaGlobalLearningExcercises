package functionalandstream;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamOperations {

    // Stream Generation (5 points)
    public Stream<Prop> generate(int n){
        return Stream.generate(() -> new Prop(UUID.randomUUID(), "prop", (int)(Math.random() * 100)))
                .limit(n);
    }

    // Flatening (5 points)
    public Stream<UUID> toIds(List<List<Prop>> input){
        return input.stream()
                .flatMap(List::stream)
                .map(Prop::id);
    }

    // Counts (5 points)
    public int count (Stream<Integer> input){
        return input.filter(x -> x % 2 == 0)
                .mapToInt(x -> x)
                .sum();
    }

    // Shortening Operations (5 points)
    public Stream<Character> shorteningOperations (Stream<Character> input){
        String collected = input
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();

        String firstWord = collected.split(" ")[0];
        return firstWord.chars().mapToObj(c -> (char) c);
    }

    // Indirect Mapping (5 points)
    public Stream<String> indirectMapping (Stream<Prop> input){
        return input.map(prop ->  {
            UUID id = prop.id();
            String name = prop.name();
            int value = prop.value();
            return String.format("%s-%s-%d", id, name, value);
        });
    }

    // Sorting (5 points)
    public List<Prop> sorting (Stream<Prop> input){
        return input.filter(prop -> prop.name() != null && !prop.name().isBlank())
                .sorted((p1, p2) -> {
                    int valueComparison = Integer.compare(p2.value(), p1.value());
                    return valueComparison != 0 ? valueComparison : p1.name().compareTo(p2.name());
                })
                .collect(Collectors.toList());
    }

    // Filter by Property (5 points)
    public List<Prop> filterByProperty (Stream<Prop> input){
        return input.filter(prop -> prop.name() != null
                && !prop.name().isBlank())
                .collect(Collectors.toList());
    }

    // Stateful filter (5 points)
    public Stream<Prop> statefulFilter (Stream<Prop> input){
        Set<UUID> seenIds = new java.util.HashSet<>();
        return input.filter(prop -> seenIds.add(prop.id()));
    }

    // Aggregation (5 points)
    public String aggregation (Stream<Prop> input){
        return input.max((java.util.Comparator.comparingInt(Prop::value)))
                .map(Prop::name)
                .orElse("");
    }

    // Combining Collectors (5 points)
    public List<Prop> combiningCollectors (Stream<Prop> input){
        Map<String, List<Prop>> repeated = new HashMap<>();
        input.forEach(prop -> {;
            repeated.computeIfAbsent(prop.name(), k -> new java.util.ArrayList<>()).add(prop);
        });
        return repeated.values().stream().filter(values -> values.size() > 1)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    // Stateful Collectors (5 points)
    public Map<String, Integer> statefulCollectors (Stream<Prop> input){
        return input.collect(Collectors.groupingBy(
                Prop::name,
                Collectors.summingInt(Prop::value)
        ));
    }

    // Collector Chaining (2 points)
    public Map<String, Integer> collectorChaining (Stream<Integer> input){
        return input.collect(Collectors.partitioningBy(
                val -> val % 2 == 0,
                Collectors.summingInt(Integer::intValue)
        )).entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey() ? "even" : "odd",
                        Map.Entry::getValue
                ));
    }

    // Custom Aggregation (3 points)
    public List<String> customAggregation (Stream<Prop> input){
        List<Prop> props = input.collect(Collectors.toList());

        String maxPropName = props.stream()
                .max(Comparator.comparingInt(Prop::value))
                .map(Prop::name)
                .orElse("");

        String minPropName = props.stream()
                .min(Comparator.comparingInt(Prop::value))
                .map(Prop::name)
                .orElse("");

        return List.of(maxPropName, minPropName);
    }

    // Functional Transformation (Optional)
    public <T> Function<T, T> fold(Stream<Function<T, T>> functions){
        return functions.reduce(Function.identity(), Function::compose);
    }

    // Functional Composition with String transformations
    Function<String, String> uppering = String::toUpperCase;
    Function<String, String> encoding = str -> str.replace('i', '1').replace('I', '1').replace('a', '4').replace('A', '4').replace('o', '0').replace('O', '0');
    public String applyFunctionalTransformations(String input){
        Stream<Function<String, String>> functions = Stream.of( uppering, encoding );
        return fold(functions).apply(input);
    }

}
