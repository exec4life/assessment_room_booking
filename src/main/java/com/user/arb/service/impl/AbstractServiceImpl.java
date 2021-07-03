package com.user.arb.service.impl;

import com.user.arb.exception.ArbException;
import com.user.arb.jpa.entity.AbstractEntity;
import com.user.arb.service.AbstractService;
import com.user.arb.service.dto.AbstractDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityExistsException;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Transactional(
        isolation = Isolation.READ_COMMITTED,
        propagation = Propagation.SUPPORTS,
        readOnly = false,
        timeout = 30)
public abstract class AbstractServiceImpl<E extends AbstractEntity, D extends AbstractDTO, K extends Serializable>
        implements AbstractService<E, D, K> {

    private static Logger LOG = LoggerFactory.getLogger(AbstractServiceImpl.class);

    protected Class<E> entityClass;
    protected Class<D> dtoClass;

    protected JpaRepository currentRepository;
    protected Converter<E, D> toDtoConverter;
    protected Converter<D, E> toEntityConverter;

    @Autowired
    protected MessageSource messageSource;

    public AbstractServiceImpl(JpaRepository currentRepository) {

        entityClass = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        dtoClass = (Class<D>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];

        this.currentRepository = currentRepository;

        generateConverters();
    }

    protected void generateConverters() {
        toDtoConverter = (E e) -> {
            try {
                D d = dtoClass.newInstance();
                BeanUtils.copyProperties(e, d);
                return d;
            } catch (FatalBeanException | InstantiationException | IllegalAccessException ex) {
                throw new ArbException(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("system.error.can.not.convert.object",
                                new Object[]{entityClass.getSimpleName(), dtoClass.getSimpleName()},
                                "Can not convert from Entity to DTO",
                                LocaleContextHolder.getLocale()),
                        ex);
            }
        };

        toEntityConverter = (D d) -> {
            try {
                E e = entityClass.newInstance();
                BeanUtils.copyProperties(d, e);
                return e;
            } catch (FatalBeanException | InstantiationException | IllegalAccessException ex) {
                throw new ArbException(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("system.error.can.not.convert.object",
                                new Object[]{dtoClass.getSimpleName(), entityClass.getSimpleName()},
                                "Can not convert from DTO to Entity",
                                LocaleContextHolder.getLocale()),
                        ex);
            }
        };
    }

    @Override
    public D find(K k) {
        Optional<E> optional = currentRepository.findById(k);

        if (optional.isPresent()) {
            return toDtoConverter.convert(optional.get());
        } else {
            throw new ArbException(
                    HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("system.entity.not.exist",
                            new Object[]{entityClass.getSimpleName(), k},
                            "This entity is not existed",
                            LocaleContextHolder.getLocale()));
        }
    }

    @Override
    public List<D> find() {
        List<E> list = currentRepository.findAll();

        if (!CollectionUtils.isEmpty(list)) {
            return list.stream().map(e -> toDtoConverter.convert(e)).collect(toList());
        }
        return new ArrayList<D>();
    }

    @Override
    @Transactional(
            rollbackFor = IllegalArgumentException.class,
            noRollbackFor = EntityExistsException.class,
            rollbackForClassName = "IllegalArgumentException",
            noRollbackForClassName = "EntityExistsException")
    public D create(D d) {
        return toDtoConverter.convert((E) currentRepository.save(toEntityConverter.convert(d)));
    }

    @Override
    @Transactional(
            rollbackFor = IllegalArgumentException.class,
            noRollbackFor = EntityExistsException.class,
            rollbackForClassName = "IllegalArgumentException",
            noRollbackForClassName = "EntityNotFoundException")
    public D update(D d) {
        return toDtoConverter.convert((E) currentRepository.save(toEntityConverter.convert(d)));
    }

    @Override
    @Transactional(
            rollbackFor = IllegalArgumentException.class,
            noRollbackFor = EntityExistsException.class,
            rollbackForClassName = "IllegalArgumentException",
            noRollbackForClassName = "EntityNotFoundException")
    public D active(K k) {EntityExistsException x;
        E e = get(k);
        e.setActive(Boolean.TRUE);
        return toDtoConverter.convert((E) currentRepository.save(e));
    }

    @Override
    @Transactional(
            rollbackFor = IllegalArgumentException.class,
            noRollbackFor = EntityExistsException.class,
            rollbackForClassName = "IllegalArgumentException",
            noRollbackForClassName = "EntityNotFoundException")
    public D archive(K k) {
        E e = get(k);
        e.setActive(Boolean.FALSE);
        return toDtoConverter.convert((E) currentRepository.save(e));
    }

    protected E get(K k) {
        Optional<E> optional = currentRepository.findById(k);

        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new ArbException(
                    HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("system.entity.not.exist",
                            new Object[]{entityClass.getSimpleName(), k},
                            "This entity is not existed",
                            LocaleContextHolder.getLocale()));
        }
    }

}