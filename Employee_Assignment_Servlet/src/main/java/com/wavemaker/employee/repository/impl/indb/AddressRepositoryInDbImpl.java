package com.wavemaker.employee.repository.impl.indb;

import com.wavemaker.employee.exception.ServerUnavilableException;
import com.wavemaker.employee.exception.address.AddressNotFoundException;
import com.wavemaker.employee.pojo.Address;
import com.wavemaker.employee.repository.AddressRepository;
import com.wavemaker.employee.util.DBConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddressRepositoryInDbImpl implements AddressRepository {

    private static final Logger logger = LoggerFactory.getLogger(AddressRepositoryInDbImpl.class);

    private static final String SELECT_ADDRESS_BY_EMP_ID = "SELECT * FROM ADDRESS WHERE EMP_ID = ?";
    private static final String INSERT_ADDRESS = "INSERT INTO ADDRESS (STATE, COUNTRY, PINCODE, EMP_ID) VALUES (?, ?, ?, ?)";
    private static final String DELETE_ADDRESS_BY_EMP_ID = "DELETE FROM ADDRESS WHERE EMP_ID = ?";
    private static final String UPDATE_ADDRESS = "UPDATE ADDRESS SET STATE = ?, COUNTRY = ?, PINCODE = ? WHERE EMP_ID = ?";

    private static Connection connection = null;

    public AddressRepositoryInDbImpl() throws SQLException {
        connection = DBConnector.getConnectionInstance();
    }

    @Override
    public Address getAddressByEmpId(int empId) throws ServerUnavilableException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SELECT_ADDRESS_BY_EMP_ID);
            preparedStatement.setInt(1, empId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToAddress(resultSet);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new ServerUnavilableException("Error reading address from database", 500);
        } finally {
            closeResources(resultSet, preparedStatement);
        }
    }

    @Override
    public Address addAddress(Address address) throws ServerUnavilableException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(INSERT_ADDRESS, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, address.getState());
            preparedStatement.setString(2, address.getCountry());
            preparedStatement.setInt(3, address.getPincode());
            preparedStatement.setInt(4, address.getEmpId());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    address.setAddressId(generatedKeys.getInt(1));
                }
                return address;
            } else {
                throw new ServerUnavilableException("Error adding address to database", 500);
            }
        } catch (SQLException | ServerUnavilableException e) {
            throw new ServerUnavilableException("Error adding address to database", 500);
        } finally {
            closeResources(null, preparedStatement);
        }
    }

    @Override
    public Address deleteAddressByEmpId(int empId) throws AddressNotFoundException, ServerUnavilableException {
        PreparedStatement preparedStatement = null;
        try {
            Address address = getAddressByEmpId(empId);
            if (address == null) {
                throw new AddressNotFoundException("Address for employee ID " + empId + " not found", 404);
            }
            preparedStatement = connection.prepareStatement(DELETE_ADDRESS_BY_EMP_ID);
            preparedStatement.setInt(1, empId);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                return address;
            } else {
                throw new ServerUnavilableException("Error deleting address from database", 500);
            }
        } catch (SQLException | ServerUnavilableException e) {
            throw new ServerUnavilableException("Error deleting address from database", 500);
        } finally {
            closeResources(null, preparedStatement);
        }
    }

    @Override
    public Address updateAddress(Address address) throws AddressNotFoundException, ServerUnavilableException {
        PreparedStatement preparedStatement = null;
        try {
            if (getAddressByEmpId(address.getEmpId()) == null) {
                throw new AddressNotFoundException("Address for employee ID " + address.getEmpId() + " not found", 404);
            }
            preparedStatement = connection.prepareStatement(UPDATE_ADDRESS);
            preparedStatement.setString(1, address.getState());
            preparedStatement.setString(2, address.getCountry());
            preparedStatement.setInt(3, address.getPincode());
            preparedStatement.setInt(4, address.getEmpId());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                return address;
            } else {
                throw new ServerUnavilableException("Error updating address in database", 500);
            }
        } catch (SQLException e) {
            throw new ServerUnavilableException("Error updating address in database", 500);
        } finally {
            closeResources(null, preparedStatement);
        }
    }

    private Address mapResultSetToAddress(ResultSet rs) throws SQLException {
        Address address = new Address();
        address.setAddressId(rs.getInt("ADDRESS_ID"));
        address.setState(rs.getString("STATE"));
        address.setCountry(rs.getString("COUNTRY"));
        address.setPincode(rs.getInt("PINCODE"));
        address.setEmpId(rs.getInt("EMP_ID"));
        return address;
    }

    private void closeResources(ResultSet rs, PreparedStatement stmt) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            logger.warn("Exception : ", e);
        }
    }
}
