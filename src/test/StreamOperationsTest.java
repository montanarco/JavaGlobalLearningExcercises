package test;

import functionalandstream.StreamOperations;
import functionalandstream.Prop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class StreamOperationsTest {

    private StreamOperations streamOperations;

    @BeforeEach
    void setUp() {
        streamOperations = new StreamOperations();
    }

    @Test
    void testShorteningOperationsWithSingleWord() {
        // Test with a single word (no spaces)
        Stream<Character> input = "hello".chars().mapToObj(c -> (char) c);
        List<Character> result = streamOperations.shorteningOperations(input).toList();

        assertEquals(5, result.size());
        assertEquals(List.of('h', 'e', 'l', 'l', 'o'), result);
    }

    @Test
    void testShorteningOperationsWithMultipleWords() {
        // Test with multiple words - should only return characters from first word
        Stream<Character> input = "hello world test".chars().mapToObj(c -> (char) c);
        List<Character> result = streamOperations.shorteningOperations(input).toList();

        assertEquals(5, result.size());
        assertEquals(List.of('h', 'e', 'l', 'l', 'o'), result);
    }

    @Test
    void testShorteningOperationsWithRandomlyGeneratedCharacters() {
        // Test with randomly generated characters that form words
        String randomWord = generateRandomWord(8);
        Stream<Character> input = randomWord.chars().mapToObj(c -> (char) c);
        List<Character> result = streamOperations.shorteningOperations(input).toList();

        assertFalse(result.isEmpty());
        assertEquals(randomWord.length(), result.size());
    }

    @Test
    void testShorteningOperationsExtractsFirstWordFromRandomInput() {
        // Test with randomly generated phrase
        String randomPhrase = generateRandomWord(5) + " " + generateRandomWord(5);
        String expectedFirstWord = randomPhrase.split(" ")[0];

        Stream<Character> input = randomPhrase.chars().mapToObj(c -> (char) c);
        List<Character> result = streamOperations.shorteningOperations(input).toList();

        String resultWord = result.stream()
                .map(String::valueOf)
                .reduce("", String::concat);

        assertEquals(expectedFirstWord, resultWord);
        assertEquals(expectedFirstWord.length(), result.size());
    }

    @Test
    void testIndirectMappingFormatsAllProperties() {
        // Test with randomly generated Prop objects
        Stream<Prop> input = streamOperations.generate(5);
        List<String> results = streamOperations.indirectMapping(input).toList();

        // Verify we have the expected number of results
        assertEquals(5, results.size());

        // Verify each result contains all properties separated by hyphens
        results.forEach(result -> {
            String[] parts = result.split("-");
            // Should have at least 3 parts: UUID, name, and value
            assertTrue(parts.length >= 3, "Result should have at least 3 parts separated by hyphens");
            // Verify the last part is a valid integer (the value)
            assertDoesNotThrow(() -> Integer.parseInt(parts[parts.length - 1]));
        });
    }

    @Test
    void testIndirectMappingIncludesNonNullProperties() {
        // Test that all properties (id, name, value) are included in the output
        Stream<Prop> input = streamOperations.generate(3);
        List<String> results = streamOperations.indirectMapping(input).toList();

        assertFalse(results.isEmpty());
        results.forEach(result -> {
            // Result format: UUID-name-value
            assertTrue(result.contains("-"), "Result should contain hyphens separating properties");
            // Verify it's not empty or null
            assertNotNull(result);
            assertFalse(result.isBlank());
        });
    }

    @Test
    void testIndirectMappingWithRandomlyGeneratedProps() {
        // Generate a larger set of random Prop objects
        int numberOfProps = 10;
        Stream<Prop> input = streamOperations.generate(numberOfProps);
        List<String> results = streamOperations.indirectMapping(input).toList();

        assertEquals(numberOfProps, results.size());

        // Verify all results follow the expected format
        results.forEach(result -> {
            // Each should match pattern: UUID-String-Integer
            assertTrue(result.matches(".*-prop-\\d+"),
                    "Result should match format: UUID-prop-value but got: " + result);
        });
    }

    @Test
    void testSortingFiltersBlanksAndNulls() {
        // Test that sorting filters out blank and null names
        List<Prop> propsWithBlanks = List.of(
                new Prop(UUID.randomUUID(), "valid", 50),
                new Prop(UUID.randomUUID(), "", 75),
                new Prop(UUID.randomUUID(), "another", 25),
                new Prop(UUID.randomUUID(), "   ", 100)
        );

        List<Prop> results = streamOperations.sorting(propsWithBlanks.stream());

        // Should only contain "valid" and "another", not blank names
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(p -> p.name() != null && !p.name().isBlank()));
    }

    @Test
    void testSortingOrdersByValueDescending() {
        // Test that sorting orders by value in descending order
        List<Prop> props = List.of(
                new Prop(UUID.randomUUID(), "first", 30),
                new Prop(UUID.randomUUID(), "second", 50),
                new Prop(UUID.randomUUID(), "third", 20),
                new Prop(UUID.randomUUID(), "fourth", 80)
        );

        List<Prop> results = streamOperations.sorting(props.stream());

        assertEquals(4, results.size());
        assertEquals(80, results.get(0).value());
        assertEquals(50, results.get(1).value());
        assertEquals(30, results.get(2).value());
        assertEquals(20, results.get(3).value());
    }

    @Test
    void testSortingWithRandomlyGeneratedProps() {
        // Test with randomly generated Prop objects
        Stream<Prop> input = streamOperations.generate(20);
        List<Prop> results = streamOperations.sorting(input);

        // Verify all names are non-null and not blank
        results.forEach(prop -> {
            assertNotNull(prop.name());
            assertFalse(prop.name().isBlank());
        });

        // Verify sorted in descending order by value
        for (int i = 0; i < results.size() - 1; i++) {
            assertTrue(results.get(i).value() >= results.get(i + 1).value(),
                    "Props should be sorted in descending order by value");
        }
    }

    @Test
    void testSortingWithEqualValues() {
        // Test sorting with props having equal values
        List<Prop> props = List.of(
                new Prop(UUID.randomUUID(), "a", 50),
                new Prop(UUID.randomUUID(), "b", 50),
                new Prop(UUID.randomUUID(), "c", 30)
        );

        List<Prop> results = streamOperations.sorting(props.stream());

        assertEquals(3, results.size());
        // First two should have value 50, last should have value 30
        assertEquals(50, results.get(0).value());
        assertEquals(50, results.get(1).value());
        assertEquals(30, results.get(2).value());
    }

    @Test
    void testSortingEmptyStream() {
        // Test with empty stream
        List<Prop> results = streamOperations.sorting(Stream.empty());

        assertTrue(results.isEmpty());
    }

    @Test
    void testSortingByValueThenByName() {
        // Test that sorting orders by value descending, then by name ascending for equal values
        List<Prop> props = List.of(
                new Prop(UUID.randomUUID(), "zebra", 50),
                new Prop(UUID.randomUUID(), "apple", 50),
                new Prop(UUID.randomUUID(), "mango", 50),
                new Prop(UUID.randomUUID(), "banana", 75),
                new Prop(UUID.randomUUID(), "cherry", 25)
        );

        List<Prop> results = streamOperations.sorting(props.stream());

        assertEquals(5, results.size());
        // First: value 75
        assertEquals(75, results.get(0).value());
        assertEquals("banana", results.get(0).name());

        // Next: value 50, sorted by name alphabetically
        assertEquals(50, results.get(1).value());
        assertEquals("apple", results.get(1).name());

        assertEquals(50, results.get(2).value());
        assertEquals("mango", results.get(2).name());

        assertEquals(50, results.get(3).value());
        assertEquals("zebra", results.get(3).name());

        // Last: value 25
        assertEquals(25, results.get(4).value());
        assertEquals("cherry", results.get(4).name());
    }

    @Test
    void testStatefulFilterRemovesDuplicateIds() {
        // Test that statefulFilter removes props with duplicate IDs
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();

        List<Prop> props = List.of(
                new Prop(id1, "first", 10),
                new Prop(id2, "second", 20),
                new Prop(id1, "duplicate_id1", 30),  // Duplicate ID - should be removed
                new Prop(id3, "third", 40),
                new Prop(id2, "duplicate_id2", 50)   // Duplicate ID - should be removed
        );

        List<Prop> results = streamOperations.statefulFilter(props.stream()).toList();

        assertEquals(3, results.size());
        // Should only have unique IDs
        assertEquals(List.of(id1, id2, id3),
                results.stream().map(Prop::id).toList());
    }

    @Test
    void testStatefulFilterKeepsFirstOccurrence() {
        // Test that statefulFilter keeps the first occurrence and removes subsequent duplicates
        UUID duplicateId = UUID.randomUUID();

        List<Prop> props = List.of(
                new Prop(duplicateId, "first_name", 100),
                new Prop(UUID.randomUUID(), "other", 50),
                new Prop(duplicateId, "second_name", 200),  // Should be filtered out
                new Prop(duplicateId, "third_name", 300)    // Should be filtered out
        );

        List<Prop> results = streamOperations.statefulFilter(props.stream()).toList();

        assertEquals(2, results.size());
        // First occurrence should have "first_name"
        assertEquals("first_name", results.get(0).name());
        assertEquals(100, results.get(0).value());
    }

    @Test
    void testStatefulFilterWithNoDuplicates() {
        // Test with stream containing no duplicates
        Stream<Prop> input = streamOperations.generate(5);
        List<Prop> results = streamOperations.statefulFilter(input).toList();

        // All IDs should be unique
        List<UUID> ids = results.stream().map(Prop::id).toList();
        assertEquals(ids.size(), new java.util.HashSet<>(ids).size(),
                "All IDs should be unique");
    }

    @Test
    void testStatefulFilterWithAllDuplicates() {
        // Test with same ID repeated multiple times
        UUID sameId = UUID.randomUUID();

        List<Prop> props = List.of(
                new Prop(sameId, "first", 10),
                new Prop(sameId, "second", 20),
                new Prop(sameId, "third", 30)
        );

        List<Prop> results = streamOperations.statefulFilter(props.stream()).toList();

        assertEquals(1, results.size());
        assertEquals("first", results.get(0).name());
        assertEquals(10, results.get(0).value());
    }

    @Test
    void testStatefulFilterWithRandomlyGeneratedPropsWithDuplicates() {
        // Test with randomly generated props where we intentionally add duplicates
        UUID[] uniqueIds = new UUID[3];
        for (int i = 0; i < 3; i++) {
            uniqueIds[i] = UUID.randomUUID();
        }

        List<Prop> props = new java.util.ArrayList<>();
        for (int i = 0; i < 10; i++) {
            UUID idToUse = uniqueIds[i % 3];  // Cycle through 3 unique IDs
            props.add(new Prop(idToUse, "prop_" + i, i * 10));
        }

        List<Prop> results = streamOperations.statefulFilter(props.stream()).toList();

        assertEquals(3, results.size());
        // Verify we have exactly 3 unique IDs
        Set<UUID> uniqueResultIds = results.stream()
                .map(Prop::id)
                .collect(java.util.stream.Collectors.toSet());
        assertEquals(3, uniqueResultIds.size());
    }

    @Test
    void testStatefulCollectorsGroupsByNameAndSums() {
        // Test that statefulCollectors groups by name and sums values correctly
        List<Prop> props = List.of(
                new Prop(UUID.randomUUID(), "apple", 10),
                new Prop(UUID.randomUUID(), "banana", 20),
                new Prop(UUID.randomUUID(), "apple", 15),
                new Prop(UUID.randomUUID(), "banana", 25),
                new Prop(UUID.randomUUID(), "apple", 5)
        );

        Map<String, Integer> results = streamOperations.statefulCollectors(props.stream());

        assertEquals(2, results.size());
        assertEquals(30, results.get("apple"));  // 10 + 15 + 5
        assertEquals(45, results.get("banana")); // 20 + 25
    }

    @Test
    void testStatefulCollectorsWithSingleItems() {
        // Test with items that have no duplicates
        List<Prop> props = List.of(
                new Prop(UUID.randomUUID(), "apple", 50),
                new Prop(UUID.randomUUID(), "banana", 75),
                new Prop(UUID.randomUUID(), "cherry", 100)
        );

        Map<String, Integer> results = streamOperations.statefulCollectors(props.stream());

        assertEquals(3, results.size());
        assertEquals(50, results.get("apple"));
        assertEquals(75, results.get("banana"));
        assertEquals(100, results.get("cherry"));
    }

    @Test
    void testStatefulCollectorsWithAllSameName() {
        // Test with all props having the same name
        List<Prop> props = List.of(
                new Prop(UUID.randomUUID(), "item", 10),
                new Prop(UUID.randomUUID(), "item", 20),
                new Prop(UUID.randomUUID(), "item", 30),
                new Prop(UUID.randomUUID(), "item", 40)
        );

        Map<String, Integer> results = streamOperations.statefulCollectors(props.stream());

        assertEquals(1, results.size());
        assertEquals(100, results.get("item")); // 10 + 20 + 30 + 40
    }

    @Test
    void testStatefulCollectorsWithRandomlyGeneratedProps() {
        // Test with randomly generated props
        Stream<Prop> input = streamOperations.generate(15);
        Map<String, Integer> results = streamOperations.statefulCollectors(input);

        // All generated props have name "prop"
        assertEquals(1, results.size());
        assertTrue(results.containsKey("prop"));
        assertTrue(results.get("prop") > 0);
    }

    @Test
    void testStatefulCollectorsPreservesAllData() {
        // Test that all values are summed correctly with various amounts
        List<Prop> props = List.of(
                new Prop(UUID.randomUUID(), "x", 100),
                new Prop(UUID.randomUUID(), "y", 50),
                new Prop(UUID.randomUUID(), "x", 25),
                new Prop(UUID.randomUUID(), "z", 200),
                new Prop(UUID.randomUUID(), "y", 75),
                new Prop(UUID.randomUUID(), "x", 50)
        );

        Map<String, Integer> results = streamOperations.statefulCollectors(props.stream());

        assertEquals(3, results.size());
        assertEquals(175, results.get("x")); // 100 + 25 + 50
        assertEquals(125, results.get("y")); // 50 + 75
        assertEquals(200, results.get("z")); // 200

        // Verify total sum
        int totalSum = results.values().stream().mapToInt(Integer::intValue).sum();
        assertEquals(500, totalSum);
    }

    @Test
    void testStatefulCollectorsEmptyStream() {
        // Test with empty stream
        Map<String, Integer> results = streamOperations.statefulCollectors(Stream.empty());

        assertTrue(results.isEmpty());
        assertEquals(0, results.size());
    }

    @Test
    void testCollectorChainingPartitionsEvenAndOdd() {
        // Test that collectorChaining partitions integers into even and odd
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Stream<Integer> input = numbers.stream();

        Map<String, Integer> results = streamOperations.collectorChaining(input);

        assertEquals(2, results.size());
        assertTrue(results.containsKey("even"));
        assertTrue(results.containsKey("odd"));

        // Even: 2 + 4 + 6 + 8 + 10 = 30
        assertEquals(30, results.get("even"));
        // Odd: 1 + 3 + 5 + 7 + 9 = 25
        assertEquals(25, results.get("odd"));
    }

    @Test
    void testCollectorChainingWithOnlyEvenNumbers() {
        // Test with only even numbers
        List<Integer> numbers = List.of(2, 4, 6, 8);
        Stream<Integer> input = numbers.stream();

        Map<String, Integer> results = streamOperations.collectorChaining(input);

        assertEquals(2, results.size());
        assertEquals(20, results.get("even")); // 2 + 4 + 6 + 8
        assertEquals(0, results.get("odd"));   // No odd numbers
    }

    @Test
    void testCollectorChainingWithOnlyOddNumbers() {
        // Test with only odd numbers
        List<Integer> numbers = List.of(1, 3, 5, 7, 9);
        Stream<Integer> input = numbers.stream();

        Map<String, Integer> results = streamOperations.collectorChaining(input);

        assertEquals(2, results.size());
        assertEquals(0, results.get("even"));   // No even numbers
        assertEquals(25, results.get("odd"));   // 1 + 3 + 5 + 7 + 9
    }

    @Test
    void testCollectorChainingWithNegativeNumbers() {
        // Test with negative numbers
        List<Integer> numbers = List.of(-4, -3, -2, -1, 0, 1, 2, 3, 4);
        Stream<Integer> input = numbers.stream();

        Map<String, Integer> results = streamOperations.collectorChaining(input);

        assertEquals(2, results.size());
        // Even: -4 + -2 + 0 + 2 + 4 = 0
        assertEquals(0, results.get("even"));
        // Odd: -3 + -1 + 1 + 3 = 0
        assertEquals(0, results.get("odd"));
    }

    @Test
    void testCollectorChainingWithZero() {
        // Test with zero (which is even)
        List<Integer> numbers = List.of(0);
        Stream<Integer> input = numbers.stream();

        Map<String, Integer> results = streamOperations.collectorChaining(input);

        assertEquals(2, results.size());
        assertEquals(0, results.get("even"));
        assertEquals(0, results.get("odd"));
    }

    @Test
    void testCollectorChainingWithLargeNumbers() {
        // Test with large numbers
        List<Integer> numbers = List.of(100, 101, 200, 201, 300);
        Stream<Integer> input = numbers.stream();

        Map<String, Integer> results = streamOperations.collectorChaining(input);

        assertEquals(2, results.size());
        // Even: 100 + 200 + 300 = 600
        assertEquals(600, results.get("even"));
        // Odd: 101 + 201 = 302
        assertEquals(302, results.get("odd"));
    }

    @Test
    void testCollectorChainingEmptyStream() {
        // Test with empty stream
        Stream<Integer> input = Stream.empty();

        Map<String, Integer> results = streamOperations.collectorChaining(input);

        assertEquals(2, results.size());
        assertEquals(0, results.get("even"));
        assertEquals(0, results.get("odd"));
    }

    @Test
    void testCollectorChainingWithRandomNumbers() {
        // Test with randomly generated numbers
        List<Integer> randomNumbers = new java.util.ArrayList<>();
        int evenSum = 0, oddSum = 0;

        for (int i = 0; i < 20; i++) {
            int randomNum = (int) (Math.random() * 100);
            randomNumbers.add(randomNum);
            if (randomNum % 2 == 0) {
                evenSum += randomNum;
            } else {
                oddSum += randomNum;
            }
        }

        Map<String, Integer> results = streamOperations.collectorChaining(randomNumbers.stream());

        assertEquals(2, results.size());
        assertEquals(evenSum, results.get("even"));
        assertEquals(oddSum, results.get("odd"));
    }

    @Test
    void testCustomAggregationFindsMaxAndMin() {
        // Test that customAggregation finds names of max and min value props
        List<Prop> props = List.of(
                new Prop(UUID.randomUUID(), "alice", 50),
                new Prop(UUID.randomUUID(), "bob", 100),
                new Prop(UUID.randomUUID(), "charlie", 25),
                new Prop(UUID.randomUUID(), "diana", 75)
        );

        List<String> results = streamOperations.customAggregation(props.stream());

        assertEquals(2, results.size());
        assertEquals("bob", results.get(0));        // Max value is 100
        assertEquals("charlie", results.get(1));    // Min value is 25
    }

    @Test
    void testCustomAggregationWithSingleElement() {
        // Test with single element - max and min should be the same
        List<Prop> props = List.of(
                new Prop(UUID.randomUUID(), "only", 50)
        );

        List<String> results = streamOperations.customAggregation(props.stream());

        assertEquals(2, results.size());
        assertEquals("only", results.get(0));   // Max
        assertEquals("only", results.get(1));   // Min
    }

    @Test
    void testCustomAggregationWithTwoElements() {
        // Test with two elements
        List<Prop> props = List.of(
                new Prop(UUID.randomUUID(), "small", 10),
                new Prop(UUID.randomUUID(), "large", 90)
        );

        List<String> results = streamOperations.customAggregation(props.stream());

        assertEquals(2, results.size());
        assertEquals("large", results.get(0));  // Max
        assertEquals("small", results.get(1));  // Min
    }

    @Test
    void testCustomAggregationWithEqualValues() {
        // Test when multiple props have the same max or min value
        List<Prop> props = List.of(
                new Prop(UUID.randomUUID(), "max1", 100),
                new Prop(UUID.randomUUID(), "max2", 100),
                new Prop(UUID.randomUUID(), "middle", 50),
                new Prop(UUID.randomUUID(), "min1", 10),
                new Prop(UUID.randomUUID(), "min2", 10)
        );

        List<String> results = streamOperations.customAggregation(props.stream());

        assertEquals(2, results.size());
        // Should return one of the max names (first encountered)
        assertTrue(results.get(0).equals("max1") || results.get(0).equals("max2"));
        // Should return one of the min names (first encountered)
        assertTrue(results.get(1).equals("min1") || results.get(1).equals("min2"));
    }

    @Test
    void testCustomAggregationWithNegativeValues() {
        // Test with negative values
        List<Prop> props = List.of(
                new Prop(UUID.randomUUID(), "negative_max", -10),
                new Prop(UUID.randomUUID(), "negative_min", -50),
                new Prop(UUID.randomUUID(), "positive", 30)
        );

        List<String> results = streamOperations.customAggregation(props.stream());

        assertEquals(2, results.size());
        assertEquals("positive", results.get(0));        // Max value is 30
        assertEquals("negative_min", results.get(1));    // Min value is -50
    }

    @Test
    void testCustomAggregationWithZero() {
        // Test with zero as part of the values
        List<Prop> props = List.of(
                new Prop(UUID.randomUUID(), "zero", 0),
                new Prop(UUID.randomUUID(), "positive", 50),
                new Prop(UUID.randomUUID(), "negative", -20)
        );

        List<String> results = streamOperations.customAggregation(props.stream());

        assertEquals(2, results.size());
        assertEquals("positive", results.get(0));   // Max value is 50
        assertEquals("negative", results.get(1));   // Min value is -20
    }

    @Test
    void testCustomAggregationWithLargeValues() {
        // Test with large numbers
        List<Prop> props = List.of(
                new Prop(UUID.randomUUID(), "billion", Integer.MAX_VALUE),
                new Prop(UUID.randomUUID(), "medium", 1000000),
                new Prop(UUID.randomUUID(), "lowest", Integer.MIN_VALUE)
        );

        List<String> results = streamOperations.customAggregation(props.stream());

        assertEquals(2, results.size());
        assertEquals("billion", results.get(0));   // Max value
        assertEquals("lowest", results.get(1));    // Min value
    }

    @Test
    void testCustomAggregationWithRandomlyGeneratedProps() {
        // Test with randomly generated props
        Stream<Prop> input = streamOperations.generate(15);
        List<String> results = streamOperations.customAggregation(input);

        assertEquals(2, results.size());
        assertNotNull(results.get(0));  // Max name
        assertNotNull(results.get(1));  // Min name
        assertFalse(results.get(0).isBlank());
        assertFalse(results.get(1).isBlank());
    }

    @Test
    void testCustomAggregationPreservesNames() {
        // Test that the actual names are returned, not modified
        String maxName = "maximum_value";
        String minName = "minimum_value";

        List<Prop> props = List.of(
                new Prop(UUID.randomUUID(), maxName, 100),
                new Prop(UUID.randomUUID(), "middle", 50),
                new Prop(UUID.randomUUID(), minName, 10)
        );

        List<String> results = streamOperations.customAggregation(props.stream());

        assertEquals(2, results.size());
        assertEquals(maxName, results.get(0));
        assertEquals(minName, results.get(1));
    }

    @Test
    void testApplyStringTransformationsBasic() {
        // Test basic transformation with simple input
        String input = "hello-world";

        String result = streamOperations.applyFunctionalTransformations(input);

        // Part 1 (uppering): "HELLO-WORLD"
        // Part 2 (encoding): Replace i with 1, a with 4, o with 0: "HELL0-W0RLD"
        assertEquals("HELL0-W0RLD", result);
    }

    @Test
    void testApplyStringTransformationsWithVowels() {
        // Test with vowels that get encoded
        String input = "apple-orange";

        String result = streamOperations.applyFunctionalTransformations(input);

        // Part 1 (uppering): "APPLE-ORANGE"
        // Part 2 (encoding): i with 1, a with 4, o with 0: "4PPLE-0R4NGE"
        assertEquals("4PPLE-0R4NGE", result);
    }

    @Test
    void testApplyStringTransformationsWithAllVowels() {
        // Test with all replaceable vowels
        String input = "aeiou";

        String result = streamOperations.applyFunctionalTransformations(input);

        // Part 1 (uppering): "AEIOU"
        // Part 2 (encoding): a with 4, i with 1, o with 0: "4E1OU"
        assertEquals("4E10U", result);
    }

    @Test
    void testApplyStringTransformationsWithNoReplacements() {
        // Test with no replaceable characters
        String input = "xyz";

        String result = streamOperations.applyFunctionalTransformations(input);

        // Part 1 (uppering): "XYZ"
        // Part 2 (encoding): No i, a, o: "XYZ"
        assertEquals("XYZ", result);
    }

    @Test
    void testApplyStringTransformationsWithMixedCase() {
        // Test with mixed case letters
        String input = "TeSt-Data";

        String result = streamOperations.applyFunctionalTransformations(input);

        // Part 1 (uppering): "TEST-DATA"
        // Part 2 (encoding): a with 4: "TEST-D4T4"
        assertEquals("TEST-D4T4", result);
    }

    @Test
    void testApplyStringTransformationsWithNumbers() {
        // Test with numbers (numbers stay the same)
        String input = "test123-data456";

        String result = streamOperations.applyFunctionalTransformations(input);

        // Part 1 (uppering): "TEST123-DATA456"
        // Part 2 (encoding): a with 4: "TEST123-D4T4456"
        assertEquals("TEST123-D4T4456", result);
    }

    @Test
    void testApplyStringTransformationsWithSpecialChars() {
        // Test with special characters
        String input = "test@mail-data!info";

        String result = streamOperations.applyFunctionalTransformations(input);

        // Part 1 (uppering): "TEST@MAIL-DATA!INFO"
        // Part 2 (encoding): i with 1, a with 4: "TEST@M41L-D4T4!1NF0"
        assertEquals("TEST@M41L-D4T4!1NF0", result);
    }

    @Test
    void testApplyStringTransformationsEmptyString() {
        // Test with empty string
        String input = "";

        String result = streamOperations.applyFunctionalTransformations(input);

        assertEquals("", result);
    }

    @Test
    void testApplyStringTransformationsOnlyConsonants() {
        // Test with only consonants
        String input = "bcdfg";

        String result = streamOperations.applyFunctionalTransformations(input);

        // Part 1 (uppering): "BCDFG"
        // Part 2 (encoding): No i, a, o: "BCDFG"
        assertEquals("BCDFG", result);
    }

    @Test
    void testApplyStringTransformationsComplexString() {
        // Test with complex real-world string
        String input = "information-available-online";

        String result = streamOperations.applyFunctionalTransformations(input);

        // Part 1 (uppering): "INFORMATION-AVAILABLE-ONLINE"
        // Part 2 (encoding): i with 1, a with 4, o with 0: "1NF0RM4T10N-4V41L4BLE-0NL1NE"
        assertEquals("1NF0RM4T10N-4V41L4BLE-0NL1NE", result);
    }

    @Test
    void testApplyStringTransformationsUppercaseInput() {
        // Test with already uppercase input
        String input = "AUDIO";

        String result = streamOperations.applyFunctionalTransformations(input);

        // Part 1 (uppering): "AUDIO" (already uppercase)
        // Part 2 (encoding): a with 4, u stays, i with 1, o with 0: "4UD10"
        assertEquals("4UD10", result);
    }

    /**
     * Helper method to generate random words for testing
     */
    private String generateRandomWord(int length) {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder word = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = (int) (Math.random() * alphabet.length());
            word.append(alphabet.charAt(randomIndex));
        }
        return word.toString();
    }
}