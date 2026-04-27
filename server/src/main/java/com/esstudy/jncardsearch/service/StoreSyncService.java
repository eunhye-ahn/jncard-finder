package com.esstudy.jncardsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import com.esstudy.jncardsearch.dto.StoreCountDto;
import com.esstudy.jncardsearch.repository.StoreRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreSyncService {

    private final StoreRepository storeRepository;
    private final ElasticsearchClient esClient; // Spring ES Client
    ObjectMapper mapper = new ObjectMapper();

    public void syncCountsToES() throws IOException {
        List<StoreCountDto> counts = storeRepository.findAllStoreCounts();

        int batchSize = 100; // 100개씩 나눠서 처리

        for (int i = 0; i < counts.size(); i += batchSize) {
            List<StoreCountDto> batch = counts.subList(i,
                    Math.min(i + batchSize, counts.size()));

            bulkUpdate(batch);
            log.info("{}~{}번 처리 완료", i, i + batchSize);
        }
    }

    private void bulkUpdate(List<StoreCountDto> batch) throws IOException {

        List<BulkOperation> operations = batch.stream()
                .map(store -> {
                    ObjectNode doc = mapper.createObjectNode();
                    doc.put("bookmarkCount", store.getBookmarkCount());
                    doc.put("reviewCount", store.getReviewCount());
                    doc.put("avgRating", store.getAvgRating());

                    return BulkOperation.of(op -> op
                            .update(u -> u
                                    .index("stores")
                                    .id(String.valueOf(store.getStoreId()))
                                    .action(a -> a.doc(doc))
                            )
                    );
                })
                .toList();

        BulkResponse response = esClient.bulk(b -> b.operations(operations));

        if (response.errors()) {
            response.items().stream()
                    .filter(item -> item.error() != null)
                    .forEach(item ->
                            log.error("실패 id:{} 이유:{}", item.id(), item.error().reason()));
        }
    }
}

