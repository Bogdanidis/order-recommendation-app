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

    @Query("SELECT e FROM #{#entityName} e WHERE e.id = :id AND e.deleted = true")
    Optional<T> findDeletedById(@Param("id") ID id);

    @Override
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.deleted = false")
    long count();

    @Modifying
    @Query("UPDATE #{#entityName} e SET e.deleted = true, e.deletedAt = :deletedAt WHERE e.id = :id")
    void softDelete(@Param("id") ID id, @Param("deletedAt") LocalDateTime deletedAt);

    @Query("SELECT e FROM #{#entityName} e WHERE e.deleted = true AND e.deletedAt BETWEEN :start AND :end")
    List<T> findDeletedBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Modifying
    @Query("UPDATE #{#entityName} e SET e.deleted = false, e.deletedAt = null WHERE e.id = :id")
    void restore(@Param("id") ID id);

    @Query("SELECT e FROM #{#entityName} e WHERE " +
            "(:startDate IS NULL OR e.createdAt >= :startDate) AND " +
            "(:endDate IS NULL OR e.createdAt <= :endDate) AND " +
            "e.deleted = false")
    List<T> findByDateRange(@Param("startDate") LocalDateTime startDate,
                            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT e FROM #{#entityName} e WHERE " +
            "e.updatedAt > e.createdAt AND " +
            "e.updatedAt >= :since AND " +
            "e.deleted = false")
    List<T> findModifiedSince(@Param("since") LocalDateTime since);
}