package com.fa.ims.repository;

import com.fa.ims.entities.Offer;

import java.util.Optional;

public interface OfferRepository extends BaseRepository<Offer, Long> {
    Optional<Offer> findByIdAndDeletedFalse(Long id);
}
