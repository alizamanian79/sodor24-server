package com.app.server.repository.companies;

import com.app.server.model.companies.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company,Long> {
    Optional<Company> findCompaniesBySlug(String slug);
    boolean existsByCompanyNameIgnoreCase(String companyName);
}
