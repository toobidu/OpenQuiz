package com.example.quizizz.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permission extends BaseEntity{
    @Column(name = "permission_name", nullable = false, unique = true)
    private String permissionName;

    @Column(name = "description", nullable = false)
    private String description;
}
