package com.yuval.model;

import org.springframework.stereotype.Repository;

@Repository
public class DroppointRepository extends BaseRepository<DbDroppoint> {

	@Override
	public Class<DbDroppoint> getEntityClass() {
		return DbDroppoint.class;
	}
	
}
