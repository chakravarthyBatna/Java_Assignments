package com.wavemaker.employee.factory;

import com.wavemaker.employee.exception.FileCreationException;
import com.wavemaker.employee.repository.AddressRepository;
import com.wavemaker.employee.repository.impl.file.AddressRepositoryInFileImpl;
import com.wavemaker.employee.repository.impl.inmemory.AddressRepositoryInMemoryImpl;

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

    public static AddressRepository getInFileAddressRepositoryInstance() throws FileCreationException {
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
