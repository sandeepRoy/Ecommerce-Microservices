package com.msa.customer.repositories;

import com.msa.customer.model.BuyLater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuyLaterRepository extends JpaRepository<BuyLater, Integer> {

}
