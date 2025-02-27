package com.example.order_app.service.base;

import com.example.order_app.model.BaseEntity;
import com.example.order_app.repository.BaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseService<T extends BaseEntity, ID> {
    protected final BaseRepository<T, ID> repository;

    @Transactional
    public T save(T entity) {
        return repository.save(entity);
    }

    @Transactional
    public void softDelete(ID id) {
        repository.softDelete(id, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<T> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<T> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return repository.findByDateRange(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public long count() {
        return repository.count();
    }
}
