package com.app.server.repository.companies;

import com.app.server.model.companies.CompanyCa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyCaRepository extends JpaRepository<CompanyCa,Long> {

    Optional<CompanyCa> findCompanyCaBySlug(String slug);
}
