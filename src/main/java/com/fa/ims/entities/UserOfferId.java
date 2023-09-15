package com.fa.ims.entities;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserOfferId implements Serializable {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "offer_id")
    private Long offerId;
}
