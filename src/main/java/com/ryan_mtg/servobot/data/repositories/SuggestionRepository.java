package com.ryan_mtg.servobot.data.repositories;

import com.ryan_mtg.servobot.data.models.SuggestionRow;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuggestionRepository extends PagingAndSortingRepository<SuggestionRow, Integer> {
    SuggestionRow findByAlias(String alias);
    Iterable<SuggestionRow> findAllByOrderByCountDescAliasAsc();
}
