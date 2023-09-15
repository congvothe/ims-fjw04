package com.fa.ims.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity extends AbstractAuditingEntity {
    private Boolean deleted;
}
