package com.example.demo.documents;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ObjectUserStatistics {
    private int addNewCar = 0;
    private int updateCar = 0;
    private int deleteCar = 0;
    private int ownerGetCars = 0;
    private int ownerGetCarBySerialNumber = 0;
    private int ownerGetBookedPeriodByCarId = 0;
    private int addNewCommentByCarId = 0;
    private int paymentForReservation = 0;
    private int makeReservation = 0;
    private int reservationCancellation = 0;
    private int unlockCarForBookingCarId = 0;
    private int lockCarForBookingCarId = 0;
    private int registrationUser = 0;
    private int updateUser = 0;
    private int deleteUser = 0;
    private int getBookedList = 0;
    private int getInvoice = 0;
    private int authUser = 0;
    private int getHistory = 0;
}
