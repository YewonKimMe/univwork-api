package net.univwork.api.api_v1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.Preview;
import net.univwork.api.api_v1.repository.WorkplaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PreviewService {

    private final WorkplaceRepository workplaceRepository;

    public List<Preview> getAFewPreview(Long num) {

        List<Preview> preview = workplaceRepository.getPreview(num);



        preview
                .forEach(
                        pv -> pv.setCommentPreview(
                                pv.getCommentPreview().substring(0, Math.min(pv.getCommentPreview().length(), 16))
                        )
                );
        return preview;
    }
}
