package com.msa.customer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.msa.customer.model.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

}
