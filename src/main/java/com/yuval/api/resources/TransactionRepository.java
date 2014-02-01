package com.yuval.api.resources;

import org.springframework.stereotype.Repository;

@Repository
public class TransactionRepository extends BaseRepository<DbTransaction> {

	@Override
	public Class<DbTransaction> getEntityClass() {
		return DbTransaction.class;
	}

	
}
