package com.ryan_mtg.servobot.data.factories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SerializationSupport {
    public static <RowType> Map<Integer, List<RowType>> getIdMapping(final Iterable<RowType> rows,
            final Iterable<Integer> ids, final Function<RowType, Integer> extractId) {
        Map<Integer, List<RowType>> rowMap = new HashMap<>();
        ids.forEach(commandId -> rowMap.put(commandId, new ArrayList<>()));
        for(RowType row : rows) {
            rowMap.get(extractId.apply(row)).add(row);
        }
        return rowMap;
    }

    public static <RowType> Iterable<Integer> getIds(final Iterable<RowType> rows,
            final Function<RowType, Integer> extractId) {
        return StreamSupport.stream(rows.spliterator(), false).map(row -> extractId.apply(row))
                .collect(Collectors.toList());
    }
}
