package com.wavemaker.employee.factory;

import com.wavemaker.employee.constant.StorageType;
import com.wavemaker.employee.exception.FileCreationException;
import com.wavemaker.employee.repository.AddressRepository;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AddressRepositoryFactory {
    private static AddressRepository addressRepository;
    private static final ConcurrentMap<StorageType, AddressRepository> repositoryMap = new ConcurrentHashMap<>();

    public static AddressRepository getAddressRepositoryInstance(StorageType storageType) throws FileCreationException, SQLException {
        if (repositoryMap.containsKey(storageType)) {
            return repositoryMap.get(storageType);
        }
        if (storageType == StorageType.IN_MEMORY) {
            addressRepository = AddressRepositoryInstanceHandler.getInMemoryAddressRepositoryInstance();
            repositoryMap.put(storageType, addressRepository);
        } else if (storageType == StorageType.IN_FILE) {
            addressRepository = AddressRepositoryInstanceHandler.getInFileAddressRepositoryInstance();
            repositoryMap.put(storageType, addressRepository);
        } else if (storageType == StorageType.IN_DB) {
            addressRepository = AddressRepositoryInstanceHandler.getInDbAddressRepositoryInstance();
        }
        return addressRepository;
    }
}
