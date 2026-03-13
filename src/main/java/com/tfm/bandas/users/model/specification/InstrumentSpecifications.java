package com.tfm.bandas.users.model.specification;

import com.tfm.bandas.users.model.entity.InstrumentEntity;
import org.springframework.data.jpa.domain.Specification;

public class InstrumentSpecifications {

    public static Specification<InstrumentEntity> instrumentNameContains(String instrumentName) {
        return (root, query, cb) ->
                instrumentName == null ? null : cb.like(cb.lower(root.get("instrumentName")), "%" + instrumentName.toLowerCase() + "%");
    }

    public static Specification<InstrumentEntity> voiceContains(String voice) {
        return (root, query, cb) ->
                voice == null ? null : cb.like(cb.lower(root.get("voice")), "%" + voice.toLowerCase() + "%");
    }
}
