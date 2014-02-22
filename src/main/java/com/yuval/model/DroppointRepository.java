package com.yuval.model;

import org.springframework.stereotype.Repository;

@Repository
public class DroppointRepository extends BaseRepository<DbDroppoints> {

	@Override
	public Class<DbDroppoints> getEntityClass() {
		return DbDroppoints.class;
	}
	
}
