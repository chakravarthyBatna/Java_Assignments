package com.wavemaker.employee.factory;

import com.wavemaker.employee.repository.AddressRepository;
import com.wavemaker.employee.repository.impl.AddressRepositoryInFileImpl;
import com.wavemaker.employee.repository.impl.AddressRepositoryInMemoryImpl;

public class AddressRepositoryInstanceHandler {
    private static volatile AddressRepository inMemoryRepository = null;
    private static volatile AddressRepository inFileRepository = null;

    public static AddressRepository getInMemoryAddressRepositoryInstance() {
        if (inMemoryRepository == null) {
            synchronized (AddressRepositoryInstanceHandler.class) {
                if (inMemoryRepository == null) {
                    inMemoryRepository = new AddressRepositoryInMemoryImpl();
                }
            }
        }
        return inMemoryRepository;
    }

    public static AddressRepository getInFileAddressRepositoryInstance() {
        if (inFileRepository == null) {
            synchronized (AddressRepositoryInstanceHandler.class) {
                if (inFileRepository == null) {
                    inFileRepository = new AddressRepositoryInFileImpl();
                }
            }
        }
        return inFileRepository;
    }

}
