package com.example.order_app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {
    @Override
    @Query("SELECT e FROM #{#entityName} e WHERE e.deleted = false")
    List<T> findAll();

    @Override
    @Query("SELECT e FROM #{#entityName} e WHERE e.deleted = false")
    Page<T> findAll(Pageable pageable);

    @Override
    @Query("SELECT e FROM #{#entityName} e WHERE e.id = :id AND e.deleted = false")
    Optional<T> findById(@Param("id") ID id);

    @Override
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.deleted = false")
    long count();

    @Query("UPDATE #{#entityName} e SET e.deleted = true, e.deletedAt = :deletedAt WHERE e.id = :id")
    @Modifying
    void softDelete(@Param("id") ID id, @Param("deletedAt") LocalDateTime deletedAt);
}