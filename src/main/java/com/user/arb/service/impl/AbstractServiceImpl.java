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

    @Autowired
    protected MessageSource messageSource;

    public AbstractServiceImpl(JpaRepository currentRepository) {

        entityClass = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        dtoClass = (Class<D>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];

        this.currentRepository = currentRepository;
    }

    protected D toDtoConvert(E e) {
        try {
            D d = dtoClass.newInstance();
            BeanUtils.copyProperties(e, d);
            return d;
        } catch (FatalBeanException | InstantiationException | IllegalAccessException ex) {
            throw throwConvertObject(dtoClass, entityClass, ex);
        }
    }

    protected E toEntityConvert(D d) {
        try {
            E e = entityClass.newInstance();
            BeanUtils.copyProperties(d, e);
            return e;
        } catch (FatalBeanException | InstantiationException | IllegalAccessException ex) {
            throw throwConvertObject(entityClass, dtoClass, ex);
        }
    }

    protected void toEntityCopy(D d, E e) {
        try {
            BeanUtils.copyProperties(d, e);
        } catch (NullPointerException | FatalBeanException ex) {
            throw throwConvertObject(dtoClass, entityClass, ex);
        }
    }

    @Override
    public D find(K k) {
        Optional<E> optional = currentRepository.findById(k);

        if (optional.isPresent()) {
            return toDtoConvert(optional.get());
        } else {
            throw throwNonExistEntity(entityClass, k);
        }
    }

    @Override
    public List<D> find() {
        List<E> list = currentRepository.findAll();

        if (!CollectionUtils.isEmpty(list)) {
            return list.stream().map(e -> toDtoConvert(e)).collect(toList());
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
        E e = toEntityConvert(d);
        e.setId(null);
        return toDtoConvert((E) currentRepository.save(e));
    }

    @Override
    @Transactional(
            rollbackFor = IllegalArgumentException.class,
            noRollbackFor = EntityExistsException.class,
            rollbackForClassName = "IllegalArgumentException",
            noRollbackForClassName = "EntityNotFoundException")
    public D update(D d) {
        Optional<E> optionalE = currentRepository.findById(d.getId());

        if (optionalE.isPresent()) {
            toEntityCopy(d, optionalE.get());
            return toDtoConvert((E) currentRepository.save(optionalE.get()));
        } else {
            throw throwNonExistEntity(entityClass, d.getId());
        }
    }

    @Override
    @Transactional(
            rollbackFor = IllegalArgumentException.class,
            noRollbackFor = EntityExistsException.class,
            rollbackForClassName = "IllegalArgumentException",
            noRollbackForClassName = "EntityNotFoundException")
    public void delete(K k) {
        Optional<E> optional = currentRepository.findById(k);

        if (optional.isPresent()) {
            currentRepository.delete(optional.get());
        } else {
            throw throwNonExistEntity(entityClass, k);
        }
    }

    @Override
    @Transactional(
            rollbackFor = IllegalArgumentException.class,
            noRollbackFor = EntityExistsException.class,
            rollbackForClassName = "IllegalArgumentException",
            noRollbackForClassName = "EntityNotFoundException")
    public D active(K k) {
        EntityExistsException x;
        E e = get(k);
        e.setActive(Boolean.TRUE);
        return toDtoConvert((E) currentRepository.save(e));
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
        return toDtoConvert((E) currentRepository.save(e));
    }

    protected E get(K k) {
        Optional<E> optional = currentRepository.findById(k);

        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw throwNonExistEntity(entityClass, k);
        }
    }

    protected ArbException throwNonExistEntity(Class<?> clazz, Object obj) {
        return new ArbException(
                HttpStatus.BAD_REQUEST,
                messageSource.getMessage("entity.validation.not.exist",
                        new Object[]{entityClass.getSimpleName(), obj},
                        "This {0}[{1}] is not existed",
                        LocaleContextHolder.getLocale()));
    }

    protected ArbException throwConvertObject(Class<?> srcClazz, Class<?> disClazz, Throwable ex) {
        LOG.error(String.format("Can not convert from %s to %s",
                srcClazz.getSimpleName(), disClazz.getSimpleName()), ex);
        return new ArbException(HttpStatus.INTERNAL_SERVER_ERROR,
                messageSource.getMessage("system.convert.error",
                        null,
                        "Has an internal error",
                        LocaleContextHolder.getLocale()),
                ex);
    }

}