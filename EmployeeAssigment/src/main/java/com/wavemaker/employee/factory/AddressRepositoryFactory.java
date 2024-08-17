package com.wavemaker.employee.factory;

import com.wavemaker.employee.constant.Storage_Type;
import com.wavemaker.employee.repository.AddressRepository;

import java.util.HashMap;
import java.util.Map;

public class AddressRepositoryFactory {
    private static AddressRepository addressRepository;
    private static final Map<Storage_Type, AddressRepository> repositoryMap = new HashMap<>();

    public static AddressRepository getAddressRepositoryInstance(Storage_Type storageType) {
        if (repositoryMap.containsKey(storageType)) {
            return repositoryMap.get(storageType);
        }
        if (storageType == Storage_Type.IN_MEMORY) {
            addressRepository = AddressRepositoryInstanceHandler.getInMemoryAddressRepositoryInstance();
            repositoryMap.put(storageType, addressRepository);
        } else if (storageType == Storage_Type.IN_FILE) {
            addressRepository = AddressRepositoryInstanceHandler.getInFileAddressRepositoryInstance();
            repositoryMap.put(storageType, addressRepository);
        }
        return repositoryMap.get(storageType);
    }
}
