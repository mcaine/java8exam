package com.mikeycaine.examcode;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class CollectorsTest {

    String [] names = {"Mike", "Fran", "Sophie", "Fluff", "Donald J Trump"};

    @Test
    public void testMapping() {
        Collector<String, ?, List<Integer>> collector = Collectors.mapping((String s) -> s.length(), Collectors.toList());
        List<Integer> result = Arrays.stream(names).collect(collector);
        System.out.println(result); // [4, 4, 6, 5, 14]
    }

    @Test
    public void testMapping2() {
        Collector<Integer, ?, Map<Integer, List<Integer>>> grouper = Collectors.groupingBy((Integer i) -> i);
        Collector<String, ?, Map<Integer, List<Integer>>> collector = Collectors.mapping(String::length, grouper);
        Map<Integer, List<Integer>> result = Arrays.stream(names).collect(collector);
        System.out.println(result); // {4=[4, 4], 5=[5], 6=[6], 14=[14]}
    }
}
