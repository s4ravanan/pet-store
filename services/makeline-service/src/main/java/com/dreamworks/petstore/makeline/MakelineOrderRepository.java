package com.dreamworks.petstore.makeline;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MakelineOrderRepository extends JpaRepository<MakelineOrder, String> {
    List<MakelineOrder> findByStatus(String status);
}
