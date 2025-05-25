package com.supplify.service.Implement;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.supplify.dto.ServiceProviderDto;
import com.supplify.entity.Role;
import com.supplify.entity.ServiceProvider;
import com.supplify.repository.RoleRepository;
import com.supplify.repository.ServiceProviderRepository;
import com.supplify.services.ServiceProviderService;

@Service
public class ServiceProviderServiceImpl implements ServiceProviderService {

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ServiceProviderServiceImpl(ServiceProviderRepository serviceProviderRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void saveServiceProvider(ServiceProviderDto serviceProviderDto) {
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setFirstName(serviceProviderDto.getFirstName());
        serviceProvider.setLastName(serviceProviderDto.getLastName());
        serviceProvider.setEmail(serviceProviderDto.getEmail());
        serviceProvider.setPhone(serviceProviderDto.getPhone());
        serviceProvider.setPassword(passwordEncoder.encode(serviceProviderDto.getPassword()));
        //Role role = roleRepository.findByName("ROLE_ADMIN");
//                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

//        Role role = roleRepository.findByName("ADMIN");
//        if (role == null) {
//            role = new Role();
//            role.setName("ADMIN");
//            role = roleRepository.save(role);
//        }
        Role role = roleRepository.findByName("ADMIN");
        if (role == null) {
            role = new Role();
            role.setName("ADMIN");
            role.setId(1l);
            role = roleRepository.save(role);  // Save it to the DB
        }

// don't replace the set — add to it
        serviceProvider.setRoles(Set.of(role));
//        serviceProvider.getRoles().add(role);

        serviceProviderRepository.save(serviceProvider);


        // serviceProvider.setRole("Admin");
          // role.setName("Admin");
//            role = roleRepository.save(role);
//        }
//
//        Set<Role> roleSet = new HashSet<>();
//        roleSet.add(role);
//        serviceProvider.setRoles(roleSet);
//        serviceProviderRepository.save(serviceProvider);
    }

    public ServiceProvider findServiceProviderByEmail(String email) {
        return serviceProviderRepository.findByEmail(email);
    }

    public List<ServiceProviderDto> findAllServiceProvider() {
        List<ServiceProvider> serviceProviders = serviceProviderRepository.findAll();
        return serviceProviders.stream()
                .map((serviceProvider) -> mapToServiceProviderDto(serviceProvider))
                .collect(Collectors.toList());
    }

    private ServiceProviderDto mapToServiceProviderDto(ServiceProvider serviceProvider) {
        ServiceProviderDto serviceProviderDto = new ServiceProviderDto();
        String[] str = serviceProvider.getFirstName().split(" ");
        serviceProviderDto.setFirstName(str[0]);
        serviceProviderDto.setLastName(str[1]);
        serviceProviderDto.setEmail(serviceProvider.getEmail());
        return serviceProviderDto;
    }

	
	
}