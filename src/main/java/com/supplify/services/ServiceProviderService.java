package com.supplify.services;

import java.io.IOException;
import java.util.List;

import com.supplify.dto.ServiceProviderDto;
import com.supplify.entity.ServiceProvider;

public interface ServiceProviderService {
	void saveServiceProvider(ServiceProviderDto  serviceProviderDto) throws IOException;
    ServiceProvider findServiceProviderByEmail(String email);
    List<ServiceProviderDto> findAllServiceProvider();
}
