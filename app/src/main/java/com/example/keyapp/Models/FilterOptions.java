package com.example.keyapp.Models;

import java.util.HashSet;
import java.util.Set;

public class FilterOptions {
    public float minRating = 0;
    public int minPrice = 0;
    public int maxPrice = Integer.MAX_VALUE;
    public Set<String> serviceTypes = new HashSet<>(); // bisa banyak

    public FilterOptions() {}
}
