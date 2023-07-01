package com.dominik;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface LifeBitRepository extends CrudRepository<LifeBit,Long> {
    public Optional<List<LifeBit>> findByStatus(int status);
}
