package com.yuval.model;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;

public abstract class BaseRepository<T> {

	abstract public Class<T> getEntityClass();
	
	@PersistenceContext
	protected EntityManager entityManager;


	
	@Transactional
	public T save(T t) {
		entityManager.persist(t);
		return t;
	}
	

	@Transactional(readOnly = true)
	public T get(final long id) {
		return entityManager.find(getEntityClass(), id);
	}
}
