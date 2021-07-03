package com.user.arb.service;

import com.user.arb.jpa.entity.AbstractEntity;
import com.user.arb.service.dto.AbstractDTO;

import java.io.Serializable;
import java.util.List;

public interface AbstractService<E extends AbstractEntity, D extends AbstractDTO, K extends Serializable> {

    D find(K k);

    List<D> find();

    D create(D d);

    D update(D d);

    D active(K k);

    D archive(K k);

}
