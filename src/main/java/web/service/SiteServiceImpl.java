package web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web.model.Site;
import web.repository.SiteRepository;

import java.util.List;

@Service
public class SiteServiceImpl implements SiteService {

    private final SiteRepository siteRepository;
    private static final Logger logger = LoggerFactory.getLogger(SiteServiceImpl.class);


    @Autowired
    public SiteServiceImpl(SiteRepository siteRepository) {
        logger.info("SiteServiceImpl created");
        this.siteRepository = siteRepository;
    }

    public List<Site> getAllSites() {
        return siteRepository.findAll();
    }

    public List<Site> getAllAvailableSites() {
        return siteRepository.findAll().stream().filter(site -> !site.isBlocked()).toList();
    }

    public List<Site> getAllBlockedSites() {
        return siteRepository.findAll().stream().filter(Site::isBlocked).toList();
    }

    public Site getSiteById(Long id) {
        return siteRepository.findById(id).orElse(null);
    }

    public Site createSite(Site site) {
        return siteRepository.save(site);
    }

    public Site updateSite(Long id, Site site) {
        Site existingSite = siteRepository.findById(id).orElse(null);
        if (existingSite != null) {
            existingSite.setName(site.getName());
            existingSite.setUrl(site.getUrl());
            return siteRepository.save(existingSite);
        }
        return null;
    }

    public void deleteSite(Long id) {
        siteRepository.deleteById(id);
    }
}
