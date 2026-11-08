package com.protrack.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Abstract Base Entity demonstrating Abstraction and Encapsulation.
 * Provides common identity and audit timestamp fields.
 */
public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    protected int id;
    protected Timestamp createdAt;
    protected Timestamp updatedAt;

    public BaseEntity() {}

    public BaseEntity(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
