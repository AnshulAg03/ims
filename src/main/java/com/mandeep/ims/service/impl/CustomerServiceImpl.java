package com.mandeep.ims.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mandeep.ims.dto.CustomerDto;
import com.mandeep.ims.dto.CustomerNameResponseDto;
import com.mandeep.ims.dto.CustomerResponseDto;
import com.mandeep.ims.entity.Address;
import com.mandeep.ims.entity.Customer;
import com.mandeep.ims.exception.CustomException;
import com.mandeep.ims.repository.CustomerRepository;
import com.mandeep.ims.service.CustomerService;
import com.mandeep.ims.util.Util;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	@Override
	public List<CustomerResponseDto> getAllCustomers() {
		List<CustomerResponseDto> customers = new ArrayList<>();
		Iterable<Customer> allcustomers = customerRepository.findByDeletedFalse();
		Iterator<Customer> customerItr = allcustomers.iterator();
		while (customerItr.hasNext()) {
			customers.add(new CustomerResponseDto(customerItr.next()));
		}
		return customers;
	}

	@Override
	public List<CustomerNameResponseDto> getAllCustomerNames() throws CustomException {
		Iterable<Customer> custIterable = customerRepository.findByDeletedFalse();
		return StreamSupport.stream(custIterable.spliterator(), false).map(CustomerNameResponseDto::new)
				.collect(Collectors.toList());
	}

	@Override
	public CustomerDto getCustomerById(int id) throws CustomException {
		Optional<Customer> customer = customerRepository.findByIdAndDeletedFalse(id);
		if (customer.isPresent()) {
			return new CustomerDto(customer.get());
		} else {
			throw new CustomException("Customer not found");
		}

	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public Customer saveCustomer(CustomerDto customerDto) throws CustomException {
		try {
			Customer customer = convertDtoToEntity(customerDto);
			return customerRepository.save(customer);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomException("Some error occured. Please try again.");
		}
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public Customer updateCustomer(int id, CustomerDto customerDto) throws CustomException {
		Optional<Customer> customer = customerRepository.findByIdAndDeletedFalse(id);
		if (customer.isPresent()) {
			Customer cust = customer.get();
			updateCustomerDetails(cust, customerDto);
			return customerRepository.save(cust);

		} else {
			throw new CustomException("Customer not found");
		}
	}

	@Override
	public void deleteCustomerById(int id) throws CustomException {
		try {
			Optional<Customer> customer = customerRepository.findByIdAndDeletedFalse(id);
			if (customer.isPresent()) {
				Customer cust = customer.get();
				cust.setDeleted(true);
				customerRepository.save(cust);
			} else {
				throw new CustomException("Customer not found");
			}
		} catch (Exception e) {
			throw new CustomException("Customer not found");
		}

	}

	private Customer convertDtoToEntity(CustomerDto customerDto) {
		Address address = new Address(customerDto);
		Customer customer = new Customer(customerDto, address);
		return customer;
	}

	private void updateCustomerDetails(Customer cust, CustomerDto customerDto) {
		cust.setName(customerDto.getName());
		cust.setPhoneNum(customerDto.getPhoneNum());
		cust.setCompany(customerDto.getCompany());
		cust.setModifiedDate(Util.getCurrentTimeStamp());
		cust.setDeleted(false);
		Address address = cust.getAddress();
		address.setAddressLine1(customerDto.getAddressLine1());
		address.setAddressLine2(customerDto.getAddressLine2());
		address.setCountry(customerDto.getCountry());
		address.setState(customerDto.getState());
		address.setZipCode(customerDto.getZipCode());
	}
}
