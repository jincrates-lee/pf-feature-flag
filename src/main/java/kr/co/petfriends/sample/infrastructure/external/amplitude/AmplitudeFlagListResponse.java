package kr.co.petfriends.sample.infrastructure.external.amplitude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record AmplitudeFlagListResponse(
    Set<AmplitudeFlagResponse> flags
) {

    record AmplitudeFlagResponse(
        String id,
        String projectId,
        List<String> deployments,
        String key,
        String name,
        String description,
        boolean enabled,
        String evaluationMode,
        String bucketingKey,
        String bucketingSalt,
        String bucketingUnit,
        List<HashMap<String, Object>> variants,
        int rolloutPercentage,
        Map<String, Integer> rolloutWeights,
        List<AmplitudeTargetSegment> targetSegments,
        AmplitudeParentDependencies parentDependencies,
        boolean deleted
    ) {

        // TargetSegment 레코드
        record AmplitudeTargetSegment(
            String name,
            List<AmplitudeCondition> conditions,
            int percentage,
            String bucketingKey,
            Map<String, Integer> rolloutWeights
        ) {

            // Condition 레코드
            record AmplitudeCondition(
                String prop,
                String op,
                String type,
                List<String> values
            ) {

            }
        }

        // ParentDependencies 레코드
        record AmplitudeParentDependencies(
            Map<String, List<String>> flags,
            String operator
        ) {

        }
    }
}
