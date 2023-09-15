package com.fa.ims.repository;

import com.fa.ims.entities.Offer;
import com.fa.ims.entities.UserOffer;

import java.util.List;

public interface UserOfferRepository extends BaseRepository<UserOffer, Long> {
    List<UserOffer> findAllByOffer_IdAndIsActiveTrue(Long id);
}
