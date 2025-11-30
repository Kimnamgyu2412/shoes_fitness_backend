package com.shoes.fitness.domain.minihomepage.controller;

import com.shoes.fitness.common.dto.ApiResponse;
import com.shoes.fitness.common.security.CurrentUser;
import com.shoes.fitness.common.security.UserPrincipal;
import com.shoes.fitness.domain.minihomepage.dto.*;
import com.shoes.fitness.domain.minihomepage.service.MiniHomepageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/fitness/mini-homepage")
@RequiredArgsConstructor
public class MiniHomepageController {

    private final MiniHomepageService miniHomepageService;

    // ==================== 센터 기본 정보 ====================

    @GetMapping("/center/{centerId}")
    public ResponseEntity<ApiResponse<CenterInfoResponse>> getCenterById(@PathVariable String centerId) {
        CenterInfoResponse response = miniHomepageService.getCenterById(centerId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/center/my")
    public ResponseEntity<ApiResponse<CenterInfoResponse>> getMyCenterInfo(@CurrentUser UserPrincipal userPrincipal) {
        CenterInfoResponse response = miniHomepageService.getMyCenterInfo(userPrincipal.getFitnessId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/center")
    public ResponseEntity<ApiResponse<CenterInfoResponse>> saveOrUpdateCenter(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody CenterInfoRequest request) {
        CenterInfoResponse response = miniHomepageService.saveOrUpdateCenter(userPrincipal.getFitnessId(), request);
        return ResponseEntity.ok(ApiResponse.success("센터 정보가 저장되었습니다.", response));
    }

    @PatchMapping("/center/public")
    public ResponseEntity<ApiResponse<CenterInfoResponse>> togglePublic(@CurrentUser UserPrincipal userPrincipal) {
        CenterInfoResponse response = miniHomepageService.togglePublic(userPrincipal.getFitnessId());
        return ResponseEntity.ok(ApiResponse.success("공개 상태가 변경되었습니다.", response));
    }

    // ==================== 갤러리 ====================

    @GetMapping("/gallery/{centerId}")
    public ResponseEntity<ApiResponse<List<GalleryResponse>>> getGalleries(@PathVariable String centerId) {
        List<GalleryResponse> response = miniHomepageService.getGalleries(centerId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/gallery")
    public ResponseEntity<ApiResponse<GalleryResponse>> addGalleryImage(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody GalleryRequest request) {
        GalleryResponse response = miniHomepageService.addGalleryImage(userPrincipal.getFitnessId(), request);
        return ResponseEntity.ok(ApiResponse.success("갤러리 이미지가 추가되었습니다.", response));
    }

    @DeleteMapping("/gallery/{galleryId}")
    public ResponseEntity<ApiResponse<Void>> deleteGalleryImage(@PathVariable String galleryId) {
        miniHomepageService.deleteGalleryImage(galleryId);
        return ResponseEntity.ok(ApiResponse.successWithMessage("갤러리 이미지가 삭제되었습니다."));
    }

    @PutMapping("/gallery/order")
    public ResponseEntity<ApiResponse<Void>> updateGalleryOrder(@RequestBody GalleryOrderRequest request) {
        miniHomepageService.updateGalleryOrder(request);
        return ResponseEntity.ok(ApiResponse.successWithMessage("갤러리 순서가 변경되었습니다."));
    }

    // ==================== 운영 시간 ====================

    @GetMapping("/operation-hours/{centerId}")
    public ResponseEntity<ApiResponse<OperationHoursResponse>> getOperationHours(@PathVariable String centerId) {
        OperationHoursResponse response = miniHomepageService.getOperationHours(centerId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/operation-hours")
    public ResponseEntity<ApiResponse<OperationHoursResponse>> saveOrUpdateOperationHours(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody OperationHoursRequest request) {
        OperationHoursResponse response = miniHomepageService.saveOrUpdateOperationHours(userPrincipal.getFitnessId(), request);
        return ResponseEntity.ok(ApiResponse.success("운영 시간이 저장되었습니다.", response));
    }

    // ==================== 가격 정보 ====================

    @GetMapping("/prices/{centerId}")
    public ResponseEntity<ApiResponse<List<PriceResponse>>> getPrices(@PathVariable String centerId) {
        List<PriceResponse> response = miniHomepageService.getPrices(centerId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/prices")
    public ResponseEntity<ApiResponse<PriceResponse>> addPrice(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody PriceRequest request) {
        PriceResponse response = miniHomepageService.addPrice(userPrincipal.getFitnessId(), request);
        return ResponseEntity.ok(ApiResponse.success("가격 항목이 추가되었습니다.", response));
    }

    @PutMapping("/prices/{priceId}")
    public ResponseEntity<ApiResponse<PriceResponse>> updatePrice(
            @PathVariable String priceId,
            @RequestBody PriceRequest request) {
        PriceResponse response = miniHomepageService.updatePrice(priceId, request);
        return ResponseEntity.ok(ApiResponse.success("가격 항목이 수정되었습니다.", response));
    }

    @DeleteMapping("/prices/{priceId}")
    public ResponseEntity<ApiResponse<Void>> deletePrice(@PathVariable String priceId) {
        miniHomepageService.deletePrice(priceId);
        return ResponseEntity.ok(ApiResponse.successWithMessage("가격 항목이 삭제되었습니다."));
    }

    @PostMapping("/prices/batch")
    public ResponseEntity<ApiResponse<List<PriceResponse>>> savePricesBatch(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody PriceBatchRequest request) {
        List<PriceResponse> response = miniHomepageService.savePricesBatch(userPrincipal.getFitnessId(), request);
        return ResponseEntity.ok(ApiResponse.success("가격 목록이 저장되었습니다.", response));
    }

    // ==================== 시설 정보 ====================

    @GetMapping("/facilities/master")
    public ResponseEntity<ApiResponse<List<FacilityMasterResponse>>> getFacilityMasterList() {
        List<FacilityMasterResponse> response = miniHomepageService.getFacilityMasterList();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/facilities/{centerId}")
    public ResponseEntity<ApiResponse<List<CenterFacilityResponse>>> getCenterFacilities(@PathVariable String centerId) {
        List<CenterFacilityResponse> response = miniHomepageService.getCenterFacilities(centerId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/facilities")
    public ResponseEntity<ApiResponse<List<CenterFacilityResponse>>> saveFacilities(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody List<String> facilityCodes) {
        FacilityBatchRequest request = FacilityBatchRequest.builder().facilityCodes(facilityCodes).build();
        List<CenterFacilityResponse> response = miniHomepageService.saveFacilities(userPrincipal.getFitnessId(), request);
        return ResponseEntity.ok(ApiResponse.success("시설 목록이 저장되었습니다.", response));
    }

    @PatchMapping("/facilities/{facilityCode}")
    public ResponseEntity<ApiResponse<Void>> toggleFacility(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable String facilityCode) {
        miniHomepageService.toggleFacility(userPrincipal.getFitnessId(), facilityCode);
        return ResponseEntity.ok(ApiResponse.successWithMessage("시설 상태가 변경되었습니다."));
    }

    // ==================== 이벤트 ====================

    @GetMapping("/events/{centerId}")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getEvents(@PathVariable String centerId) {
        List<EventResponse> response = miniHomepageService.getEvents(centerId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/events/detail/{eventId}")
    public ResponseEntity<ApiResponse<EventResponse>> getEventDetail(@PathVariable String eventId) {
        EventResponse response = miniHomepageService.getEventDetail(eventId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/events")
    public ResponseEntity<ApiResponse<EventResponse>> addEvent(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody EventRequest request) {
        EventResponse response = miniHomepageService.addEvent(userPrincipal.getFitnessId(), request);
        return ResponseEntity.ok(ApiResponse.success("이벤트가 추가되었습니다.", response));
    }

    @PutMapping("/events/{eventId}")
    public ResponseEntity<ApiResponse<EventResponse>> updateEvent(
            @PathVariable String eventId,
            @RequestBody EventRequest request) {
        EventResponse response = miniHomepageService.updateEvent(eventId, request);
        return ResponseEntity.ok(ApiResponse.success("이벤트가 수정되었습니다.", response));
    }

    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable String eventId) {
        miniHomepageService.deleteEvent(eventId);
        return ResponseEntity.ok(ApiResponse.successWithMessage("이벤트가 삭제되었습니다."));
    }

    // ==================== 통합 API ====================

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<MiniHomepageAllResponse>> getAllData(@RequestParam String centerId) {
        MiniHomepageAllResponse response = miniHomepageService.getAllData(centerId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<MiniHomepageAllResponse>> saveAll(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody MiniHomepageSaveRequest request) {
        MiniHomepageAllResponse response = miniHomepageService.saveAll(userPrincipal.getFitnessId(), request);
        return ResponseEntity.ok(ApiResponse.success("미니홈페이지가 저장되었습니다.", response));
    }
}
