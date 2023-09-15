package com.fa.ims.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class UserOffer extends AbstractAuditingEntity {
    @EmbeddedId
    private UserOfferId id;

    @MapsId(value = "userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId(value = "offerId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id")
    private Offer offer;

    private Boolean isActive;

    public UserOffer(User user, Offer offer, Boolean isActive) {
        this.id = new UserOfferId(user.getId(), offer.getId());
        this.user = user;
        this.offer = offer;
        this.isActive = isActive;
    }
}
