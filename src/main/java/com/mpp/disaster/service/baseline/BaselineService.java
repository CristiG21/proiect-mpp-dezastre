package com.mpp.disaster.service.baseline;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpp.disaster.DisasterApp;
import com.mpp.disaster.config.ApplicationProperties;
import com.mpp.disaster.repository.*;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class BaselineService {

    private static final Logger LOG = LoggerFactory.getLogger(BaselineService.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private CenterRepository centerRepository;

    @Autowired
    private CenterTypeWrapperRepository centerTypeWrapperRepository;

    @Autowired
    private CommunityMessageRepository communityMessageRepository;

    @Autowired
    private DisasterRepository disasterRepository;

    @Autowired
    private OfficialMessageRepository officialMessageRepository;

    @Autowired
    private PhotoURLRepository photoURLRepository;

    @PostConstruct
    public void init() {
        boolean isBaselineEnabled = applicationProperties.isBaselineLoadEnabled();
        LOG.info("Baseline Data Loading at startup is {}", isBaselineEnabled ? "enabled" : "disabled");
        if (!isBaselineEnabled) return;

        reloadData(centerRepository, new TypeReference<>() {});
        reloadData(centerTypeWrapperRepository, new TypeReference<>() {});
        reloadData(communityMessageRepository, new TypeReference<>() {});
        reloadData(disasterRepository, new TypeReference<>() {});
        reloadData(officialMessageRepository, new TypeReference<>() {});
        reloadData(photoURLRepository, new TypeReference<>() {});
    }

    private <T> void reloadData(JpaRepository<T, ?> repository, TypeReference<List<T>> typeRef) {
        repository.deleteAll();
        repository.saveAll(parseJson(typeRef));
    }

    private <T> List<T> parseJson(TypeReference<List<T>> typeRef) {
        String fileName;
        try {
            String typeName =
                ((Class<?>) ((java.lang.reflect.ParameterizedType) typeRef.getType()).getActualTypeArguments()[0]).getSimpleName()
                    .toLowerCase(Locale.ROOT);

            fileName = "baseline/" + typeName + ".json";

            try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName)) {
                if (is == null) {
                    throw new RuntimeException("File not found: " + fileName);
                }
                return objectMapper.readValue(is, typeRef);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load JSON for type reference", e);
        }
    }
}
