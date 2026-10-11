package com.app.server.repository;

import com.app.server.model.CompaniesCa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompaniesCaRepository extends JpaRepository<CompaniesCa,Long> {




}
