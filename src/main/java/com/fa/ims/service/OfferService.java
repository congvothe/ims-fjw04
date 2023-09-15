package com.fa.ims.service;

import com.fa.ims.dto.OfferDto;
import com.fa.ims.entities.Offer;
import com.fa.ims.enums.Department;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface OfferService extends BaseService<Offer> {
    OfferDto getDataFromInterviewEntity(Long id);

    Offer create(OfferDto offerDto);

    Page<OfferDto> findAll(String search, Department department,String status,int page,int size);

    Optional<Offer> findByIdAndDeletedFalse(Long id);

    OfferDto getInformationInterview(Long id);

    void update(OfferDto offerDto);

    void updateResultOffer(OfferDto offerDto);
}
