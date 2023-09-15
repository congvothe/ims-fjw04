package com.fa.ims.service;

import com.fa.ims.entities.BaseEntity;
import org.springframework.data.jpa.domain.Specification;

public interface BaseService<E extends BaseEntity> {
    default Specification<E> undeletedSpec() {
        return (root, query, builder) -> builder.equal(root.get("deleted"), false);
    }
}
