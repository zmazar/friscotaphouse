package com.friscotaphouse.beer;

import java.util.Comparator;

/**
 * Created by Zach on 8/8/2015.
 */
public class BeerSort implements Comparator<Beer> {
    @Override
    public int compare(Beer lhs, Beer rhs) {
        String leftName = lhs.getName().toLowerCase();
        String rightName = rhs.getName().toLowerCase();

        return leftName.compareTo(rightName);
    }
}

