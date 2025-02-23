package com.example.order_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import java.time.LocalDateTime;
import java.util.List;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {
    @Query("UPDATE #{#entityName} e SET e.deleted = true, e.deletedAt = ?2 WHERE e.id = ?1")
    @Modifying
    void softDelete(ID id, LocalDateTime deletedAt);

    @Query("SELECT e FROM #{#entityName} e WHERE e.deleted = false")
    List<T> findAllActive();
}