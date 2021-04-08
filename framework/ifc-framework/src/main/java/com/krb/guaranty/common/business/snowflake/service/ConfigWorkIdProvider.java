package com.krb.guaranty.common.business.snowflake.service;

import org.springframework.beans.factory.annotation.Value;

public class ConfigWorkIdProvider implements WorkIdProvider {

	@Value("${work.id:0}")
	private long workId;
	
	@Value("#{ new java.util.Random().nextInt(1023) + 1 }")
	private int randomWorkId;
	
	@Override
	public long getWorkId() {
		return workId > 0 ? workId : randomWorkId;
	}

}
