package web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import web.model.Site;

@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {

}
