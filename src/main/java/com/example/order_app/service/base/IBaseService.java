package com.example.order_app.service.base;

import com.example.order_app.model.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IBaseService<T extends BaseEntity, ID> {
    T save(T entity);
    T update(T entity);
    void softDelete(ID id);
    void restore(ID id);
    Optional<T> findById(ID id);
    List<T> findAll();
    Page<T> findAll(Pageable pageable);
    boolean isDeleted(ID id);
    List<T> findDeletedBetween(LocalDateTime start, LocalDateTime end);
    List<T> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<T> findModifiedSince(LocalDateTime since);
    long count();
}