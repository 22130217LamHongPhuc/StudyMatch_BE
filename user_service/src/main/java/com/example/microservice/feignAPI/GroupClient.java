package com.example.microservice.feignAPI;

import com.example.microservice.dto.respone.AdminOverviewResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "GROUP-SERVICE", url = "${GROUP_SERVICE_URL:http://localhost:8086}")
public interface GroupClient {

    @GetMapping("/api/groups/{groupId}/exists")
    boolean existsById(@PathVariable Long groupId);

    @GetMapping("/api/groups/{groupId}")
    java.util.Map<String, Object> getGroup(@PathVariable("groupId") Long groupId);
    @GetMapping("/api/admin/overview/subjects")
    List<AdminOverviewResponse.SubjectGroupStatDto> getTopSubjects();

    @GetMapping("/api/admin/overview/study-duration")
    List<AdminOverviewResponse.StudyDurationTimelineDto> getStudyDurationTimeline(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate);
}
