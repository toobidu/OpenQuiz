package com.example.quizizz.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class BaseEntity implements SoftDeleted{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(updatable = false)
    private Long createdBy;

    private Long updatedBy;

    private Boolean isDelete = false;

    @Override
    public void setDeleted(Boolean deleted) {
        this.isDelete = deleted;
    }

    @Override
    public Boolean isDeleted() {
        return this.isDelete;
    }
}
