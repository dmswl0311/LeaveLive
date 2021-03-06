package leavelive.accommodation.controller;

import leavelive.accommodation.domain.dto.AccommodationResDto;
import leavelive.accommodation.domain.dto.AccommodationResNope;
import leavelive.accommodation.domain.dto.AccommodationResRes;
import leavelive.accommodation.service.ReservationServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/accommodation/reservation")
@RequiredArgsConstructor
@Slf4j
public class ReservationController {
    private final ReservationServiceImpl service;

    @GetMapping("/")
    public ResponseEntity<List<AccommodationResDto>> getAllAccommodationFav(HttpServletResponse response) {
        String userId = response.getHeader("userId");
        log.info("AcommodationResController.getAllAccommodationFav.userId:" + userId);
        List<AccommodationResDto> list = service.findByUserId(userId);
        return new ResponseEntity(list, HttpStatus.OK);
    }

    @GetMapping("/all/{accommodation_id}")
    public ResponseEntity<List<AccommodationResDto>> getAllAccommodation(@PathVariable("accommodation_id") Long id) {
        List<AccommodationResNope> list = service.findAllByAccommodatinoId(id);
        return new ResponseEntity(list, HttpStatus.OK);
    }

    @PostMapping("/{accommodation_id}")
    public ResponseEntity<Long> reservationAccommodation(HttpServletResponse response, @PathVariable("accommodation_id") Long id, @RequestBody AccommodationResDto request) {
        String userId = response.getHeader("userId");
        log.info("AcommodationResController.reservationAccommodation.userId:" + userId);
        Long result = service.saveReservation(userId, id, request);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @DeleteMapping("/{accommodation_res_id}")
    public ResponseEntity<Boolean> deleteAccommodationRes(HttpServletResponse response, @PathVariable("accommodation_res_id") Long id) {
        String userId = response.getHeader("userId");
        log.info("AcommodationResController.deleteAccommodationRes.userId:" + userId);
        Boolean result = service.deleteReservation(userId, id);
        return new ResponseEntity(result, HttpStatus.OK);
    }
    @GetMapping("/my")
    public ResponseEntity<List<AccommodationResRes>> getAllMyAccRes(HttpServletResponse response){
        String userId = response.getHeader("userId");
        log.info("AccommodationController.getAllMyAccRes.userId:" + userId);
        List<AccommodationResRes> result=service.getAllMyReservation(userId);
        return new ResponseEntity(result,HttpStatus.OK);
    }
    @GetMapping("/{date}")
    public ResponseEntity<List<AccommodationResDto>> getAllMyAccResByDate(HttpServletResponse response, @PathVariable("date") String date){
        String userId = response.getHeader("userId");
        log.info("AccommodationController.getAllMyAccRes.userId:" + userId);
        List<AccommodationResDto> result=service.getAllMyAccResByDate(date,userId);
        return new ResponseEntity(result,HttpStatus.OK);
    }
}
