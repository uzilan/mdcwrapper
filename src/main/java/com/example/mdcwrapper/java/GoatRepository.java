package com.example.mdcwrapper.java;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Goat entity.
 */
@Repository
public interface GoatRepository extends CrudRepository<Goat, Long> {
}

