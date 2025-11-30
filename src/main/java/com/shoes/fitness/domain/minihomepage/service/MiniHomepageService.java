package com.shoes.fitness.domain.minihomepage.service;

import com.shoes.fitness.domain.minihomepage.dto.*;
import com.shoes.fitness.domain.minihomepage.repository.*;
import com.shoes.fitness.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MiniHomepageService {

    private final FitnessCenterRepository centerRepository;
    private final FitnessCenterGalleryRepository galleryRepository;
    private final FitnessCenterOperationHoursRepository operationHoursRepository;
    private final FitnessCenterPriceRepository priceRepository;
    private final FitnessFacilityMasterRepository facilityMasterRepository;
    private final FitnessCenterFacilityRepository centerFacilityRepository;
    private final FitnessCenterEventRepository eventRepository;

    // ==================== 센터 기본 정보 ====================

    public CenterInfoResponse getCenterById(String centerId) {
        FitnessCenter center = centerRepository.findById(centerId)
                .orElseThrow(() -> new IllegalArgumentException("센터를 찾을 수 없습니다. ID: " + centerId));
        return CenterInfoResponse.from(center);
    }

    public CenterInfoResponse getMyCenterInfo(String fitnessId) {
        FitnessCenter center = centerRepository.findByFitnessId(fitnessId)
                .orElse(null);
        return center != null ? CenterInfoResponse.from(center) : null;
    }

    @Transactional
    public CenterInfoResponse saveOrUpdateCenter(String fitnessId, CenterInfoRequest request) {
        FitnessCenter center = centerRepository.findByFitnessId(fitnessId)
                .orElse(FitnessCenter.builder().fitnessId(fitnessId).build());

        center.setCenterName(request.getCenterName());
        center.setCategory(request.getCategory());
        center.setPhone(request.getPhone());
        center.setEmail(request.getEmail());
        center.setDescription(request.getDescription());
        center.setAddress(request.getAddress());
        center.setAddressDetail(request.getAddressDetail());
        center.setLatitude(request.getLatitude());
        center.setLongitude(request.getLongitude());
        if (request.getIsPublic() != null) {
            center.setIsPublic(request.getIsPublic());
        }
        if (request.getThumbnailUrl() != null) {
            center.setThumbnailUrl(request.getThumbnailUrl());
        }

        FitnessCenter saved = centerRepository.save(center);
        log.info("센터 정보 저장/수정 완료. fitnessId: {}, centerId: {}", fitnessId, saved.getCenterId());
        return CenterInfoResponse.from(saved);
    }

    @Transactional
    public CenterInfoResponse togglePublic(String fitnessId) {
        FitnessCenter center = centerRepository.findByFitnessId(fitnessId)
                .orElseThrow(() -> new IllegalArgumentException("센터를 찾을 수 없습니다."));

        center.setIsPublic(!center.getIsPublic());
        FitnessCenter saved = centerRepository.save(center);
        log.info("센터 공개 상태 토글. fitnessId: {}, isPublic: {}", fitnessId, saved.getIsPublic());
        return CenterInfoResponse.from(saved);
    }

    // ==================== 갤러리 ====================

    public List<GalleryResponse> getGalleries(String centerId) {
        return galleryRepository.findByCenterIdOrderBySortOrderAsc(centerId)
                .stream()
                .map(GalleryResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public GalleryResponse addGalleryImage(String fitnessId, GalleryRequest request) {
        FitnessCenter center = centerRepository.findByFitnessId(fitnessId)
                .orElseThrow(() -> new IllegalArgumentException("센터를 찾을 수 없습니다."));

        FitnessCenterGallery gallery = FitnessCenterGallery.builder()
                .centerId(center.getCenterId())
                .imageUrl(request.getImageUrl())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .build();

        FitnessCenterGallery saved = galleryRepository.save(gallery);
        log.info("갤러리 이미지 추가. centerId: {}, galleryId: {}", center.getCenterId(), saved.getGalleryId());
        return GalleryResponse.from(saved);
    }

    @Transactional
    public void deleteGalleryImage(String galleryId) {
        galleryRepository.deleteById(galleryId);
        log.info("갤러리 이미지 삭제. galleryId: {}", galleryId);
    }

    @Transactional
    public void updateGalleryOrder(GalleryOrderRequest request) {
        for (GalleryOrderRequest.GalleryOrderItem item : request.getItems()) {
            galleryRepository.updateSortOrder(item.getGalleryId(), item.getSortOrder());
        }
        log.info("갤러리 순서 변경 완료. 변경 건수: {}", request.getItems().size());
    }

    // ==================== 운영 시간 ====================

    public OperationHoursResponse getOperationHours(String centerId) {
        return operationHoursRepository.findByCenterId(centerId)
                .map(OperationHoursResponse::from)
                .orElse(null);
    }

    @Transactional
    public OperationHoursResponse saveOrUpdateOperationHours(String fitnessId, OperationHoursRequest request) {
        FitnessCenter center = centerRepository.findByFitnessId(fitnessId)
                .orElseThrow(() -> new IllegalArgumentException("센터를 찾을 수 없습니다."));

        FitnessCenterOperationHours hours = operationHoursRepository.findByCenterId(center.getCenterId())
                .orElse(FitnessCenterOperationHours.builder().centerId(center.getCenterId()).build());

        hours.setIsAlwaysOpen(request.getIsAlwaysOpen());
        hours.setAlwaysOpenStart(request.getAlwaysOpenStartAsTime());
        hours.setAlwaysOpenEnd(request.getAlwaysOpenEndAsTime());
        hours.setWeekdayClosed(request.getWeekdayClosed());
        hours.setWeekdayOpen(request.getWeekdayOpenAsTime());
        hours.setWeekdayClose(request.getWeekdayCloseAsTime());
        hours.setSaturdayClosed(request.getSaturdayClosed());
        hours.setSaturdayOpen(request.getSaturdayOpenAsTime());
        hours.setSaturdayClose(request.getSaturdayCloseAsTime());
        hours.setSundayClosed(request.getSundayClosed());
        hours.setSundayOpen(request.getSundayOpenAsTime());
        hours.setSundayClose(request.getSundayCloseAsTime());
        hours.setHolidayClosed(request.getHolidayClosed());
        hours.setHolidayOpen(request.getHolidayOpenAsTime());
        hours.setHolidayClose(request.getHolidayCloseAsTime());

        FitnessCenterOperationHours saved = operationHoursRepository.save(hours);
        log.info("운영시간 저장/수정 완료. centerId: {}", center.getCenterId());
        return OperationHoursResponse.from(saved);
    }

    // ==================== 가격 정보 ====================

    public List<PriceResponse> getPrices(String centerId) {
        return priceRepository.findByCenterIdOrderBySortOrderAsc(centerId)
                .stream()
                .map(PriceResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public PriceResponse addPrice(String fitnessId, PriceRequest request) {
        FitnessCenter center = centerRepository.findByFitnessId(fitnessId)
                .orElseThrow(() -> new IllegalArgumentException("센터를 찾을 수 없습니다."));

        FitnessCenterPrice price = FitnessCenterPrice.builder()
                .centerId(center.getCenterId())
                .name(request.getName())
                .duration(request.getDuration())
                .price(request.getPrice())
                .discountPrice(request.getDiscountPrice())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        FitnessCenterPrice saved = priceRepository.save(price);
        log.info("가격 항목 추가. centerId: {}, priceId: {}", center.getCenterId(), saved.getPriceId());
        return PriceResponse.from(saved);
    }

    @Transactional
    public PriceResponse updatePrice(String priceId, PriceRequest request) {
        FitnessCenterPrice price = priceRepository.findById(priceId)
                .orElseThrow(() -> new IllegalArgumentException("가격 항목을 찾을 수 없습니다. ID: " + priceId));

        price.setName(request.getName());
        price.setDuration(request.getDuration());
        price.setPrice(request.getPrice());
        price.setDiscountPrice(request.getDiscountPrice());
        if (request.getSortOrder() != null) {
            price.setSortOrder(request.getSortOrder());
        }
        if (request.getIsActive() != null) {
            price.setIsActive(request.getIsActive());
        }

        FitnessCenterPrice saved = priceRepository.save(price);
        log.info("가격 항목 수정. priceId: {}", priceId);
        return PriceResponse.from(saved);
    }

    @Transactional
    public void deletePrice(String priceId) {
        priceRepository.deleteById(priceId);
        log.info("가격 항목 삭제. priceId: {}", priceId);
    }

    @Transactional
    public List<PriceResponse> savePricesBatch(String fitnessId, PriceBatchRequest request) {
        FitnessCenter center = centerRepository.findByFitnessId(fitnessId)
                .orElseThrow(() -> new IllegalArgumentException("센터를 찾을 수 없습니다."));

        // 기존 가격 삭제
        priceRepository.deleteByCenterId(center.getCenterId());

        // 새로운 가격 저장
        List<PriceResponse> results = new ArrayList<>();
        int sortOrder = 0;
        for (PriceRequest priceRequest : request.getPrices()) {
            FitnessCenterPrice price = FitnessCenterPrice.builder()
                    .centerId(center.getCenterId())
                    .name(priceRequest.getName())
                    .duration(priceRequest.getDuration())
                    .price(priceRequest.getPrice())
                    .discountPrice(priceRequest.getDiscountPrice())
                    .sortOrder(priceRequest.getSortOrder() != null ? priceRequest.getSortOrder() : sortOrder++)
                    .isActive(priceRequest.getIsActive() != null ? priceRequest.getIsActive() : true)
                    .build();
            results.add(PriceResponse.from(priceRepository.save(price)));
        }

        log.info("가격 목록 일괄 저장 완료. centerId: {}, 저장 건수: {}", center.getCenterId(), results.size());
        return results;
    }

    // ==================== 시설 정보 ====================

    public List<FacilityMasterResponse> getFacilityMasterList() {
        return facilityMasterRepository.findByIsActiveTrueOrderBySortOrderAsc()
                .stream()
                .map(FacilityMasterResponse::from)
                .collect(Collectors.toList());
    }

    public List<CenterFacilityResponse> getCenterFacilities(String centerId) {
        return centerFacilityRepository.findByCenterId(centerId)
                .stream()
                .map(CenterFacilityResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<CenterFacilityResponse> saveFacilities(String fitnessId, FacilityBatchRequest request) {
        FitnessCenter center = centerRepository.findByFitnessId(fitnessId)
                .orElseThrow(() -> new IllegalArgumentException("센터를 찾을 수 없습니다."));

        // 기존 시설 삭제
        centerFacilityRepository.deleteByCenterId(center.getCenterId());

        // 새로운 시설 저장
        List<CenterFacilityResponse> results = new ArrayList<>();
        for (String facilityCode : request.getFacilityCodes()) {
            FitnessCenterFacility facility = FitnessCenterFacility.builder()
                    .centerId(center.getCenterId())
                    .facilityCode(facilityCode)
                    .isActive(true)
                    .build();
            results.add(CenterFacilityResponse.from(centerFacilityRepository.save(facility)));
        }

        log.info("시설 목록 일괄 저장 완료. centerId: {}, 저장 건수: {}", center.getCenterId(), results.size());
        return results;
    }

    @Transactional
    public void toggleFacility(String fitnessId, String facilityCode) {
        FitnessCenter center = centerRepository.findByFitnessId(fitnessId)
                .orElseThrow(() -> new IllegalArgumentException("센터를 찾을 수 없습니다."));

        FitnessCenterFacility facility = centerFacilityRepository
                .findByCenterIdAndFacilityCode(center.getCenterId(), facilityCode)
                .orElseThrow(() -> new IllegalArgumentException("시설 정보를 찾을 수 없습니다."));

        facility.setIsActive(!facility.getIsActive());
        centerFacilityRepository.save(facility);
        log.info("시설 활성/비활성 토글. centerId: {}, facilityCode: {}, isActive: {}",
                center.getCenterId(), facilityCode, facility.getIsActive());
    }

    // ==================== 이벤트 ====================

    public List<EventResponse> getEvents(String centerId) {
        return eventRepository.findByCenterIdOrderByStartDateDesc(centerId)
                .stream()
                .map(EventResponse::from)
                .collect(Collectors.toList());
    }

    public EventResponse getEventDetail(String eventId) {
        FitnessCenterEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("이벤트를 찾을 수 없습니다. ID: " + eventId));
        return EventResponse.from(event);
    }

    @Transactional
    public EventResponse addEvent(String fitnessId, EventRequest request) {
        FitnessCenter center = centerRepository.findByFitnessId(fitnessId)
                .orElseThrow(() -> new IllegalArgumentException("센터를 찾을 수 없습니다."));

        FitnessCenterEvent event = FitnessCenterEvent.builder()
                .centerId(center.getCenterId())
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .discountRate(request.getDiscountRate())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(request.getStatus() != null ?
                        FitnessCenterEvent.EventStatus.valueOf(request.getStatus()) :
                        FitnessCenterEvent.EventStatus.SCHEDULED)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        FitnessCenterEvent saved = eventRepository.save(event);
        log.info("이벤트 추가. centerId: {}, eventId: {}", center.getCenterId(), saved.getEventId());
        return EventResponse.from(saved);
    }

    @Transactional
    public EventResponse updateEvent(String eventId, EventRequest request) {
        FitnessCenterEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("이벤트를 찾을 수 없습니다. ID: " + eventId));

        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setImageUrl(request.getImageUrl());
        event.setDiscountRate(request.getDiscountRate());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        if (request.getStatus() != null) {
            event.setStatus(FitnessCenterEvent.EventStatus.valueOf(request.getStatus()));
        }
        if (request.getIsActive() != null) {
            event.setIsActive(request.getIsActive());
        }

        FitnessCenterEvent saved = eventRepository.save(event);
        log.info("이벤트 수정. eventId: {}", eventId);
        return EventResponse.from(saved);
    }

    @Transactional
    public void deleteEvent(String eventId) {
        eventRepository.deleteById(eventId);
        log.info("이벤트 삭제. eventId: {}", eventId);
    }

    // ==================== 통합 API ====================

    public MiniHomepageAllResponse getAllData(String centerId) {
        CenterInfoResponse centerInfo = getCenterById(centerId);

        return MiniHomepageAllResponse.builder()
                .centerInfo(centerInfo)
                .galleries(getGalleries(centerId))
                .operationHours(getOperationHours(centerId))
                .prices(getPrices(centerId))
                .facilities(getCenterFacilities(centerId))
                .events(getEvents(centerId))
                .build();
    }

    @Transactional
    public MiniHomepageAllResponse saveAll(String fitnessId, MiniHomepageSaveRequest request) {
        // 1. 센터 정보 저장 (신규 등록 또는 수정)
        // 센터가 없으면 새로 생성, 있으면 업데이트
        CenterInfoResponse centerInfo = saveOrUpdateCenter(fitnessId, request.getCenter());
        String centerId = centerInfo.getCenterId();

        log.info("센터 저장 완료. fitnessId: {}, centerId: {}", fitnessId, centerId);

        // 2. 갤러리 저장
        List<GalleryResponse> galleries = new ArrayList<>();
        if (request.getGallery() != null && !request.getGallery().isEmpty()) {
            galleryRepository.deleteByCenterId(centerId);
            int sortOrder = 0;
            for (GalleryRequest galleryRequest : request.getGallery()) {
                FitnessCenterGallery gallery = FitnessCenterGallery.builder()
                        .centerId(centerId)
                        .imageUrl(galleryRequest.getImageUrl())
                        .sortOrder(galleryRequest.getSortOrder() != null ? galleryRequest.getSortOrder() : sortOrder++)
                        .build();
                galleries.add(GalleryResponse.from(galleryRepository.save(gallery)));
            }
            log.info("갤러리 저장 완료. centerId: {}, 저장 건수: {}", centerId, galleries.size());
        }

        // 3. 운영시간 저장
        OperationHoursResponse operationHours = null;
        if (request.getOperationHours() != null) {
            OperationHoursRequest opReq = request.getOperationHours();
            FitnessCenterOperationHours hours = operationHoursRepository.findByCenterId(centerId)
                    .orElse(FitnessCenterOperationHours.builder().centerId(centerId).build());

            hours.setIsAlwaysOpen(opReq.getIsAlwaysOpen());
            hours.setAlwaysOpenStart(opReq.getAlwaysOpenStartAsTime());
            hours.setAlwaysOpenEnd(opReq.getAlwaysOpenEndAsTime());
            hours.setWeekdayClosed(opReq.getWeekdayClosed());
            hours.setWeekdayOpen(opReq.getWeekdayOpenAsTime());
            hours.setWeekdayClose(opReq.getWeekdayCloseAsTime());
            hours.setSaturdayClosed(opReq.getSaturdayClosed());
            hours.setSaturdayOpen(opReq.getSaturdayOpenAsTime());
            hours.setSaturdayClose(opReq.getSaturdayCloseAsTime());
            hours.setSundayClosed(opReq.getSundayClosed());
            hours.setSundayOpen(opReq.getSundayOpenAsTime());
            hours.setSundayClose(opReq.getSundayCloseAsTime());
            hours.setHolidayClosed(opReq.getHolidayClosed());
            hours.setHolidayOpen(opReq.getHolidayOpenAsTime());
            hours.setHolidayClose(opReq.getHolidayCloseAsTime());

            operationHours = OperationHoursResponse.from(operationHoursRepository.save(hours));
            log.info("운영시간 저장 완료. centerId: {}", centerId);
        }

        // 4. 가격 저장
        List<PriceResponse> prices = new ArrayList<>();
        if (request.getPrices() != null && !request.getPrices().isEmpty()) {
            priceRepository.deleteByCenterId(centerId);
            int sortOrder = 0;
            for (PriceRequest priceRequest : request.getPrices()) {
                FitnessCenterPrice price = FitnessCenterPrice.builder()
                        .centerId(centerId)
                        .name(priceRequest.getName())
                        .duration(priceRequest.getDuration())
                        .price(priceRequest.getPrice())
                        .discountPrice(priceRequest.getDiscountPrice())
                        .sortOrder(priceRequest.getSortOrder() != null ? priceRequest.getSortOrder() : sortOrder++)
                        .isActive(priceRequest.getIsActive() != null ? priceRequest.getIsActive() : true)
                        .build();
                prices.add(PriceResponse.from(priceRepository.save(price)));
            }
            log.info("가격 저장 완료. centerId: {}, 저장 건수: {}", centerId, prices.size());
        }

        // 5. 시설 저장
        List<CenterFacilityResponse> facilities = new ArrayList<>();
        if (request.getFacilities() != null && !request.getFacilities().isEmpty()) {
            centerFacilityRepository.deleteByCenterId(centerId);
            for (String facilityCode : request.getFacilities()) {
                FitnessCenterFacility facility = FitnessCenterFacility.builder()
                        .centerId(centerId)
                        .facilityCode(facilityCode)
                        .isActive(true)
                        .build();
                facilities.add(CenterFacilityResponse.from(centerFacilityRepository.save(facility)));
            }
            log.info("시설 저장 완료. centerId: {}, 저장 건수: {}", centerId, facilities.size());
        }

        // 6. 이벤트 저장
        List<EventResponse> events = new ArrayList<>();
        if (request.getEvents() != null && !request.getEvents().isEmpty()) {
            eventRepository.deleteByCenterId(centerId);
            for (EventRequest eventRequest : request.getEvents()) {
                FitnessCenterEvent event = FitnessCenterEvent.builder()
                        .centerId(centerId)
                        .name(eventRequest.getName())
                        .description(eventRequest.getDescription())
                        .imageUrl(eventRequest.getImageUrl())
                        .discountRate(eventRequest.getDiscountRate())
                        .startDate(eventRequest.getStartDate())
                        .endDate(eventRequest.getEndDate())
                        .status(eventRequest.getStatus() != null ?
                                FitnessCenterEvent.EventStatus.valueOf(eventRequest.getStatus()) :
                                FitnessCenterEvent.EventStatus.SCHEDULED)
                        .isActive(eventRequest.getIsActive() != null ? eventRequest.getIsActive() : true)
                        .build();
                events.add(EventResponse.from(eventRepository.save(event)));
            }
            log.info("이벤트 저장 완료. centerId: {}, 저장 건수: {}", centerId, events.size());
        }

        log.info("미니홈페이지 전체 저장 완료. fitnessId: {}, centerId: {}", fitnessId, centerId);

        return MiniHomepageAllResponse.builder()
                .centerInfo(centerInfo)
                .galleries(galleries)
                .operationHours(operationHours)
                .prices(prices)
                .facilities(facilities)
                .events(events)
                .build();
    }
}
