package com.app.server.repository.companies;

import com.app.server.model.companies.CompanyCa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyCaRepository extends JpaRepository<CompanyCa,Long> {




}
